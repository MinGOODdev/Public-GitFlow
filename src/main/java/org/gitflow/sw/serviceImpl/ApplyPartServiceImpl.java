package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.ApplyPart;
import org.gitflow.sw.mapper.ApplyPartMapper;
import org.gitflow.sw.service.ApplyPartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ApplyPartServiceImpl implements ApplyPartService {

    @Resource
    private ApplyPartMapper applyPartMapper;

    /**
     * 지원 파트 전체 조회
     *
     * @return
     */
    @Override
    public List<ApplyPart> findAll() {
        return applyPartMapper.findAll();
    }

    /**
     * 해당 지원 파트 이름 조회
     *
     * @param id
     * @return
     */
    @Override
    public ApplyPart findById(int id) {
        return applyPartMapper.findById(id);
    }

}
