package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.QuestionComment;
import org.gitflow.sw.mapper.QuestionCommentMapper;
import org.gitflow.sw.model.QuestionCommentModel;
import org.gitflow.sw.service.QuestionCommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class QuestionCommentServiceImpl implements QuestionCommentService {

    @Resource
    private QuestionCommentMapper questionCommentMapper;

    /**
     * 해당 문의 사항의 댓글 목록
     *
     * @param questionId
     * @return
     */
    @Override
    public List<QuestionComment> findAllByQuestionId(int questionId) {
        return questionCommentMapper.findAllByQuestionId(questionId);
    }

    /**
     * 댓글 작성
     *
     * @param questionCommentModel
     */
    @Override
    public void insertQuestionComment(QuestionCommentModel questionCommentModel) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

        QuestionComment questionComment = new QuestionComment();
        questionComment.setQuestionId(questionCommentModel.getQuestionId());
        questionComment.setComment(questionCommentModel.getComment());
        questionComment.setCreatedAt(simpleDateFormat.format(date));
        questionCommentMapper.insertQuestionComment(questionComment);
    }

}
