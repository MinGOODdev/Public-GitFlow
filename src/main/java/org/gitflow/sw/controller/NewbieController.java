package org.gitflow.sw.controller;

import org.gitflow.sw.model.BackModel;
import org.gitflow.sw.model.FrontModel;
import org.gitflow.sw.model.Pagination;
import org.gitflow.sw.service.BackService;
import org.gitflow.sw.service.FrontService;
import org.gitflow.sw.service.PaginationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("newbie")
public class NewbieController {

    private FrontService frontService;
    private BackService backService;
    private PaginationService paginationService;

    public NewbieController(FrontService frontService,
                            BackService backService,
                            PaginationService paginationService) {
        this.frontService = frontService;
        this.backService = backService;
        this.paginationService = paginationService;
    }

    @GetMapping("front/all")
    public String front(Model model,
                        Pagination pagination,
                        @RequestParam(value = "pg", defaultValue = "1") int pg) {
        model.addAttribute("fronts", frontService.findAll(pagination));
        model.addAttribute("pgPrev", pg - 1);
        model.addAttribute("pgNext", pg + 1);
        model.addAttribute("pgStart", paginationService.pgStartCheck(pg));
        model.addAttribute("pgEnd", paginationService.pgEndCheck(pg, pagination));
        return "newbie/front";
    }

    @PostMapping("front/create")
    public String createFront(@ModelAttribute("frontModel") @Validated FrontModel frontModel,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/newbie/front/all";
        }
        frontService.insert(frontModel.getContent());
        return "redirect:/newbie/front/all";
    }

    @GetMapping("back/all")
    public String back(Model model,
                       Pagination pagination,
                       @RequestParam(value = "pg", defaultValue = "1") int pg) {
        model.addAttribute("backs", backService.findAll(pagination));
        model.addAttribute("pgPrev", pg - 1);
        model.addAttribute("pgNext", pg + 1);
        model.addAttribute("pgStart", paginationService.pgStartCheck(pg));
        model.addAttribute("pgEnd", paginationService.pgEndCheck(pg, pagination));
        return "newbie/back";
    }

    @PostMapping("back/create")
    public String backCreate(@ModelAttribute("backModel") @Validated BackModel backModel,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/newbie/back/all";
        }
        backService.insert(backModel.getContent());
        return "redirect:/newbie/back/all";
    }
}
