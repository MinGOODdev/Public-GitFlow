package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Question {

    private int id;
    private String content;
    private String createdAt;

}
