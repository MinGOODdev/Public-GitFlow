package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Front;
import org.gitflow.sw.mapper.FrontMapper;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.FrontService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FrontServiceImpl implements FrontService {
    @Resource
    private FrontMapper frontMapper;

    @Override
    public List<Front> findAll(Pagination pagination) {
        int count = frontMapper.count();
        pagination.setRecordCount(count);
        return frontMapper.findAll(pagination);
    }

    @Override
    public void insert(String content) {
        frontMapper.insert(content);
    }
}
