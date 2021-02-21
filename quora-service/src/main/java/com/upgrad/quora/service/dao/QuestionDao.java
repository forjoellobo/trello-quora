package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to create a new QuestionEntity
     *
     * @param questionEntity contains the questionEntity to be persisted
     * @return QuestionEntity that has been persisted in the database
     */

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }



    /**
     * Method to get the List of All the questions
     *
     * @return List<QuestionEntity>
     */

    public List<QuestionEntity> getAllQuestions() {

        try {
            return entityManager.createNamedQuery("selectAll", QuestionEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {

            return null;
        }
    }


    /**
     * Method to get the QuestionEntity by uuid
     *
     * @param questionId
     * @return QuestionEntity
     */

    public QuestionEntity getQuestionByUuid(String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
                    .setParameter("uuid", questionId).getSingleResult();

        } catch (NoResultException nre) {

            return null;
        }
    }

    /**
     * Method to update the content of the Question
     *
     * @param questionEntity
     * @return QuestionEntity after updating
     */

    public QuestionEntity updateQuestion(QuestionEntity questionEntity) {

        entityManager.merge(questionEntity);
        return questionEntity;

    }

    /**
     * Method to delete a question associated with the uuid
     *
     * @param uuid of the question to be deleted
     */

    public void deleteQuestionByUUID(String uuid) {
        Query finalQuery = entityManager.createNamedQuery("deleteByUuid")
                .setParameter("uuid", uuid);
        int rowsAffected = finalQuery.executeUpdate();
    }

}
