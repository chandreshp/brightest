package com.imaginea.brightest.client.test;

import com.imaginea.brightest.client.ui.RootFrame;

public class Step {

    public static final String SUCCESS = null;
    public static final String FAILURE = null;

    public Object getStepNumber() {
        throw new UnsupportedOperationException();
    }

    public String getDescription() {
        throw new UnsupportedOperationException();
    }
    
    public Test getTest() {
        throw new UnsupportedOperationException();
    }
    
    public void setStatus(String success, RootFrame owner) {
        throw new UnsupportedOperationException();
    }
    
    public String getExpectedResult() {
        throw new UnsupportedOperationException();
    }

    public String getNavigation() {
        throw new UnsupportedOperationException();
    }

    public String getDependentStepName() {
        throw new UnsupportedOperationException();
    }

    public String getDataWorksheetString() {
        throw new UnsupportedOperationException();
    }

    public String getStatus() {
        throw new UnsupportedOperationException();
    }

    public String getMsgId() {
        throw new UnsupportedOperationException();
    }

    public String getNotes() {
        throw new UnsupportedOperationException();
    }

    public boolean isValidated() {
        throw new UnsupportedOperationException();
    }

    public String getType() {
        return getClass().getSimpleName();
    }

}
