package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.ApplyContent;

import java.util.List;

@Mapper
public interface ApplyContentMapper {

    ApplyContent findById(int id);

    ApplyContent findByUserId(int userId);

    List<ApplyContent> findAllByPartId(int partId);

    void insert(ApplyContent applyContent);

    void update(ApplyContent applyContent);

    void deleteByUserId(int userId);

}
