package org.gitflow.sw.command;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.mapper.ExcludeMapper;
import org.gitflow.sw.mapper.IgnorePathMapper;
import org.gitflow.sw.mapper.IncludeMapper;
import org.gitflow.sw.mapper.MustContainMapper;
import org.gitflow.sw.util.command.WindowCmd;
import org.gitflow.sw.util.reader.FileReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CmdTest {

    @Resource
    private IncludeMapper includeMapper;
    @Resource
    private ExcludeMapper excludeMapper;
    @Resource
    private IgnorePathMapper ignorePathMapper;
    @Resource
    private MustContainMapper mustContainMapper;
    @Autowired
    private FileReader fileReader;

    private static final String GIT_URL = "https://github.com/tails5555/MentoringProject.git";

    private static final String TEMP_PATH = GIT_URL.replaceFirst("https://github.com/", "")
            .replaceFirst("/", "")
            .replaceFirst(".git", "");

    @Test
    public void cmd_test() throws Exception {
        WindowCmd cmd = new WindowCmd();
        String command = cmd.inputCommand("git clone ").concat(GIT_URL).concat(" dump/" + TEMP_PATH);
        cmd.execCommand(command);
    }

    @Test
    public void read_test() throws Exception {
        List<Include> includes = includeMapper.findAll();
        List<Exclude> excludes = excludeMapper.findAll();
        List<IgnorePath> ignorePaths = ignorePathMapper.findAll();
        List<MustContain> mustContains = mustContainMapper.findAll();
        log.info("** {}", fileReader.countTotalLine("dump/" + TEMP_PATH, includes, excludes, ignorePaths, mustContains));
    }

    /**
     * 폴더 삭제
     * /s : 내부가 비어있지 않아도 삭제
     * /q : 삭제 confirm 없이 바로 삭제
     *
     * @throws Exception
     */
    @Test
    public void rmdir_test() throws Exception {
        WindowCmd cmd = new WindowCmd();
        String command = cmd.inputCommand("rd /s /q dump");
        // linux: rm -r dump
        cmd.execCommand(command);
    }

}
