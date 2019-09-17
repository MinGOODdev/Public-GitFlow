package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.LibExpect;
import org.gitflow.sw.mapper.LibExpectMapper;
import org.gitflow.sw.service.LibExpectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LibExpectServiceImpl implements LibExpectService {

    @Resource
    private LibExpectMapper libExpectMapper;

    @Override
    public List<LibExpect> findAll() {
        return libExpectMapper.findAll();
    }

    /**
     * 라이브러리 의심 파일 저장
     *
     * @param fileName
     * @param codeLine
     */
    @Override
    public void insert(String fileName, int codeLine) {
        LibExpect libExpect = new LibExpect();
        libExpect.setFileName(fileName);
        libExpect.setCodeLine(codeLine);
        libExpectMapper.insert(libExpect);
    }

    @Override
    public void deleteAll() {
        libExpectMapper.deleteAll();
    }
}
