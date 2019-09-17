package org.gitflow.sw.service;

import org.gitflow.sw.dto.Front;
import org.gitflow.sw.model.Pagination;

import java.util.List;

public interface FrontService {

    List<Front> findAll(Pagination pagination);

    void insert(String content);

}
