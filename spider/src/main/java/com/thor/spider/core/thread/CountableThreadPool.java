package com.thor.spider.core.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br></br>
 * Use {@link ExecutorService} as inner implement. <br></br>
 * New feature: <br></br>
 * 1. Block when thread pool is full to avoid poll many urls without process. <br></br>
 * 2. Count of thread alive for monitor.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
@Slf4j
public class CountableThreadPool {

    private int threadNum;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    private ExecutorService executorService;

    public void execute(final Runnable runnable) {
        waitThreadAliveLessThreadNum(threadNum);

        if (threadNum < threadAlive.incrementAndGet()){
            log.error("threadNum = {}, threadAlive = {}", threadNum, threadAlive.get(), new Exception());
        }

        boolean success = false;
        try {
            executorService.execute(() -> exec(runnable));
            success = true;
        }catch (RejectedExecutionException e){
            log.error("拒绝执行", e);
        }catch (Throwable e){
            log.error("执行异常", e);
        }finally {
            if (!success) {
                notifyComplete();
            }
        }
    }

    public void waitThreadAliveLessThreadNum(int threadNum){
        if (threadAlive.get() >= threadNum) {
            try {
                reentrantLock.lock();
                while (threadAlive.get() >= threadNum) {
                    try {
                        log.info("wait threadNum = {} threadAlive {}", threadNum, threadAlive);
                        condition.await(30000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    private void exec(Runnable runnable){
        try {
            runnable.run();
        } finally {
            notifyComplete();
        }
    }

    private void notifyComplete(){
        try {
            reentrantLock.lock();
            int count = threadAlive.decrementAndGet();
            log.info("任务处理完成, threadAlive {}", count);
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public void shutdown() {
        executorService.shutdown();
    }


}
