/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.io.File;

/**
 * Entry class for the bytecode viewer.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.2 $ $Date: 2005-01-14 15:01:03 $
 */
public class BrowserApplication {

    /**
     * Title of the application.
     */
    public static final String APPLICATION_TITLE = "Bytecode viewer";
    /**
     * System property used to choose default look and feel.
     */
    public static final String SYSTEM_PROPERTY_LAF_DEFAULT = "jclasslib.laf.default";
    /**
     * Version of the application.
     */
    public static final String APPLICATION_VERSION = "3.1";
    /**
     * Suffix for workspace files.
     */
    public static final String WORKSPACE_FILE_SUFFIX = "jcw";

    private static BrowserMDIFrame frame;

    /**
     * Entry point for the class file browser application.
     *
     * @param args arguments for the application. As an argument, a workspace
     *             file or a class file can be passed.
     */
    public static void main(final String[] args) {

        if (!Boolean.getBoolean(BrowserApplication.SYSTEM_PROPERTY_LAF_DEFAULT)) {
            String lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel(lookAndFeelClass);
            } catch (Exception ex) {
            }
        }

        frame = new BrowserMDIFrame();
        frame.setVisible(true);

        if (args.length > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String fileName = args[0];
                    File file = new File(fileName);
                    if (file.exists()) {
                        if (fileName.toLowerCase().endsWith("." + WORKSPACE_FILE_SUFFIX)) {
                            frame.openWorkspace(file);
                        } else if (fileName.toLowerCase().endsWith(".class")) {
                            BrowserInternalFrame internalFrame = frame.openClassFromFile(file);
                            try {
                                internalFrame.setMaximum(true);
                            } catch (PropertyVetoException e) {
                            }
                        }
                    }
                }
            });
        }
    }


}
