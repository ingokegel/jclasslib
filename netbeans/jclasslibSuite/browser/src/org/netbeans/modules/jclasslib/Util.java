/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
 */

package org.netbeans.modules.jclasslib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JEditorPane;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.browser.config.window.CategoryHolder;
import org.gjt.jclasslib.browser.config.window.IndexHolder;
import org.gjt.jclasslib.browser.config.window.ReferenceHolder;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;
import org.netbeans.editor.BaseDocument;
import org.netbeans.jmi.javamodel.Array;
import org.netbeans.jmi.javamodel.CallableFeature;
import org.netbeans.jmi.javamodel.Constructor;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.Feature;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.Method;
import org.netbeans.jmi.javamodel.Parameter;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.Type;
import org.netbeans.modules.editor.java.JMIUtils;
import org.netbeans.modules.javacore.api.JavaModel;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 * @author Martin Krauskopf
 */
final class Util {
    
    private static final Map PARAM_TO_JVM_BASE_TYPE/*<String, Character>*/ = new HashMap();
    
    private static final char VOID_TAG = 'V';
    
    static {
        PARAM_TO_JVM_BASE_TYPE.put("byte", new Character(ElementValue.BYTE_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("char", new Character(ElementValue.CHAR_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("double", new Character(ElementValue.DOUBLE_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("float", new Character(ElementValue.FLOAT_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("int", new Character(ElementValue.INT_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("long", new Character(ElementValue.LONG_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("short", new Character(ElementValue.SHORT_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("boolean", new Character(ElementValue.BOOL_TAG));
        PARAM_TO_JVM_BASE_TYPE.put("void", new Character(VOID_TAG));
    }
    
    private Util() { /** Use static methods instead. */ }
    
    /**
     * Returns {@link BrowserPath browser path} for the given source file. May
     * return <code>null</code> if e.g. there is not corresponding open
     * document.
     */
    static BrowserPath getBrowserPath(final FileObject sourceFile) {
        BrowserPath path = null;
        try {
            DataObject dObj = DataObject.find(sourceFile);
            EditorCookie editor = dObj == null ? null :
                (EditorCookie) dObj.getCookie(EditorCookie.class);
            if (editor != null) {
                JEditorPane[] panes = editor.getOpenedPanes();
                if (panes != null) {
                    BaseDocument doc = (BaseDocument) editor.openDocument();
                    int caretPos = panes[0].getCaretPosition();
                    if (!isInnerClass(doc, caretPos)) {
                        path = getBrowserPath(doc, caretPos);
                    }
                } // else not open - leave null
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
        return path;
    }
    
    /**
     * Returns {@link BrowserPath browser path} corresponding to the given
     * offset. May return <code>null</code> if the element at the given offset
     * is unsupported.
     */
    private static BrowserPath getBrowserPath(final BaseDocument doc, final int offset) {
        BrowserPath path = null;
        JavaModel.getJavaRepository().beginTrans(true);
        try {
            Element el = getCurrentElement(doc, offset);
            while (el != null && !isMethodOrContructor(el)) {
                el = (Element) el.refImmediateComposite();
            }
            if (el != null) {
                path = getBrowserPath(el);
            }
        } finally {
            JavaModel.getJavaRepository().endTrans();
        }
        return path;
    }
    
    private static boolean isMethodOrContructor(final Element el) {
        // Method and Constructor must have declaring class
        return ((el instanceof Method) || (el instanceof Constructor)) &&
                ((Feature) el).getDeclaringClass().getName() != null; // anonymous does not have names
    }
    
    private static Element getCurrentElement(final BaseDocument doc, int offset) {
        JMIUtils utils = JMIUtils.get(doc);
        Resource resource = utils.getResource();
        JavaModel.setClassPath(resource);
        return resource.getElementByOffset(offset);
    }
    
    /** Tests whether the given offset is inside of an inner class or not. */
    private static boolean isInnerClass(final BaseDocument doc, final int offset) {
        boolean isInner = true; // safer fallback
        JavaModel.getJavaRepository().beginTrans(true);
        try {
            Element el = getCurrentElement(doc, offset);
            while (el != null && !(el instanceof JavaClass)) {
                el = (Element) el.refImmediateComposite();
            }
            isInner = el != null ? ((JavaClass) el).isInner() : false;
        } finally {
            JavaModel.getJavaRepository().endTrans();
        }
        return isInner;
    }
    
    private static BrowserPath getBrowserPath(final Element el) {
        BrowserPath path = null;
        if (el instanceof Method || el instanceof Constructor) {
            path = new BrowserPath();
            path.addPathComponent(new CategoryHolder(BrowserTreeNode.NODE_METHOD));
            CallableFeature cf = (CallableFeature) el;
            path.addPathComponent(new ReferenceHolder(
                    (el instanceof Constructor) ? "<init>" : cf.getName(),
                    getJVMMethodTypeDescriptor(cf)));
            path.addPathComponent(new IndexHolder(0));
        } // else unsupported
        return path;
    }
    
    private static Element getOuterInstanceOf(final Class clazz,
            final BaseDocument doc, final int offset) {
        assert Element.class.isAssignableFrom(clazz);
        JMIUtils utils = JMIUtils.get(doc);
        Resource resource = utils.getResource();
        JavaModel.setClassPath(resource);
        Element el = resource.getElementByOffset(offset);
        while (el != null && !(clazz.isInstance(el))) {
            el = (Element) el.refImmediateComposite();
        }
        return el;
    }
    
    private static String getJVMMethodTypeDescriptor(final CallableFeature cf) {
        StringBuffer desc = new StringBuffer("(");
        List/*<Parameter>*/ params = cf.getParameters();
        for (Iterator it = params.iterator(); it.hasNext();) {
            Parameter param = (Parameter) it.next();
            desc.append(getJVMFieldType(param.getType()));
        }
        desc.append(')');
        desc.append((cf instanceof Constructor) ?
            String.valueOf(Util.VOID_TAG) : getJVMFieldType(cf.getType()));
        return desc.toString();
    }
    
    private static String getJVMFieldType(final Type type) {
        Resource res = type.getResource();
        String name;
        if (res != null) {
            String dotPkgName = res.getPackageName();
            if (dotPkgName != null && dotPkgName.length() > 0) {
                dotPkgName += '.';
            }
            String typeName = type.getName();
            if (dotPkgName.length() > typeName.length()) {
                name = typeName;
            } else {
                String cName = typeName.substring(dotPkgName.length()).replace('.', '$');
                name = dotPkgName + cName;
            }
        } else {
            name = type.getName();
        }
        StringBuffer jvmFieldType = new StringBuffer();
        if (type instanceof Array) {
            int bracketPos = name.indexOf('[');
            String base = name.substring(0, bracketPos);
            int dimension = name.substring(bracketPos).length() / 2;
            for (int i = 0; i < dimension; i++) {
                jvmFieldType.append('[');
            }
            jvmFieldType.append(getJVMFieldType(base));
        } else {
            jvmFieldType.append(getJVMFieldType(name));
        }
        return jvmFieldType.toString();
    }
    
    /** <strong>Note:</strong> package private for unit tests only. */
    static String getJVMFieldType(String nonArrayType) {
        String type;
        assert nonArrayType.indexOf('[') == -1 : "Called for array type";
        Character c = (Character) PARAM_TO_JVM_BASE_TYPE.get(nonArrayType);
        if (c != null) {
            type = c.toString();
        } else {
            type = 'L' + nonArrayType.replace('.', '/') + ';';
        }
        return type;
    }
    
}
