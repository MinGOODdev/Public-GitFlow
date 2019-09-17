package org.gitflow.sw.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Front {
    private int id;
    @Size(min = 10)
    private String content;
}
