package org.gitflow.sw.service;

import org.gitflow.sw.dto.ApplyPart;

import java.util.List;

public interface ApplyPartService {

    List<ApplyPart> findAll();

    ApplyPart findById(int id);

}
