package com.push;

import com.push.core.CommitManager;
import com.push.core.DataPushServer;
import com.push.core.Ihandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Yan XinYu
 */
@Configuration
public class DataPushConfiguration {

    @Bean
    public CommitManager commitManager(){
        return new CommitManager();
    }

    @Bean
    public DataPushServer dataPushServer(Ihandler eventHandler, CommitManager commitManager){
        return new DataPushServer(eventHandler, commitManager);
    }
}
