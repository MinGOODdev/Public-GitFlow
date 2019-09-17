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

/**
 * 스케줄러에 사용되는 FileReader
 */
@SuppressWarnings("Duplicates")
@Slf4j
@Component
public class FileReader {

    @Autowired
    private FileReaderService fileReaderService;

    /**
     * File을 읽으면서 전체 코드 라인 수를 구합니다.
     *
     * @param psPath
     * @param includes:     포함되어야 하는 유형
     * @param excludes:     제외되어야 하는 유형
     * @param ignorePaths:  무시해야 하는 경로
     * @param mustContains: 반드시 포함해야 하는 FileName
     * @return
     */
    public int countTotalLine(String psPath,
                              List<Include> includes,
                              List<Exclude> excludes,
                              List<IgnorePath> ignorePaths,
                              List<MustContain> mustContains) {
        int tnRetVal = 0;
        File toPath = new File(psPath);
        File[] files = toPath.listFiles();

        for (int i = 0, len = files.length; i < len; ++i) {
            String fileName = files[i].getName().toLowerCase();
            boolean pathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);

            if (files[i].isDirectory() && pathFlag) {
                tnRetVal += countTotalLine(files[i].getPath(), includes, excludes, ignorePaths, mustContains);
            } else if (files[i].isFile()) {
                int tnLineCount = 0;
                boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                if (includeFlag && excludeFlag || mustContainFlag) {
                    try {
                        BufferedReader in = new BufferedReader(new java.io.FileReader(files[i].getPath()));
                        while (in.readLine() != null) {
                            ++tnLineCount;
                        }
                    } catch (IOException e) {
                        log.info("*** 파일을 읽는 중 예외 발생");
                    }
                    tnRetVal += tnLineCount;
                    log.info("**** {} / {} / {}", fileName, files[i].getPath(), tnLineCount);
                }
            }
        }
        return tnRetVal;
    }

}
