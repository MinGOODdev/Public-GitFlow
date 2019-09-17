package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class QuestionComment {

    private int id;
    private int questionId;
    private String comment;
    private String createdAt;

}
