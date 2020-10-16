package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(QuestionDao.class);

    public QuestionEntity createQuestion(QuestionEntity userEntity) {
        logger.info("createQuestion method in QuestionDao called");

        try{
         entityManager.persist(userEntity);
         return userEntity;
     }catch(Exception e){throw e;
     }

    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return (List<QuestionEntity>) entityManager.createNamedQuery("getAllQuestionsByAllUsers", QuestionEntity.class).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestionContent(QuestionEntity questionEntity) {
        logger.info("editQuestionContent method in QuestionDao called");
        try{
            entityManager.merge(questionEntity);
            return questionEntity;
        }catch(Exception e){
            throw e;
        }

    }

    public QuestionEntity getQuestionUser(Integer id) {
        try {
            return entityManager.createNamedQuery("questionUserByUuid",
                    QuestionEntity.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
    public List<QuestionEntity> getAllQuestionsByUser(String userId) {
        try {
            return (List<QuestionEntity>) entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class).setParameter("userId", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public boolean deleteQuestion(Integer id) {
        try {
            entityManager.createNamedQuery("deleteQuestion").setParameter("id", id).executeUpdate();
            entityManager.flush();
            return true;
        } catch (NoResultException nre){
            return false;
        }
    }
}
