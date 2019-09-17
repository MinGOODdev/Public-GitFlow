package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.mapper.MustContainMapper;
import org.gitflow.sw.service.MustContainService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MustContainServiceImpl implements MustContainService {

    @Resource
    private MustContainMapper mustContainMapper;

    @Override
    public List<MustContain> findAll() {
        return mustContainMapper.findAll();
    }

}
