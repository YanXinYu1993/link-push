package com.push.core.mainDto;

import com.push.entity.CommitLog;
import lombok.Data;

import java.util.List;

/**
 * @description: 传输对象
 * @author: Yan XinYu
 */
@Data
public class CommitLogDto {

    public CommitLogDto(CommitLog commitLog, List<TransferDto> list) {
        this.commitLog = commitLog;
        this.list = list;
    }

    private CommitLog commitLog;
    private List<TransferDto> list;
}
