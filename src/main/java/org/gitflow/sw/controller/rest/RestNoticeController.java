package org.gitflow.sw.controller.rest;

import lombok.AllArgsConstructor;
import org.gitflow.sw.dto.Notice;
import org.gitflow.sw.model.ResponseModel;
import org.gitflow.sw.service.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api")
public class RestNoticeController {

    private NoticeService noticeService;

    /**
     * 공지사항 전체 목록 조회
     *
     * @return
     */
    @GetMapping("notices")
    public ResponseEntity<ResponseModel> notices() {
        ResponseModel responseModel = new ResponseModel();
        List<Notice> noticeList = noticeService.findAll();

        responseModel.setData(noticeList);
        responseModel.setMsg("GET Notice List");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * 해당 공지사항 조회
     *
     * @param noticeId
     * @return
     */
    @GetMapping("notice/{noticeId}")
    public ResponseEntity<ResponseModel> notice(@PathVariable("noticeId") int noticeId) {
        ResponseModel responseModel = new ResponseModel();
        Notice notice = noticeService.findById(noticeId);

        responseModel.setData(notice);
        responseModel.setMsg("GET" + noticeId + "Notice");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * 해당 공지사항 삭제
     *
     * @param noticeId
     * @return
     */
    @DeleteMapping("notice/{noticeId}")
    public ResponseEntity<ResponseModel> delete(@PathVariable("noticeId") int noticeId) {
        ResponseModel responseModel = new ResponseModel();
        Notice notice = noticeService.findById(noticeId);

        responseModel.setData(notice);
        responseModel.setMsg("DELETE" + noticeId + "Notice");
        noticeService.delete(noticeId);
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
