package com.push.core.mainDto;

import com.push.entity.PushData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 推送数据
 * @author: Yan XinYu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushDto {

     private String url;
     private Integer companyId;
     private List<PushData> records;
}
