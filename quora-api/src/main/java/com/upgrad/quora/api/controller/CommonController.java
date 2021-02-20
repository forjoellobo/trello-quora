package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /*
    * This controller is used to get the details of any user
    * Also can be accessed by any user in the application.
    * Accepts user ID and authorization token
    * */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserEntity> userProfile(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonService.getUserProfile(userId, authorization);

        return new ResponseEntity<UserEntity>(userEntity, HttpStatus.OK);
    }

}
