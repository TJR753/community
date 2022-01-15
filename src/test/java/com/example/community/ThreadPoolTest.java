package com.example.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    public static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);
    /**
     * JDK普通线程池
     */
    ExecutorService pool = Executors.newFixedThreadPool(5);
    /**
     * JDK定时线程池
     */
    ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);

    public void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pool() {
        Runnable pool_execute = new Runnable() {
            @Override
            public void run() {
                logger.info("pool execute");
            }
        };
        pool.submit(pool_execute);
        sleep(10000);
    }

    @Test
    public void scheduledPool() {
        Runnable pool_execute = new Runnable() {
            @Override
            public void run() {
                logger.info("scheduledPool execute");
            }
        };
        scheduledPool.scheduleAtFixedRate(pool_execute, 1000, 1000, TimeUnit.MILLISECONDS);
        sleep(1000 * 10);
    }

    /**
     * spring普通线程池
     */
    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    public void taskExecutor() {
        Runnable pool_execute = new Runnable() {
            @Override
            public void run() {
                logger.info("scheduledPool execute");
            }
        };
        taskExecutor.execute(pool_execute);
        sleep(1000);
    }

    /**
     * spring定时线程池
     */
    @Autowired
    private TaskScheduler taskScheduler;

    @Test
    public void taskScheduler() {
        Runnable pool_execute = new Runnable() {
            @Override
            public void run() {
                logger.info("taskScheduler execute");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 1000);
        taskScheduler.scheduleAtFixedRate(pool_execute,startTime,1000);
        sleep(1000*10);
    }
}
