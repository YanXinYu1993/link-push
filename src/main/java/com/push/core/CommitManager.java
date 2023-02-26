package com.push.core;

import com.alibaba.fastjson2.JSON;
import com.push.entity.CommitLog;
import com.push.mapper.CommitLogMapper;
import com.push.mapper.PushDataMapper;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @author: Yan XinYu
 */
public class CommitManager {

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CommitLogMapper commitLogMapper;
    @Autowired
    private PushDataMapper pushDataMapper;
    private final String lockKey = UUID.randomUUID().toString().replaceAll("-","");
    private final String indexCacheKey = "push.commitlog.index";
    private final String commitLogQueue = "push.commitlog.queue";

    public CommitLog getAndLock(){
        RLock lock = redisson.getLock(lockKey);
        lock.lock();
        try{
            CommitLog commitLog = get();
            if(commitLog != null){
                commitLogMapper.updateStatus(commitLog.getId(),true);
            }
            return commitLog;
        } finally {
            lock.unlock();
        }
    }
    private CommitLog get(){
            // 查redis缓存
            String commitLogJson = stringRedisTemplate.opsForList().leftPop(commitLogQueue);
            if(commitLogJson == null || commitLogJson.isEmpty()){
                // 查数据库
                List<CommitLog> commitLogs = commitLogMapper.queryALl();
                if(CollectionUtils.isEmpty(commitLogs)){
                    // 查流水表，从而创建commitlog
                    List<CommitLog> newCommitLogs = createCommitLogs();
                    if(newCommitLogs != null && !newCommitLogs.isEmpty()){
                        if(newCommitLogs.size() == 1){
                            return newCommitLogs.get(0);
                        } else {
                            // redis 事务支持，保证原子操作
                            stringRedisTemplate.execute(new SessionCallback<Object>() {
                                @Override
                                public Object execute(RedisOperations operations) throws DataAccessException {
                                    operations.multi();
                                    ListOperations listOperations = operations.opsForList();
                                    for (CommitLog item : newCommitLogs) {
                                        listOperations.rightPush(commitLogQueue,JSON.toJSONString(item));
                                    }
                                    return operations.exec();
                                }
                            });
                        }
                    }
                } else {
                    // redis 事务支持，保证原子操作
                    stringRedisTemplate.execute(new SessionCallback<Object>() {
                        @Override
                        public Object execute(RedisOperations operations) throws DataAccessException {
                            operations.multi();
                            ListOperations listOperations = operations.opsForList();
                            for (CommitLog item : commitLogs) {
                                listOperations.rightPush(commitLogQueue,JSON.toJSONString(item));
                            }
                            return operations.exec();
                        }
                    });
                }
                String s = stringRedisTemplate.opsForList().leftPop(commitLogQueue);
                return JSON.parseObject(s, CommitLog.class);
            } else {
                return JSON.parseObject(commitLogJson, CommitLog.class);
            }

    }

    public List<CommitLog> createCommitLogs(){
        Long lastId = getCurrentIndex();
        Long maxId = pushDataMapper.maxId();

        // 没有数据就不推。
        if(lastId.equals(maxId)){
            return null;
        }

        Long step = 100L;
        Long stepSize = 1000L;
        Long count = (maxId / stepSize);

        // 最大生成100个commitlogs
        if(count > step){
            count = step;
        }

        // 计算最终ID位置
        Long finalIndex = (count * stepSize) > maxId ? maxId : (count * stepSize);

        List<CommitLog> list;
        if( count > 0 ){
            list = new ArrayList<>(count.intValue());
            for (int i = 0; i < count+1; i++) {
                Long startIndex = lastId + stepSize * i;
                Long endIndex;
                if (i == count) {
                    endIndex = maxId;
                } else {
                    endIndex = lastId + stepSize * i;
                }
                CommitLog commitLog = new CommitLog();
                commitLog.setStartIndex(startIndex);
                commitLog.setEndIndex(endIndex);
                commitLog.setStatus(false);
                commitLog.setCreateTime(LocalDateTime.now());
                list.add(commitLog);
            }
            commitLogMapper.saveBatch(list);
        } else {
            list = new ArrayList<>(1);
            CommitLog commitLog = new CommitLog();
            commitLog.setStartIndex(lastId);
            commitLog.setEndIndex(maxId);
            commitLog.setStatus(false);
            commitLog.setCreateTime(LocalDateTime.now());
            list.add(commitLog);
            commitLogMapper.insert(commitLog);
        }
        setIndexOfValue(finalIndex);
        return list;
    }

    public Long getCurrentIndex(){
        String s = stringRedisTemplate.opsForValue().get(indexCacheKey);
        if(s == null || s.isEmpty()){
            return pushDataMapper.minId();
        }
        return Long.valueOf(s);
    }

    public void setIndexOfValue(Long value){
        stringRedisTemplate.opsForValue().set(indexCacheKey,String.valueOf(value));
    }

    public void del(long logId){
        commitLogMapper.deleteById(logId);
    }

}
