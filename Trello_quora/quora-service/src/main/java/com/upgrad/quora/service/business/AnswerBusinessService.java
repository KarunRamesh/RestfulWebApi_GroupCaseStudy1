package com.upgrad.quora.service.business;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Objects;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private UserBusinessService userBusinessService;
    Logger logger = LoggerFactory.getLogger(AnswerBusinessService.class);

    public AnswerEntity editAnswerContent(String autherization, Integer id) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.authorize(autherization);
        if(userAuthTokenEntity==null){
            logger.error("Exception occured in editQuestionContent method"+"user has not signed in");
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            logger.info("else called");
            ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
            ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
            ZonedDateTime currentTime = ZonedDateTime.now();
            //   if ((logOutTime==null || expiryTime==null)||(!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
            if(logOutTime!=null){
                logger.info("inside signed condition passed");
                AnswerEntity answerEntity = answerDao.getAnswerUser(id);
                if (!Objects.isNull(answerEntity)) {
                    logger.info("Answer id found in database");
                    if (userAuthTokenEntity.getUser().getId() == answerEntity.getUser().getId()) {
                        logger.info("given answer is belongs to the logged user");
                        return answerDao.editAnswerContent(answerEntity);
                    } else {
                        logger.info("Exception occured in UserBusinessService class"+"Only the answer owner can edit the question");
                        throw new AuthorizationFailedException("ANS-001", "Entered answer uuid does not exist");
                    }
                } else {
                    logger.info("Exception occured in UserBusinessService class"+"Entered answer uuid does not exist");
                    throw new InvalidQuestionException("ATHR-003", "Only the answer owner can edit the answer");
                }
            } else {
                logger.info("Exception occured in UserBusinessService class"+"User is signed out.Sign in first to get user details");

                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
    }

    public AnswerEntity deleteAnswer(String autherization, Integer id) throws AuthorizationFailedException, InvalidQuestionException {
        logger.info("deleteAnswer method in AnswerBusinessService called");

        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.authorize(autherization);
        if(userAuthTokenEntity==null){
            logger.error("Exception occured in deleteAnswer method"+"user has not signed in");
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else {
            logger.info("else called");
            ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
            ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
            ZonedDateTime currentTime = ZonedDateTime.now();
            if ((logOutTime==null || expiryTime==null)||(!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
                logger.info("inside signed condition passed");
                AnswerEntity answerEntity = answerDao.getAnswerUser(id);
                if (!Objects.isNull(answerEntity)) {
                    logger.info("Question id found in database");
                    if (userAuthTokenEntity.getUser().getId() == answerEntity.getUser().getId()) {
                        logger.info("given answer is belongs to the logged user");
                        answerDao.deleteAnswer(id);
                        return answerEntity;
                    } else {
                        logger.info("Exception occured in AnswerBusinessService class"+"Only the question owner can edit the question");
                        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
                    }
                } else {
                    logger.info("Exception occured in AnswerBusinessService class"+"Entered answer uuid does not exist");
                    throw new InvalidQuestionException("ANS-001", "Entered answer uuid does not exist'.");
                }
            } else {
                logger.info("Exception occured in AnswerBusinessService class"+"User is signed out.Sign in first to get user details");

                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
    }
}
