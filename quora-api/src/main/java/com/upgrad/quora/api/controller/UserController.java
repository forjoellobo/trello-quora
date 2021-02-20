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
    UserAuthenticationBusinessService userAuthService;

    /**
     * This method is for user signup. This method receives the object of SignupUserRequest type with
     * its attributes being set.
     *
     * @return SignupUserResponse - UUID of the user created.
     * @throws SignUpRestrictedException - if the username or email already exist in the database.
     */


    private String[] bearerAccessToken;


    // userSignup
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(SignupUserRequest signupUserRequest ) throws SignUpRestrictedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        //Since this is user sign up so the role is set as "nonadmin"
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        UserEntity createdUserEntity = userAuthService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }

    /**
     * This method is for a user to signIn.
     *
     * @param authorization for the basic authentication
     * @return Signin resopnse which has userId and access-token in response header.
     * @throws AuthenticationFailedException : if username or password is invalid
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        // The encoded Base64 format string has to be decoded to a separate string of username and password
        // and need to pass as arguments to the authenticate method for calling the business logic

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthEntity = userAuthService.signin(decodedArray[0], decodedArray[1]);
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthEntity.getUser().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    //**userSignout**//
    /**
     * Request mapping to sign-out user
     *
     * @param acessToken
     * @return SignoutResponse
     * @throws SignOutRestrictedException
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<SignoutResponse> signout(
            @RequestHeader("authorization") final String acessToken) throws SignOutRestrictedException {
        UserEntity userEntity = userAuthService.signOut(acessToken);
        SignoutResponse signoutResponse =
                new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }



}
