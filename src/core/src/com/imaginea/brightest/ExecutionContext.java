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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

import com.imaginea.brightest.execution.CommandExecutor;
import com.imaginea.brightest.execution.PreferenceListener;
import com.imaginea.brightest.util.HierarchicalProperties;
import com.imaginea.brightest.util.Util;
import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;

/**
 * Central control for everything we need for executing a test. Has selenium server, client and preferences.
 */
public final class ExecutionContext {
    private static final Log LOG = LogFactory.getLog(ExecutionContext.class);
    private static final ExecutionContext INSTANCE = new ExecutionContext();
    private final CommandExecutor executor;
    private final ApplicationPreferences preferences;
    private CommandProcessor commandProcessor;
    private Selenium client;
    private SeleniumServer server;
    private final HierarchicalProperties properties = new HierarchicalProperties();
    private final List<PreferenceListener> preferenceListeners = new ArrayList<PreferenceListener>();

    public static ExecutionContext getInstance() {
        return INSTANCE;
    }
    
    public static ExecutionContext getNonStaticInstance() {
        return new ExecutionContext();
    }

    protected ExecutionContext() {
        preferences = new ApplicationPreferences();
        executor = new CommandExecutor(preferences);
        preferenceListeners.add(executor);
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public Selenium getSelenium() {
        if (client == null) {
            commandProcessor = new HttpCommandProcessor(preferences.getHost(), preferences.getPort(), preferences.getBrowser(), preferences.getUrl());
            client = new DefaultSelenium(commandProcessor);
        }
        return client;
    }

    /**
     * is idempotent, if a server has been already started does nothing, for a stopped server restarts
     */
    public void startServer() {
        if (server == null) {
            try {
                server = new SeleniumServer();
                server.start();
                LOG.debug(String.format("Started server with details [%s %s]", preferences.getHost(), preferences.getPort()));
            } catch (Exception e) {
                throw new ServerStartException("Server could not be started");
            }
        }
    }

    /**
     * Idempotent, on a stopped server is a no op.
     */
    public void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
            LOG.debug("Stopped server");
        }
    }

    /**
     * Updates preference with values from the passed param.
     * 
     * @param preferences
     */
    public void updatePreferences(ApplicationPreferences preferences) {
        this.preferences.update(preferences);
        for (PreferenceListener listener : preferenceListeners) {
            listener.changed(this.preferences);
        }
        initializeProperties();
    }

    public void startClient() {
        getSelenium().start();
        client.windowMaximize();
        LOG.debug("Started client");
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

    private static class ServerStartException extends RuntimeException {
        private static final long serialVersionUID = -4232597188829218581L;

        public ServerStartException(String message) {
            super(message);
        }
    }
}
