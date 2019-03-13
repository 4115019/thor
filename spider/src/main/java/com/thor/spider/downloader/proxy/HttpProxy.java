package com.thor.spider.downloader.proxy;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpHost;

import java.io.Serializable;

/**
 * Created by cw on 15-11-26.
 */
@ToString
@Slf4j
@Data
public class HttpProxy extends Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scheme = "http";

    private String username;

    private String password;

    private String ip;

    private int port;

    private long timeout = 60000;

    private double permitsPerSecond = 10;

    //最终存活时间
    private long deadline;

    //最近检测时间
    private long checkTime = Long.MAX_VALUE;

    private transient RateLimiter rateLimiter;

    public HttpProxy() {
    }

    public HttpProxy(HttpHost httpHost) {
        ip = httpHost.getHostName();
        port = httpHost.getPort();
        scheme = httpHost.getSchemeName();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(ip) || this.deadline <= System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    public boolean checkedJustNow() {
        return System.currentTimeMillis() > checkTime && (timeout / (System.currentTimeMillis() - checkTime) >= 5 || System.currentTimeMillis() - checkTime < 5000) && (System.currentTimeMillis() - checkTime < 600000);
    }

    public boolean needRealm() {
        return StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password);
    }

    public void limit() {
        if (rateLimiter != null) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            rateLimiter.acquire();
            stopWatch.stop();
            log.info("代理等待时间 {} , {} ms", this, stopWatch.getTime());
        }
    }

    @Override
    public String toString() {
        return "HttpProxy{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public String getKey() {
        return String.format("%s:%s", ip, port);
    }


    public boolean equals(HttpProxy proxy) {
        if (proxy == null) return false;
        if (proxy.hashCode() != this.hashCode()) return false;
        if (proxy.getKey().hashCode() != proxy.getKey().hashCode()) return false;
        return proxy.getKey().equals(proxy.getKey());
    }
}
