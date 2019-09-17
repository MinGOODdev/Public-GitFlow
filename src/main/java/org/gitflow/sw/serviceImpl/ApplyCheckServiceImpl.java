package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.ApplyContent;
import org.gitflow.sw.service.ApplyCheckService;
import org.gitflow.sw.service.ApplyContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplyCheckServiceImpl implements ApplyCheckService {

    @Autowired
    private ApplyContentService applyContentService;

    /**
     * 사용자의 지원 여부 체크
     *
     * @param userId
     * @return
     */
    @Override
    public Boolean applyExistCheck(int userId) {
        ApplyContent applyContent = applyContentService.findByUserId(userId);
        if (applyContent != null) return true;
        else return false;
    }

}
