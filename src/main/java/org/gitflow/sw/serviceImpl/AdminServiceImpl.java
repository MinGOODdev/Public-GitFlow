package org.gitflow.sw.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.service.AdminService;
import org.gitflow.sw.util.command.Cmd;
import org.gitflow.sw.util.command.WindowCmd;
import org.kohsuke.github.GHRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    /**
     * 배포 서버용(리눅스) 명령어 실행
     *
     * @param gitUrl
     * @param tempPath
     */
    @Override
    public void runLinuxGitClone(String gitUrl, String tempPath) {
        Cmd cmd = new Cmd();
        String command = cmd.inputCommand("git clone ")
                .concat(gitUrl)
                .concat(" dump/" + tempPath);
        cmd.execCommand(command);
        log.info("*** {} / linux git clone command 실행", gitUrl);
    }

    /**
     * 로컬(윈도우) 명령어 실행
     *
     * @param gitUrl
     * @param tempPath
     */
    @Override
    public void runWindowGitClone(String gitUrl, String tempPath) {
        WindowCmd cmd = new WindowCmd();
        String command = cmd.inputCommand("git clone ")
                .concat(gitUrl)
                .concat(" dump/" + tempPath);
        log.info("***** {}", command);
        cmd.execCommand(command);
        log.info("*** {} / window git clone 실행", gitUrl);
    }

    /**
     * 배포 서버용(리눅스) git clone 경로 삭제
     */
    @Override
    public void runLinuxDirectoryRemove() {
        Cmd cmd = new Cmd();
        String command = cmd.inputCommand("rm -r dump");
        cmd.execCommand(command);
    }

    /**
     * 로컬(윈도우) git clone 경로 삭제
     */
    @Override
    public void runWindowDirectoryRemove() {
        WindowCmd cmd = new WindowCmd();
        String command = cmd.inputCommand("rmdir /s /q dump");
        cmd.execCommand(command);
    }

    /**
     * 확장자 별 코드 수 계산을 위한 Map 생성
     *
     * @param includes
     * @return
     */
    @Override
    public Map<String, Integer> makeBaseCodeMap(List<Include> includes) {
        Map<String, Integer> codeMap = new HashMap<>();
        for (Include include : includes) {
            codeMap.put(include.getIncludeRegex(), 0);
        }
        return codeMap;
    }

    /**
     * 결과값을 화면에 표현하기 위한 Map 생성
     *
     * @param codeMap
     * @return
     */
    @Override
    public Map<String, Integer> makeResultCodeMap(Map<String, Integer> codeMap) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (String key : codeMap.keySet()) {
            if (codeMap.get(key) != 0) {
                resultMap.put(key, codeMap.get(key));
            }
        }
        return resultMap;
    }

    /**
     * Contributor마다 확장자 별 코드 수를 표현하기 위한 Deep Map 생성
     *
     * @param contributorCodeMap
     * @return
     */
    public Map<String, Map<String, Integer>> makeDeepCodeMap(Map<String, Map<String, Integer>> contributorCodeMap) {
        Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        for (String contributorName : contributorCodeMap.keySet()) {
            Map<String, Integer> tempCodeMap = new HashMap<>();
            for (String extension : contributorCodeMap.get(contributorName).keySet()) {
                if (contributorCodeMap.get(contributorName).get(extension) != 0) {
                    tempCodeMap.put(extension, contributorCodeMap.get(contributorName).get(extension));
                }
            }
            resultMap.put(contributorName, tempCodeMap);
        }
        return resultMap;
    }

    /**
     * Map Value로 정렬하기
     *
     * @param map
     * @param isASC
     * @return
     */
    @Override
    public Map<Object, Object> sortByValues(Map map, boolean isASC) {
        List list = new LinkedList(map.entrySet());

        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        if (isASC) {
            Collections.reverse(list);
        }

        HashMap<Object, Object> sortedHashMap = new LinkedHashMap();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

    /**
     * Contributor 별 LogMap과 CodeMap 만들기
     *
     * @param contributorList
     */
    @Override
    public void makeLogAndCodeMapByContributor(List<GHRepository.Contributor> contributorList,
                                               Map<String, Map<String, Integer>> contributorLogMap,
                                               Map<String, Map<String, Integer>> contributorCodeMap,
                                               List<Include> includes) {
        for (GHRepository.Contributor contributor : contributorList) {
            Map<String, Integer> logMap = new HashMap<>();
            Map<String, Integer> codeMap = this.makeBaseCodeMap(includes);
            contributorLogMap.put(contributor.getLogin().toLowerCase(), logMap);
            contributorCodeMap.put(contributor.getLogin().toLowerCase(), codeMap);
        }
    }

    /**
     * Contributor 별 작업량 계산
     *
     * @param contributorWriteCodeMap
     * @param userName
     * @param userCodeLine
     */
    @Override
    public void contributorWriteCodeLineCalculator(Map<String, Integer> contributorWriteCodeMap,
                                                   String userName, int userCodeLine) {
        if (contributorWriteCodeMap.get(userName) == null) {
            contributorWriteCodeMap.put(userName, userCodeLine);
        } else {
            contributorWriteCodeMap.put(userName, contributorWriteCodeMap.get(userName) + userCodeLine);
        }
    }

    /**
     * Contributor 별 작업 로그 정리
     *
     * @param contributorLogMap
     * @param userName
     * @param fileName
     * @param userCodeLine
     */
    @Override
    public void contributorLogArrange(Map<String, Map<String, Integer>> contributorLogMap,
                                      String userName, String fileName, int userCodeLine) {
        if (contributorLogMap.get(userName).get(fileName) == null) {
            contributorLogMap.get(userName).put(fileName, userCodeLine);
        } else {
            contributorLogMap.get(userName).put(fileName, contributorLogMap.get(userName).get(fileName) + userCodeLine);
        }
    }

    /**
     * Contributor 별 Extension 별 작업량 계산
     *
     * @param contributorCodeMap
     * @param fileName
     * @param userCodeLine
     */
    @Override
    public void extensionWorkloadByContributor(Map<String, Map<String, Integer>> contributorCodeMap,
                                               String userName, String fileName, int userCodeLine) {
        for (String extension : contributorCodeMap.get(userName).keySet()) {
            String[] splitFileNames = fileName.toLowerCase().split("[+.+]");
            String str = "." + splitFileNames[splitFileNames.length - 1];

            if (str.equals(extension)) {
                contributorCodeMap.get(userName)
                        .put(extension, contributorCodeMap.get(userName).get(extension) + userCodeLine);
            }
        }
    }

    /**
     * Contributor 별 Log 목록 작업량 DESC 정렬
     *
     * @param contributorLogMap
     * @return
     */
    @Override
    public Map<Object, Object> orderByDescContributorLogMap(Map<String, Map<String, Integer>> contributorLogMap) {
        Map<Object, Object> contributorOrderedLogMap = new HashMap<>();
        for (String key : contributorLogMap.keySet()) {
            Map<String, Integer> logMap = contributorLogMap.get(key);
            Map<Object, Object> orderedLogMap = this.sortByValues(logMap, true);
            contributorOrderedLogMap.put(key, orderedLogMap);
        }
        return contributorOrderedLogMap;
    }

}
