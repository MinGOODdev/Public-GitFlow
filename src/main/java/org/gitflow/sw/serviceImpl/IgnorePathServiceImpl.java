package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.mapper.IgnorePathMapper;
import org.gitflow.sw.service.IgnorePathService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IgnorePathServiceImpl implements IgnorePathService {

    @Resource
    private IgnorePathMapper ignorePathMapper;

    @Override
    public List<IgnorePath> findAll() {
        return ignorePathMapper.findAll();
    }

}
