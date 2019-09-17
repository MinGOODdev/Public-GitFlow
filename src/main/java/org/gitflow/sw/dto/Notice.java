package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Notice {

    private int id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

}
