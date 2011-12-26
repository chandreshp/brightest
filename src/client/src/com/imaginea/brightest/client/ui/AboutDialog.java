package com.imaginea.brightest.client.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.imaginea.brightest.client.Client;

/**
 * About Dialog
 */
public class AboutDialog extends JDialog implements ActionListener {
    private static final long  serialVersionUID = -1472358080347355072L;
    private final BorderLayout borderLayout1    = new BorderLayout();
    private final BorderLayout borderLayout2    = new BorderLayout();
    private final FlowLayout   flowLayout1      = new FlowLayout();
    private final GridLayout   gridLayout1      = new GridLayout();
    private final JButton      button1          = new JButton();
    private final JLabel       imageLabel       = new JLabel();
    private final JLabel       label0           = new JLabel();
    private final JLabel       label1           = new JLabel();
    private final JLabel       label2           = new JLabel();
    private final JLabel       label3           = new JLabel();
    private final JLabel       label4           = new JLabel();
    private final JPanel       insetsPanel0     = new JPanel();
    private final JPanel       insetsPanel1     = new JPanel();
    private final JPanel       insetsPanel2     = new JPanel();
    private final JPanel       insetsPanel3     = new JPanel();
    private final JPanel       panel1           = new JPanel();
    private final JPanel       panel2           = new JPanel();
    private final String       comments         = "";
    private final String       copyright        = "Copyright (c) 2003-2008";
    private final String       product          = "BrighTest";
    private final String       version          = Client.getInstance().getVersion();
    private ImageIcon          companyLogo      = null;

    /**
     * Creates a new HomeFrame_AboutBox object.
     * 
     * @param parent Frame of the calling window frame
     */
    public AboutDialog(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the dialog on a button event
     * 
     * @param e The event of the user click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cancel();
        }
    }

    /**
     * Overridden so we can exit when window is closed
     * 
     * @param e The event of the user action
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }

        super.processWindowEvent(e);
    }

    /**
     * Close the dialog
     */
    void cancel() {
        dispose();
    }

    // Component initialization
    private void jbInit() throws Exception {
        //
        // Get the image from the jar, which is in JNLP so a little tricky.
        // But this is the recommended Sun way.
        //
        URL iconURL = null;
        ClassLoader cl = this.getClass().getClassLoader();

        iconURL = cl.getResource("image/logo.jpg");
        companyLogo = new ImageIcon(iconURL, "Company Logo.");

        this.setTitle("About");
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(4);
        gridLayout1.setColumns(1);
        label0.setIcon(companyLogo);
        label1.setText(product);
        label2.setText(version);
        label3.setText(copyright);
        label4.setText(comments);
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        button1.setText("Ok");
        button1.addActionListener(this);
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        this.getContentPane().add(panel1, null);
        insetsPanel0.add(label0, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);
        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        panel2.add(insetsPanel0, BorderLayout.NORTH);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
        setResizable(true);
    }
}
