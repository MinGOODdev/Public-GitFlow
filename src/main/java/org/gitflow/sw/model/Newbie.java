package org.gitflow.sw.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Newbie {

    private String userName;
    private String password1;
    private String password2;
    private int departmentId;

}
