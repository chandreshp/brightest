package com.imaginea.brightest.client.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;


/**
 * Present the user with a dialog box, seeking confirmation whether a file should be overwritten or not.
 *
 */
public class ConfirmOverwriteDialog extends JDialog {

    private static final long serialVersionUID = 3218240656389494097L;
    private BorderLayout borderLayout1 = new BorderLayout();
    private FlowLayout   flowLayout1  = new FlowLayout();
    private JButton      cancelButton = new JButton();
    private JButton      okayButton   = new JButton();
    private JPanel       jPanel1      = new JPanel();
    private JPanel       panel1       = new JPanel();
    private JTextArea    msg          = new JTextArea();
    private boolean      confirmed    = false;

    /**
     * Creates a new ConfirmOverwriteDialog object.
     *
     * @param frame The main application frame
     * @param msg The message that should be presented to the user
     */
    public ConfirmOverwriteDialog(Frame frame, String msg) {
        this(frame, "Overwrite File?", true);
        this.msg.setText(msg);
    }


    /**
     * Creates a new ConfirmOverwriteDialog object.
     *
     * @param frame The main application frame
     * @param title The window title
     * @param modal Whether the dialog box should be modal
     */
    public ConfirmOverwriteDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Creates a new ConfirmOverwriteDialog object.
     */
    public ConfirmOverwriteDialog() {
        this(null, "", false);
    }

    /*
     * Getter for whether the user confirmed the overwrite or cancelled
     */
    public boolean isConfirmed() {
        return confirmed;
    }


    private void cancelButton_actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }


    private void okayButton_actionPerformed(ActionEvent e) {
        confirmed = true;
        setVisible(false);
        dispose();
    }


    private void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        msg.setFont(new java.awt.Font("SansSerif", 1, 12));
        msg.setText("jTextArea1");
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        jPanel1.setLayout(flowLayout1);
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
        this.setTitle("Overwrite File?");
        getContentPane().add(panel1);
        panel1.add(msg, BorderLayout.CENTER);
        panel1.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(okayButton, null);
        jPanel1.add(cancelButton, null);
    }
}
