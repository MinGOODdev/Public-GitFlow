package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.IgnorePath;

import java.util.List;

@Mapper
public interface IgnorePathMapper {

    List<IgnorePath> findAll();

}
