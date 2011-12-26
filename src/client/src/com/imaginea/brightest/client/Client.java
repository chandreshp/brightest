/*
 * * Copyright (c) 2010 Tervela Inc, All rights reserved.
 */
package com.imaginea.brightest.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;


import com.imaginea.brightest.client.ui.RootFrame;

/**
 * Entry point for the brightest client. This class will delegate to the proper implementation depending on the args.
 * 
 * @author apurba
 */
public class Client {

    private static final Client   INSTANCE = new Client();

    public static ExecutorService execSvc  = Executors.newFixedThreadPool(5);

    private Client() {

    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) throws Exception {
        Client.getInstance().start();
    }

    public void execute(Runnable runnable) {
        execSvc.execute(runnable);
    }

    public String getVersion() {
        return "0.2";
    }
    
    private void start() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        RootFrame mainUi = new RootFrame(new Preferences(), Settings.getInstance(), Environment.getCurrentEnvironment(), null);
        mainUi.setVisible(true);
        
    }
}
