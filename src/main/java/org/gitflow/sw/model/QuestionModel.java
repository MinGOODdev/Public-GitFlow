package org.gitflow.sw.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class QuestionModel {

    @Size(min = 10)
    private String body;

}
