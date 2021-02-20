package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;


    /* The signUp method for registering a new user checks for two conditions */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(final UserEntity userEntity) throws SignUpRestrictedException, NullPointerException {
        String userName = userEntity.getUserName();
        UserEntity userNameExists = userDao.getUserByUserName(userName);
        String email = userEntity.getEmail();
        UserEntity emailExists = userDao.getUserByEmail(email);
        String password = userEntity.getPassword();

        if(userNameExists!=null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (emailExists!=null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    //The below method checks if the user token is valid.If yes,then it returns the corresponding user entity

    public  UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        ZonedDateTime logoutTime = userAuthTokenEntity.getLogoutAt();
        if(logoutTime!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if(userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return userEntity;

    }

}
