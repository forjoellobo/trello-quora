package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService implements EndPointIdentifier {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    AuthorizationService authorizationService;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {

        return questionDao.createQuestion(questionEntity);

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions() throws AuthorizationFailedException {

        return questionDao.getAllQuestions();


    }

    /**
     * Method to update a question
     *
     * @param questionEntity to be updated
     * @return updated questionEntity
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity updateQuestion(QuestionEntity questionEntity) {
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
    public QuestionEntity checkQuestion(String accessToken, String questionId) throws AuthorizationFailedException, InvalidQuestionException {

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
    public String deleteQuestion(String questionId, String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
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

    QuestionEntity checkQuestionIsValid(String uuid) throws InvalidQuestionException {
        QuestionEntity existingQuestionEntity = questionDao.getQuestionById(uuid);

        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else {
            return existingQuestionEntity;
        }
    }



}
