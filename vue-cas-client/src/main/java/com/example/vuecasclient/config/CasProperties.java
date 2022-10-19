package com.example.vuecasclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="cas")
public class CasProperties {

    private Boolean enable;

    private String clientHostUrl;

    private String serverUrlPrefix;

    private String serverLoginUrl;

    private String ignorePattern;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getClientHostUrl() {
        return clientHostUrl;
    }

    public void setClientHostUrl(String clientHostUrl) {
        this.clientHostUrl = clientHostUrl;
    }

    public String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    public void setServerUrlPrefix(String serverUrlPrefix) {
        this.serverUrlPrefix = serverUrlPrefix;
    }

    public String getServerLoginUrl() {
        return serverLoginUrl;
    }

    public void setServerLoginUrl(String serverLoginUrl) {
        this.serverLoginUrl = serverLoginUrl;
    }

    public String getIgnorePattern() {
        return ignorePattern;
    }

    public void setIgnorePattern(String ignorePattern) {
        this.ignorePattern = ignorePattern;
    }
}
