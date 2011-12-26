package com.imaginea.brightest.client;

import java.io.File;

import com.imaginea.brightest.client.ui.SeleniumHelper;

/**
 * Need to revisit this, to tie it up with java.util.prefs.*
 * @author apurba
 *
 */
public class Preferences {
    
    Boolean chainGeneratedSteps = false;
    Boolean abortOnValidationFailure = false;
    Boolean seleniumAutoStartServer = false;
    Boolean seleniumKillProcessesByName = false;
    Boolean seleniumAutoTaskCleanup = false;
    
    String initials = null;
    Environment env;
    

    public String getDirectory() {
        return new File("preferences").getAbsolutePath();
    }

    public void setChainGeneratedSteps(String string) {
        chainGeneratedSteps = Boolean.valueOf(string);
    }

    public void setAbortOnValidationFailure(String string) {
        chainGeneratedSteps = Boolean.valueOf(string);
    }

    public void setSeleniumAutoStartServer(String string) {
        chainGeneratedSteps = Boolean.valueOf(string);
    }

    public void setSeleniumKillProcessesByName(String string) {
        chainGeneratedSteps = Boolean.valueOf(string);
    }

    public void setSeleniumAutoTaskCleanup(String string) {
        chainGeneratedSteps = Boolean.valueOf(string);
    }

    public Environment getEnvironment() {
        return env;
    }

    public void write() {
        //do nothing
    }

    public void setInitials(String text) {
        this.initials = text;
    }

    public void setEnvironment(String selectedItem) {
        
    }

    public void setSeleniumBrowser(String selectedItem) {
        //do nothing
    }

    public boolean isSeleniumAutoTaskCleanup() {
        return seleniumAutoTaskCleanup;
    }

    public boolean isSeleniumKillProcessesByName() {
        return seleniumKillProcessesByName;
    }

    public boolean isSeleniumAutoStartServer() {
        return seleniumAutoStartServer;
    }

    public boolean getAbortOnValidationFailure() {
        return abortOnValidationFailure;
    }

    public boolean getChainGeneratedSteps() {
        return chainGeneratedSteps;
    }

    public String getInitials() {
        return initials;
    }

    public String getSeleniumBrowser() {
        return SeleniumHelper.BROWSER_FIREFOX;
    }

}
