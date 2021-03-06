package com.upgrad.quora.api.controller;

import com.google.gson.Gson;
import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;


@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final Integer questionId,@RequestHeader("autherization")String autherization,final AnswerRequest answerRequest) throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthToken=userBusinessService.authorize(autherization);
        UserEntity user = userAuthToken.getUser();
        final ZonedDateTime now = ZonedDateTime.now();
        final AnswerEntity userEntity = new AnswerEntity();
        userEntity.setUuid(user.getUuid());
        userEntity.setUser(user);
        userEntity.setAns(answerRequest.getAnswer());
        final AnswerEntity createdUserEntity = answerBusinessService.createAnswer(userEntity,questionId);
        AnswerResponse userResponse = new AnswerResponse().id(createdUserEntity.getUuid()).status("ANSWER Created");
        if(userResponse!=null) {
            return new ResponseEntity<AnswerResponse>(userResponse, HttpStatus.CREATED);
        }
        else {
            QuestionResponse userRes=new QuestionResponse().status("Internal Server Error");
            return new ResponseEntity<>(userResponse,HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(method=RequestMethod.PUT,path="/answer/edit/{id}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse>editAnswerContent(@RequestHeader("autherization")String autherization, @PathVariable("id") Integer id) throws AuthorizationFailedException, InvalidQuestionException { final AnswerEntity answerEntity=answerBusinessService.editAnswerContent(autherization,id);
   AnswerEditResponse answerEditResponse=new AnswerEditResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
   return new ResponseEntity<>(answerEditResponse,HttpStatus.OK);
    }
    @RequestMapping(method=RequestMethod.DELETE,path="answer/delete/{id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse>deleteAnswer(@RequestHeader("autherization")String autherization,@PathVariable("id") Integer id) throws AuthorizationFailedException, InvalidQuestionException {
    final AnswerEntity answerEntity=answerBusinessService.deleteAnswer(autherization,id);
    AnswerResponse answerResponse=new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
    return new ResponseEntity<>(answerResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDetailsResponse> getAllAnswersToQuestion (@RequestHeader("authorization") final String authorization,@PathVariable("questionId")Integer quesInteger) throws AuthorizationFailedException, InvalidQuestionException {
        final List<AnswerEntity> questionsList = answerBusinessService.getAllAnswersToQuestion(authorization,quesInteger);
        String result=null;
        Gson gson=new Gson();
        result=gson.toJson(questionsList);
        QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().content(result);
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }
}
