package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Question;
import org.gitflow.sw.model.Pagination;

import java.util.List;

@Mapper
public interface QuestionMapper {

    List<Question> findAll();

    List<Question> findAllWithPagination(Pagination pagination);

    Question findById(int id);

    void insertQuestion(Question question);

    int count();

}
