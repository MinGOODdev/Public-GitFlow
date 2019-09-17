package org.gitflow.sw.service;

import org.gitflow.sw.dto.LibExpect;

import java.util.List;

public interface LibExpectService {

    List<LibExpect> findAll();

    void insert(String fileName, int codeLine);

    void deleteAll();

}
