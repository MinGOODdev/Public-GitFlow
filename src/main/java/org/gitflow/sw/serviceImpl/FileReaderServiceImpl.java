package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.service.FileReaderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileReaderServiceImpl implements FileReaderService {

    /**
     * Path가 무시해도 되는 경로인지 검사
     *
     * @param fileName
     * @param ignorePaths
     * @return
     */
    @Override
    public boolean ignorePathCheck(String fileName, List<IgnorePath> ignorePaths) {
        boolean pathFlag = true;
        for (IgnorePath ignorePath : ignorePaths) {
            if (fileName.contains(ignorePath.getPathName())) {
                pathFlag = false;
            }
        }
        return pathFlag;
    }

    /**
     * File이 읽어야 하는 것인지 검사
     *
     * @param fileName
     * @param includes
     * @return
     */
    @Override
    public boolean includeCheck(String fileName, List<Include> includes) {
        boolean includeFlag = false;
        String[] strArr = fileName.toLowerCase().split("[+.+]");
        String str = "." + strArr[strArr.length - 1];
        for (Include include : includes) {
            if (str.equals(include.getIncludeRegex())) {
                includeFlag = true;
            }
        }
        return includeFlag;
    }

    /**
     * File이 무시해야 하는 것인지 검사
     *
     * @param fileName
     * @param excludes
     * @return ignore case: return false
     */
    @Override
    public boolean excludeCheck(String fileName, List<Exclude> excludes) {
        boolean excludeFlag = true;
        for (Exclude exclude : excludes) {
            if (fileName.contains(exclude.getExcludeRegex())) {
                excludeFlag = false;
            }
        }
        return excludeFlag;
    }

    /**
     * File이 제외 항목 검사에 포함되지만, 포함했으면 하는 경우
     * - 단, 이것은 부분 일치가 아니라 전부 일치해야 합니다.
     *
     * @param fileName
     * @param mustContains
     * @return
     */
    @Override
    public boolean mustContainCheck(String fileName, List<MustContain> mustContains) {
        boolean mustContainFlag = false;
        for (MustContain mustContain : mustContains) {
            if (fileName.toLowerCase().equals(mustContain.getContainName())) {
                mustContainFlag = true;
            }
        }
        return mustContainFlag;
    }

}
