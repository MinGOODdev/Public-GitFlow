package org.gitflow.sw.service;

import org.gitflow.sw.dto.ApplyForm;

public interface ApplyFormService {

    ApplyForm findById(int id);

    ApplyForm findByPartId(int partId);

}
