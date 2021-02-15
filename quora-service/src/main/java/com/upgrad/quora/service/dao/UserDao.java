package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    //The method getUserByUserName fetches the user based on userName and if no record is found in the database it returns null

    public UserEntity getUserByUserName(final String userName) throws NoResultException  {
        try {
            return entityManager.createNamedQuery("userByName", UserEntity.class).setParameter("userName", userName)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    //The method getUserByEmail fetches the user based on email and if no record is found in the database it returns null

    public UserEntity getUserByEmail(final String email) throws NoResultException {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    //This method persists an object of UserAuthTokenEntity to the database

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    // This method retrieves the accesstoken from the database if it exists otherwise returns null

    public UserAuthTokenEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    //Now the logoutAt attribute is updated  and the UserAuthTokenEntity object is in detached state

    public void updateUserLogoutAt(final UserAuthTokenEntity updateUserLogoutAt) {

        entityManager.merge(updateUserLogoutAt);
    }

    //This method retrieves the user based on user uuid, if found returns user else null

    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    //This method deletes the user record from database

    public UserEntity deleteUser(final UserEntity userEntity) {
        entityManager.remove(userEntity);
        return userEntity;
    }

}
