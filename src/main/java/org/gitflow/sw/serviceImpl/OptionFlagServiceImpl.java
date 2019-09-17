package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.OptionFlag;
import org.gitflow.sw.mapper.OptionFlagMapper;
import org.gitflow.sw.service.OptionFlagService;
import org.gitflow.sw.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OptionFlagServiceImpl implements OptionFlagService {

    @Resource
    private OptionFlagMapper optionFlagMapper;

    @Autowired
    private SchedulerService schedulerService;

    /**
     * OptionFlag 조회
     *
     * @param id
     * @return
     */
    @Override
    public OptionFlag findById(int id) {
        return optionFlagMapper.findById(id);
    }

    /**
     * 지원 기능 활성/비활성을 위한 메소드
     */
    @Override
    public void update() {
        OptionFlag optionFlag = this.findById(1);
        if (optionFlag.getSchedulerActive() == 0) {
            optionFlag.setSchedulerActive(1);
        } else if (optionFlag.getSchedulerActive() == 1) {
            optionFlag.setSchedulerActive(0);
        }
        optionFlagMapper.update(optionFlag);
    }

    @Override
    public Boolean optionFlagCheck() {
        OptionFlag optionFlag = this.findById(1);
        if (optionFlag.getSchedulerActive() == 1) return true;
        else return false;
    }

    /**
     * OptionFlag 업데이트
     * - 스케줄러 동작 시작할 때, schedulerActive 1로 변경, 시작 시간 저장
     * - 스케줄러 동작 종료할 때, schedulerActive 0으로 변경, 종료 시간 저장
     */
    @Deprecated
    @Override
    public void optionFlagUpdate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
        OptionFlag optionFlag = this.findById(1);

        boolean schedulerActive = schedulerService.schedulerRunningCheck();
        if (schedulerActive) {
            // 스케줄러 동작 End
            optionFlag.setSchedulerActive(0);
            optionFlag.setEndedAt(simpleDateFormat.format(date));
        } else {
            // 스케줄러 동작 Start
            optionFlag.setSchedulerActive(1);
            optionFlag.setStartedAt(simpleDateFormat.format(date));
        }

        optionFlagMapper.optionFlagUpdate(optionFlag);
    }

    @Deprecated
    @Override
    public void tempOddUpdate(int rateLimit) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
        OptionFlag optionFlag = this.findById(1);

        boolean schedulerActive = schedulerService.schedulerRunningCheck();
        if (schedulerActive) {
            optionFlag.setSchedulerActive(0);
            optionFlag.setOddEndedAt(simpleDateFormat.format(date));
            optionFlag.setOddLimit(rateLimit);
        } else {
            optionFlag.setSchedulerActive(1);
            optionFlag.setOddStartedAt(simpleDateFormat.format(date));
        }

        optionFlagMapper.tempOddUpdate(optionFlag);
    }

    @Deprecated
    @Override
    public void tempEvenUpdate(int rateLimit) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
        OptionFlag optionFlag = this.findById(1);

        boolean schedulerActive = schedulerService.schedulerRunningCheck();
        if (schedulerActive) {
            optionFlag.setSchedulerActive(0);
            optionFlag.setEvenEndedAt(simpleDateFormat.format(date));
            optionFlag.setEvenLimit(rateLimit);
        } else {
            optionFlag.setSchedulerActive(1);
            optionFlag.setEvenStartedAt(simpleDateFormat.format(date));
        }

        optionFlagMapper.tempEvenUpdate(optionFlag);
    }

}
