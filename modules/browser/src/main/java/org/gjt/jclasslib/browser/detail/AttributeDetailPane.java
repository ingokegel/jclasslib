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
 *
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
    private static final String SCREEN_BOOTSTRAP_METHODS = "BootstrapMethods";
    private static final String SCREEN_STACK_MAP_TABLE = "StackMapTable";
    private static final String SCREEN_METHOD_PARAMETERS = "MethodParameters";

    private HashMap<String, AbstractDetailPane> attributeTypeToDetailPane;
    
    // Visual components
    
    private JPanel specificInfoPane;
    private GenericAttributeDetailPane genericInfoPane;
    private AbstractDetailPane lastSpecificPane;

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

    @Override
    public String getClipboardText() {
        if (lastSpecificPane != null) {
            return lastSpecificPane.getClipboardText();
        } else {
            return null;
        }
    }

    public void show(TreePath treePath) {

        AttributeInfo attribute = getAttribute(treePath);
        String paneName = getPaneName(attribute.getClass());
        CardLayout layout = (CardLayout)specificInfoPane.getLayout();
        if (paneName == null) {
            layout.show(specificInfoPane, SCREEN_UNKNOWN);
            lastSpecificPane = null;
        } else {
            lastSpecificPane = getDetailPane(paneName);
            lastSpecificPane.show(treePath);
            layout.show(specificInfoPane, paneName);
        }

        genericInfoPane.show(treePath);
    }

    private String getPaneName(Class<? extends AttributeInfo> attributeInfoClass) {
        
        if (ConstantValueAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_CONSTANT_VALUE;
        } else if (CodeAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_CODE;
        } else if (ExceptionsAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_EXCEPTIONS;
        } else if (InnerClassesAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_INNER_CLASSES;
        } else if (SourceFileAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_SOURCE_FILE;
        } else if (LineNumberTableAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_LINE_NUMBER_TABLE;
        } else if (LocalVariableTableAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_LOCAL_VARIABLE_TABLE;
        } else if (EnclosingMethodAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_ENCLOSING_METHOD;
        } else if (SignatureAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_SIGNATURE;
        } else if (LocalVariableTypeTableAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_LOCAL_VARIABLE_TYPE_TABLE;
        } else if (AnnotationHolder.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_RUNTIME_ANNOTATIONS;
        } else if (AnnotationDefaultAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_ANNOTATION_DEFAULT;
        } else if (BootstrapMethodsAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_BOOTSTRAP_METHODS;
        } else if (StackMapTableAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_STACK_MAP_TABLE;
        } else if (MethodParametersAttribute.class.isAssignableFrom(attributeInfoClass)) {
            return SCREEN_METHOD_PARAMETERS;
        } else {
            return null;
        }
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

    /**
     * Get the <tt>AbstractDetailPane</tt> showing the details of a
     * specified attribute.
     *
     * @return the detail pane
     */
    public AbstractDetailPane getAttributeDetailPane(Class<? extends AttributeInfo> attributeInfoClass) {
        return getDetailPane(getPaneName(attributeInfoClass));
    }

    private AbstractDetailPane getDetailPane(String attributeType) {
        AbstractDetailPane detailPane = attributeTypeToDetailPane.get(attributeType);
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
            return new ConstantValueAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_CODE)) {
            return new CodeAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_EXCEPTIONS)) {
            return new ExceptionsAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_INNER_CLASSES)) {
            return new InnerClassesAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_SOURCE_FILE)) {
            return new SourceFileAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_LINE_NUMBER_TABLE)) {
            return new LineNumberTableAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_LOCAL_VARIABLE_TABLE)) {
            return new LocalVariableTableAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_ENCLOSING_METHOD)) {
            return new EnclosingMethodAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_SIGNATURE)) {
            return new SignatureAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_LOCAL_VARIABLE_TYPE_TABLE)) {
            return new LocalVariableTypeTableAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_RUNTIME_ANNOTATIONS)) {
            return new RuntimeAnnotationsAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_ANNOTATION_DEFAULT)) {
            return new AnnotationDefaultAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_BOOTSTRAP_METHODS)) {
            return new BootstrapMethodsAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_STACK_MAP_TABLE)) {
        	return new StackMapTableAttributeDetailPane(getBrowserServices());
        } else if (attributeType.equals(SCREEN_METHOD_PARAMETERS)) {
        	return new MethodParametersAttributeDetailPane(getBrowserServices());
        } else {
            return null;
        }
    }

    private void buildGenericInfoPane() {

        genericInfoPane = new GenericAttributeDetailPane(getBrowserServices());
        genericInfoPane.setBorder(createTitledBorder("Generic info:"));
    }

    private void buildSpecificInfoPane() {

        specificInfoPane = new JPanel();
        specificInfoPane.setBorder(createTitledBorder("Specific info:"));

        specificInfoPane.setLayout(new CardLayout());
        attributeTypeToDetailPane = new HashMap<String, AbstractDetailPane>();
        JPanel pane;

        pane = new JPanel();
        specificInfoPane.add(pane, SCREEN_UNKNOWN);

    }

    private Border createTitledBorder(String title) {
        Border simpleBorder = BorderFactory.createEtchedBorder();
        return BorderFactory.createTitledBorder(simpleBorder, title);
    }
}

