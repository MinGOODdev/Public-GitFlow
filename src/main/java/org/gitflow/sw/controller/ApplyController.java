package org.gitflow.sw.controller;

import org.gitflow.sw.dto.ApplyContent;
import org.gitflow.sw.dto.ApplyForm;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.EmailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
@RequestMapping("apply")
public class ApplyController {

    private UserService userService;
    private ApplyPartService applyPartService;
    private ApplyFormService applyFormService;
    private ApplyContentService applyContentService;
    private ApplyCheckService applyCheckService;
    private EmailSender emailSender;
    private CommonService commonService;
    private OptionFlagService optionFlagService;

    public ApplyController(UserService userService,
                           ApplyPartService applyPartService,
                           ApplyFormService applyFormService,
                           ApplyContentService applyContentService,
                           ApplyCheckService applyCheckService,
                           EmailSender emailSender,
                           CommonService commonService,
                           OptionFlagService optionFlagService) {
        this.userService = userService;
        this.applyPartService = applyPartService;
        this.applyFormService = applyFormService;
        this.applyContentService = applyContentService;
        this.applyCheckService = applyCheckService;
        this.emailSender = emailSender;
        this.commonService = commonService;
        this.optionFlagService = optionFlagService;
    }

    /**
     * 지원 분야를 선택할 수 있는 View
     *
     * @param model
     * @return
     */
    @GetMapping("main")
    public String applyMain(Model model) {
        commonService.commonAttributeSetting(model);
        GitUser currentUser = userService.findByUserName(userService.getPrincipalUserName());
        boolean applyFlag = applyCheckService.applyExistCheck(currentUser.getId());
        boolean optionFlag = optionFlagService.optionFlagCheck();

        model.addAttribute("applyPart1", applyPartService.findById(1));
        model.addAttribute("applyPart2", applyPartService.findById(2));
        model.addAttribute("applyFlag", applyFlag);

        if (optionFlag) return "apply/main";
        else return "apply/end";
    }

    /**
     * 해당 지원 분야의 지원 양식
     *
     * @param model
     * @param partId
     * @return
     */
    @GetMapping("form/{partId}")
    public String applyForm(Model model,
                            @PathVariable("partId") int partId,
                            RedirectAttributes redirectAttributes) {
        commonService.commonAttributeSetting(model);
        GitUser currentUser = userService.findByUserName(userService.getPrincipalUserName());
        boolean applyFlag = applyCheckService.applyExistCheck(currentUser.getId());
        boolean optionFlag = optionFlagService.optionFlagCheck();

        model.addAttribute("applyForm", applyFormService.findByPartId(partId));
        model.addAttribute("partName", applyPartService.findById(partId).getPartName());
        model.addAttribute("applyFlag", applyFlag);

        // 지원 가능
        if (optionFlag) {
            if (applyFlag) {
                redirectAttributes.addFlashAttribute("msg", "이미 작성된 지원서가 존재합니다.");
                return "redirect:/apply/main";
            } else return "apply/form";
            // 지원 불가능
        } else return "apply/end";
    }

    /**
     * 해당 분야 지원 완료
     *
     * @param model
     * @param applyContent
     * @return
     */
    @PostMapping("submit")
    public String applySubmit(Model model,
                              @ModelAttribute("applyContent") @Valid ApplyContent applyContent,
                              BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "redirect:/apply/form/" + applyContent.getPartId();
        } else {
            commonService.commonAttributeSetting(model);
            applyContentService.insert(applyContent);

            // 최종 지원 완료 시 메일 전송
            emailSender.applyConfirmEmail(applyContent.getUserName(), applyContent.getEmail());
            return "apply/success";
        }
    }

    /**
     * 나의 지원서 삭제
     *
     * @return
     */
    @GetMapping("delete")
    public String applyDelete() {
        String currentUserName = userService.getPrincipalUserName();
        GitUser currentUser = userService.findByUserName(currentUserName);
        boolean optionFlag = optionFlagService.optionFlagCheck();

        if (optionFlag) {
            applyContentService.deleteByUserId(currentUser.getId());
            return "redirect:/apply/main";
        } else return "apply/end";
    }

    /**
     * 나의 지원서 조회
     *
     * @param model
     * @return
     */
    @GetMapping("me")
    public String getMyApply(Model model) {
        commonService.commonAttributeSetting(model);
        String currentUserName = userService.getPrincipalUserName();
        GitUser currentUser = userService.findByUserName(currentUserName);
        ApplyContent applyContent = applyContentService.findByUserId(currentUser.getId());
        String partName = applyPartService.findById(applyContent.getPartId()).getPartName();
        ApplyForm applyForm = applyFormService.findByPartId(applyContent.getPartId());

        model.addAttribute("applyForm", applyForm);
        model.addAttribute("applyContent", applyContent);
        model.addAttribute("partName", partName);
        return "apply/me";
    }

}
