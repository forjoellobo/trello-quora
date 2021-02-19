package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserAuthenticationBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
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

//The RestController annotation adds both @Controller and @ResponseBoy annotations.
@RestController
@RequestMapping("/")

//This method is for user signup
//This method receives the object of SignupUserRequest type with its attributes being set.
//This method is listening for a HTTP POST request as indicated by method= RequestMethod.POST ,
// maps to a URL request of type '/user/signup' , consumes and produces Json.

public class UserController {


    @Autowired
    UserBusinessService userBusinessService;

    @Autowired
    UserAuthenticationBusinessService userAuthenticationBusinessService;
    private String[] bearerAccessToken;


    // userSignup
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest ) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());

        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        //Since this is user sign up so the role is set as "nonadmin"
        userEntity.setRole("nonadmin");

        //calling the business logic
        // The Http Status code 201 , the response code HttpStatus.CREATED and the status message USER SUCCESSFULLY REGISTERED
        //defined here are as per the requirement provided in the user.json

        final UserEntity createdUserEntity = userBusinessService.signUp(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }

    /* userSignIn */
    //This method defines the user can login to application after the successfull registration
    //This endpoint requests for the User credentials to be passed in the authorization field of header as part of Basic authentication.
    //username:password of the String is encoded to Base64 format in the authorization header
    //For example, a username of ‘AdilA’ and a password of ‘12345’ becomes the string ‘AdilA:12345’
    // and then this string is encoded to Base64 format to ‘QXJjaGFuYUE6MTIzNDU=’
    //Since this is basic authentication the format of authorization header is Basic QXJjaGFuYUE6MTIzNDU=
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        // The encoded Base64 format string has to be decoded to a separate string of username and password
        // and need to pass as arguments to the authenticate method for calling the business logic

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = userAuthenticationBusinessService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse authenticatedSigninResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        // The Http Status code 200 , the response code HttpStatus.OK and the status message SIGNED IN SUCCESSFULLY

        return new ResponseEntity<SigninResponse>(authenticatedSigninResponse, headers, HttpStatus.OK);
    }

    //**userSignout**//
    //This endpoint requests for the access token in the authorisation header as a part of Bearer authentication

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignout(@RequestHeader("authorization") final String authorization)
            throws SignOutRestrictedException {
        //The input can be of any form "Bearer <accesstoken>" or "<accesstoken>" in the authorization header

        UserAuthTokenEntity userAuthToken;
        try {
            String[] bearerAccessToken = authorization.split("Bearer ");
            userAuthToken = userAuthenticationBusinessService.signOut(bearerAccessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            userAuthToken = userAuthenticationBusinessService.signOut(authorization);
        }
        UserEntity user = userAuthToken.getUser();

        SignoutResponse authorizedSignoutResponse = new SignoutResponse().id(user.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        //This method returns an object of SignoutResponse and HttpStatus
        return new ResponseEntity<SignoutResponse>(authorizedSignoutResponse, HttpStatus.OK);
    }



}
