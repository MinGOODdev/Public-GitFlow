package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.ApplyForm;

@Mapper
public interface ApplyFormMapper {

    ApplyForm findById(int id);

    ApplyForm findByPartId(int partId);

}
