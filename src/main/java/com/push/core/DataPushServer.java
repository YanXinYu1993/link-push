package com.push.core;

import com.push.entity.CommitLog;
import com.push.entity.PushData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Yan XinYu
 */
@Slf4j
public class DataPushServer {
    private final Ihandler eventHandler;
    private final CommitManager commitManager;
    private DisruptorMq queue;
    private MqEventHandler mqEventHandler;
    private ThreadPoolExecutor executor;
    private volatile boolean running = false;

    public DataPushServer(Ihandler eventHandler, CommitManager commitManager) {
        this.eventHandler = eventHandler;
        this.commitManager = commitManager;
    }

    public synchronized void start() throws InterruptedException {
        if(!this.running){
            this.executor = new ThreadPoolExecutor(1,1,60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1),
                    new BasicThreadFactory.Builder().namingPattern("readThread-%d").build());
            this.mqEventHandler = new MqEventHandler(eventHandler,commitManager);
            this.queue = new DisruptorMq(mqEventHandler);
            this.queue.start();
            this.executor.execute(this::run);
            this.running = true;
        }
    }

    public void run(){
        try{
            while (running){
                CommitLog commitLog = commitManager.getAndLock();
                if(commitLog == null){
                    ThreadUtils.sleep(Duration.ofMillis(200));
                    continue;
                }
                List<PushData> dataList = eventHandler.getDataList(commitLog);
                if(dataList == null || dataList.isEmpty()){
                    commitManager.del(commitLog.getId());
                    continue;
                }
                queue.push(commitLog,dataList);
            }
        } catch (Exception e){
            log.error("reader running error.",e);
        }
    }

    public synchronized void close(){
        running = false;
        queue.close();
        executor.shutdown();
        this.notify();
    }
}
