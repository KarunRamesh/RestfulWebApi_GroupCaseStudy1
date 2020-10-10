package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws AuthenticationFailedException {
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        UserEntity userEntityEmail = userDao.getUserByEmail(userEntity.getEmailAddress());
        UserEntity userEntityName = userDao.getUserByUserName(userEntity.getUserName());
        if (userEntityEmail == null || StringUtils.isEmpty(userEntityEmail)) {
            if (userEntityName == null || StringUtils.isEmpty(userEntityName)) {
                return userDao.createUser(userEntity);
            } else {
                throw new AuthenticationFailedException("SGR-001", "Try any other Username, this Username has already been taken");
            }
        } else {
            throw new AuthenticationFailedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
    }
}


