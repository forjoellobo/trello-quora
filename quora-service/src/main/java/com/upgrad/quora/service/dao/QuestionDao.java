package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

//Dao class for performing CRUD operations on question table in the database.
@Repository
public class QuestionDao {
    //@PersistenceContext annotation is used to inject an EntityManager
    @PersistenceContext
    EntityManager entityManager;

    public QuestionEntity getQuestionByUuid(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid",QuestionEntity.class).setParameter("uuid",questionUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
