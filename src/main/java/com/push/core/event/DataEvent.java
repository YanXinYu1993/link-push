package com.push.core.event;

import com.push.entity.CommitLog;
import com.push.entity.PushData;
import lombok.Data;

import java.util.List;


/**
 * @description:
 * @author: Yan XinYu
 */
@Data
public class DataEvent {

    private CommitLog commitLog;
    private List<PushData> list;
}
