package com.imaginea.brightest.client.ui;

import com.imaginea.brightest.client.test.InputStep;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;


/**
 * Class to present the user with a dialog that prompts the user for a value
 * and updates the variable list accordingly
 *
 * @author bsturim
 */
public class InputDialog extends JDialog {
    private static final long serialVersionUID = -1881927707936059532L;
    private BorderLayout  borderLayout2          = new BorderLayout();
    private GridBagLayout gridBagLayout1         = new GridBagLayout();
    private InputStep     step;
    private JButton       jButtonSubmit          = new JButton();
    private JLabel        inputLabel             = new JLabel();
    private JLabel        jdescriptionLabel      = new JLabel();
    private JPanel        buttonPanel            = new JPanel();
    private JPanel        panel1                 = new JPanel();
    private JScrollPane   jScrollPaneDescription = new JScrollPane();
    private JTextArea     description            = new JTextArea();
    private JTextField    inputValue             = new JTextField();


    /**
     * Creates a new InputDialog object.
     *
     * @param frame The calling frame
     * @param step The step to be presented to the user
     */
    public InputDialog(Frame frame, InputStep step) {
        this(frame, "Step: " + step.getStepNumber(), true, step);
    }


    /**
     * Creates a new SuccessFailureDialog object.
     *
     * @param frame The calling frame
     * @param title The window title
     * @param modal Whether or not the dialog is modal
     * @param step The step to be presented to the user
     */
    public InputDialog(Frame frame, String title, boolean modal, InputStep step) {
        super(frame, title, modal);
        this.step = step;

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        panel1.setLayout(gridBagLayout1);
        jScrollPaneDescription.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.getContentPane().setLayout(borderLayout2);
        jdescriptionLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        jdescriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        jdescriptionLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        jdescriptionLabel.setText("Description:");

        inputLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        inputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        inputLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        inputLabel.setText("Value:");

        inputValue.setFont(new java.awt.Font("SansSerif", 1, 12));
        inputValue.setHorizontalAlignment(SwingConstants.LEFT);
        inputValue.setText("");

        description.setText(step.getTest().processTextForTagReferences(step.getDescription()));
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        jButtonSubmit.setText("Submit");
        getContentPane().add(panel1, BorderLayout.CENTER);
        panel1.add(jdescriptionLabel,
                   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                          new Insets(0, 0, 0, 0), 5, 0));
        panel1.add(jScrollPaneDescription,
                   new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                          new Insets(0, 0, 0, 0), 0, 0));
        panel1.add(inputLabel,
                   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                          new Insets(0, 0, 0, 0), 5, 0));
        panel1.add(inputValue,
                   new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                          new Insets(0, 0, 0, 0), 0, 0));
        jScrollPaneDescription.getViewport().add(description, null);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(jButtonSubmit, null);

        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    successButton_actionPerformed(e);
                }
            });
    }


    private void successButton_actionPerformed(ActionEvent e) {
        if (inputValue.getText().length() > 0) {
            step.setVariableValue(inputValue.getText());
            dispose();
        }
    }
}
