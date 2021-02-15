package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


//A UserAuthenticationServiceClass for validating the user credentials username and password provided by user during login
@Service
public class UserAuthenticationBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;


    // authenticateToken for signin//

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticate(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUserName((userName));

        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            String uuid = userEntity.getUuid();

            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(uuid, now, expiresAt));
            userAuthToken.setUuid(uuid);
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthToken);
            return userAuthToken;

        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }

    }



    //signOut//
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signOut(final String bearerAcccessToken) throws SignOutRestrictedException, NullPointerException {
        UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(bearerAcccessToken);
        if (userAuthToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
                final ZonedDateTime now = ZonedDateTime.now();
                userAuthToken.setLogoutAt(now);
                userDao.updateUserLogoutAt(userAuthToken);
                return userAuthToken;
            }
        }
    }
