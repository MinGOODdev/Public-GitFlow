package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.ApplyPart;

import java.util.List;

@Mapper
public interface ApplyPartMapper {

    List<ApplyPart> findAll();

    ApplyPart findById(int id);

}
