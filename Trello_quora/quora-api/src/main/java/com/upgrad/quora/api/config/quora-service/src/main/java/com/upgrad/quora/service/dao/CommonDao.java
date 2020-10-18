package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CommonDao {
    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthTokenEntity getUserAuthToken(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",
                    UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            System.out.println("Error");
            return null;
        }
    }

    public UserEntity getUser(String uuid) {
        try {
            System.out.println("user id value is"+uuid);
            return entityManager.createNamedQuery("userByUuid",
                    UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public boolean deleteUser(String uuid) {
        try {
            entityManager.createNamedQuery("deleteUser").setParameter("uuid", uuid).executeUpdate();
            entityManager.flush();
            //entityManager.remove(uuid);
            return true;
        } catch (NoResultException nre){
            return false;
        }
    }


}
