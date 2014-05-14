/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import com.exe4j.runtime.util.LazyFileOutputStream;
import com.install4j.api.launcher.StartupNotification;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Entry class for the bytecode viewer.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 *
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

        if (isLoadedFromJar()) {
            File stdErrFile = new File(System.getProperty("java.io.tmpdir"), "jclasslib_error.log");
            PrintStream err = new PrintStream(new BufferedOutputStream(new LazyFileOutputStream(stdErrFile.getPath())), true);
            System.setErr(err);
        }
        StartupNotification.registerStartupListener(new StartupNotification.Listener() {
            @Override
            public void startupPerformed(String argLine) {
                List<String> args = splitupCommandLine(argLine);
                if (args.size() > 0) {
                    frame.openExternalFile(args.get(0));
                }
            }
        });

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new BrowserMDIFrame();
                frame.setVisible(true);
                if (args.length > 0) {
                    frame.openExternalFile(args[0]);
                }
            }
        });
    }

    private static boolean isLoadedFromJar() {
        return BrowserApplication.class.getResource(BrowserApplication.class.getSimpleName() + ".class").toString().startsWith("jar:");
    }

    private static List<String> splitupCommandLine(String command) {
        List<String> cmdList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(command, " \"", true);
        boolean insideQuotes = false;
        StringBuilder argument = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("\"")) {
                if (insideQuotes && argument.length() > 0) {
                    cmdList.add(argument.toString());
                    argument.setLength(0);
                }
                insideQuotes = !insideQuotes;
            } else if (" ".contains(token)) {
                if (insideQuotes) {
                    argument.append(" ");
                } else if (argument.length() > 0) {
                    cmdList.add(argument.toString());
                    argument.setLength(0);
                }
            } else {
                argument.append(token);
            }
        }
        if (argument.length() > 0) {
            cmdList.add(argument.toString());
        }
        return cmdList;
    }

}
