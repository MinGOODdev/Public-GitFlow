package org.gitflow.sw.controller.rest;

import lombok.AllArgsConstructor;
import org.gitflow.sw.dto.Question;
import org.gitflow.sw.dto.QuestionComment;
import org.gitflow.sw.model.QuestionCommentModel;
import org.gitflow.sw.model.QuestionModel;
import org.gitflow.sw.model.ResponseModel;
import org.gitflow.sw.service.QuestionCommentService;
import org.gitflow.sw.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class RestQuestionController {

    private QuestionService questionService;
    private QuestionCommentService questionCommentService;

    /**
     * 문의 사항 목록 전체 조회
     *
     * @return
     */
    @GetMapping("questions")
    public ResponseEntity<ResponseModel> questions() {
        ResponseModel responseModel = new ResponseModel();
        List<Question> questionList = questionService.findAll();

        responseModel.setData(questionList);
        responseModel.setMsg("GET Question List");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * 문의사항 작성
     *
     * @param question
     * @return
     */
    @PostMapping("question")
    public ResponseEntity<ResponseModel> postQuestion(@RequestBody QuestionModel question) {
        ResponseModel responseModel = new ResponseModel();
        questionService.insertQuestion(question);

        responseModel.setData(question);
        responseModel.setMsg("POST Question Save Success");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * 댓글 조회
     *
     * @param questionId
     * @return
     */
    @GetMapping("question/{questionId}/comments")
    public ResponseEntity<ResponseModel> questionComments(@PathVariable("questionId") int questionId) {
        ResponseModel responseModel = new ResponseModel();
        List<QuestionComment> questionComments = questionCommentService.findAllByQuestionId(questionId);

        responseModel.setData(questionComments);
        responseModel.setMsg("GET" + questionId + " Question's Comment List");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * 댓글 작성
     *
     * @param questionCommentModel
     * @return
     */
    @PostMapping("question/comment")
    public ResponseEntity<ResponseModel> postComments(@RequestBody QuestionCommentModel questionCommentModel) {
        ResponseModel responseModel = new ResponseModel();
        questionCommentService.insertQuestionComment(questionCommentModel);

        responseModel.setData(questionCommentModel);
        responseModel.setMsg("POST" + questionCommentModel.getQuestionId() + " Question's Comment Save Success");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
