package org.gitflow.sw.service;

import org.gitflow.sw.dto.Notice;
import org.gitflow.sw.model.NoticeModel;
import org.gitflow.sw.model.Pagination;

import java.util.List;

public interface NoticeService {

    List<Notice> findAll();

    List<Notice> findAllWithPagination(Pagination pagination);

    Notice findById(int id);

    void insert(NoticeModel noticeModel);

    void update(NoticeModel noticeModel);

    void delete(int id);

}
