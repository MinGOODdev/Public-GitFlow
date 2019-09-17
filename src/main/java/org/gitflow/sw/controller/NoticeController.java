package org.gitflow.sw.controller;

import org.gitflow.sw.model.NoticeModel;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.NoticeService;
import org.gitflow.sw.service.PaginationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("notice")
public class NoticeController {

    private NoticeService noticeService;
    private PaginationService paginationService;
    private CommonService commonService;

    public NoticeController(NoticeService noticeService,
                            PaginationService paginationService,
                            CommonService commonService) {
        this.noticeService = noticeService;
        this.paginationService = paginationService;
        this.commonService = commonService;
    }

    /**
     * 공지사항 전체 목록 조회
     *
     * @param model
     * @param pagination
     * @param pg
     * @return
     */
    @GetMapping("all")
    public String notices(Model model,
                          Pagination pagination,
                          @RequestParam(value = "pg", defaultValue = "1") int pg) {
        commonService.commonAttributeSetting(model);

        model.addAttribute("notices", noticeService.findAllWithPagination(pagination));
        model.addAttribute("pgPrev", pg - 1);
        model.addAttribute("pgNext", pg + 1);
        model.addAttribute("pgStart", paginationService.pgStartCheck(pg));
        model.addAttribute("pgEnd", paginationService.pgEndCheck(pg, pagination));
        return "notice/all";
    }

    /**
     * 해당 공지사항 조회
     *
     * @param model
     * @param noticeId
     * @return
     */
    @GetMapping("{noticeId}")
    public String showNotice(Model model,
                             @PathVariable("noticeId") int noticeId) {
        commonService.commonAttributeSetting(model);
        model.addAttribute("notice", noticeService.findById(noticeId));
        return "notice/show";
    }

    /**
     * 공지사항 작성 GET
     *
     * @return
     */
    @GetMapping("new")
    public String newNotice(Model model) {
        commonService.commonAttributeSetting(model);
        return "notice/new";
    }

    /**
     * 공지사항 작성 POST
     *
     * @param model
     * @param noticeModel
     * @return
     */
    @PostMapping("create")
    public String createNotice(Model model,
                               @ModelAttribute("noticeModel") NoticeModel noticeModel) {
        commonService.commonAttributeSetting(model);
        noticeService.insert(noticeModel);
        return "redirect:/notice/all";
    }

    /**
     * 공지사항 수정 GET
     *
     * @param model
     * @param noticeId
     * @return
     */
    @GetMapping("edit")
    public String editNotice(Model model,
                             @RequestParam("noticeId") int noticeId) {
        commonService.commonAttributeSetting(model);
        model.addAttribute("notice", noticeService.findById(noticeId));
        return "notice/edit";
    }

    /**
     * 공지사항 수정 POST
     *
     * @param noticeModel
     * @return
     */
    @PostMapping("update")
    public String updateNotice(@ModelAttribute("noticeModel") NoticeModel noticeModel) {
        noticeService.update(noticeModel);
        return "redirect:/notice/" + noticeModel.getId();
    }

    /**
     * 해당 공지사항 삭제
     *
     * @param noticeId
     * @return
     */
    @GetMapping("delete")
    public String deleteNotice(@RequestParam("noticeId") int noticeId) {
        noticeService.delete(noticeId);
        return "redirect:/notice/all";
    }

}
