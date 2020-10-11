package com.upgrad.quora.service.dao;

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
            return null;
        }
    }

    public UserEntity getUser(String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid",
                    UserEntity.class).setParameter("userUuid", userUuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity deleteUser(String uuid) {
        try {
            return entityManager.createNamedQuery("deleteUser",
                    UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
}
