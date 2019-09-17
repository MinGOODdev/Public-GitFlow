package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Include;
import org.gitflow.sw.mapper.IncludeMapper;
import org.gitflow.sw.service.IncludeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IncludeServiceImpl implements IncludeService {

    @Resource
    private IncludeMapper includeMapper;

    @Override
    public List<Include> findAll() {
        return includeMapper.findAll();
    }

}
