package org.gitflow.sw.controller;

import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.model.QuestionCommentModel;
import org.gitflow.sw.model.QuestionModel;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.PaginationService;
import org.gitflow.sw.service.QuestionCommentService;
import org.gitflow.sw.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("question")
public class QuestionController {

    private QuestionService questionService;
    private QuestionCommentService questionCommentService;
    private PaginationService paginationService;
    private CommonService commonService;

    public QuestionController(QuestionService questionService,
                              QuestionCommentService questionCommentService,
                              PaginationService paginationService,
                              CommonService commonService) {
        this.questionService = questionService;
        this.questionCommentService = questionCommentService;
        this.paginationService = paginationService;
        this.commonService = commonService;
    }

    /**
     * 문의사항 전체 목록 조회
     *
     * @param model
     * @return
     */
    @GetMapping("all")
    public String questions(Model model,
                            Pagination pagination,
                            @RequestParam(value = "pg", defaultValue = "1") int pg) {
        commonService.commonAttributeSetting(model);

        model.addAttribute("questions", questionService.findAll(pagination));
        model.addAttribute("pgPrev", pg - 1);
        model.addAttribute("pgNext", pg + 1);
        model.addAttribute("pgStart", paginationService.pgStartCheck(pg));
        model.addAttribute("pgEnd", paginationService.pgEndCheck(pg, pagination));
        return "question/question";
    }

    /**
     * 문의사항 작성
     *
     * @param questionModel
     * @param bindingResult
     * @return
     */
    @PostMapping("create")
    public String createQuestion(@ModelAttribute("questionModel") @Validated QuestionModel questionModel,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/question/all";
        }
        questionService.insertQuestion(questionModel);
        return "redirect:/question/all";
    }

    /**
     * 해당 문의사항의 댓글 목록 조회
     *
     * @param model
     * @param questionId
     * @return
     */
    @GetMapping("{questionId}/comments")
    public String getReplies(Model model,
                             @PathVariable("questionId") int questionId) {
        commonService.commonAttributeSetting(model);

        model.addAttribute("questionId", questionId);
        model.addAttribute("question", questionService.findById(questionId));
        model.addAttribute("comments", questionCommentService.findAllByQuestionId(questionId));
        return "question/comment";
    }

    /**
     * 문의사항에 대한 댓글 작성
     *
     * @param questionCommentModel
     * @param bindingResult
     * @return
     */
    @PostMapping("comment/create")
    public String createQuestionComment(@ModelAttribute("questionCommentModel") @Validated QuestionCommentModel questionCommentModel,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/question/" + questionCommentModel.getQuestionId() + "/comments";
        }
        questionCommentService.insertQuestionComment(questionCommentModel);
        return "redirect:/question/" + questionCommentModel.getQuestionId() + "/comments";
    }

}
