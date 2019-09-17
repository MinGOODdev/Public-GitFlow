package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.mapper.ExcludeMapper;
import org.gitflow.sw.service.ExcludeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ExcludeServiceImpl implements ExcludeService {

    @Resource
    private ExcludeMapper excludeMapper;

    @Override
    public List<Exclude> findAll() {
        return excludeMapper.findAll();
    }

}
