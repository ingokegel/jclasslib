/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * About dialog.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.2 $ $Date: 2005-01-14 15:01:03 $
 */
public class BrowserAboutDialog extends JDialog {

    private JButton btnOk;

    /**
     * Constructor.
     *
     * @param parent parent frame.
     */
    public BrowserAboutDialog(JFrame parent) {
        super(parent);
        setupControls();
        setupComponent();
    }

    private void setupComponent() {

        setModal(true);
        setTitle("About the jclasslib bytecode viewer");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        GUIHelper.centerOnParentWindow(this, getOwner());

        JComponent contentPane = (JComponent)getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 5, 0, 5);
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;

        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;

        JLabel label = new JLabel("jclasslib bytecode viewer");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        contentPane.add(label, gc);
        gc.insets.top = 5;
        contentPane.add(new JLabel("Version " + BrowserApplication.APPLICATION_VERSION), gc);
        contentPane.add(new JLabel("Copyright ej-technologies GmbH, 2001-2005"), gc);
        contentPane.add(new JLabel("Licensed under the General Public License"), gc);

        gc.weighty = 0;
        gc.insets.top = 20;
        gc.insets.bottom = 5;
        gc.fill = GridBagConstraints.NONE;
        contentPane.add(btnOk, gc);

        Dimension size = contentPane.getPreferredSize();
        size.width += 100;
        contentPane.setPreferredSize(size);
        pack();
        setResizable(false);

    }

    private void setupControls() {

        btnOk = new JButton("Ok");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                dispose();
            }
        });
    }
}
