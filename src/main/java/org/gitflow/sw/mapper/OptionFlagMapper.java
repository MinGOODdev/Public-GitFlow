package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.OptionFlag;

@Mapper
public interface OptionFlagMapper {

    OptionFlag findById(int id);

    void update(OptionFlag optionFlag);

    void optionFlagUpdate(OptionFlag optionFlag);

    void tempOddUpdate(OptionFlag optionFlag);

    void tempEvenUpdate(OptionFlag optionFlag);

}
