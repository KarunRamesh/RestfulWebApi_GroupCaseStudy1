package com.upgrad.quora.service.business;

 import com.upgrad.quora.service.dao.UserDao;
 import com.upgrad.quora.service.entity.UserEntity;
 import com.upgrad.quora.service.exception.AuthenticationFailedException;
 import com.upgrad.quora.service.exception.AuthorizationFailedException;
 import com.upgrad.quora.service.exception.SignOutRestrictedException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Propagation;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.util.StringUtils;
 
 import java.time.ZonedDateTime;

@Service
public class UserBusinessService {
     @Autowired
     private UserDao userDao;
     @Autowired
     private PasswordCryptographyProvider passwordCryptographyProvider;
    Logger logger = LoggerFactory.getLogger(UserBusinessService.class);

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
                 throw new AuthenticationFailedException("SGR001", "Try any other Username, this Username has already been taken");
             }
         } else {
             throw new AuthenticationFailedException("SGR002", "This user has already been registered, try with any other emailId");
         }
     }
 
     @Transactional(propagation = Propagation.REQUIRED)
     public UserAuthTokenEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
         UserEntity userEntity = userDao.getUserByEmail(username);
         if(userEntity == null){
             throw new AuthenticationFailedException("ATH001", "This username does not exist");
         }
 
         final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
         if(encryptedPassword.equals(userEntity.getPassword())){
             JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
             UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
             userAuthToken.setUser(userEntity);
             final ZonedDateTime now = ZonedDateTime.now();
             final ZonedDateTime expiresAt = now.plusHours(8);
             System.out.println("user id is"+userEntity.getUuid());
             userAuthToken.setUuid(userEntity.getUuid());
             userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
             userAuthToken.setLoginAt(now);
             userAuthToken.setExpiresAt(expiresAt);
             //userAuthToken.setCreatedBy("apibackend");
             //userAuthToken.setCreatedAt(now);
             userDao.createAuthToken(userAuthToken);
             userDao.updateUser(userEntity);
             //userEntity.setLastLoginAt(now);
             return userAuthToken;
         }
         else{
             throw new AuthenticationFailedException("ATH002", "Password failed");
         }
     }
 
     public UserAuthTokenEntity authorize(final String authorizationToken) throws AuthorizationFailedException {
         UserAuthTokenEntity userTokenEntity = userDao.getUserAuthTokenEntityByToken(authorizationToken);

         if (userTokenEntity == null) {
             throw new AuthorizationFailedException("ATHR001", "User has not signed in");
         }
         return userTokenEntity;
     }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity logout(String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userTokenEntity = userDao.getUserAuthTokenEntityByToken(authorization);
        if (userTokenEntity == null) {
             throw new SignOutRestrictedException("ATHR001", "User has not signed in");
         }
        UserEntity user=userTokenEntity.getUser();
        final ZonedDateTime expiresAt = ZonedDateTime.now();
        userTokenEntity.setLogoutAt(expiresAt);
        userTokenEntity.setExpiresAt(expiresAt);
        UserAuthTokenEntity logoutUser= userDao.logout(userTokenEntity);
        return logoutUser.getUser();
    }
}