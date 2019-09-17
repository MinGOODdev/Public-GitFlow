package org.gitflow.sw.controller;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.util.markdown.MarkDownRendering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class UtilController {

    @Autowired
    public MarkDownRendering markDownRendering;

    @Autowired
    private CommonService commonService;

    @RequestMapping("/util/markdown")
    public String markdown(Model model) {
        commonService.commonAttributeSetting(model);
        return "/util/markdown";
    }

    @GetMapping(value = "/rendering")
    public @ResponseBody
    String rendering(@RequestParam(value = "readme-text") String text) {
        String editText = markDownRendering.newLineRendering(text);
        return markDownRendering.markDownRendering(editText);
    }

    @GetMapping(value = "/getRateLimit")
    public @ResponseBody
    int getGitHubApiRateLimit() {
        return markDownRendering.getGitHubApiRateLimit();
    }

}
