package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.QuestionComment;

import java.util.List;

@Mapper
public interface QuestionCommentMapper {

    List<QuestionComment> findAllByQuestionId(int questionId);

    void insertQuestionComment(QuestionComment questionComment);

}
