package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {
    @Autowired
    private AdminBusinessService adminBusinessService;
    @RequestMapping(method = RequestMethod.DELETE,path="/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@RequestHeader("authorization") final String authorization, @PathVariable("id") final String userId) throws AuthorizationFailedException, UserNotFoundException {
        final UserEntity userEntity =  adminBusinessService.getUser(userId,authorization);
        UserDetailsResponse userDetailsResponse;
        userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}
