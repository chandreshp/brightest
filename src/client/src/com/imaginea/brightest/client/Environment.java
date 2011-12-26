package com.imaginea.brightest.client;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private static List<Environment> envs               = new ArrayList<Environment>();
    private static Environment       currentEnvironment = new Environment("Default", "BrighTest");

    private String                   name               = null;
    private String                   appName            = null;

    private Environment(String name, String appName) {
        this.name = name;
        this.appName = appName;
        envs.add(this);
    }

    public static Environment getCurrentEnvironment() {
        return currentEnvironment;
    }

    public String getName() {
        return name;
    }

    public static void setCurrentEnvironment(Environment environment) {
        currentEnvironment = environment;
    }

    public List<Environment> getEnvironments() {
        return envs;
    }

    public String getApplication() {
        return appName;
    }

    public List<String> getApplications() {
        List<String> appNames = new ArrayList<String>();
        for (Environment env : envs) {
            appNames.add(env.getApplication());
        }
        return appNames;
    }

}
