package org.gitflow.sw.service;

import org.gitflow.sw.dto.Question;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.model.QuestionModel;

import java.util.List;

public interface QuestionService {

    List<Question> findAll();

    List<Question> findAll(Pagination pagination);

    Question findById(int id);

    void insertQuestion(QuestionModel questionModel);

}
