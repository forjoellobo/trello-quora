package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

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
