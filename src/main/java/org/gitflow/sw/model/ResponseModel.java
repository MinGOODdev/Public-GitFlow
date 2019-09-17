package org.gitflow.sw.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseModel {

    private Object data;
    private String msg;

    public ResponseModel() {
        this.data = null;
        this.msg = null;
    }

}
