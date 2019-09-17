package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ApplyForm {

    private int id;
    private int partId;
    private String form1;
    private String form2;
    private String form3;

}
