/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.AttributeInfo;

/**
 * Detail pane showing a <tt>LocalVariableTypeTable</tt> attribute.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public class LocalVariableTypeTableAttributeDetailPane extends LocalVariableCommonAttributeDetailPane {

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public LocalVariableTypeTableAttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return createTableModel(attribute, "signature");
    }
}
