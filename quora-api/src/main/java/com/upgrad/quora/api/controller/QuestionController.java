package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAuthenticationBusinessService;
import com.upgrad.quora.service.common.EndPointIdentifier;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;



@RestController
@RequestMapping("/")
public class QuestionController implements EndPointIdentifier {

    // Implemented Endpoint Identifier interface for generic AuthorizationFailedException Handling

    @Autowired
    QuestionService questionService;


    @Autowired
    AuthorizationService authorizationService;



    @PostMapping(path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") String accessToken,
                                                          @RequestBody final QuestionRequest questionRequest) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.getUserAuthTokenEntity(accessToken,QUESTION_ENDPOINT);

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUserId(userAuthTokenEntity.getUser());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
                .status("QUESTION CREATED");

        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }



}

