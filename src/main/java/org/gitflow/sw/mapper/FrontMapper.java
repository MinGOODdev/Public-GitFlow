package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Front;
import org.gitflow.sw.model.Pagination;

import java.util.List;

@Mapper
public interface FrontMapper {

    List<Front> findAll(Pagination pagination);

    void insert(String content);

    int count();

}
