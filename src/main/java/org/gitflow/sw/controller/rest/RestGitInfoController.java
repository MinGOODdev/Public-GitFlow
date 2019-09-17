package org.gitflow.sw.controller.rest;

import lombok.AllArgsConstructor;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.ResponseModel;
import org.gitflow.sw.service.DepartmentService;
import org.gitflow.sw.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class RestGitInfoController {

    private UserService userService;
    private DepartmentService departmentService;

    /**
     * 소속 별 사용자 목록 조회 + 랭킹
     *
     * @param departmentId
     * @param urlUserName
     * @return
     */
    @GetMapping("users/{departmentId}/{urlUserName}")
    public ResponseEntity<ResponseModel> getUsers(@PathVariable("departmentId") int departmentId,
                                                  @PathVariable("urlUserName") String urlUserName) {
        ResponseModel responseModel = new ResponseModel();
        GitUser gitUser = userService.findByUserName(urlUserName);
        String userName = gitUser.getLogin();

        List<GitUser> gitUserList = userService.findAllByDepartmentIdOrderByTotalUserCodeLineDesc(departmentId);
        String departmentName = departmentService.findById(departmentId).getDepartmentName();
        int totalCodeLineRank = userService.getUserRankByTotalCodeLine(userName, gitUserList);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("currentUser", gitUser);
        responseMap.put("gitUserList", gitUserList);
        responseMap.put("departmentName", departmentName);
        responseMap.put("totalCodeLineRank", totalCodeLineRank);

        responseModel.setData(responseMap);
        responseModel.setMsg("GET" + departmentId + "'s User List (Contain Ranking)");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
