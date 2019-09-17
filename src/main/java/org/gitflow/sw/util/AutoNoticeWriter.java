package org.gitflow.sw.util;

import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.model.NoticeModel;
import org.gitflow.sw.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Component
public class AutoNoticeWriter {

    @Autowired
    private NoticeService noticeService;

    /**
     * 업데이트된 내역 자동 공지사항으로 작성
     *
     * @param updatingMap
     */
    public void updatingListWriter(Map<String, List<Repo>> updatingMap) {
        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setTitle("[Updated List] " + this.updatingUserCount(updatingMap) + "명");
        noticeModel.setContent(this.makeNoticeContent(updatingMap));
        noticeService.insert(noticeModel);
    }

    /**
     * 업데이트할 내용이 있는 사용자 카운트
     *
     * @param updatingMap
     * @return
     */
    private int updatingUserCount(Map<String, List<Repo>> updatingMap) {
        int count = 0;
        for (String userName : updatingMap.keySet()) {
            List<Repo> repoList = updatingMap.get(userName);
            if (!repoList.isEmpty()) ++count;
        }
        return count;
    }

    /**
     * 작성할 공지사항 내용 만들기
     *
     * @param updatingMap
     * @return
     */
    private String makeNoticeContent(Map<String, List<Repo>> updatingMap) {
        StringBuilder sb = new StringBuilder();

        for (String userName : updatingMap.keySet()) {
            List<Repo> repoList = updatingMap.get(userName);

            if (!repoList.isEmpty()) {
                sb.append(userName.toUpperCase() + "\n");

                for (Repo repo : repoList) {
                    sb.append(repo.getId() + " ")
                            .append(repo.getRepoName() + " ")
                            .append(repo.getRepoUrl() + "\n");
                }
                sb.append("-------------------------\n");
            }
        }
        return sb.toString();
    }

}
