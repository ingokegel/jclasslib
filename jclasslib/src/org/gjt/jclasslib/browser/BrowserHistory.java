/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.detail.attributes.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;


/**
    Manages the navigation history of a single child window.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class BrowserHistory {

    private static int MAX_HISTORY_ENTRIES = 50;
    
    private BrowserServices services;

    private LinkedList history = new LinkedList();
    private int historyPointer = -1;
    
    public BrowserHistory(BrowserServices services) {
        this.services = services;
    }

    /** 
        Clear the navigation history.
     */
    public void clear() {
        history.clear();
        historyPointer = -1;
    }
    
    /** 
        Move one step backward in the navigation history.
     */
    public void historyBackward() {
        if (historyPointer == 0) {
            return;
        }
        historyPointer--;
        syncWithHistory();
    }

    /** 
        Move one step forward in the navigation history.
     */
    public void historyForward() {
        if (historyPointer == history.size() - 1) {
            return;
        }
        historyPointer++;
        syncWithHistory();
    }
    
    /** 
        Update the availability of the actions associated with the
        navigation history.
     */
    public void updateActions() {

        if (services.getActionBackward() == null || services.getActionForward() == null) {
            return;
        }
        services.getActionBackward().setEnabled(historyPointer > 0);
        services.getActionForward().setEnabled(historyPointer < history.size() - 1);
    }

    /** 
        Add a navigation step to the history.
        @param newPath the selected tree path in <tt>BrowserTreePane</tt>
     */
    public void updateHistory(TreePath newPath) {
        updateHistory(newPath, null);
    }
    
    /** 
        Add a navigation step to the history.
        @param newPath the selected tree path in <tt>BrowserTreePane</tt>
        @param offset the target's offset in the bytecode. <tt>Null</tt> if 
                      not applicable.
     */
    public void updateHistory(TreePath newPath, Integer offset) {
        

        BrowserHistoryEntry newEntry = new BrowserHistoryEntry(newPath, offset);
        
        if (!checkForOffset(newEntry) &&
                (historyPointer < 0 || !newEntry.equals(history.get(historyPointer)))) {
                        
            eliminateForwardEntries();
            
            if (historyPointer > MAX_HISTORY_ENTRIES) {
                history.removeFirst();
                historyPointer--;
            }
            
            history.add(newEntry);
            historyPointer++;
            
        }
        updateActions();
    }
    
    private boolean checkForOffset(BrowserHistoryEntry newEntry) {

        if (historyPointer >= 0) {

            BrowserHistoryEntry currentEntry = (BrowserHistoryEntry)history.get(historyPointer);
            if (currentEntry.getTreePath().equals(newEntry.getTreePath())) {
                if (newEntry.getOffset() == null) {
                    // Ignore history event, since it is more unspecific than the current one
                    return true;

                } else if (currentEntry.getOffset() == null) {
                    // merge with current entry to achieve more specific history entry
                    eliminateForwardEntries();
                    currentEntry.setOffset(newEntry.getOffset());
                    // Do not add another history event
                    return true;
                }
            }
        }
        return false;
    }
    
    private void eliminateForwardEntries() {
        
        if (historyPointer < history.size() - 1) {
            ListIterator it = history.listIterator(historyPointer + 1);
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
    }
    
    private void syncWithHistory() {
        
        BrowserHistoryEntry entry = (BrowserHistoryEntry)history.get(historyPointer);

        JTree treeView = services.getBrowserComponent().getTreePane().getTreeView();
        
        treeView.setSelectionPath(entry.getTreePath());
        treeView.scrollPathToVisible(entry.getTreePath());
        
        Integer offset = entry.getOffset();
        
        if (offset != null) {
            BrowserDetailPane detailPane = services.getBrowserComponent().getDetailPane();
            
            CodeAttributeDetailPane codeAttributeDetailPane =
                detailPane.getAttributeDetailPane().getCodeAttributeDetailPane();
            
            codeAttributeDetailPane.selectByteCodeDetailPane();
            
            codeAttributeDetailPane.getCodeAttributeByteCodeDetailPane().
                scrollToOffset(offset.intValue());
        }
        
        updateActions();
    }
    
    private class BrowserHistoryEntry {
        
        private TreePath treePath;
        private Integer offset;
        
        public BrowserHistoryEntry(TreePath treePath, Integer offset) {
            this.treePath = treePath;
            this.offset = offset;
        }
        
        public TreePath getTreePath() {
            return treePath;
        }
        
        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }
        
        public boolean equals(Object object) {
            
            if (object == null && !(object instanceof BrowserHistoryEntry)) {
                return false;
            }
            BrowserHistoryEntry other = (BrowserHistoryEntry)object;
            
            return isEqual(offset, other.getOffset()) &&
                   isEqual(treePath, other.getTreePath());
        }
        
        private boolean isEqual(Object one, Object two) {
            
            if (one == null) {
                if (two != null) {
                    return false;
                }
            } else {
                if (!one.equals(two)) {
                    return false;
                }
            }
            return true;
        }
        
        public String toString() {
            return treePath.toString() + " / offset " + (offset == null ? "null" : offset.toString());
        }
    }
}
