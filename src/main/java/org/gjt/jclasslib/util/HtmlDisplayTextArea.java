/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

public class HtmlDisplayTextArea extends JEditorPane implements TextDisplay {

    public static final Color COLOR_LINK = new Color(0, 128, 0);

    private static final Insets NO_MARGIN = new Insets(0, 0, 0, 0);

    public HtmlDisplayTextArea() {
        this(null);
    }

    public HtmlDisplayTextArea(String text) {
        setEditable(false);
        setBackground(UIManager.getColor("Label.background"));
        setRequestFocusEnabled(false);
        setFocusable(false);
        setMargin(NO_MARGIN);
        updateUI();

        setEditorKit(new HTMLEditorKit());
        Font font = UIManager.getFont("Label.font");
        StyleSheet css = ((HTMLDocument)getDocument()).getStyleSheet();
        setOpaque(false);

        css.addRule("body {color : #" + getHexValue(UIManager.getColor("Label.foreground")) + " }");
        css.addRule("body {font-size : " + font.getSize() + "pt; }");
        css.addRule("body {font-family :" + font.getFontName() + "; }");
        setInverted(false);

        if (text != null) {
            setText(text);
        }
    }

    public void setInverted(boolean inverted) {
        Document document = getDocument();
        if (document instanceof HTMLDocument) {
            HTMLDocument htmlDocument = (HTMLDocument)document;
            StyleSheet css = htmlDocument.getStyleSheet();
            css.addRule("a {color : #" + getHexValue(inverted ? getForeground() : COLOR_LINK) + " }");
            css.addRule("a:active {color : #" + getHexValue(inverted ? getForeground() : COLOR_LINK) + " }");
        }
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension minimumSize = super.getMinimumSize();
        minimumSize.width = 0;
        return minimumSize;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        Document document = getDocument();
        if (document instanceof HTMLDocument) {
            HTMLDocument htmlDocument = (HTMLDocument)document;
            StyleSheet css = htmlDocument.getStyleSheet();
            css.addRule("body {color : #" + getHexValue(fg) + " }");
        }
    }

    private String getHexValue(Color color) {
        StringBuilder buffer = new StringBuilder();
        appendComponent(buffer, color.getRed());
        appendComponent(buffer, color.getGreen());
        appendComponent(buffer, color.getBlue());
        return buffer.toString();
    }

    private void appendComponent(StringBuilder buffer, int component) {
        buffer.append(padLeft(Integer.toHexString(component), '0', 2));
    }

    public static String padLeft(String val, char padChar, int length) {
        StringBuilder buffer = new StringBuilder(length);
        for (int i=0; i< length - val.length(); i++) {
            buffer.append(padChar);
        }
        buffer.append(val.substring(0, Math.min(length, val.length())));
        return buffer.toString();
    }

    @Override
    public void setText(String text) {
        if (!text.startsWith("<html>")) {
            text = "<html>" + text;
        }
        super.setText(text);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d = (d == null) ? new Dimension(400, 400) : d;
        if (d.height == 0) {
            d.height = 10;
        }
        return d;
    }

    @Override
    public int getBaseline(int width, int height) {
        Insets insets = getInsets();
        View rootView = getUI().getRootView(this);
        if (rootView.getViewCount() > 0) {
            height = height - insets.top - insets.bottom;
            int baseline = insets.top;
            int fieldBaseline = getBaseline(
                    rootView.getView(0), width - insets.left -
                            insets.right, height);
            if (fieldBaseline < 0) {
                return -1;
            }
            return baseline + fieldBaseline;
        }
        return -1;
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return BaselineResizeBehavior.CONSTANT_ASCENT;
    }

    // static methods copied from BasicHTML
    private static int getBaseline(View view, int w, int h) {
        if (hasParagraph(view)) {
            view.setSize(w, h);
            return getBaseline(view, new Rectangle(0, 0, w, h));
        }
        return -1;
    }

    private static int getBaseline(View view, Shape bounds) {
        if (view.getViewCount() == 0) {
            return -1;
        }
        AttributeSet attributes = view.getElement().getAttributes();
        Object name = null;
        if (attributes != null) {
            name = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int index = 0;
        if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
            // For html on widgets the header is not visible, skip it.
            index++;
        }
        bounds = view.getChildAllocation(index, bounds);
        if (bounds == null) {
            return -1;
        }
        View child = view.getView(index);
        if (view instanceof javax.swing.text.ParagraphView) {
            Rectangle rect;
            if (bounds instanceof Rectangle) {
                rect = (Rectangle)bounds;
            } else {
                rect = bounds.getBounds();
            }
            return rect.y + (int)(rect.height *
                    child.getAlignment(View.Y_AXIS));
        }
        return getBaseline(child, bounds);
    }

    private static boolean hasParagraph(View view) {
        if (view instanceof javax.swing.text.ParagraphView) {
            return true;
        }
        if (view.getViewCount() == 0) {
            return false;
        }
        AttributeSet attributes = view.getElement().getAttributes();
        Object name = null;
        if (attributes != null) {
            name = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int index = 0;
        if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
            // For html on widgets the header is not visible, skip it.
            index = 1;
        }
        return hasParagraph(view.getView(index));
    }

}
