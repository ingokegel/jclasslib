/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
    Menu that holds recent workspace files.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:00:22 $
*/
public class RecentMenu extends JMenu implements ActionListener {

    private static final int RECENT_PROJECTS_MAX_SIZE = 10;
    private static final String SETTINGS_RECENT_WORKSPACES = "recentWorkspaces";

    private static final String ACTION_CLEAR_LIST = "clearList";
    private BrowserMDIFrame frame;

    private LinkedList recentWorkspaces = new LinkedList();

    /**
        Constructor.
        @param frame the parent frame.
     */
    public RecentMenu(BrowserMDIFrame frame) {
        this.frame = frame;
        setText("Reopen workspace");
        setIcon(GUIHelper.ICON_EMPTY);
    }

    public void menuSelectionChanged(boolean isIncluded) {
        super.menuSelectionChanged(isIncluded);

        updateContents();
    }

    public void actionPerformed(final ActionEvent event) {

        if (event.getActionCommand().equals(ACTION_CLEAR_LIST)) {
            recentWorkspaces.clear();
        } else {
            setPopupMenuVisible(false);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.openWorkspace(new File(((JMenuItem)event.getSource()).getText()));
                }
            });

        }
    }

    /**
        Add a file to the list of recently useed workspaces.
        @param file the workspace file.
     */
    public void addRecentWorkspace(File file) {

        try {
            String fileName = file.getCanonicalFile().getAbsolutePath();
            recentWorkspaces.remove(fileName);
            recentWorkspaces.addFirst(fileName);
            if (recentWorkspaces.size() > RECENT_PROJECTS_MAX_SIZE) {
                recentWorkspaces.removeLast();
            }
        } catch (IOException e) {
        }
    }

    /**
        Read the list of recently used workspaces from the preferences store.
        @param preferences the preferences node
     */
    public void read(Preferences preferences) {

        recentWorkspaces.clear();

        TreeMap numberToFile = new TreeMap();
        Preferences recentNode = preferences.node(SETTINGS_RECENT_WORKSPACES);
        try {
            String[] keys = recentNode.keys();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String fileName = recentNode.get(key, null);
                if (fileName != null) {
                    numberToFile.put(new Integer(key), fileName);
                }
            }
            recentWorkspaces.addAll(numberToFile.values());
        } catch (BackingStoreException ex) {
        }
    }

    /**
        Save the list of recently used workspaces to the preferences store.
        @param preferences the preferences node
     */
    public void save(Preferences preferences) {

        Preferences recentNode = preferences.node(SETTINGS_RECENT_WORKSPACES);
        try {
            recentNode.clear();
        } catch (BackingStoreException e) {
        }
        int count = 0;
        Iterator it = recentWorkspaces.iterator();
        while (it.hasNext()) {
            String fileName = (String)it.next();
            recentNode.put(String.valueOf(count++), fileName);
        }
    }

    private void updateContents() {

        removeAll();
        if (recentWorkspaces.size() > 0) {
            Iterator it = recentWorkspaces.iterator();
            while (it.hasNext()) {
                String fileName = (String)it.next();
                JMenuItem menuItem = new JMenuItem(fileName);
                menuItem.addActionListener(this);
                add(menuItem);
            }
            addSeparator();
            JMenuItem menuItem = new JMenuItem("Clear list");
            menuItem.setActionCommand(ACTION_CLEAR_LIST);
            menuItem.addActionListener(this);
            add(menuItem);
        } else {
            JMenuItem menuItem = new JMenuItem("(Empty)");
            menuItem.setEnabled(false);
            add(menuItem);
        }
    }

}
