package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService implements EndPointIdentifier {

    @Autowired
    UserDao userDao;

    @Autowired
    QuestionDao questionDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {

        return questionDao.createQuestion(questionEntity);

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions() throws AuthorizationFailedException {

        return questionDao.getAllQuestions();


    }




}
