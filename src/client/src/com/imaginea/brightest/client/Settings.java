package com.imaginea.brightest.client;

//TODO refactor to contain Environment
public class Settings {
    
    private static Settings instance = new Settings();

    public static Settings getInstance() {
        return instance;
    }

    public Settings getCurrentSetting(Environment environments) {
        throw new UnsupportedOperationException();
    }

    public String getEnvironmentDefUrl() {
        throw new UnsupportedOperationException();
    }

}
