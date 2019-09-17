package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.ApplyContent;
import org.gitflow.sw.mapper.ApplyContentMapper;
import org.gitflow.sw.service.ApplyContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ApplyContentServiceImpl implements ApplyContentService {

    @Resource
    private ApplyContentMapper applyContentMapper;

    @Override
    public ApplyContent findById(int id) {
        return applyContentMapper.findById(id);
    }

    @Override
    public ApplyContent findByUserId(int userId) {
        return applyContentMapper.findByUserId(userId);
    }

    @Override
    public List<ApplyContent> findAllByPartId(int partId) {
        return applyContentMapper.findAllByPartId(partId);
    }

    @Override
    public void insert(ApplyContent applyContent) {
        applyContentMapper.insert(applyContent);
    }

    @Override
    public void update(ApplyContent applyContent) {
        applyContentMapper.update(applyContent);
    }

    @Override
    public void deleteByUserId(int userId) {
        applyContentMapper.deleteByUserId(userId);
    }

}
