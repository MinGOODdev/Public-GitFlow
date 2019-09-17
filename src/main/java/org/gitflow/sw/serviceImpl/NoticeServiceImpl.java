package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Notice;
import org.gitflow.sw.mapper.NoticeMapper;
import org.gitflow.sw.model.NoticeModel;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 공지사항 목록 조회
     *
     * @return
     */
    @Override
    public List<Notice> findAll() {
        return noticeMapper.findAll();
    }

    /**
     * 공지사항 목록 조회 + 페이지네이션
     *
     * @param pagination
     * @return
     */
    @Override
    public List<Notice> findAllWithPagination(Pagination pagination) {
        int count = noticeMapper.count();
        pagination.setRecordCount(count);
        return noticeMapper.findAllWithPagination(pagination);
    }

    /**
     * 해당 공지글 조회
     *
     * @param id
     * @return
     */
    @Override
    public Notice findById(int id) {
        return noticeMapper.findById(id);
    }

    /**
     * 공지사항 작성
     *
     * @param noticeModel
     */
    @Override
    public void insert(NoticeModel noticeModel) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

        Notice notice = new Notice();
        notice.setTitle(noticeModel.getTitle());
        notice.setContent(noticeModel.getContent());
        notice.setCreatedAt(simpleDateFormat.format(date));
        notice.setUpdatedAt(simpleDateFormat.format(date));
        noticeMapper.insert(notice);
    }

    /**
     * 공지사항 수정
     *
     * @param noticeModel
     */
    @Override
    public void update(@ModelAttribute("noticeModel") NoticeModel noticeModel) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

        Notice notice = noticeMapper.findById(noticeModel.getId());
        notice.setTitle(noticeModel.getTitle());
        notice.setContent(noticeModel.getContent());
        notice.setUpdatedAt(simpleDateFormat.format(date));
        noticeMapper.update(notice);
    }

    /**
     * 공지사항 삭제
     *
     * @param id
     */
    @Override
    public void delete(int id) {
        noticeMapper.delete(id);
    }
}
