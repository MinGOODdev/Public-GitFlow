package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.LibExpect;

import java.util.List;

@Mapper
public interface LibExpectMapper {

    List<LibExpect> findAll();

    void insert(LibExpect libExpect);

    void deleteAll();

}
