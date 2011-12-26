package com.imaginea.brightest.client.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.imaginea.brightest.client.Client;
import com.imaginea.brightest.client.Environment;
import com.imaginea.brightest.client.Preferences;
import com.imaginea.brightest.client.Settings;
import com.imaginea.brightest.client.UIUtil;
import com.imaginea.brightest.client.test.Test;

/**
 * The main UI frame class for the presentation layer of BrighTest
 */
public class RootFrame extends JFrame {
    private static final long serialVersionUID = -6172657014993966598L;
    private static final Logger logger = Logger.getLogger(RootFrame.class);
    private static final BorderLayout borderLayout1 = new BorderLayout();
    private static final BorderLayout borderLayout2 = new BorderLayout();
    private static final BorderLayout progressLayout = new BorderLayout();
    private static final GridBagLayout gridBagLayout1 = new GridBagLayout();
    private static final JButton jCloseButton = new JButton();
    private static final JButton jHelpButton = new JButton();
    private static final JButton jOpenButton = new JButton();
    private static final JButton jRunButton = new JButton();
    private static final JButton jSaveButton = new JButton();
    private static final JButton jViewXMLButton = new JButton();
    private static final JComboBox startStep = new JComboBox();
    private static final JSpinner repetitions = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 10));
    private static final JLabel jLabel1 = new JLabel();
    private static final JLabel jLabel2 = new JLabel();
    private static final JLabel jLabel4 = new JLabel();
    private static final JLabel progressLabel = new JLabel();
    private static final JLabel statusBar = new JLabel();
    private static final JMenu jMenuFile = new JMenu();
    private static final JMenu jMenuDebug = new JMenu();
    private static final JMenu jMenuHelp = new JMenu();
    private static final JMenuBar jMenuBar1 = new JMenuBar();
    private static final JMenuItem jMenuFileClose = new JMenuItem();
    private static final JMenuItem jMenuFileExit = new JMenuItem();
    private static final JMenuItem jMenuFileOpen = new JMenuItem();
    private static final JMenuItem jMenuFileWatch = new JMenuItem();
    private static final JMenuItem jMenuFilePreferences = new JMenuItem();
    private static final JMenuItem jMenuFileRun = new JMenuItem();
    private static final JMenuItem jMenuFileSave = new JMenuItem();
    private static final JMenuItem jMenuDebugDebugMode = new JMenuItem();
    private static final JMenuItem jMenuHelpAbout = new JMenuItem();
    private static final JMenuItem jMenuHelpHelp = new JMenuItem();
    private static final JPanel ProgressPanel = new JPanel();
    private static final JPanel jPanel1 = new JPanel();
    private static final JPanel jPanel2 = new JPanel();
    private static final JScrollPane jScrollTestTable = new JScrollPane();
    private static final JTable testTable = new JTable();
    private static final JTextArea description = new JTextArea();
    private static final JTextArea name = new JTextArea();
    private static final JToolBar jToolBar = new JToolBar();
    protected JFileChooser openFileChooser = null;
    protected JFileChooser saveFileChooser = null;
    private Environment environments = null;
    private ImageIcon closeImage;
    private ImageIcon helpImage;
    private ImageIcon openImage;
    private ImageIcon runImage;
    private ImageIcon saveImage;
    private ImageIcon viewXMLImage;
    private JPanel contentPane;
    private Preferences preferences = null;
    private Test test = null;
    private boolean progresBarStopped = false;
    private boolean progressBarStarted = false;

    /**
     * Creates a new Root frame
     * 
     * @param preferences The preferences as read from the preferences.xml file
     * @param settings The settings as read from the settings.xml file
     * @param environments The environment list as read from the environment URL
     * @param testFile The workbook containing the test to open automatically, or null if no file should be opened
     *            automatically
     * @throws FileNotFoundException Thrown if the file specified by the testFile cannot be found.
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public RootFrame(Preferences preferences, Settings settings, Environment environments, String testFile) throws Exception {
        this.preferences = preferences;
        this.environments = environments;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            logger.error("An error occurred during intialization", e);
            e.printStackTrace();
            System.exit(1);
        }
        if ((environments != null) && (testFile != null)) {
            openAction(testFile);
        }
    }

    /**
     * Inform the table model that the data has changed so that the table will be redrawn
     * 
     * @param stepNumber The step whose status is updated
     */
    public void dirtyTableStepStatus(String stepNumber) {
        ((TestTableModel) testTable.getModel()).dirtyStatusValue(testTable, stepNumber);
    }

    /**
     * Diplay the alert dialog
     * 
     * @param e An exception that was thrown
     */
    public static void Alert(Throwable e) {
        showAlert(null, e);
    }

    /**
     * Diplay the alert dialog
     * 
     * @param msg The message to display in the dialog box
     * @param e An exception that was thrown
     */
    public static void showAlert(String msg, Throwable e) {
        StringWriter sw = new StringWriter();
        String stacktrace = "";
        if (e != null) {
            e.printStackTrace(new PrintWriter(sw));
            stacktrace = sw.toString();
        }
        AlertDialog alert = new AlertDialog(null, ((msg == null) ? "" : (msg + "\n")) + stacktrace);
        alert.setSize(650, 480);
        UIUtil.centerWindow(alert);
        alert.setVisible(true);
    }

    /**
     * Update the status bar
     */
    public void updateProgess() {
        if (test == null) {
            return;
        }
        if (!progressBarStarted && test.isTestStarted()) {
            progressBarStarted = true;
        }
        int percentComplete = test.calculatePercentageComplete();
        if (progressBarStarted && (percentComplete == 0)) {
            percentComplete = 1;
        }
        // AN: Not so happy about progress bar going missing
        // progressBar.setPercent(percentComplete);
        if (test.isExecutionComplete() && !progresBarStopped) {
            // progressBar.stop();
            progresBarStopped = true;
        }
    }

    /**
     * Update the text in the status bar at the bottom of the page. If no test is opened, the status bar will be blank.
     * Otherwise, the full path name of the test workbook file will be displayed. If the appendedText parameter is
     * specified, that will also be shown in parenthesis following the filename
     * 
     * @param appendedText Text to be shown following the filename in the status bar
     */
    public void updateStatusBar(String appendedText) {
        if (test == null) {
            statusBar.setText(" ");
        } else {
            String text = test.getFilename();
            if (appendedText != null) {
                text = text + " (" + appendedText + ")";
            }
            statusBar.setText(text);
        }
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
            jMenuFileExit_actionPerformed(null);
        }
    }

    private void setTest(Test test) {
        this.test = test;
        startStep.removeAllItems();
        if (test == null) {
            name.setText("");
            description.setText("");
            jMenuFileRun.setEnabled(false);
            jMenuFileClose.setEnabled(false);
            jMenuFileWatch.setEnabled(true);
            jMenuFileOpen.setEnabled(openFileChooser != null);
            jRunButton.setEnabled(false);
            jViewXMLButton.setEnabled(false);
            jOpenButton.setEnabled(openFileChooser != null);
            jCloseButton.setEnabled(false);
            jMenuFileSave.setEnabled(false);
            jSaveButton.setEnabled(false);
            updateStatusBar(null);
            setTitle("BrighTest");
        } else {
            name.setText(test.getName());
            description.setText(test.getDescription());
            jMenuFileRun.setEnabled(true);
            jViewXMLButton.setEnabled(true);
            jMenuFileClose.setEnabled(true);
            jMenuFileWatch.setEnabled(false);
            jMenuFileOpen.setEnabled(false);
            jRunButton.setEnabled(true);
            jOpenButton.setEnabled(false);
            jCloseButton.setEnabled(true);
            jMenuFileSave.setEnabled(true);
            jSaveButton.setEnabled(true);
            progresBarStopped = false;
            progressBarStarted = false;
            updateProgess();
            if (Environment.getCurrentEnvironment() != null && Environment.getCurrentEnvironment().getName() != null) {
                setTitle("BrighTest - " + test.getName() + " @ " + Environment.getCurrentEnvironment().getName());
            } else {
                setTitle("BrighTest - " + test.getName());
            }
            for (int i = 0; i < test.getSteps().size(); i++) {
                startStep.addItem((test.getSteps().get(i)).getStepNumber());
            }
        }
        testTable.setModel(new TestTableModel(test));
        ((TestTableModel) testTable.getModel()).formatTable(testTable);
        dirtyTable();
    }

    private void closeAction() {
        test.stopExecution();
        setTest(null);
    }

    /**
     * Inform the table model that the data has changed so that the table will be redrawn
     */
    private void dirtyTable() {
        ((TestTableModel) testTable.getModel()).dirtyTable();
    }

    private void jCloseButton_actionPerformed(ActionEvent e) {
        closeAction();
    }

    /**
     * Process help button clicked
     * 
     * @param e User action event
     */
    private void jHelpButton_actionPerformed(ActionEvent e) {
        showAbout();
    }

    private void jMenuFileClose_actionPerformed(ActionEvent e) {
        closeAction();
    }

    /**
     * File | Exit action performed
     * 
     * @param e User action event
     */
    private void jMenuFileExit_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void jMenuFileWatch_actionPerformed(ActionEvent e) {
        JFileChooser watchFolderChooser = new JFileChooser();
        watchFolderChooser.setDialogTitle("Select Folder to Watch");
        watchFolderChooser.setApproveButtonText("Watch");
        watchFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (watchFolderChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File watchDirectory = watchFolderChooser.getSelectedFile();
            if (!watchDirectory.isDirectory()) {
                showAlert("Internal Error: Selected directory should have been a directory", new Exception("Internal Error: Selected directory should have been a directory"));
            }
            try {
                JDialog watchDialog = new WatchDialog(null, "Watch Folder", preferences, watchDirectory);
                watchDialog.setSize(800, 600);
                UIUtil.centerWindow(watchDialog);
                watchDialog.setVisible(true);
                this.setVisible(false);
                this.dispose();
            } catch (Exception ex) {
                showAlert("Error", ex);
            }
        }
    }

    private void jMenuFileOpen_actionPerformed(ActionEvent e) {
        try {
            openAction(null);
        } catch (FileNotFoundException ex) {
            // do nothing
        }
    }

    private void jMenuFilePreferences_actionPerformed(ActionEvent e) {
        JDialog prefDialog = new PreferencesDialog(this, preferences, environments);
        prefDialog.setSize(470, 320);
        UIUtil.centerWindow(prefDialog);
        prefDialog.setVisible(true);
    }

    /**
     * DOCUMENT ME!
     */
    public void updateWindowTitle() {
        setTest(test); // call this to force title to change if they selected a
        // new environment
    }

    private void jMenuFileRun_actionPerformed(ActionEvent e) {
        runTestAction();
    }

    private void jMenuFileSave_actionPerformed(ActionEvent e) {
        saveAction();
    }

    /**
     * Help | About action performed
     * 
     * @param e User action event
     */
    private void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        showAbout();
    }

    private void jMenuDebugDebugMode_actionPerformed(ActionEvent e) {
        logger.error("Now in debug mode");
    }

    private void jOpenButton_actionPerformed(ActionEvent e) {
        try {
            openAction(null);
        } catch (FileNotFoundException ex) {
            // do nothing
        }
    }

    private void jRunButton_actionPerformed(ActionEvent e) {
        runTestAction();
    }

    private void jSaveButton_actionPerformed(ActionEvent e) {
        saveAction();
    }

    // Component initialization
    private void jbInit() throws Exception {
        openImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("openFile.gif"));
        closeImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("closeFile.gif"));
        helpImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("help.gif"));
        runImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("run.gif"));
        saveImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("saveFile.gif"));
        // setIconImage(Toolkit.getDefaultToolkit().createImage(HomeFrame.class.
        // getResource("[Your
        // Icon]")));
        ProgressPanel.setBorder(BorderFactory.createEtchedBorder());
        ProgressPanel.setLayout(progressLayout);
        progressLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        progressLabel.setText("Execution Progress:");
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(921, 590));
        this.setTitle("BrighTest");
        statusBar.setText(" ");
        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");
        jMenuFileExit.setMnemonic('X');
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileExit_actionPerformed(e);
            }
        });
        jMenuDebug.setMnemonic('D');
        jMenuDebug.setText("Debug");
        jMenuDebugDebugMode.setMnemonic('M');
        jMenuDebugDebugMode.setText("Set Debug Mode");
        jMenuHelpHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuDebugDebugMode_actionPerformed(e);
            }
        });
        jMenuHelp.setMnemonic('H');
        jMenuHelp.setText("Help");
        jMenuHelpHelp.setMnemonic('H');
        jMenuHelpHelp.setText("BrighTest Help");
        jMenuHelpHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // jMenuHelpHelp_actionPerformed(e);
            }
        });
        jMenuHelpAbout.setMnemonic('A');
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuHelpAbout_actionPerformed(e);
            }
        });
        jOpenButton.setIcon(openImage);
        jOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jOpenButton_actionPerformed(e);
            }
        });
        jOpenButton.setToolTipText("Open File");
        jCloseButton.setIcon(closeImage);
        jCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jCloseButton_actionPerformed(e);
            }
        });
        jCloseButton.setToolTipText("Close Test");
        jRunButton.setIcon(runImage);
        jHelpButton.setIcon(helpImage);
        jHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jHelpButton_actionPerformed(e);
            }
        });
        jHelpButton.setToolTipText("Help");
        jMenuFilePreferences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFilePreferences_actionPerformed(e);
            }
        });
        jMenuFileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileOpen_actionPerformed(e);
            }
        });
        jMenuFileClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileClose_actionPerformed(e);
            }
        });
        jMenuFileSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileSave_actionPerformed(e);
            }
        });
        jMenuFileRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileRun_actionPerformed(e);
            }
        });
        startStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startStep_actionPerformed(e);
            }
        });
        jScrollTestTable.addComponentListener(new WindowResizedListener());
        jMenuFileOpen.setActionCommand("Open");
        jMenuFileOpen.setMnemonic('O');
        jMenuFileOpen.setText("Open");
        jPanel1.setLayout(borderLayout2);
        jPanel2.setBorder(BorderFactory.createEtchedBorder());
        jPanel2.setLayout(gridBagLayout1);
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12));
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText("Name:");
        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12));
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("Description:");
        name.setFont(new java.awt.Font("SansSerif", 0, 12));
        name.setDisabledTextColor(Color.white);
        name.setEditable(false);
        description.setFont(new java.awt.Font("SansSerif", 0, 12));
        description.setMinimumSize(new Dimension(70, 35));
        description.setPreferredSize(new Dimension(200, 17));
        description.setEditable(false);
        description.setText("jTextArea1");
        jMenuFilePreferences.setMnemonic('P');
        jMenuFilePreferences.setText("Properties");
        jRunButton.setToolTipText("Run Test");
        jRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jRunButton_actionPerformed(e);
            }
        });
        jMenuFileRun.setMnemonic('R');
        jMenuFileRun.setText("Run");
        jMenuFileClose.setMnemonic('C');
        jMenuFileClose.setText("Close");
        jMenuFileWatch.setMnemonic('W');
        jMenuFileWatch.setText("Watch Folder");
        jMenuFileWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jMenuFileWatch_actionPerformed(e);
            }
        });
        jMenuFileSave.setMnemonic('S');
        jMenuFileSave.setText("Save");
        jSaveButton.setToolTipText("Save Test Results");
        jSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jSaveButton_actionPerformed(e);
            }
        });
        jSaveButton.setIcon(saveImage);
        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12));
        jLabel4.setText("Repetitions:");
        jToolBar.add(jOpenButton);
        jToolBar.add(jCloseButton);
        jToolBar.add(jSaveButton, null);
        jToolBar.add(jRunButton, null);
        jToolBar.add(jViewXMLButton, null);
        jToolBar.add(jHelpButton);
        jMenuFile.add(jMenuFileOpen);
        jMenuFile.add(jMenuFileRun);
        jMenuFile.add(jMenuFileSave);
        jMenuFile.add(jMenuFileClose);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuFileWatch);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuFilePreferences);
        jMenuFile.add(jMenuFileExit);
        jMenuDebug.add(jMenuDebugDebugMode);
        jMenuHelp.add(jMenuHelpHelp);
        jMenuHelp.add(jMenuHelpAbout);
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuDebug);
        jMenuBar1.add(jMenuHelp);
        this.setJMenuBar(jMenuBar1);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(jPanel2, BorderLayout.NORTH);
        jPanel2.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jPanel2.add(name, new GridBagConstraints(1, 0, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 9, 0, 0), 0, 0));
        jPanel2.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jPanel2.add(description, new GridBagConstraints(1, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 9, 0, 0), 0, 0));
        jPanel2.add(progressLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0));
        jPanel2.add(ProgressPanel, new GridBagConstraints(1, 3, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 9, 0, 0), 0, 0));
        jPanel1.add(jScrollTestTable, BorderLayout.CENTER);
        jScrollTestTable.getViewport().add(testTable, null);
        setTest(null);
        testTable.setForeground(Color.BLACK);
        // viewXMLImage = new ImageIcon(com.imaginea.brightest.client.ui.RootFrame.class.getResource("viewXML.gif"));
        // jViewXMLButton.setIcon(viewXMLImage);
        JFileChooserDaemon filechooserDaemon = new JFileChooserDaemon();
        Client.getInstance().execute(filechooserDaemon);
    }

    private void openAction(String testFileString) throws FileNotFoundException {
        File testFile = null;
        if (testFileString == null) {
            if (openFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                testFile = openFileChooser.getSelectedFile();
            }
        } else {
            testFile = new File(testFileString);
            if (!testFile.exists()) {
                throw new FileNotFoundException(testFile + " not found");
            }
        }
        if (testFile != null) {
            logger.info("Opening file: " + testFile.getAbsolutePath());
            Test tmpTest = new Test(preferences);
            try {
                tmpTest.initialize(testFile, this, Environment.getCurrentEnvironment());
                setTest(tmpTest);
            } catch (Exception e) {
                logger.error("An error occurred while opening workbook", e);
                e.printStackTrace();
                showAlert("Error", e);
                setTest(null);
            }
        }
    }

    private void runTestAction() {
        try {
            if (environments == null) {
                showAlert(
                        "Could not connect to " + Settings.getInstance().getCurrentSetting(environments).getEnvironmentDefUrl() + " to read environment definitions. You will not be able to execute any tests",
                        null);
                return;
            }
            int howMany = (Integer) repetitions.getValue();
            test.execute(this, (String) startStep.getSelectedItem(), Environment.getCurrentEnvironment(), Settings.getInstance().getCurrentSetting(environments), false, howMany);
        } catch (Exception e) {
            logger.error("An error occurred during test execution", e);
            e.printStackTrace();
            showAlert("Error", e);
        }
    }

    private void saveAction() {
        if (saveFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = saveFileChooser.getSelectedFile();
            saveFile = new File(saveFile.getAbsolutePath().replaceAll("\\.\\S*$", "") + ".xls");
            if (saveFile.exists()) {
                ConfirmOverwriteDialog dlg = new ConfirmOverwriteDialog(this, "File exists. Are you sure you want to overwrite " + saveFile.getName() + "?");
                dlg.setSize(300, 150);
                UIUtil.centerWindow(dlg);
                dlg.setVisible(true);
                if (!dlg.isConfirmed()) {
                    return;
                }
            }
            try {
                test.saveResultsToFile(saveFile, (TestTableModel) testTable.getModel());
            } catch (Exception e) {
                showAlert("Error", e);
            }
        }
    }

    private void showAbout() {
        AboutDialog dlg = new AboutDialog(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation(((frmSize.width - dlgSize.width) / 2) + loc.x, ((frmSize.height - dlgSize.height) / 2) + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.setVisible(true);
    }

    private void startStep_actionPerformed(ActionEvent e) {
        if (test == null) {
            return;
        }
        test.updateSkippedStatus((String) startStep.getSelectedItem(), this);
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $author$
     * @version $Revision$
     */
    public class WindowResizedListener implements ComponentListener {
        /*
         * Do nothing
         */
        public void componentHidden(ComponentEvent e) {
            // do nothing
        }

        /*
         * Do nothing
         */
        public void componentMoved(ComponentEvent e) {
            // do nothing
        }

        /*
         * Window has been resized, so update the enclosed table
         */
        public void componentResized(ComponentEvent e) {
            dirtyTable();
        }

        /*
         * Do nothing
         */
        public void componentShown(ComponentEvent e) {
            // do nothing
        }
    }

    private class XLSFileFilter extends FileFilter {
        /**
         * A file filter for the Excel file choosers
         * 
         * @return The description of types of files to open
         */
        @Override
        public String getDescription() {
            return "Excel Files";
        }

        /**
         * Whether this file shoudl be visible in the chooser
         * 
         * @param f The file
         * @return True if it should be displayed, false otherwise
         */
        @Override
        public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".xls");
        }
    }

    /**
     * DOCUMENT_ME
     */
    public class JFileChooserDaemon implements Runnable {
        // ~ Constructors
        // ����������������������������������������������������������������������
        // �������������������������
        /**
         * Initializes the file chooser...this is very slow so it is done in the background
         */
        public JFileChooserDaemon() {
        }

        // ~ Methods
        // ����������������������������������������������������������������������
        // ������������������������������
        /**
         * DOCUMENT ME!
         */
        public void run() {
            statusBar.setText("Initializing the file system...");
            openFileChooser = new JFileChooser();
            openFileChooser.setFileFilter(new XLSFileFilter());
            openFileChooser.setDialogTitle("Open Test");
            saveFileChooser = new JFileChooser();
            saveFileChooser.setFileFilter(new XLSFileFilter());
            saveFileChooser.setDialogTitle("Save Test Results");
            saveFileChooser.setApproveButtonText("Save");
            if ((preferences.getDirectory() != null) && (preferences.getDirectory().length() > 0)) {
                openFileChooser.setSelectedFile(new File(preferences.getDirectory()));
                saveFileChooser.setCurrentDirectory(new File(preferences.getDirectory()));
            }
            setTest(null);
        }
    }
}
