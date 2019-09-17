package org.gitflow.sw.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Slf4j
public class Pagination {

    private int pg = 1;
    private int sz = 10;
    private String st;
    private int recordCount;

    public String getQueryString() {
        String url = null;
        try {
            String tempSt = (st == null) ? "" : URLEncoder.encode(st, "UTF-8");
            url = String.format("pg=%d&sz=%d&st=%s", pg, sz, tempSt);
        } catch (UnsupportedEncodingException e) {
            log.error("Pagination 인코딩 에러");
        }
        return url;
    }

}
