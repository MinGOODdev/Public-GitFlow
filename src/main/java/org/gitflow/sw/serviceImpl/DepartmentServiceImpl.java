package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Department;
import org.gitflow.sw.mapper.DepartmentMapper;
import org.gitflow.sw.service.DepartmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> findAll() {
        return departmentMapper.findAll();
    }

    @Override
    public Department findById(int id) {
        return departmentMapper.findById(id);
    }

}
