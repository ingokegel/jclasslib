/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.attributes.ByteCodeDocument.DocumentLink;
import org.gjt.jclasslib.structures.attributes.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.text.AbstractDocument.AbstractElement;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
    Detail pane showing the bytecode of a <tt>Code</tt> attribute.

    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class CodeAttributeByteCodeDetailPane extends AbstractDetailPane {

    private static final Rectangle origin = new Rectangle(0, 0, 0, 0);

    private static final int LINE_NUMBERS_OFFSET = 9;

    private static StyleContext styles = new StyleContext();
    private static WeakHashMap attributeToByteCodeDocument = new WeakHashMap();

    private Dimension opcodeCounterSize = new Dimension();
    private int characterWidth;

    private DocumentLinkListener listener;

    // Visual components

    private JTextPane byteCodeTextPane;
    private JTextPane opcodeCounterTextPane;
    private JScrollPane scrollPane;

    public CodeAttributeByteCodeDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupComponent() {
        setLayout(new BorderLayout());


        add(buildByteCodeScrollPane(), BorderLayout.CENTER);

        listener = new DocumentLinkListener(byteCodeTextPane);
    }
    
    public void show(TreePath treePath) {

        CodeAttribute attribute = (CodeAttribute)findAttribute(treePath);
        
        ByteCodeDocument byteCodeDocument = getCachedByteCodeDocument(attribute);
        
        if (byteCodeTextPane.getDocument() != byteCodeDocument) {

            byteCodeTextPane.setDocument(byteCodeDocument);
            opcodeCounterTextPane.setDocument(byteCodeDocument.getOpcodeCounterDocument());
            
            byteCodeTextPane.setCaretPosition(0);
            byteCodeTextPane.scrollRectToVisible(origin);

            opcodeCounterSize.width = characterWidth * byteCodeDocument.getOpcodeCounterWidth() + LINE_NUMBERS_OFFSET;
            opcodeCounterTextPane.setMinimumSize(opcodeCounterSize);
            opcodeCounterTextPane.setPreferredSize(opcodeCounterSize);
            
            opcodeCounterTextPane.invalidate();
            scrollPane.validate();
        }

    }
    
    /**
        Scroll the bytecode to a specified bytecode offset.
        @param offset the offset
     */
    public void scrollToOffset(int offset) {

        ByteCodeDocument byteCodeDocument = (ByteCodeDocument)byteCodeTextPane.getDocument();
        int position = byteCodeDocument.getPosition(offset);
        try {
            Rectangle target = byteCodeTextPane.modelToView(position);
            target.height = byteCodeTextPane.getHeight();
            byteCodeTextPane.setCaretPosition(position);
            byteCodeTextPane.scrollRectToVisible(target);

        } catch (BadLocationException ex) {
        }
    }
    
    private JScrollPane buildByteCodeScrollPane() {

        byteCodeTextPane = new JTextPane();
        byteCodeTextPane.setEditable(false);

        // easiest way to prevent word wrapping in a JEditorPane without
        // subclassing is to wrap it in a panel so that its parent is not
        // of type JViewport
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(byteCodeTextPane, BorderLayout.CENTER);
        
        scrollPane = new JScrollPane(panel);
        scrollPane.setRowHeaderView(buildOpcodeCounterTextPane());
        
        return scrollPane;
    }
    
    
    private JTextPane buildOpcodeCounterTextPane() {
        opcodeCounterTextPane = new OpcodeCounterTextPane();
        opcodeCounterTextPane.setEditable(false);
        opcodeCounterTextPane.setEnabled(false);
        // the following line should but does not work (see OpcodeCounterTextPane)
        opcodeCounterTextPane.setAutoscrolls(false);
        
        characterWidth = opcodeCounterTextPane.getFontMetrics(
                            styles.getFont(ByteCodeDocument.STYLE_LINE_NUMBER)).charWidth('0');
        
        return opcodeCounterTextPane; 
        
    }
    
    private ByteCodeDocument getCachedByteCodeDocument(CodeAttribute attribute) {
        
        ByteCodeDocument byteCodeDocument = (ByteCodeDocument)attributeToByteCodeDocument.get(attribute);
        if (byteCodeDocument == null) {
            byteCodeDocument = createByteCodeDocument(attribute);
            attributeToByteCodeDocument.put(attribute, byteCodeDocument);
        }
        return byteCodeDocument;
    }

    private ByteCodeDocument createByteCodeDocument(CodeAttribute attribute) {

        ByteCodeDocument byteCodeDocument = new ByteCodeDocument(styles, attribute, services.getClassFile());
        
        return byteCodeDocument;
    }
    
    private void link(DocumentLink link) {
        
        int linkType = link.getType();
        int sourceOffset = link.getSourceOffset();
        updateHistory(sourceOffset);

        if (linkType == DocumentLink.CONSTANT_POOL_LINK) {
            ConstantPoolHyperlinkListener.link(services, link.getIndex());

        } else if (linkType == DocumentLink.OFFSET_LINK) {
            scrollToOffset(link.getIndex());
            
            int targetOffset = link.getIndex();
            updateHistory(targetOffset);
            
        }
    }
    
    private void updateHistory(int offset) {
        
        TreePath treePath = services.getBrowserComponent().getTreePane().getTreeView().getSelectionPath();

        BrowserHistory history = services.getBrowserComponent().getHistory();
        history.updateHistory(treePath, new Integer(offset));
    }
    
    private class DocumentLinkListener extends MouseAdapter
                                       implements MouseMotionListener
    {

        private JTextPane textPane;
        
        private Cursor defaultCursor;
        private int defaultCursorType;
        private Cursor handCursor;
        
        public DocumentLinkListener(JTextPane textPane) {
            this.textPane = textPane;
            textPane.addMouseListener(this);
            textPane.addMouseMotionListener(this);
            
            defaultCursor = Cursor.getDefaultCursor();
            defaultCursorType = defaultCursor.getType();
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        
        public void mouseClicked(MouseEvent event) {
            
            int position = textPane.viewToModel(event.getPoint());
            
            if (isLink(position)) {
                link(getLinkAttribute(position));
            }
        }

        public void mouseDragged(MouseEvent event) {
        }
        
        public void mouseMoved(MouseEvent event) {
            
            int position = textPane.viewToModel(event.getPoint());

            if (textPane.getCursor().getType() == defaultCursorType && isLink(position)) {
                textPane.setCursor(handCursor);
            } else if (!isLink(position)) {
                textPane.setCursor(defaultCursor);
            }
        }
        
        private boolean isLink(int position) {
            return getLinkAttribute(position) != null;
        }
        
        private DocumentLink getLinkAttribute(int position) {

            DefaultStyledDocument document = (DefaultStyledDocument)textPane.getDocument();
            AbstractElement element = (AbstractElement)document.getCharacterElement(position);
            return (DocumentLink)element.getAttribute(ByteCodeDocument.ATTRIBUTE_NAME_LINK);
        }

        
    }
    
    // setAutoScroll(false) does not successfully remove the Autoscroller 
    // set by BasicTextUI. Since OpcodeCounterTextPane should not be
    // scrollable by dragging, mouse motion events are ignored
    private class OpcodeCounterTextPane extends JTextPane {
        
        protected void processMouseMotionEvent(MouseEvent e) {
        }
    }
    
    
}

