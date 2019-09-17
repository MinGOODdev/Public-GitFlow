package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 1: 활성화
 * 0: 비활성화
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OptionFlag {

    private int id;
    private int schedulerActive;
    private String startedAt;
    private String endedAt;

    private String oddStartedAt;
    private String oddEndedAt;
    private String evenStartedAt;
    private String evenEndedAt;
    private int oddLimit;
    private int evenLimit;

}
