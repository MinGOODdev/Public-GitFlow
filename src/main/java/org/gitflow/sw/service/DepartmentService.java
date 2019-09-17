package org.gitflow.sw.service;

import org.gitflow.sw.dto.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> findAll();

    Department findById(int id);

}
