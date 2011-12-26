package com.imaginea.brightest.client.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;


/**
 * Class to show a dialog box with an error message contained within
 *
 * @author $author$
 * @version $Revision$
 */
public class AlertDialog extends JDialog {

    private static final long serialVersionUID = 5716944471835313008L;

    private BorderLayout borderLayout1 = new BorderLayout();
    private JButton      jButton1      = new JButton();
    private JPanel       jPanel1       = new JPanel();
    private JPanel       panel1        = new JPanel();
    private JScrollPane  msgScrollPane = new JScrollPane();
    private JTextArea    msg           = new JTextArea();


    /**
     * Creates a new AlertDialog object.
     *
     * @param frame The jFrame from which the alert is called
     * @param text The error text to display
     */
    public AlertDialog(Frame frame, String text) {
        this(frame, "Alert", true, text);
    }


    /**
     * Creates a new AlertDialog object.
     *
     * @param frame The jFrame from which the alert is called
     * @param title The title of the new window
     * @param modal True if the dialog should be modal, false otherwise
     */
    public AlertDialog(Frame frame, String title, boolean modal, String text) {
        super(frame, title, modal);

        try {
            jbInit(text);
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Creates a new AlertDialog object.
     */
    public AlertDialog() {
        this(null, "", false, "");
    }

    //~ Methods ��������������������������������������������������������������������������������������������������������

    private void jButton1_actionPerformed(ActionEvent e) {
        setVisible(false);
        this.dispose();
    }


    private void jbInit(String text) throws Exception {
        panel1.setLayout(borderLayout1);
        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jButton1_actionPerformed(e);
                }
            });
        this.setTitle("Alert");

        //msg.setFont(new java.awt.Font("SansSerif", 1, 12));
        msg.setToolTipText("");
        msg.setEditable(false);
        msg.setText(text);
        msg.setCaretPosition(0);
        msg.setLineWrap(false);

        //msg.setRows(5);
        msg.setWrapStyleWord(false);
        getContentPane().add(panel1);
        panel1.add(msgScrollPane, BorderLayout.CENTER);
        panel1.add(jPanel1, BorderLayout.SOUTH);
        msgScrollPane.getViewport().add(msg, null);
        jPanel1.add(jButton1, null);
    }
}
