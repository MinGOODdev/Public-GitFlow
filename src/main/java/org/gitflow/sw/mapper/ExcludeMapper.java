package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Exclude;

import java.util.List;

@Mapper
public interface ExcludeMapper {

    List<Exclude> findAll();

}
