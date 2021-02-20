package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * get User auth by token
     *
     * @param accessToken : access token to authenticate
     * @return single user auth details
     */
    public UserAuthTokenEntity getUserAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthTokenByAcessTokan", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Persists user authen entity in database.
     *
     * @param userAuthTokenEntity to be persisted in the DB.
     * @return UserAuthTokenEntity
     */

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * Update UserAuthEntity in Database
     *
     * @param updatedUserAuthEntity: UserAuthEntity object
     */

    public void  updateUserAuth(UserAuthTokenEntity updatedUserAuthEntity) {
        entityManager.merge(updatedUserAuthEntity);
    }



}
