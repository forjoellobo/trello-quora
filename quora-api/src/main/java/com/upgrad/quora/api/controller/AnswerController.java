package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
// @RestController annotation combines @Controller and @ResponseBody – which eliminates the need to annotate every request handling method of the controller class with the @ResponseBody annotation.
@RestController
//Default mapping of the answer controller
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerService answerService;
    // This is a endpoint or controller method for creating an answer for a given question
     // It takes authorization or access token from RequestHeader, QuestionId from PathVariable and an AnswerRequest as a parameter and throws AuthorizationFailed and InvalidQuestion exception and returns a ResponseEntity.
    // Method is mapped to URL type "/question/{questionId}/answer/create", and RequestMethod is of type post, produces Json content
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String accessToken,
                                                       @PathVariable("questionId") final String questionId,
                                                       AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity = answerService.createAnswer(answerEntity, accessToken, questionId);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(answerEntity.getUuid());
        answerResponse.setStatus("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }
    //endpoint or controller method for editing an answer for a question
    // It takes authorization or access token from RequestHeader, answerId from PathVariable and an answerEditRequest as a parameter and throws AuthorizationFailed and AnswerNotFound exception and returns a ResponseEntity.
    // Method is mapped to URL type "/answer/edit/{answerId}", and RequestMrthod is of type put, produces Json content
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@RequestHeader("authorization") final String accessToken, @PathVariable("answerId") final String answerId, AnswerEditRequest answerEditRequest)throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        AnswerEntity answerEntity = answerService.editAnswerContent(accessToken, answerId, answerEditRequest.getContent());
        answerEditResponse.setId(answerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
}
