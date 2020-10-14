package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequestRequest) throws AuthenticationFailedException {
        try {
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

            userEntity.setUuid(UUID.randomUUID().toString());
            userEntity.setContent(questionRequestRequest.getContent());
            userEntity.setUser(user);
            userEntity.setDate(now);
            final QuestionEntity createdUserEntity = questionBusinessService.createQuestion(userEntity);

            QuestionResponse userResponse = new QuestionResponse().id(createdUserEntity.getUuid()).status("Question Created");
            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            QuestionResponse userResponse = new QuestionResponse().status("Internal Server Error");

            return new ResponseEntity<QuestionResponse>(userResponse,HttpStatus.BAD_REQUEST);
        }
    }

}

