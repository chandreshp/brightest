package com.imaginea.brightest.client.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.imaginea.brightest.client.Client;
import com.imaginea.brightest.client.Environment;
import com.imaginea.brightest.client.Preferences;

/**
 * Watch Dialog
 */
public class WatchDialog extends JDialog {

    private static final long  serialVersionUID = 378196681030717495L;

    static Logger              logger           = Logger.getLogger(WatchDialog.class);

    private final BorderLayout borderLayout1    = new BorderLayout();
    private final JButton      stopButton       = new JButton();
    private final JScrollPane  jScrollPaneLog   = new JScrollPane();
    private final JTextArea    log              = new JTextArea();
    private final JPanel       panel1           = new JPanel();
    private File               watchDir         = null;
    private final StringBuffer logBuf           = new StringBuffer();

    /**
     * Creates a new PicLinkDialog object.
     * 
     * @param frame The frame from which the dialog is called
     * @param title The title of the dialog window
     * @param settings The settings structure
     * @param preferences The user preferences, as read from the preferences.xml file
     * @param watchDir The directory in which to watch for files
     */
    public WatchDialog(Frame frame, String title, Preferences preferences, File watchDir) throws InvalidParameterException {
        super(frame, title, true);
        if (Environment.getCurrentEnvironment() == null) {
            throw new InvalidParameterException("Environment must be set before executing tests");
        }

        setTitle(title);

        // this.preferences = preferences;
        this.watchDir = watchDir;

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Client.getInstance().execute(new DirectoryWatcherDaemon());
    }

    /**
     * Overridden so we can exit when window is closed
     * 
     * @param e User action event
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            stopButton_actionPerformed(null);
        }
    }

    /**
     * Close the dialog on a button event
     * 
     * @param e The event of the user click
     */
    public void stopButton_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        stopButton.setText("Stop");

        getContentPane().add(panel1);

        jScrollPaneLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPaneLog.getViewport().add(log);
        panel1.add(jScrollPaneLog, BorderLayout.CENTER);
        panel1.add(stopButton, BorderLayout.SOUTH);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButton_actionPerformed(e);
            }
        });
    }

    /**
     * DOCUMENT_ME
     */
    public class DirectoryWatcherDaemon implements Runnable {
        private static final String PROCESSED_DIRECTORY = "processed";

        /**
         * Initializes the directory watcher
         */
        public DirectoryWatcherDaemon() {
        }

        /**
         * DOCUMENT ME!
         */
        public void run() {
            log("Watching " + watchDir.getAbsolutePath());
            log("Publishing to " + Environment.getCurrentEnvironment().getName());
            try {
                File processedDir = new File(watchDir, PROCESSED_DIRECTORY);
                if (!processedDir.exists()) {
                    if (!processedDir.mkdir()) {
                        throw new Exception("Could not create processed directory");
                    }

                    log("Created directory " + processedDir.getAbsolutePath());
                }
                while (true) {
                    if (logger.isDebugEnabled()) {
                        log("Checking for files");
                    }

                    File[] files = watchDir.listFiles();

                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].isDirectory()) {
                            log("Processing " + files[i].getName());
                        }
                    }

                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                String stacktrace = "";

                if (e != null) {
                    StringWriter sw = new StringWriter();

                    e.printStackTrace(new PrintWriter(sw));

                    stacktrace = sw.toString();
                    log(stacktrace);
                    log("Terminating watching folder");
                }
            }
        }

        private void log(String msg) {
            logger.info(msg);
            logBuf.append(new Date().toString()).append(": ").append(msg).append('\n');
            log.setText(logBuf.toString());
            log.setCaretPosition(logBuf.length() - 1);
            if (logBuf.length() > 5000) {
                logBuf.delete(0, logBuf.length() - 5000);
            }
        }
    }
}
