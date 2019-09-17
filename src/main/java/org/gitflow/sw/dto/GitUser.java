package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GitUser {

    private int id;
    private int departmentId;
    private String login;
    private String password;
    private String userLanguage;
    private int totalUserCommit;
    private int totalUserCodeLine;
    private String authorization;
    private String profileUrl;

    private String departmentName;

}
