package com.upgrad.quora.service.dao;
import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//Dao class for performing CRUD operations on answer table in the database.
@Repository
public class AnswerDao {

    //@PersistenceContext annotation is used to inject an EntityManager
    @PersistenceContext
    private EntityManager entityManager;

    //This method persists the AnswerEntity in the database
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }
}
