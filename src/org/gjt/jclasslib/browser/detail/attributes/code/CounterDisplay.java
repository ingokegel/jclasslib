/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

/**
    Line number renderer used as a row header view for a <tt>BytecodeDisplay</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:19:37 $
*/
public class CounterDisplay extends JPanel {

    private static final Map STYLE;
    private static final Color COLOR_BACKGROUND = UIManager.getColor("Panel.background");

    static {
        Font baseFont = UIManager.getFont("TextArea.font");

        STYLE = new HashMap(3);
        STYLE.put(TextAttribute.FAMILY, baseFont.getFamily());
        STYLE.put(TextAttribute.SIZE, new Float(baseFont.getSize() - 2));
        STYLE.put(TextAttribute.FOREGROUND, new Color(92, 92, 92));
    }

    private int maxCount;
    private int lineHeight;
    private int ascent;

    private int maxChars;
    private FontRenderContext frc;

    /**
     * Constructor.
     */
    public CounterDisplay() {
        setBorder(ByteCodeDisplay.BORDER);
        setDoubleBuffered(false);
        setOpaque(false);
    }

    /**
     * Initialize with the properties of a given bytecode display.
     * @param byteCodeDisplay the bytecode display.
     */
    public void init(ByteCodeDisplay byteCodeDisplay) {

        this.maxCount = byteCodeDisplay.getLineCount();
        this.lineHeight = byteCodeDisplay.getLineHeight();
        this.ascent = byteCodeDisplay.getAscent();

        frc = ((Graphics2D)getGraphics()).getFontRenderContext();
        maxChars = Math.max(1, String.valueOf(maxCount).length());

        TextLayout textLayout = new TextLayout(getCharacterIterator(maxCount), frc);

        setPreferredSize(new Dimension((int)textLayout.getAdvance() + 2 * ByteCodeDisplay.MARGIN_X, maxCount * lineHeight + 2 * ByteCodeDisplay.MARGIN_Y));
        invalidate();
    }

    private AttributedCharacterIterator getCharacterIterator(int number) {
        AttributedString attrSting = new AttributedString(ByteCodeDisplay.getPaddedValue(number, maxChars), STYLE);

        return attrSting.getIterator();
    }

    protected void paintComponent(Graphics graphics) {

        if (maxCount == 0 || lineHeight == 0) {
            return;
        }

        Graphics2D g = (Graphics2D)graphics;
        g.translate(ByteCodeDisplay.MARGIN_X, ByteCodeDisplay.MARGIN_Y);
        Rectangle clipBounds = graphics.getClipBounds();
        Paint oldPaint = g.getPaint();
        g.setPaint(COLOR_BACKGROUND);
        g.fill(clipBounds);
        g.setPaint(oldPaint);

        int startLine = Math.max(0, clipBounds.y / lineHeight - 1);
        int endLine = Math.min(maxCount, (clipBounds.y + clipBounds.height) / lineHeight + 1);
        for (int i = startLine; i < endLine; i++) {
            TextLayout textLayout = new TextLayout(getCharacterIterator(i + 1), frc);
            textLayout.draw(g, 0, i * lineHeight + ascent);
        }

        g.translate(-ByteCodeDisplay.MARGIN_X, -ByteCodeDisplay.MARGIN_Y);
    }
}
