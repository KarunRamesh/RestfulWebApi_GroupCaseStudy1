package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao dao;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private UserBusinessService userBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        try {
            return dao.createQuestion(questionEntity);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<QuestionEntity> getAllQuestions(UserAuthTokenEntity userAuthTokenEntity) throws AuthorizationFailedException {
        ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now();
        if (logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return dao.getAllQuestions();
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String authorization, String uuid) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.authorize(authorization);
       if(userAuthTokenEntity==null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
           ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
           ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
           ZonedDateTime currentTime = ZonedDateTime.now();
            if ((logOutTime==null || expiryTime==null)||(!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
                QuestionEntity questionEntity = dao.getQuestionUser(uuid);
                if (!Objects.isNull(questionEntity)) {
                    if (userAuthTokenEntity.getUser().getId() == questionEntity.getUser().getId()) {
                        return dao.editQuestionContent(questionEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist'.");

                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

    }
}
