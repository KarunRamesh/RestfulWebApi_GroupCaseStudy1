package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private UserBusinessService userBusinessService;

     @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
     public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws AuthenticationFailedException {
         final UserEntity userEntity = new UserEntity();
         userEntity.setUuid(UUID.randomUUID().toString());
         userEntity.setFirstName(signupUserRequest.getFirstName());
         userEntity.setLastName(signupUserRequest.getLastName());
         userEntity.setEmailAddress(signupUserRequest.getEmailAddress());
         userEntity.setPassword(signupUserRequest.getPassword());
         userEntity.setSalt("1234abc");
         userEntity.setCountry(signupUserRequest.getCountry());
         userEntity.setDob(signupUserRequest.getDob());
         userEntity.setAboutMe(signupUserRequest.getAboutMe());
         userEntity.setContactNumber(signupUserRequest.getContactNumber());
         userEntity.setRole("nonadmin");
         userEntity.setUserName(signupUserRequest.getUserName());
         final UserEntity createdUserEntity = userBusinessService.signup(userEntity);
         SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
         return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
     }
 
 
     @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
     public ResponseEntity<SigninResponse> userSignin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
         byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
         String decodedText = new String(decode);
         String[] decodedArray = decodedText.split(":");
         UserAuthTokenEntity userAuthToken = userBusinessService.authenticate(decodedArray[0],decodedArray[1]);
         UserEntity user = userAuthToken.getUser();
         SigninResponse authorizedUserResponse =  new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
         HttpHeaders headers = new HttpHeaders();
         headers.add("accesstoken", userAuthToken.getAccessToken());
         return new ResponseEntity<SigninResponse>(authorizedUserResponse,headers, HttpStatus.OK);
     }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> logout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException, AuthenticationFailedException {
        final UserEntity createdUserEntity = userBusinessService.logout(authorization);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

}