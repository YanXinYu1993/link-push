package com.push.core.mainDto;

import com.push.entity.CommitLog;
import com.push.entity.PushData;
import lombok.Data;

import java.util.List;

/**
 * @description: 传输数据
 * @author: Yan XinYu
 */
@Data
public class TransferDto {

    private List<PushData> records;

    private String url;

    private Integer companyId;

}
