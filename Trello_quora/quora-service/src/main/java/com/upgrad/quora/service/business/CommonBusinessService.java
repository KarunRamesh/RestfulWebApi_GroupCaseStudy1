package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonBusinessService {
    @Autowired
    private CommonDao commonDao;

    public UserEntity getUser(String authorization, String uuid) throws AuthorizationFailedException, UserNotFoundException {
        System.out.println("tmnmn" + authorization);
        UserAuthTokenEntity userAuthTokenEntity = commonDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else {
            ZonedDateTime logOutTime = userAuthTokenEntity.getLogoutAt();
            ZonedDateTime expiryTime = userAuthTokenEntity.getExpiresAt();
            ZonedDateTime currentTime = ZonedDateTime.now();
            if ((logOutTime == null || expiryTime == null) || (!(logOutTime.isBefore(currentTime) || expiryTime.isBefore(currentTime)))) {
                UserEntity userEntity = commonDao.getUser(uuid);
                if (userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist'.");
                }
                return userEntity;
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }
    }

}