package org.gitflow.sw.controller.rest;

import lombok.AllArgsConstructor;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.model.ResponseModel;
import org.gitflow.sw.service.RepoService;
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
public class RestRepoController {

    private RepoService repoService;
    private UserService userService;

    /**
     * 사용자의 repository 목록 조회
     *
     * @param userId
     * @returndto
     */
    @GetMapping("user/{userId}/repos")
    public ResponseEntity<ResponseModel> repos(@PathVariable("userId") int userId) {
        ResponseModel responseModel = new ResponseModel();
        GitUser gitUser = userService.findById(userId);
        List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("user", gitUser);
        map.put("repoList", repoList);

        responseModel.setData(map);
        responseModel.setMsg("GET" + gitUser.getLogin() + "'s repository list");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
