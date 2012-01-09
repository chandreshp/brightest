package com.imaginea.brightest;

/**
 * Configuration exception used to communication problems with configuring the runner. For example port not set or any
 * other such issue.
 */
public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 8630949774629203042L;

    public ConfigurationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
