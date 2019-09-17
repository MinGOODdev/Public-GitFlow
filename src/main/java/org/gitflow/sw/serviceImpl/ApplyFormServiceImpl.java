package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.ApplyForm;
import org.gitflow.sw.mapper.ApplyFormMapper;
import org.gitflow.sw.service.ApplyFormService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ApplyFormServiceImpl implements ApplyFormService {

    @Resource
    private ApplyFormMapper applyFormMapper;

    @Override
    public ApplyForm findById(int id) {
        return applyFormMapper.findById(id);
    }

    @Override
    public ApplyForm findByPartId(int partId) {
        return applyFormMapper.findByPartId(partId);
    }

}
