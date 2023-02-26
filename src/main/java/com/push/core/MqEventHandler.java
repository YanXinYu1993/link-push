package com.push.core;

import com.alibaba.fastjson2.JSON;
import com.lmax.disruptor.EventHandler;
import com.push.core.event.DataEvent;
import com.push.core.mainDto.CommitLogDto;
import com.push.core.mainDto.PushDto;
import com.push.core.mainDto.TransferDto;
import com.push.entity.PushData;
import com.push.utils.OkhttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.Timeout;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @description: 事件处理
 * @author: Yan XinYu
 */
@Slf4j
public class MqEventHandler implements EventHandler<DataEvent> {

    private final Ihandler ihandler;
    private final CommitManager commitManager;
    private final ThreadPoolExecutor executor;

    private static final int CORE_THREAD_NUM = Runtime.getRuntime().availableProcessors();

    public MqEventHandler(Ihandler ihandler,CommitManager commitManager) {
        this.ihandler = ihandler;
        this.commitManager = commitManager;
        this.executor = new ThreadPoolExecutor(CORE_THREAD_NUM, CORE_THREAD_NUM * 8,60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new BasicThreadFactory.Builder().namingPattern("readThread-%d").build());
        this.executor.prestartAllCoreThreads();
    }

    @Override
    public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("event: {}, sequence: {}, endOfBatch: {}", event, sequence, endOfBatch);
        CompletableFuture
                // 任务处理
                .supplyAsync(()->{
                            List<TransferDto> dto = ihandler.jobHandler(event.getList());
                            return new CommitLogDto(event.getCommitLog(),dto);
                        },executor)
                // 执行推送功能,将结果保存
                .thenApply((commitLogDto)->{
                    for (TransferDto dto : commitLogDto.getList()) {
                        OkhttpUtils.AsyncPost(dto.getUrl(), JSON.toJSONString(dto.getRecords()), new Callback() {

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                ihandler.onFailure(dto,e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                log.info("response:{}",response);
                                if (response.isSuccessful()) {
                                    ihandler.onSuccess(dto);
                                } else {
                                    ihandler.onFailure(dto,null);
                                }
                            }

                        });
                    }
                    return commitLogDto.getCommitLog();
                })
                // 将数据发送到网络中后，删除 commit_log 记录点。
                .thenAccept((commitLog)->{
                    commitManager.del(commitLog.getId());
                });
    }
}
