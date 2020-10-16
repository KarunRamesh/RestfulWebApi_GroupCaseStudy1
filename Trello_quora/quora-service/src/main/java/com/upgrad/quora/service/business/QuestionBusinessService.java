package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(QuestionBusinessService.class);


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        logger.info("createQuestion method in QuestionBusinessService called");
            return dao.createQuestion(questionEntity);
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
    public QuestionEntity editQuestionContent(String authorization, Integer uuid) throws AuthorizationFailedException, InvalidQuestionException {
        logger.info("editQuestionContent method in QuestionBusinessService called");

        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.authorize(authorization);
       if(userAuthTokenEntity==null){
           logger.error("Exception occured in editQuestionContent method"+"user has not signed in");
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
           logger.info("else called");
           ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
           ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
           ZonedDateTime currentTime = ZonedDateTime.now();
            if ((logOutTime==null || expiryTime==null)||(!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
                logger.info("inside signed condition passed");
                QuestionEntity questionEntity = dao.getQuestionUser(uuid);
                if (!Objects.isNull(questionEntity)) {
                    logger.info("Question id found in database");
                    if (userAuthTokenEntity.getUser().getId() == questionEntity.getUser().getId()) {
                          logger.info("given question is belongs to the logged user");
                        return dao.editQuestionContent(questionEntity);
                    } else {
                        logger.info("Exception occured in QuestionBusinessService class"+"Only the question owner can edit the question");
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    logger.info("Exception occured in QuestionBusinessService class"+"Entered question uuid does not exist");
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist'.");
                }
            } else {
                logger.info("Exception occured in QuestionBusinessService class"+"User is signed out.Sign in first to get user details");

                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
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
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String authorization, Integer id) throws AuthorizationFailedException, InvalidQuestionException {

        logger.info("deleteQuestion method in QuestionBusinessService called");

        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.authorize(authorization);
        if(userAuthTokenEntity==null){
            logger.error("Exception occured in deleteQuestion method"+"user has not signed in");
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            logger.info("else called");
            ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
            ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
            ZonedDateTime currentTime = ZonedDateTime.now();
            if ((logOutTime==null || expiryTime==null)||(!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
                logger.info("inside signed condition passed");
                QuestionEntity questionEntity = dao.getQuestionUser(id);
                if (!Objects.isNull(questionEntity)) {
                    logger.info("Question id found in database");
                    if (userAuthTokenEntity.getUser().getId() == questionEntity.getUser().getId()) {
                        logger.info("given question is belongs to the logged user");
                         dao.deleteQuestion(id);
                         return questionEntity;
                    } else {
                        logger.info("Exception occured in QuestionBusinessService class"+"Only the question owner can edit the question");
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    logger.info("Exception occured in QuestionBusinessService class"+"Entered question uuid does not exist");
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist'.");
                }
            } else {
                logger.info("Exception occured in QuestionBusinessService class"+"User is signed out.Sign in first to get user details");

                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
    }
}
