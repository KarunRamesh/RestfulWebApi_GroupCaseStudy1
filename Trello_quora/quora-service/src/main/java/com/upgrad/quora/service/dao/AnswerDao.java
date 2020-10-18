package com.upgrad.quora.service.dao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(QuestionDao.class);

    public AnswerEntity getAnswerUser(Integer id) {
        try{
            return entityManager.createNamedQuery("getAnswerById",AnswerEntity.class).setParameter("id",id).getSingleResult();
        }catch(NoResultException nre){
            return  null;
        }
    }

    public AnswerEntity editAnswerContent(AnswerEntity answerEntity) {
        try{
            entityManager.merge(answerEntity);
            return answerEntity;
        }catch(Exception e){
            throw e;
        }
    }

    public boolean deleteAnswer(Integer id) {
        try{
            entityManager.createNamedQuery("deleteAnswer",AnswerEntity.class).getSingleResult();
            entityManager.flush();
            return true;
        }catch (NoResultException nre){
            return false;
        }
    }
}
