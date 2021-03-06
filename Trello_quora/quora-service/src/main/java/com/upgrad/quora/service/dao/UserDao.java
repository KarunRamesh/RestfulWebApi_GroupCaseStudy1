package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository; 
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {
     @PersistenceContext
     private EntityManager entityManager;
 
     public UserEntity createUser(UserEntity userEntity) {
         entityManager.persist(userEntity);
         return userEntity;
     }
 
     public UserEntity getUserByEmail(String emailAddress) {
         try {
             return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("emailAddress", emailAddress).getSingleResult();
         } catch (NoResultException nre) {
             return null;
         }
     }
 
     public UserEntity getUserByUserName(String userName){
         try{
             return entityManager.createNamedQuery("userByUserName",UserEntity.class).setParameter("userName",userName).getSingleResult();
         } catch (NoResultException nre) {
             return null;
         }
     }
 
     public UserAuthTokenEntity getUserAuthTokenEntityByToken(String accessToken){
         try{
             return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
         } catch (NoResultException nre) {
             return null;
         }
     }
 
     public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity)
     {
         entityManager.persist(userAuthTokenEntity);
         return userAuthTokenEntity;
     }
 
     public void updateUser(final UserEntity updatedUserEntity)
     {
         entityManager.merge(updatedUserEntity);
     }



    public UserAuthTokenEntity logout(UserAuthTokenEntity userAuthTokenEntity) {
        try{
           // return entityManager.createNamedQuery("userSignOut",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).setParameter("expiresAt",expiresAt).getSingleResult();
            entityManager.merge(userAuthTokenEntity);
           return userAuthTokenEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

}