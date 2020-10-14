package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao dao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        try{
            return dao.createQuestion(questionEntity);
        }catch(Exception e){
            throw e;
        }
    }

    public List<QuestionEntity> getAllQuestions(UserAuthTokenEntity userAuthTokenEntity) throws AuthorizationFailedException {
        ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();
        if(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return dao.getAllQuestions();
    }

    public List<QuestionEntity> getAllQuestionsByUser(UserAuthTokenEntity userAuthTokenEntity, String userId) throws AuthorizationFailedException {
        ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();
        if(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return dao.getAllQuestionsByUser(userId);
    }

}
