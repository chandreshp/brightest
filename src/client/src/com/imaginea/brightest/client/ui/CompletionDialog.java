package com.imaginea.brightest.client.ui;


import com.imaginea.brightest.client.Preferences;
import com.imaginea.brightest.client.test.Test;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

/**
 * Class to display a dialog box after the test has completed, asking the user
 * if they wish to send the results to PIC, and enter comments about the test.
 *
 */
public class CompletionDialog extends JDialog {
    private static final long serialVersionUID = 7508972428022940684L;
    private BorderLayout  borderLayout1  = new BorderLayout();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JButton       sendButton     = new JButton();
    private JPanel        commentsPanel  = new JPanel();
    private JPanel        jPanel1        = new JPanel();
    private JPanel        jPanel2        = new JPanel();
    private JPanel        panel1         = new JPanel();
    private JTextArea     comments       = new JTextArea();

    public CompletionDialog(Frame frame, String title, String defaultComments, Test test, Preferences preferences) {
        super(frame, title, true);
        setTitle(title);

        try {
            jbInit(defaultComments);
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close the dialog on a button event
     *
     * @param e The event of the user click
     */
    public void sendButton_actionPerformed(ActionEvent e) {
        try {
            //            PicLink picLink = new PicLink();
            //
            //            picLink.sendTestResult(test, comments.getText(), preferences.getPicUsername(), preferences.getWcBuild(), preferences.getWcVersion());
        } catch (Exception ex) {
            ex.printStackTrace();
            RootFrame.showAlert("Error",ex);
        }

        dispose();
    }


    private void jbInit(String defaultComments) throws Exception {
        panel1.setLayout(borderLayout1);
        sendButton.setText("OK");
        comments.setText(defaultComments);
        commentsPanel.setLayout(gridBagLayout1);

        getContentPane().add(panel1);
        panel1.add(jPanel1, BorderLayout.NORTH);
        panel1.add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(sendButton, null);
        panel1.add(commentsPanel, BorderLayout.CENTER);
        commentsPanel.add(comments,
                          new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                                                 GridBagConstraints.BOTH, new Insets(5, 8, 0, 8), -540, 0));
        comments.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        comments.setMargin(new Insets(1, 1, 1, 1));
        comments.setWrapStyleWord(true);
        comments.setLineWrap(true);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendButton_actionPerformed(e);
                }
            });
    }
}
