package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * For MySQL
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Repo {

    private int id;
    private int userId;
    private String repoName;
    private String repoLanguage;
    private String repoUrl;
    private int userCommitCount;
    private int allCommitCount;
    private int userCodeLine;
    private int totalCodeLine;
    private int contributors;

}
