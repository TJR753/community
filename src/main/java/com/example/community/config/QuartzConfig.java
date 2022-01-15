package com.example.community.config;

import com.example.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetailFactoryBean postScoreRefreshJob(){
        JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
        jobDetail.setJobClass(PostScoreRefreshJob.class);
        jobDetail.setName("postScoreRefreshJob");
        jobDetail.setGroup("postScoreRefreshJobGroup");
        jobDetail.setDurability(true);
        jobDetail.setRequestsRecovery(true);
        return jobDetail;
    }
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJob){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJob);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("postScoreRefreshTriggerGroup");
        factoryBean.setRepeatInterval(1000*60*5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
