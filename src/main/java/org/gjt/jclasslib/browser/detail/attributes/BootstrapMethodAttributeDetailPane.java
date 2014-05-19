/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener;
import org.gjt.jclasslib.browser.detail.ListDetailPane;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute;
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsEntry;

/**
    Detail pane showing an <tt>BootstrapMethods</tt> attribute.

*/
public class BootstrapMethodAttributeDetailPane extends ListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public BootstrapMethodAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(BootstrapMethodsEntry bootstrapMethod) {
        return new BootstrapMethodTableModel(bootstrapMethod);
    }

    protected float getRowHeightFactor() {
        return 2f;
    }

    private class BootstrapMethodTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 1;
        
        private static final int BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT;
     
        private static final int ARGUMENTS_REF_LINK_COLUMN_WIDTH = 250;
        
        private BootstrapMethodsEntry bootstrapMethod;
        
        private BootstrapMethodTableModel(BootstrapMethodsEntry bootstrapMethod) {
            super(null);
            this.bootstrapMethod = bootstrapMethod;
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX:
                   return ARGUMENTS_REF_LINK_COLUMN_WIDTH;
                default:
                    return 80;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex;
            switch (column) {
                case BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX:
                    constantPoolIndex = bootstrapMethod.getArgumentRefs()[row];
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
        }
        
        public int getRowCount() {
            return bootstrapMethod.getArgumentNumber();
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX:
                   return "argument";
                default:
                   return "";
            }
        }

		protected Class doGetColumnClass(int column) {
			switch (column) {
			case BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX:
				return Link.class;
			default:
				return String.class;
			}
		}
        
        protected Object doGetValueAt(int row, int column) {

            switch (column) {
                case BOOTSTAP_ARGUMENT_INFO_INDEX_COLUMN_INDEX:
                    return createCommentLink(bootstrapMethod.getArgumentRefs()[row]);
                default:
                    return "";
            }
        }
    }

	@Override
	protected TableModel getTableModel(TreePath treePath) {
		return createTableModel((BootstrapMethodsEntry)((BrowserTreeNode)treePath.getLastPathComponent()).getElement());
	}
}
