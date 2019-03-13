package com.thor.spider.downloader.proxy;

import lombok.Data;

/**
 * Created by huangpin on 17/5/22.
 */
@Data
public abstract class Product {
    /**
     * 是否需要check
     */
    private boolean check = true;

    /**
     * 该产品不能被释放
     */
    private boolean forever = false;

    /**
     * 该产品不能被加入黑名单
     */
    private boolean white = false;

    /**
     * ip提供商
     */
    private String provider;

    /**
     * 创建时间
     */
    private long createTime = System.currentTimeMillis();

    /**
     * 获取产品唯一标识
     * @return
     */
    public abstract String getKey();

    public abstract boolean validate();
}
