package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ApplyContent {

    private int id;
    private int userId;
    private int partId;
    @NotEmpty
    private String userName;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String email;
    private String content1;
    private String content2;
    private String content3;
    private String finalFlag;

}
