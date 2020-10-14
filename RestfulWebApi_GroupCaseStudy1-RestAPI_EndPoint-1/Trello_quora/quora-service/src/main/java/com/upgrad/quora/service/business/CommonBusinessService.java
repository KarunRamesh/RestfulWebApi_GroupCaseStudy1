package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {
    @Autowired
    private CommonDao commonDao;
    public UserEntity getUser(String authorization, String uuid) throws AuthorizationFailedException, UserNotFoundException {
        System.out.println("tmnmn"+authorization);
        UserAuthTokenEntity userAuthTokenEntity = commonDao.getUserAuthToken(authorization);
        System.out.println("hghjgjh"+userAuthTokenEntity);
       // if (userAuthTokenEntity == null) {
          //  if (userAuthTokenEntity.getExpiresAt() == null) {
                System.out.println("user id from the service is "+uuid);
                UserEntity userEntity =  commonDao.getUser(uuid);
                if(userEntity == null){
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist'.");
                }
                return userEntity;
//            } else {
//                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
//            }
        } //else {
        //    throw new AuthorizationFailedException("ATHR-001", "User is not Signed in, sign in first to upload an image");
       // }


    }

