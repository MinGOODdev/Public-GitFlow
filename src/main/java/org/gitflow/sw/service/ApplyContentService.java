package org.gitflow.sw.service;

import org.gitflow.sw.dto.ApplyContent;

import java.util.List;

public interface ApplyContentService {

    ApplyContent findById(int id);

    ApplyContent findByUserId(int userId);

    List<ApplyContent> findAllByPartId(int partId);

    void insert(ApplyContent applyContent);

    void update(ApplyContent applyContent);

    void deleteByUserId(int userId);

}
