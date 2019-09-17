package org.gitflow.sw.util.reader;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.service.FileReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 레포의 언어별 코드 수 구하기에 사용
 */
@SuppressWarnings("Duplicates")
@Slf4j
@Component
public class DetailFileReader {

    @Autowired
    private FileReaderService fileReaderService;

    /**
     * 확장자 별로 코드가 몇 라인인지 계산
     *
     * @param psPath
     * @param includes
     * @param excludes
     * @param ignorePaths
     * @param mustContains
     * @return
     */
    public void countTotalLine(String psPath,
                               List<Include> includes,
                               List<Exclude> excludes,
                               List<IgnorePath> ignorePaths,
                               List<MustContain> mustContains,
                               Map<String, Integer> codeMap,
                               Map<String, Integer> logMap) {
        File toPath = new File(psPath);
        File[] files = toPath.listFiles();

        for (int i = 0, len = files.length; i < len; ++i) {
            String fileName = files[i].getName().toLowerCase();
            boolean pathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);

            if (files[i].isDirectory() && pathFlag) {
                countTotalLine(files[i].getPath(), includes, excludes, ignorePaths, mustContains, codeMap, logMap);
            } else if (files[i].isFile()) {
                boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                if (includeFlag && excludeFlag || mustContainFlag) {
                    int tnLineCount = 0;
                    try {
                        BufferedReader in = new BufferedReader(new java.io.FileReader(files[i].getPath()));
                        while (in.readLine() != null) {
                            ++tnLineCount;
                        }
                    } catch (IOException e) {
                        log.info("*** 파일을 읽는 중 예외 발생");
                    }

                    // 로그를 보여주기 위함
                    String filePath = files[i].getPath();
                    if (logMap.get(filePath) == null) {
                        logMap.put(filePath, tnLineCount);
                    } else {
                        logMap.put(filePath, logMap.get(filePath) + tnLineCount);
                    }
                    log.info("**** {} / {} / {}", fileName, filePath, tnLineCount);

                    // 확장자 별 코드 수 Map에 갱신
                    for (String extension : codeMap.keySet()) {
                        String[] splitFileNames = fileName.toLowerCase().split("[+.+]");
                        String str = "." + splitFileNames[splitFileNames.length - 1];
                        if (str.equals(extension)) {
                            codeMap.put(extension, codeMap.get(extension) + tnLineCount);
                        }
                    }
                }
            }
        }
    }

}
