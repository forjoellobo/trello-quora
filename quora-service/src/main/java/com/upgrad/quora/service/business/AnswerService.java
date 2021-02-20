package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
//@Service annotation is used to mark a class as a service provider
@Service
// Service class for all the answer related services
public class AnswerService {
    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;
    // @Transactional annotation is the metadata that specifies the semantics of the transactions on a method
    // It is declarative way to rollback a transaction
    @Transactional(propagation = Propagation.REQUIRED)
   // This method checks for valid accessToken and validates the question and calls another method from AnswerDao to create answer
    public AnswerEntity createAnswer(AnswerEntity answerEntity, final String accessToken, final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        //check weather user is signed in or not.
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if(userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException( "ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);

        //check whether the provided question is valid or not
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        // If none of the exceptions above are thrown, means user is signed in and question is valid
        // set the attributes of answerEntity and call createAnswer method from AnswerDao
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestionEntity(questionEntity);
        answerEntity.setUserEntity(userAuthEntity.getUser());
        return answerDao.createAnswer(answerEntity);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    // This service method is used to edit answer by calling another method from AnswerDao class
    // This method accepts answerID, accessToken and new edited answer in string format as parameter
    public AnswerEntity editAnswerContent(final String accessToken, final String answerId, final String newAnswer)
            throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        //check whether user is signed in or not.If not then it will throw an exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        // check whether the answer exist or not
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        //check editing user is owner of the answer or not
        if (!answerEntity.getUserEntity().getUuid().equals(userAuthEntity.getUser().getUuid())) {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner can edit the answer");
        }
        //Now we will update new edited answer
        answerEntity.setAnswer(newAnswer);
        answerDao.updateAnswer(answerEntity);
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    // This service method is used to delete answer by calling another method from AnswerDao class
    // This method accepts answerID and accessToken in string format as parameter
    public AnswerEntity deleteAnswer(final String answerId, final String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        //It will check weather user is signed in or not.If not then it will throw an exception

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        //It will check the answer exist or not
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        //it will check deleting user is owner of the answer or admin or not
        if (userAuthEntity.getUser().getRole().equals("admin") || answerEntity.getUserEntity().getUuid().equals(userAuthEntity.getUser().getUuid())) {
            return answerDao.deleteAnswer(answerId);
        } else {
            throw new AuthorizationFailedException(
                    "ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }
}
