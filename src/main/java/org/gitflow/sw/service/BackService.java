package org.gitflow.sw.service;

import org.gitflow.sw.dto.Back;
import org.gitflow.sw.model.Pagination;

import java.util.List;

public interface BackService {

    List<Back> findAll(Pagination pagination);

    void insert(String content);

}
