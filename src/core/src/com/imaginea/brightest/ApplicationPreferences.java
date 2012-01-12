package com.imaginea.brightest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.server.RemoteControlConfiguration;

import com.imaginea.brightest.util.Util;

/**
 * Contains preferences, exposes finer methods for better control
 */
public class ApplicationPreferences {
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final String DEFAULT_PREFIX = "wd/hub";
    private String host;
    private String port;
    private String browser;
    private String url;
    private String testPath;
    private String reportDir;
    private String configurationPath;
    private String reportPath;
    private String dslPath;
    private String timeout;
    private final String translatePaths = "true";

    public ApplicationPreferences() {
        host = "localhost";
        port = Integer.toString(RemoteControlConfiguration.DEFAULT_PORT);
        browser = "firefox";
        url = "http://www.google.co.in";
        timeout = "10000";
    }

    public ApplicationPreferences(String filePath) {
        this();

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
            configurationPath = filePath;
            // fill in all string properties reflectively
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class) {
                    String providedValue = properties.getProperty(field.getName(), null);
                    if (Util.isNotBlank(providedValue)) {
                        field.set(this, providedValue);
                    }
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("Problems with configuration file path ->" + filePath, e);
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Problems with configuration file parsing ->" + filePath, e);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException("Problems with configuration file parsing ->" + filePath, e);
        }
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setBrowserName(browser);
        capability.setPlatform(Platform.ANY);
        return capability;
    }

    public URL getServerUrl() {
        try {
            return new URL("http://" + this.host + ((getPort() == DEFAULT_HTTP_PORT) ? "" : ":" + this.port) + "/" + DEFAULT_PREFIX);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Server URL issues", e);
        }
    }

    public boolean shouldTranslatePaths() {
        // true if no translate path or if translate path set to true
        return (translatePaths == null || translatePaths.trim().length() == 0) || Boolean.valueOf(translatePaths);
    }

    public String getDslPath() {
        return translateRelativeToAbsolute(dslPath);
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
        return Integer.parseInt(port);
    }

    public ApplicationPreferences setPort(int port) {
        this.port = Integer.toString(port);
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
        return translateRelativeToAbsolute(testPath);
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
        return translateRelativeToAbsolute(reportPath);
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

    private String translateRelativeToAbsolute(String relativePath) {
        String filePath = relativePath;
        if (shouldTranslatePaths()) {
            if (Util.isNotBlank(relativePath)) {
                relativePath = relativePath.trim();
                if (Util.isNotBlank(configurationPath)) {
                    boolean isAbsolutePath = (relativePath.startsWith("/") || relativePath.startsWith(File.separator) || relativePath.contains(":"));
                    if (isAbsolutePath) {
                        filePath = relativePath;
                    } else {
                        String configurationPathParent = new File(configurationPath).getParent();
                        filePath = ((configurationPathParent == null) ? "" : (configurationPathParent + File.separator)) + relativePath;
                    }
                } else {
                    filePath = relativePath;
                }
            }
        }
        return filePath;
    }
}
