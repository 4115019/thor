package com.thor.spider.core.pipeline;

import com.google.gson.Gson;
import com.thor.spider.downloader.ResultItems;
import com.thor.spider.downloader.Task;
import com.thor.spider.downloader.Template;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Slf4j
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        log.info("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            log.info(entry.getKey() + ":\t" + entry.getValue());
        }
        for(Template template : resultItems.getTemplates()){
            log.info(new Gson().toJson(template));
        }
    }
}
