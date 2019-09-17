package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LibExpect {

    private int id;
    private String fileName;
    private int codeLine;

}
