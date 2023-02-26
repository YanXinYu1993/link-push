package com.push.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Yan XinYu
 */
@Slf4j
public class ThreadUtils {

    /**
     * 线程休眠
     * @param millisTime 毫秒值
     */
    public static void sleepTime(long millisTime){
        try {
            TimeUnit.MILLISECONDS.sleep(millisTime);
        } catch (Exception e){
            log.error("thread sleep error.",e);
        }
    }
}
