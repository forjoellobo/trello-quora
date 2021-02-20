package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AdminService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    /*
    * This method deletes the user from the database
    * accepts uuid and authorization token
    * */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(final String uuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(authorizationToken);

        // check if valid auth token
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // check if user has signed out
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        // check if role of user is nonadmin
        if (userAuthTokenEntity.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userEntity = userDao.getUserByUuid(uuid);

        // check if valid uuid
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        userDao.deleteUser(uuid);
    }
}
