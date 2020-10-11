package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {
    @Autowired
    private AdminDao adminDao;
    @Autowired
    private CommonDao commonDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String userUuid, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = commonDao.getUserAuthToken(authorization);
        //if (userAuthTokenEntity == null) {
         //   if (userAuthTokenEntity.getExpiresAt() == null) {
                UserEntity userEntity = commonDao.getUser(userUuid);
                if (userEntity != null) {
                    String role = userEntity.getRole();
                    System.out.println("Role is"+role);
                  //  if (!role.equalsIgnoreCase("nonadmin")) {
                        System.out.println("inside role"+userUuid);
                         commonDao.deleteUser(userUuid);
                        return  userEntity;
//                    } else {
//                        throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
//                    }
                } else {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist'.");
                }
//            } else {
//                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
//            }
//        } else {
//            throw new AuthorizationFailedException("ATHR-001", "User is not Signed in, sign in first to upload an image");
//        }
    }
}

