package org.gitflow.sw.service;

import org.gitflow.sw.dto.IgnorePath;

import java.util.List;

public interface IgnorePathService {

    List<IgnorePath> findAll();

}
