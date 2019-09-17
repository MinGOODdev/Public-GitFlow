package org.gitflow.sw.util.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 필터 적용 X
 */
@SuppressWarnings("Duplicates")
@Slf4j
@Component
public class NoFilterFileReader {

    /**
     * 예외 없이 전부 다 읽어서 확장자 별로 분류
     *
     * @param psPath
     * @param codeMap
     * @param logMap
     */
    public void countTotalLine(String psPath,
                               Map<String, Integer> codeMap,
                               Map<String, Integer> logMap) {
        File toPath = new File(psPath);
        File[] files = toPath.listFiles();

        for (int i = 0, len = files.length; i < len; ++i) {
            String fileName = files[i].getName().toLowerCase();

            if (files[i].isDirectory()) {
                countTotalLine(files[i].getPath(), codeMap, logMap);
            } else if (files[i].isFile()) {


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
