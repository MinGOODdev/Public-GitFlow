package org.gitflow.sw.service;

import org.gitflow.sw.dto.Include;
import org.kohsuke.github.GHRepository;

import java.util.List;
import java.util.Map;

public interface AdminService {

    void runLinuxGitClone(String gitUrl, String tempPath);

    void runWindowGitClone(String gitUrl, String tempPath);

    void runLinuxDirectoryRemove();

    void runWindowDirectoryRemove();

    Map<String, Integer> makeBaseCodeMap(List<Include> includes);

    Map<String, Integer> makeResultCodeMap(Map<String, Integer> codeMap);

    Map<String, Map<String, Integer>> makeDeepCodeMap(Map<String, Map<String, Integer>> contributorCodeMap);

    Map<Object, Object> sortByValues(Map map, boolean isASC);

    void makeLogAndCodeMapByContributor(List<GHRepository.Contributor> contributorList,
                                        Map<String, Map<String, Integer>> contributorLogMap,
                                        Map<String, Map<String, Integer>> contributorCodeMap,
                                        List<Include> includes);

    void contributorWriteCodeLineCalculator(Map<String, Integer> contributorWriteCodeMap,
                                            String userName, int userCodeLine);

    void contributorLogArrange(Map<String, Map<String, Integer>> contributorLogMap,
                               String userName, String fileName, int userCodeLine);

    void extensionWorkloadByContributor(Map<String, Map<String, Integer>> contributorCodeMap,
                                        String userName, String fileName, int userCodeLine);

    Map<Object, Object> orderByDescContributorLogMap(Map<String, Map<String, Integer>> contributorLogMap);

}
