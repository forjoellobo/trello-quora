package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService implements EndPointIdentifier {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Method to create a new user.
     * @param questionEntity the QuestionEntity to be created
     * @return created UserEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.getUserAuthTokenEntity(accessToken,QUESTION_ENDPOINT);

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setDate(ZonedDateTime.now());
        return questionDao.createQuestion(questionEntity);

    }

    /**
     * Method to get all the questions
     *
     * @param accessToken accessToken assigned to the user
     * @return List<QuestionEntity> list of all the questions associated with the user
     * @throws AuthorizationFailedException
     */
    public List<QuestionEntity> getAllQuestions(final String accessToken) throws AuthorizationFailedException {
        authorizationService.getUserAuthTokenEntity(accessToken,GET_ALL_QUESTIONS);

        return questionDao.getAllQuestions();
    }

    /**
     * Method to update a question
     *
     * @param questionEntity to be updated
     * @return updated questionEntity
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        return questionDao.updateQuestion(questionEntity);
    }


    /**
     * @param accessToken accessToken assigned to the user
     * @param questionId  the uuid of the question
     * @return QuestionEntity
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity checkQuestion(final String accessToken, final String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = authorizationService.getUserAuthTokenEntity(accessToken,CHECK_QUESTION);
        UserEntity user = userAuthTokenEntity.getUser();

        QuestionEntity existingQuestionEntity = checkQuestionIsValid(questionId);

        if (!user.getId().equals(existingQuestionEntity.getUser().getId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        } else {
            return existingQuestionEntity;
        }

    }

    /**
     * Method to delete a given question
     *
     * @param questionId  uuid of the question to be deleted
     * @param accessToken accessId assigned to the user
     * @return uuid of the deleted question
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.getUserAuthTokenEntity(accessToken,DELETE_QUESTION);

        UserEntity user = userAuthTokenEntity.getUser();

        QuestionEntity existingQuestionEntity = checkQuestionIsValid(questionId);

        if (!(user.getId().equals(existingQuestionEntity.getUser().getId()) || (user.getRole().equals("admin")))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        } else {
            questionDao.deleteQuestionByUUID(questionId);
            String deletedQuestionid=questionId;
            return (deletedQuestionid);
        }
    }

    /*
     * This method fetch all the questions of the user from the database
     * accepts uuid and access token
     * */
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        // check authentication validation
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.getUserAuthTokenEntity(accessToken, GET_ALL_QUESTIONS_USER);

        UserEntity userEntity = userDao.getUserByUuid(userId);

        // check if valid uuid
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUser(userId);

    }

    QuestionEntity checkQuestionIsValid(String uuid) throws InvalidQuestionException {
        QuestionEntity existingQuestionEntity = questionDao.getQuestionByUuid(uuid);

        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else {
            return existingQuestionEntity;
        }
    }



}
