package com.push.core;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.push.core.event.DataEvent;
import com.push.core.mainDto.TransferDto;
import com.push.entity.CommitLog;
import com.push.entity.PushData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * @description: 内存队列
 * @author: Yan XinYu
 */
@Slf4j
public class DisruptorMq {

    private final Disruptor<DataEvent> disruptor = new Disruptor<>(
            DataEvent::new,
            1024 * 1024,
            Executors.defaultThreadFactory(),
            ProducerType.SINGLE,
            new YieldingWaitStrategy()
    );

    public final MqEventHandler mqEventHandler;
    private RingBuffer<DataEvent> ringBuffer;

    public DisruptorMq(MqEventHandler mqEventHandler) {
        this.mqEventHandler = mqEventHandler;
    }

    public void start(){
        disruptor.handleEventsWith(mqEventHandler);
        disruptor.start();

        this.ringBuffer = disruptor.getRingBuffer();
    }

    public void close(){
        disruptor.shutdown();
    }

    public void push(CommitLog commitLog,List<PushData> list){
        long sequence = ringBuffer.next();
        try {
            DataEvent dataEvent = ringBuffer.get(sequence);
            dataEvent.setCommitLog(commitLog);
            dataEvent.setList(list);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

}
