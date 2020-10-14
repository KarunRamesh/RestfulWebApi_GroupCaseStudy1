package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequestRequest) throws AuthenticationFailedException {

            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            UserAuthTokenEntity userAuthToken = userBusinessService.authenticate(decodedArray[0], decodedArray[1]);
            UserEntity user = userAuthToken.getUser();
            final ZonedDateTime now = ZonedDateTime.now();
            if (Objects.isNull(user)) {
                throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
            }
            final QuestionEntity userEntity = new QuestionEntity();

            userEntity.setUuid(user.getUuid());
            userEntity.setContent(questionRequestRequest.getContent());
            userEntity.setUser(user);
            userEntity.setDate(now);
            final QuestionEntity createdUserEntity = questionBusinessService.createQuestion(userEntity);

            QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Created");
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);


    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/{all}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        try {
            UserAuthTokenEntity userAuthToken = userBusinessService.authorize(authorization);
            final List<QuestionEntity> questionsList = questionBusinessService.getAllQuestions(userAuthToken);
            String lstOfQuestionContent = questionsList.get(0).getContent() + "\n";
            for (int idx = 1; idx < questionsList.size(); idx++) {
                lstOfQuestionContent = lstOfQuestionContent + questionsList.get(idx).getContent() + "\n";
            }
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(userAuthToken.getUuid()).content(lstOfQuestionContent);
            return new ResponseEntity<>(questionResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().content("Internal Server Error");

            return new ResponseEntity<QuestionDetailsResponse>(questionResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        try {
            UserAuthTokenEntity userAuthToken = userBusinessService.authorize(authorization);
            final List<QuestionEntity> questionsList = questionBusinessService.getAllQuestionsByUser(userAuthToken, userId);
            String lstOfQuestionContent = questionsList.get(0).getContent() + "\n";
            for (int idx = 1; idx < questionsList.size(); idx++) {
                lstOfQuestionContent = lstOfQuestionContent + questionsList.get(idx).getContent() + "\n";
            }
            QuestionDetailsResponse userResponse = new QuestionDetailsResponse().id(userAuthToken.getUuid()).content(lstOfQuestionContent);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().content("Internal Server Error");
            return new ResponseEntity<QuestionDetailsResponse>(questionResponse,HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(method=RequestMethod.PUT,path="/question/edit/{id}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> editQuestionContent(@RequestHeader("authorization") final String authorization,@PathVariable("id") final String id) throws AuthorizationFailedException, InvalidQuestionException {

            final QuestionEntity createdUserEntity = questionBusinessService.editQuestionContent(authorization,id);

            QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Created");
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);

    }
}

