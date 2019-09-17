package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Back;
import org.gitflow.sw.mapper.BackMapper;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.BackService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BackServiceImpl implements BackService {
    @Resource
    private BackMapper backMapper;

    @Override
    public List<Back> findAll(Pagination pagination) {
        int count = backMapper.count();
        pagination.setRecordCount(count);
        return backMapper.findAll(pagination);
    }

    @Override
    public void insert(String content) {
        backMapper.insert(content);
    }
}
