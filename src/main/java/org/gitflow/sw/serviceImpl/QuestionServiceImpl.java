package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Question;
import org.gitflow.sw.mapper.QuestionMapper;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.model.QuestionModel;
import org.gitflow.sw.service.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    /**
     * Q&A 목록 전체 조회
     *
     * @return
     */
    @Override
    public List<Question> findAll() {
        return questionMapper.findAll();
    }

    /**
     * Q&A 목록 전체 조회 (페이지네이션)
     *
     * @return
     */
    @Override
    public List<Question> findAll(Pagination pagination) {
        int count = questionMapper.count();
        pagination.setRecordCount(count);
        return questionMapper.findAllWithPagination(pagination);
    }

    /**
     * 해당 Q&A 검색
     *
     * @param id
     * @return
     */
    @Override
    public Question findById(int id) {
        return questionMapper.findById(id);
    }

    @Override
    public void insertQuestion(QuestionModel questionModel) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

        Question question = new Question();
        question.setContent(questionModel.getBody());
        question.setCreatedAt(simpleDateFormat.format(date));
        questionMapper.insertQuestion(question);
    }

}
