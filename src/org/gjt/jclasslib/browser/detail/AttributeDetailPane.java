/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.AbstractDetailPane;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.attributes.*;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.attributes.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;

/**
 * Detail pane for an attribute of class <tt>org.gjt.jclasslib.structures.AttributeInfo</tt>.
 * This class is a container for the classes defined in the <tt>attributes</tt>
 * subpackage and switches between the contained panes as required.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.7 $ $Date: 2007-10-31 12:04:13 $
 */
public class AttributeDetailPane extends AbstractDetailPane {

    private static final String SCREEN_UNKNOWN = "Unknown";
    private static final String SCREEN_CONSTANT_VALUE = "ConstantValue";
    private static final String SCREEN_CODE = "Code";
    private static final String SCREEN_EXCEPTIONS = "Exceptions";
    private static final String SCREEN_INNER_CLASSES = "InnerClasses";
    private static final String SCREEN_SOURCE_FILE = "SourceFile";
    private static final String SCREEN_LINE_NUMBER_TABLE = "LineNumberTable";
    private static final String SCREEN_LOCAL_VARIABLE_TABLE = "LocalVariableTable";
    private static final String SCREEN_ENCLOSING_METHOD = "EnclosingMethod";
    private static final String SCREEN_SIGNATURE = "Signature";
    private static final String SCREEN_LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable";
    private static final String SCREEN_RUNTIME_ANNOTATIONS = "RuntimeAnnotations";
    private static final String SCREEN_ANNOTATION_DEFAULT = "AnnotationDefault";

    private HashMap attributeTypeToDetailPane;
    
    // Visual components
    
    private JPanel specificInfoPane;
    private GenericAttributeDetailPane genericInfoPane;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public AttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupComponent() {

        buildGenericInfoPane();
        buildSpecificInfoPane();

        setLayout(new BorderLayout());

        add(genericInfoPane, BorderLayout.NORTH);
        add(specificInfoPane, BorderLayout.CENTER);

    }

    public void show(TreePath treePath) {

        AttributeInfo attribute = findAttribute(treePath);

        String paneName = null;
        if (attribute instanceof ConstantValueAttribute) {
            paneName = SCREEN_CONSTANT_VALUE;
        } else if (attribute instanceof CodeAttribute) {
            paneName = SCREEN_CODE;
        } else if (attribute instanceof ExceptionsAttribute) {
            paneName = SCREEN_EXCEPTIONS;
        } else if (attribute instanceof InnerClassesAttribute) {
            paneName = SCREEN_INNER_CLASSES;
        } else if (attribute instanceof SourceFileAttribute) {
            paneName = SCREEN_SOURCE_FILE;
        } else if (attribute instanceof LineNumberTableAttribute) {
            paneName = SCREEN_LINE_NUMBER_TABLE;
        } else if (attribute instanceof LocalVariableTableAttribute) {
            paneName = SCREEN_LOCAL_VARIABLE_TABLE;
        } else if (attribute instanceof EnclosingMethodAttribute) {
            paneName = SCREEN_ENCLOSING_METHOD;
        } else if (attribute instanceof SignatureAttribute) {
            paneName = SCREEN_SIGNATURE;
        } else if (attribute instanceof LocalVariableTypeTableAttribute) {
            paneName = SCREEN_LOCAL_VARIABLE_TYPE_TABLE;
        } else if (attribute instanceof RuntimeAnnotationsAttribute) {
            paneName = SCREEN_RUNTIME_ANNOTATIONS;
        } else if (attribute instanceof AnnotationDefaultAttribute) {
            paneName = SCREEN_ANNOTATION_DEFAULT;
        }

        CardLayout layout = (CardLayout)specificInfoPane.getLayout();
        if (paneName == null) {
            layout.show(specificInfoPane, SCREEN_UNKNOWN);
        } else {
            AbstractDetailPane pane = getDetailPane(paneName);
            pane.show(treePath);
            layout.show(specificInfoPane, paneName);
        }

        genericInfoPane.show(treePath);
    }

    /**
     * Get the <tt>CodeAttributeDetailPane</tt> showing the details of a
     * <tt>Code</tt> attribute.
     *
     * @return the <tt>CodeAttributeDetailPane</tt>
     */
    public CodeAttributeDetailPane getCodeAttributeDetailPane() {
        return (CodeAttributeDetailPane)getDetailPane(SCREEN_CODE);
    }

    private AbstractDetailPane getDetailPane(String attributeType) {
        AbstractDetailPane detailPane = (AbstractDetailPane)attributeTypeToDetailPane.get(attributeType);
        if (detailPane == null) {
            detailPane = createDetailPanel(attributeType);
            if (detailPane != null) {
                if (detailPane instanceof FixedListDetailPane) {
                    specificInfoPane.add(((FixedListDetailPane)detailPane).getScrollPane(), attributeType);
                } else {
                    specificInfoPane.add(detailPane, attributeType);
                }
                attributeTypeToDetailPane.put(attributeType, detailPane);
            }
        }
        return detailPane;
    }

    private AbstractDetailPane createDetailPanel(String attributeType) {
        if (attributeType.equals(SCREEN_CONSTANT_VALUE)) {
            return new ConstantValueAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_CODE)) {
            return new CodeAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_EXCEPTIONS)) {
            return new ExceptionsAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_INNER_CLASSES)) {
            return new InnerClassesAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_SOURCE_FILE)) {
            return new SourceFileAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_LINE_NUMBER_TABLE)) {
            return new LineNumberTableAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_LOCAL_VARIABLE_TABLE)) {
            return new LocalVariableTableAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_ENCLOSING_METHOD)) {
            return new EnclosingMethodAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_SIGNATURE)) {
            return new SignatureAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_LOCAL_VARIABLE_TYPE_TABLE)) {
            return new LocalVariableTypeTableAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_RUNTIME_ANNOTATIONS)) {
            return new RuntimeAnnotationsAttributeDetailPane(services);
        } else if (attributeType.equals(SCREEN_ANNOTATION_DEFAULT)) {
            return new AnnotationDefaultAttributeDetailPane(services);
        } else {
            return null;
        }
    }

    private void buildGenericInfoPane() {

        genericInfoPane = new GenericAttributeDetailPane(services);
        genericInfoPane.setBorder(createTitledBorder("Generic info:"));
    }

    private void buildSpecificInfoPane() {

        specificInfoPane = new JPanel();
        specificInfoPane.setBorder(createTitledBorder("Specific info:"));

        specificInfoPane.setLayout(new CardLayout());
        attributeTypeToDetailPane = new HashMap();
        JPanel pane;

        pane = new JPanel();
        specificInfoPane.add(pane, SCREEN_UNKNOWN);

    }

    private Border createTitledBorder(String title) {
        Border simpleBorder = BorderFactory.createEtchedBorder();
        return BorderFactory.createTitledBorder(simpleBorder, title);
    }
}

