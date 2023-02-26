package com.push.core;

import com.push.core.mainDto.TransferDto;
import com.push.entity.CommitLog;
import com.push.entity.PushData;

import java.util.List;

public interface Ihandler {

    /**
     * 获取commoit_log
     * @param commitLog
     * @return
     */
    List<PushData> getDataList(CommitLog commitLog);

    /**
     * 任务处理
     * @param records
     * @return
     */
    List<TransferDto> jobHandler(List<PushData> records);

    /**
     * 处理失败的任务
     * @param dto
     * @param e
     */
    void onFailure(TransferDto dto ,Throwable e);

    /**
     * 处理成功的任务
     * @param dto
     */
    void onSuccess(TransferDto dto);

}
