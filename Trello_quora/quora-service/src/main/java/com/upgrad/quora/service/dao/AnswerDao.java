package com.upgrad.quora.service.dao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    @Modifying
    public boolean deleteAnswer(Integer id) {
        try{
            entityManager.createNamedQuery("deleteAnswer").setParameter("id",id).executeUpdate();
            entityManager.flush();
            return true;
        }catch (NoResultException nre){
            return false;
        }
    }

    public List<AnswerEntity> getAnswerByQuestionId(Integer questionId) {
        try {
            return entityManager.createNamedQuery("getAnswerByQuestionId", AnswerEntity.class).setParameter("questionId", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity createAnswer(AnswerEntity userEntity) {
        try{
                 entityManager.persist(userEntity);
                 return userEntity;
        }catch(NoResultException nre){
            return  null;
        }
    }
}
