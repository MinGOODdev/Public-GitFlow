package org.gitflow.sw.service;

import org.gitflow.sw.model.Pagination;

public interface PaginationService {

    boolean pgStartCheck(int pg);

    boolean pgEndCheck(int pg, Pagination pagination);

}
