package com.imaginea.brightest.client.test;

import java.io.File;
import java.util.List;


import com.imaginea.brightest.client.Environment;
import com.imaginea.brightest.client.Preferences;
import com.imaginea.brightest.client.Settings;
import com.imaginea.brightest.client.ui.RootFrame;
import com.imaginea.brightest.client.ui.TestTableModel;

public class Test {

    public Test(Preferences preferences) {
        // TODO Auto-generated constructor stub
    }

    public void updateSkippedStatus(String selectedItem, RootFrame homeFrame) {
        throw new UnsupportedOperationException();
    }

    public void saveResultsToFile(File saveFile, TestTableModel model) {
        throw new UnsupportedOperationException();
    }

    public void initialize(File testFile, RootFrame homeFrame, Environment currentEnvironment) {
        //TODO remove homeFrame
    }

    public void stopExecution() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public List<Step> getSteps() {
        throw new UnsupportedOperationException();
    }

    public String getDescription() {
        throw new UnsupportedOperationException();
    }

    public String getFilename() {
        throw new UnsupportedOperationException();
    }

    public boolean isExecutionComplete() {
        throw new UnsupportedOperationException();
    }

    public int calculatePercentageComplete() {
        throw new UnsupportedOperationException();
    }

    public boolean isTestStarted() {
        throw new UnsupportedOperationException();
    }

    public void execute(RootFrame homeFrame, String selectedItem, Environment currentEnvironment, Settings currentSetting, boolean b, int howMany) {
        throw new UnsupportedOperationException();
    }

    public String processTextForTagReferences(String description) {
        throw new UnsupportedOperationException();
    }

}
