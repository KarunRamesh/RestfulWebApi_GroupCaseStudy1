package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
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


@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> createAnswer(@PathVariable("questionId") final String questionId, final AnswerRequest answerRequest) throws AuthenticationFailedException {
        
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
}
