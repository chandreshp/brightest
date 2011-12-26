package com.imaginea.brightest;

import org.openqa.selenium.server.RemoteControlConfiguration;

import com.imaginea.brightest.util.Util;

/**
 * Contains preferences, exposes finer methods for better control
 */
public class ApplicationPreferences {
    private String host;
    private int port;
    private String browser;
    private String url;
    private String testPath;
    private String reportDir;
    private String configurationPath;
    private String reportPath;
    private String dslPath;
    private String timeout;

    public ApplicationPreferences() {
        host = "localhost";
        port = RemoteControlConfiguration.DEFAULT_PORT;
        browser = "*chrome";
        url = "http://www.google.co.in";
        timeout = "10000";
    }

    public String getDslPath() {
        return dslPath;
    }

    public ApplicationPreferences setDslPath(String dslPath) {
        this.dslPath = dslPath;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ApplicationPreferences setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ApplicationPreferences setPort(int port) {
        this.port = port;
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    public ApplicationPreferences setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ApplicationPreferences setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTestPath() {
        return testPath;
    }

    public ApplicationPreferences setTestPath(String testPath) {
        this.testPath = testPath;
        return this;
    }

    public String getReportDir() {
        return reportDir;
    }

    public ApplicationPreferences setReportDir(String reportDir) {
        this.reportDir = reportDir;
        return this;
    }

    public String getConfigurationPath() {
        return configurationPath;
    }

    public ApplicationPreferences setConfigurationPath(String configurationPath) {
        this.configurationPath = configurationPath;
        return this;
    }

    public String getReportPath() {
        return reportPath;
    }

    public ApplicationPreferences setReportPath(String reportPath) {
        this.reportPath = reportPath;
        return this;
    }

    public void update(ApplicationPreferences preferences) {
        this.host = preferences.host;
        this.port = preferences.port;
        this.browser = preferences.browser;
        this.url = preferences.url;
        this.testPath = preferences.testPath;
        this.reportDir = preferences.reportDir;
        this.configurationPath = preferences.configurationPath;
        this.reportPath = preferences.reportPath;
        this.dslPath = preferences.dslPath;
        this.timeout = preferences.timeout;
    }

    public String toString() {
        return String.format("%s [host: %s, testPath: %s]", this.getClass().getSimpleName(), this.host, this.testPath);
    }

    public String getTimeout() {
        return timeout;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ApplicationPreferences) {
            ApplicationPreferences other = (ApplicationPreferences) obj;
            return (Util.isEqual(this.host, other.host) && Util.isEqual(this.browser, other.browser) && Util.isEqual(this.url, other.url) && Util.isEqual(this.testPath,
                    other.testPath) && Util.isEqual(this.reportDir, other.reportDir) && Util.isEqual(this.configurationPath, other.configurationPath) && Util.isEqual(
                    this.reportPath, other.reportPath) && Util.isEqual(this.dslPath, other.dslPath) && (this.port == other.port) && Util.isEqual(this.timeout, other.timeout));
        } else {
            return false;
        }
    }
}
