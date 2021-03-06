package com.upgrad.quora.api.controller;

import com.google.gson.Gson;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequestRequest) throws AuthenticationFailedException, AuthorizationFailedException {
            logger.info("create question api called");
           UserAuthTokenEntity userAuthToken=userBusinessService.authorize(authorization);
           logger.info(userAuthToken.getExpiresAt()+" "+userAuthToken.getLogoutAt()+" "+userAuthToken.getUser());
           UserEntity user = userAuthToken.getUser();
            final ZonedDateTime now = ZonedDateTime.now();
            final QuestionEntity userEntity = new QuestionEntity();
            userEntity.setUuid(user.getUuid());
            userEntity.setContent(questionRequestRequest.getContent());
            userEntity.setUser(user);
            userEntity.setDate(now);
            final QuestionEntity createdUserEntity = questionBusinessService.createQuestion(userEntity);
            QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Created");
           if(userResponse!=null) {
               logger.info("Question Created Sucessfully");
               return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
           }
           else {
               logger.error("Error occured while crating the question");
               QuestionResponse userRes=new QuestionResponse().status("Internal Server Error");
               return new ResponseEntity<>(userResponse,HttpStatus.BAD_REQUEST);
           }
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/{all}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
            UserAuthTokenEntity userAuthToken = userBusinessService.authorize(authorization);
            final List<QuestionEntity> questionsList = questionBusinessService.getAllQuestions(userAuthToken);
            String lstOfQuestionContent = questionsList.get(0).getContent() + "\n";
            for (int idx = 1; idx < questionsList.size(); idx++) {
                lstOfQuestionContent = lstOfQuestionContent + questionsList.get(idx).getContent() + "\n";
            }
            String result=null;
            Gson gson=new Gson();
            result=gson.toJson(questionsList);
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(userAuthToken.getUuid()).content(result);
            return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthToken = userBusinessService.authorize(authorization);
        final List<QuestionEntity> questionsList = questionBusinessService.getAllQuestionsByUser(userAuthToken, userId);
        String lstOfQuestionContent = questionsList.get(0).getContent() + "\n";
        for (int idx = 1; idx < questionsList.size(); idx++) {
            lstOfQuestionContent = lstOfQuestionContent + questionsList.get(idx).getContent() + "\n";
        }
        QuestionDetailsResponse userResponse = new QuestionDetailsResponse().id(userAuthToken.getUuid()).content(lstOfQuestionContent);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }
    @RequestMapping(method=RequestMethod.PUT,path="/question/edit/{id}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> editQuestionContent(@RequestHeader("authorization") final String authorization,@PathVariable("id") final Integer id) throws AuthorizationFailedException, InvalidQuestionException {
         logger.info("edit question api called");
            final QuestionEntity createdUserEntity = questionBusinessService.editQuestionContent(authorization,id);
            QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Deleted Successfully");
        if(userResponse!=null) {
            logger.info("Question Modified Sucessfully");
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        }
        else {
            logger.error("Error occured while editing  the question");
            QuestionResponse userRes=new QuestionResponse().status("Internal Server Error");
            return new ResponseEntity<>(userResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method=RequestMethod.DELETE,path="/question/delete/{id}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> deleteQuestion (@RequestHeader("authorization") final String authorization,@PathVariable("id") final Integer id) throws AuthorizationFailedException, InvalidQuestionException {
        logger.info("Delete question api called");
        final QuestionEntity createdUserEntity = questionBusinessService.deleteQuestion(authorization,id);
        QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Modified Successfully");
        if(userResponse!=null) {
            logger.info("Question Deleted Sucessfully");
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        }
        else {
            logger.error("Error occured while Deleting  the question");
            QuestionResponse userRes=new QuestionResponse().status("Internal Server Error");
            return new ResponseEntity<>(userResponse,HttpStatus.BAD_REQUEST);
        }
    }
}

