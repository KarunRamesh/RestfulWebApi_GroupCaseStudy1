package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;


    public QuestionEntity createQuestion(QuestionEntity userEntity) {
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

    public List<QuestionEntity> getAllQuestionsByUser(String userId) {
        try {
            return (List<QuestionEntity>) entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class).setParameter("userId", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
