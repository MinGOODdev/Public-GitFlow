package org.gitflow.sw.service;

import org.gitflow.sw.dto.QuestionComment;
import org.gitflow.sw.model.QuestionCommentModel;

import java.util.List;

public interface QuestionCommentService {

    List<QuestionComment> findAllByQuestionId(int questionId);

    void insertQuestionComment(QuestionCommentModel questionCommentModel);

}
