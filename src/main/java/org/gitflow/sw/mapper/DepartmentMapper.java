package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Department;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    List<Department> findAll();

    Department findById(int id);

}
