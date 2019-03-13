package com.thor.spider.core.scheduler;

import com.thor.spider.core.scheduler.component.DuplicateRemover;
import com.thor.spider.core.scheduler.component.HashSetDuplicateRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thor.spider.downloader.Request;
import com.thor.spider.downloader.Task;

/**
 * Remove duplicate urls and only push urls which are not duplicate.<br></br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(Request request, Task task){
        logger.trace("get a candidate url {}", request.getUrl());
        if (!duplicatedRemover.isDuplicate(request, task)) {
            logger.debug("push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request, task);
        }
    }

    protected void pushWhenNoDuplicate(Request request, Task task){

    }
}
