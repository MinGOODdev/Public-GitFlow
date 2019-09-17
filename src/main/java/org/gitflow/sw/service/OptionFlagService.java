package org.gitflow.sw.service;

import org.gitflow.sw.dto.OptionFlag;

public interface OptionFlagService {

    OptionFlag findById(int id);

    void update();

    Boolean optionFlagCheck();

    @Deprecated
    void optionFlagUpdate();

    @Deprecated
    void tempOddUpdate(int rateLimit);

    @Deprecated
    void tempEvenUpdate(int rateLimit);

}
