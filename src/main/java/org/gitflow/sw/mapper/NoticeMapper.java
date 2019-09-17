package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.Notice;
import org.gitflow.sw.model.Pagination;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<Notice> findAll();

    List<Notice> findAllWithPagination(Pagination pagination);

    Notice findById(int id);

    void insert(Notice notice);

    void update(Notice notice);

    void delete(int id);

    int count();

}
