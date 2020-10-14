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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequestRequest) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthToken = userBusinessService.authenticate(decodedArray[0],decodedArray[1]);
        UserEntity user = userAuthToken.getUser();
        final ZonedDateTime now = ZonedDateTime.now();

        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequestRequest.getContent());
        questionEntity.setUser(user);
        questionEntity.setDate(now);
        final QuestionEntity createdUserEntity = questionBusinessService.createQuestion(questionEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/{all}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userBusinessService.authorize(authorization);
        final List<QuestionEntity> questionsList = questionBusinessService.getAllQuestions(userAuthToken);
        String lstOfQuestionContent = questionsList.get(0).getContent() + "\n";
        for(int idx = 1; idx < questionsList.size(); idx++)
        {
            lstOfQuestionContent = lstOfQuestionContent + questionsList.get(idx).getContent() + "\n";
        }
        QuestionDetailsResponse userResponse = new QuestionDetailsResponse().id(userAuthToken.getUuid()).content(lstOfQuestionContent);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
