package com.imaginea.brightest.client.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.imaginea.brightest.client.Environment;
import com.imaginea.brightest.client.Preferences;

/**
 * Preferences Dialog
 * 
 */
public class PreferencesDialog extends JDialog {
    private static final long   serialVersionUID            = -4434070450887104735L;
    private final BorderLayout  borderLayout1               = new BorderLayout();
    private Environment        environments                = null;
    private final GridBagLayout gridBagLayout1              = new GridBagLayout();
    private RootFrame           homeFrame                   = null;
    private final JButton       cancelButton                = new JButton();
    private final JButton       okayButton                  = new JButton();
    private final JCheckBox     abortOnValidationFailure    = new JCheckBox();
    private final JCheckBox     chainGeneratedSteps         = new JCheckBox();
    // private final JCheckBox validateXML = new JCheckBox();
    private final JComboBox     application                 = new JComboBox();
    private final JComboBox     environment                 = new JComboBox();
    private final JComboBox     seleniumBrowser             = new JComboBox();
    private final JCheckBox     seleniumAutoStartServer     = new JCheckBox();
    private final JCheckBox     seleniumKillProcessesByName = new JCheckBox();
    private final JCheckBox     seleniumAutoTaskCleanup     = new JCheckBox();

    private final JLabel        seleniumBrowserLabel        = new JLabel();
    private final JLabel        applicationLabel            = new JLabel();
    private final JLabel        environmentLabel            = new JLabel();
    private final JLabel        initialsLabel               = new JLabel();
    private final JLabel        msg                         = new JLabel();

    private final JPanel        jPanel1                     = new JPanel();
    private final JPanel        jPanel2                     = new JPanel();
    private final JPanel        jPanel3                     = new JPanel();
    private final JPanel        panel1                      = new JPanel();

    private final JTextField    initials                    = new JTextField();
    private Preferences         preferences                 = null;

    /**
     * Creates a new PreferencesDialog object.
     * 
     * @param frame The frame from which the dialog is called
     * @param title The title of the dialog window
     * @param modal True if the dialog should be opened in modal mode, false otherwise
     * @param preferences The user preferences, as read from the preferences.xml file
     * @param environments The list of environments
     */
    public PreferencesDialog(RootFrame frame, String title, boolean modal, Preferences preferences, Environment environments) {
        super(frame, title, modal);
        homeFrame = frame;
        this.preferences = preferences;
        this.environments = environments;

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a new PreferencesDialog object.
     * 
     * @param preferences The user preferences to be updated
     * @param environments The list of environments
     */
    public PreferencesDialog(RootFrame frame, Preferences preferences, Environment environments) {
        this(frame, "", false, preferences, environments);
    }

    private void cancelButton_actionPerformed(ActionEvent e) {
        dispose();
    }

    private void jbInit() throws Exception {
        this.setTitle("Preferences");
        panel1.setLayout(borderLayout1);
        okayButton.setText("OK");
        okayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okayButton_actionPerformed(e);
            }
        });
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButton_actionPerformed(e);
            }
        });
        jPanel1.setLayout(gridBagLayout1);

        initializeLabel(initialsLabel, "Initials");
        initializeLabel(applicationLabel, "Application Filter");
        initializeLabel(environmentLabel, "Environment");
        initializeLabel(seleniumBrowserLabel, "Browser for Selenium");
        initializeLabel(msg, "Preferences");

        jPanel1.setBorder(BorderFactory.createEtchedBorder());

        // validateXML.setFont(new java.awt.Font("SansSerif", 1, 12));
        // validateXML.setHorizontalTextPosition(SwingConstants.LEFT);
        // validateXML.setText("Validate XML");
        chainGeneratedSteps.setFont(new java.awt.Font("SansSerif", 1, 12));
        chainGeneratedSteps.setHorizontalTextPosition(SwingConstants.LEFT);
        chainGeneratedSteps.setText("Chain Generated Steps");
        abortOnValidationFailure.setFont(new java.awt.Font("SansSerif", 1, 12));
        abortOnValidationFailure.setHorizontalTextPosition(SwingConstants.LEFT);
        abortOnValidationFailure.setText("Abort on Validation Failure");
        getContentPane().add(panel1);

        int yPosition = 0;

        panel1.add(jPanel1, BorderLayout.CENTER);

        Insets topInsets = new Insets(3, 8, 3, 1);
        Insets defaultInsets = new Insets(0, 8, 3, 1);
        int defaultPadX = 100;
        int defaultComboPadX = 289;

        jPanel1.add(initialsLabel, new GridBagConstraints(0, yPosition, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, topInsets, 0, 0));
        jPanel1.add(initials, new GridBagConstraints(1, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, topInsets, defaultPadX, 1));

        // jPanel2.add(validateXML, null);
        // jPanel2.add(chainGeneratedSteps, null);
        jPanel2.add(okayButton, null);
        jPanel2.add(cancelButton, null);
        this.getContentPane().add(jPanel2, BorderLayout.SOUTH);

        jPanel1.add(jPanel3, new GridBagConstraints(0, yPosition++, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, topInsets, 365, 1));
        jPanel3.setBorder(BorderFactory.createEtchedBorder());
        jPanel3.add(applicationLabel, new GridBagConstraints());
        jPanel3.add(application, new GridBagConstraints());

        jPanel1.add(environmentLabel, new GridBagConstraints(0, yPosition, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(environment, new GridBagConstraints(1, yPosition++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, defaultComboPadX, 1));
        jPanel1.add(seleniumBrowserLabel, new GridBagConstraints(0, yPosition, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(seleniumBrowser, new GridBagConstraints(1, yPosition++, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets,
                defaultComboPadX, 1));

        seleniumAutoStartServer.setFont(new java.awt.Font("SansSerif", 1, 12));
        seleniumAutoStartServer.setHorizontalTextPosition(SwingConstants.LEFT);
        seleniumAutoStartServer.setText("Auto Start Selenium Server");
        seleniumAutoStartServer.setToolTipText("Allows the test to be run with a separate Selenium Server, useful for debugging.");

        seleniumKillProcessesByName.setFont(new java.awt.Font("SansSerif", 1, 12));
        seleniumKillProcessesByName.setHorizontalTextPosition(SwingConstants.LEFT);
        seleniumKillProcessesByName.setText("Kill Processes By Name");
        seleniumKillProcessesByName.setToolTipText("Forces IE to close at the end of the test.  Note it closes ALL IE instances!");

        seleniumAutoTaskCleanup.setFont(new java.awt.Font("SansSerif", 1, 12));
        seleniumAutoTaskCleanup.setHorizontalTextPosition(SwingConstants.LEFT);
        seleniumAutoTaskCleanup.setText("Auto Cleanup");
        seleniumAutoTaskCleanup.setToolTipText("Allows the test to hold the tasks created in this test, so that it does not impact next test");

        jPanel1.add(seleniumAutoStartServer, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(seleniumKillProcessesByName, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(seleniumAutoTaskCleanup, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));

        // jPanel1.add(validateXML, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
        // GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(chainGeneratedSteps, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        jPanel1.add(abortOnValidationFailure, new GridBagConstraints(0, yPosition++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0, 0));
        panel1.add(msg, BorderLayout.NORTH);

        if (environments != null) {
            String selectedApplication = null;
            Iterator<Environment> iter = environments.getEnvironments().iterator();

            for (int i = 0; iter.hasNext(); i++) {
                Environment env = (Environment) iter.next();

                environment.addItem(env.getName());

                if (env.getName().equals(preferences.getEnvironment())) {
                    environment.setSelectedIndex(i);
                    selectedApplication = env.getApplication();
                }
            }

            //
            // Add the applications
            //
            this.application.addItem(null);
            for (String app: environments.getApplications()) {
                this.application.addItem(app);
            }

            application.setSelectedItem(selectedApplication);

            // Repopulate to initialize
            repopulateEnvironments(selectedApplication);

            // Add listener
            application.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    String applicationType = (String) application.getSelectedItem();

                    repopulateEnvironments(applicationType);
                }
            });
        }

        /*
         * Add the Supported Browsers
         */
        seleniumBrowser.addItem(SeleniumHelper.BROWSER_FIREFOX);
        seleniumBrowser.addItem(SeleniumHelper.BROWSER_FIREFOX_CUSTOM);
        seleniumBrowser.addItem(SeleniumHelper.BROWSER_IE);
        seleniumBrowser.addItem(SeleniumHelper.BROWSER_IEPROXY);
        seleniumBrowser.addItem(SeleniumHelper.BROWSER_IE_CUSTOM);
        seleniumBrowser.setSelectedItem(preferences.getSeleniumBrowser());

        initials.setText(preferences.getInitials());

        // validateXML.setSelected(preferences.getValidateXml().equalsIgnoreCase("Y"));
        chainGeneratedSteps.setSelected(preferences.getChainGeneratedSteps());
        abortOnValidationFailure.setSelected(preferences.getAbortOnValidationFailure());
        seleniumAutoStartServer.setSelected(preferences.isSeleniumAutoStartServer());
        seleniumKillProcessesByName.setSelected(preferences.isSeleniumKillProcessesByName());
        seleniumAutoTaskCleanup.setSelected(preferences.isSeleniumAutoTaskCleanup());
    }

    /**
     * Initialize this label
     * 
     * @param label
     * @param text
     */
    private void initializeLabel(JLabel label, String text) {
        label.setFont(new java.awt.Font("SansSerif", 1, 12));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setText(text + ":");
    }

    private void repopulateEnvironments(String applicationType) {
        environment.removeAllItems();

        Iterator<Environment> iter = environments.getEnvironments().iterator();

        for (int i = 0; iter.hasNext(); i++) {
            Environment env = (Environment) iter.next();

            if (applicationType != null && env.getApplication() != null) {
                if (!env.getApplication().equals(applicationType)) {
                    continue;
                }
            }

            environment.addItem(env.getName());

            if (env.getName().equals(preferences.getEnvironment())) {
                environment.setSelectedItem(env.getName());
            }
        }
    }

    private void okayButton_actionPerformed(ActionEvent e) {
        if (initials.getText().length() == 0) {
            msg.setText("* * * You must specify initials * * *");

            return;
        }

        preferences.setInitials(initials.getText());
        preferences.setEnvironment((String) environment.getSelectedItem());

        preferences.setSeleniumBrowser((String) seleniumBrowser.getSelectedItem());

        // preferences.setValidateXml(validateXML.isSelected() ? "Y" : "N");
        preferences.setChainGeneratedSteps(chainGeneratedSteps.isSelected() ? "Y" : "N");
        preferences.setAbortOnValidationFailure(abortOnValidationFailure.isSelected() ? "Y" : "N");
        preferences.setSeleniumAutoStartServer(Boolean.toString(seleniumAutoStartServer.isSelected()));
        preferences.setSeleniumKillProcessesByName(Boolean.toString(seleniumKillProcessesByName.isSelected()));
        preferences.setSeleniumAutoTaskCleanup(Boolean.toString(seleniumAutoTaskCleanup.isSelected()));

        Environment.setCurrentEnvironment(preferences.getEnvironment());

        try {
            preferences.write();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        homeFrame.updateWindowTitle();
        dispose();
    }
}
