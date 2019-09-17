package org.gitflow.sw.util.markdown;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;

@Slf4j
@Component
public class MarkDownRendering {

    private GitHub gitHub;

    public MarkDownRendering(@Value("${markdown.git.username}") String username,
                             @Value("${markdown.git.password}") String password) {

        log.info("MarkDown GitHub account, username : {}", username);

        try {
            gitHub = GitHub.connectUsingPassword(username, password);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String markDownRendering(String text) {
        StringBuilder htmlTag = new StringBuilder();

        try {
            Reader markdown = gitHub.renderMarkdown(text);

            int readData;
            while ((readData = markdown.read()) != -1) {
                char htmlWordData = (char) readData;
                htmlTag.append(htmlWordData);  //char 형태로 html tag 생성되서, 합쳐줘야 한다.
            }

            log.info(htmlTag.toString());
            log.info(gitHub.getRateLimit().toString());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return htmlTag.toString();
    }

    /*
    markdown 랜더링 시에 '개행(new line)'을 처리하지 못하는 에러가 있다.
    'Enter' 키를 두번 입력해야 개행이 들어간다. 따라서 개행을 위해 '개행'을 두번 넣어주는 방식으로 진행.
     */
    public String newLineRendering(String input) {

        String pattern = System.getProperty("line.separator");  // "\r\n";
        if (input.contains(pattern)) {
            //split으로 자르고 자른 배열 뒤에 pattern을 붙여줘야 한다.
            String[] piece = input.split(pattern);

            StringBuilder collectPieces = new StringBuilder();

            int pieceLength = piece.length;
            for (int i = 0; i < pieceLength; i++) {
                collectPieces.append(piece[i]);
                collectPieces.append(pattern);
                collectPieces.append(pattern);
            }

            return collectPieces.toString();

        } else return input;
    }

    public int getGitHubApiRateLimit() {
        int result = 0;

        try {
            result = gitHub.getRateLimit().remaining;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }
}
