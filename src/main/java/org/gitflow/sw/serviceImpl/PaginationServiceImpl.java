package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.PaginationService;
import org.springframework.stereotype.Service;

@Service
public class PaginationServiceImpl implements PaginationService {

    /**
     * 현재 페이지가 1 페이지인지 검사
     *
     * @param pg
     * @return
     */
    @Override
    public boolean pgStartCheck(int pg) {
        boolean pgStart = true;
        if (pg == 1) {
            pgStart = false;
        }
        return pgStart;
    }

    /**
     * 현재 페이지가 끝 페이지인지 검사
     *
     * @param pg
     * @param pagination
     * @return
     */
    @Override
    public boolean pgEndCheck(int pg, Pagination pagination) {
        boolean pgEnd = true;
        if (pg == (pagination.getRecordCount() / pagination.getSz()) + 1) {
            pgEnd = false;
        }
        return pgEnd;
    }

}
