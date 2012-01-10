/*
 * Copyright (c) 2011 Imaginea Technologies Private Ltd. 
 * Hyderabad, India
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following condition
 * is met:
 *
 *     + Neither the name of Imaginea, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imaginea.brightest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.execution.PreferenceListener;
import com.imaginea.brightest.format.CSVFormatHandler;
import com.imaginea.brightest.format.FormatHandler;
import com.imaginea.brightest.format.HTMLFormatHandler;
import com.imaginea.brightest.format.XLSFormatHandler;
import com.imaginea.brightest.util.HierarchicalProperties;
import com.imaginea.brightest.util.Util;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Central control for everything we need for executing a test. Has selenium server, client and preferences.
 */
public final class ExecutionContext {
    /**
     * System property used for defining the configuration file path
     */
    public static final String CONFIGURATION_PATH = "brightest.configuration.file";
    private static final Log LOG = LogFactory.getLog(ExecutionContext.class);
    private static final ExecutionContext INSTANCE = new ExecutionContext();
    private final CommandExecutor executor;
    private final ApplicationPreferences preferences;
    private Selenium client;
    private final HierarchicalProperties properties = new HierarchicalProperties();
    private final List<PreferenceListener> preferenceListeners = new ArrayList<PreferenceListener>();
    private final List<FormatHandler> formatHandlers = new ArrayList<FormatHandler>();

    public static ExecutionContext getInstance() {
        return INSTANCE;
    }
    
    public static ExecutionContext getNonStaticInstance() {
        return new ExecutionContext();
    }

    private ExecutionContext() {
        String configurationPath = null;
        if ((configurationPath = System.getProperty(CONFIGURATION_PATH, null)) != null) {
            preferences = new ApplicationPreferences(configurationPath);
        } else {
            preferences = new ApplicationPreferences();
        }
        executor = new CommandExecutor(preferences);
        preferenceListeners.add(executor);
        formatHandlers.add(new XLSFormatHandler());
        formatHandlers.add(new CSVFormatHandler());
        formatHandlers.add(new HTMLFormatHandler());
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public Selenium getSelenium() {
        if (client == null) {
            LOG.debug(String.format("Starting selenium with %s", preferences.toString()));
            WebDriver driver = getDriver(preferences.getCapabilities());
            client = new WebDriverBackedSelenium(driver, preferences.getUrl());
        }
        return client;
    }


    public ExecutionContext setSelenium(Selenium selenium) {
        this.client = selenium;
        return this;
    }

    public boolean stop() {
        if (client != null) {
            client.stop();
        }
        return true;
    }

    public boolean start() {
        return true;
    }


    /**
     * Updates preference with values from the passed param.
     * 
     * @param preferences
     */
    public void updatePreferences(ApplicationPreferences preferences) {
        for (PreferenceListener listener : preferenceListeners) {
            listener.changed(preferences);
        }
        this.preferences.update(preferences);
        initializeProperties();
    }

    public void putValue(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getValue(String key) {
        if (Util.isNotBlank(key)) {
            return properties.getProperty(key);
        } else {
            return null;
        }
    }

    public void registerListener(PreferenceListener listener) {
        preferenceListeners.add(listener);
    }

    public ApplicationPreferences getPreferences() {
        return this.preferences;
    }

    private void initializeProperties() {
        if (Util.isNotBlank(preferences.getConfigurationPath())) {
            String[] configurationFiles = preferences.getConfigurationPath().split(File.pathSeparator);
            for (String configurationFile : configurationFiles) {
                addProperty(configurationFile);
            }
        }
    }

    private void addProperty(String path) {
        if (Util.isNotBlank(path)) {
            try {
                properties.addProperties(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private WebDriver getDriver(DesiredCapabilities capabilities) {
        String browser = capabilities.getBrowserName();
        if (DesiredCapabilities.firefox().getBrowserName().equals(browser)) {
            return new FirefoxDriver();
        } else if (DesiredCapabilities.internetExplorer().getBrowserName().equals(browser)) {
            return new InternetExplorerDriver();
        } else if (DesiredCapabilities.chrome().getBrowserName().equals(browser)) {
            return new ChromeDriver();
        }
        throw new SeleniumException("Unable to determine which driver to use: " + capabilities);
    }

    public List<FormatHandler> getFormatHandlers() {
        return Collections.unmodifiableList(formatHandlers);
    }
}
