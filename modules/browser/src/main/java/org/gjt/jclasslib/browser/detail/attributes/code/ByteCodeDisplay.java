/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code;

import org.gjt.jclasslib.browser.BrowserHistory;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener;
import org.gjt.jclasslib.bytecode.*;
import org.gjt.jclasslib.io.ByteCodeReader;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.io.IOException;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

/**
    Bytecode renderer.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ByteCodeDisplay extends JPanel implements Scrollable {

    /** Horizontal margin. */
    public static final int MARGIN_X = 3;
    /** Vertical margin. */
    public static final int MARGIN_Y = 3;

    /** Border for the renderer. */
    public static final Border BORDER = new EmptyBorder(MARGIN_Y, MARGIN_X, MARGIN_Y, MARGIN_X);

    private static Map<TextAttribute, Object> STYLE_BASE;
    private static Map<TextAttribute, Object> STYLE_NORMAL;
    private static Map<TextAttribute, Object> STYLE_SMALL;
    private static Map<TextAttribute, Object> STYLE_LINK;
    private static Map<TextAttribute, Object> STYLE_OFFSET;
    private static Map<TextAttribute, Object> STYLE_INSTRUCTION;
    private static Map<TextAttribute, Object> STYLE_IMMEDIATE_VALUE;

    private static final String TAB_STRING = "        ";

    static {
        initStyles(null);
    }

    public static void initStyles(Font baseFont) {

        STYLE_BASE = new HashMap<TextAttribute, Object>(2);
        if (baseFont != null) {
            STYLE_BASE.put(TextAttribute.FAMILY, baseFont.getFamily());
        } else {
            baseFont = UIManager.getFont("TextArea.font");
            STYLE_BASE.put(TextAttribute.FAMILY, "MonoSpaced");
        }

        STYLE_BASE.put(TextAttribute.SIZE, (float)baseFont.getSize());

        STYLE_NORMAL = new HashMap<TextAttribute, Object>(0);

        STYLE_SMALL = new HashMap<TextAttribute, Object>(1);
        STYLE_SMALL.put(TextAttribute.SIZE, (float)(baseFont.getSize() - 1));

        STYLE_LINK = new HashMap<TextAttribute, Object>(3);
        STYLE_LINK.put(TextAttribute.FOREGROUND, new Color(0, 128, 0));
        STYLE_LINK.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        STYLE_LINK.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

        STYLE_OFFSET = new HashMap<TextAttribute, Object>(1);
        STYLE_OFFSET.put(TextAttribute.FOREGROUND, new Color(128, 0, 0));

        STYLE_INSTRUCTION = new HashMap<TextAttribute, Object>(1);
        STYLE_INSTRUCTION.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

        STYLE_IMMEDIATE_VALUE = new HashMap<TextAttribute, Object>(2);
        STYLE_IMMEDIATE_VALUE.put(TextAttribute.FOREGROUND, Color.magenta);
        STYLE_IMMEDIATE_VALUE.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    }

    private ByteCodeDetailPane detailPane;

    private CodeAttribute codeAttribute;
    private ClassFile classFile;

    private int offsetWidth;
    private String offsetBlank;
    private HashMap<Integer, Integer> offsetToLine = new HashMap<Integer, Integer>();
    private ArrayList<AttributedString> lines = new ArrayList<AttributedString>();
    private ArrayList<String> textLines = new ArrayList<String>();
    private TextLayout[] textLayouts;
    private Map<Integer, BytecodeLink> lineToLink = new HashMap<Integer, BytecodeLink>();
    private Set<Instruction> invalidBranches = new HashSet<Instruction>();

    private LinkedList<LineCacheEntry> currentLineCache = new LinkedList<LineCacheEntry>();
    private FontRenderContext frc;
    private float currentHeight;
    private float currentWidth;
    private int lineHeight;
    private int ascent;
    private int characterWidth;

    /**
     * Get the left-padded value for a number.
     * @param number the number
     * @param width the total width.
     * @return the padded string.
     */
    public static String getPaddedValue(int number, int width) {

        StringBuilder buffer = new StringBuilder();
        String value = String.valueOf(number);
        int valueLength = value.length();
        for (int i = valueLength; i < width; i++) {
            buffer.append(' ');
        }
        buffer.append(value);
        return buffer.toString();
    }

    /**
     * Constructor.
     * @param detailPane the parent detail pane.
     */
    public ByteCodeDisplay(ByteCodeDetailPane detailPane) {
        this.detailPane = detailPane;

        setupComponent();
        setupEventHandlers();
    }

    // Scrollable

    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

        if (orientation == SwingConstants.HORIZONTAL) {
            return 10;
        } else {
            if (lineHeight == 0) {
                return 1;
            }
            int currentY = ((JViewport)getParent()).getViewPosition().y;
            float line = 1f * (currentY - MARGIN_Y) / lineHeight;
            int targetLine = (int)(direction < 0 ? Math.floor(line) - 1: Math.ceil(line) + 1);
            int targetY = MARGIN_Y + targetLine * lineHeight + 1;
            return Math.abs(currentY - targetY);
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

        JViewport viewport = (JViewport)getParent();
        if (orientation == SwingConstants.HORIZONTAL) {
            return viewport.getWidth();
        } else {
            if (lineHeight == 0) {
                return 1;
            }
            int currentY = viewport.getViewPosition().y;
            int rawTargetY = currentY + (direction < 0 ? -1 : 1) * viewport.getHeight();
            float line = 1f * (rawTargetY - MARGIN_Y) / lineHeight;
            int targetLine = (int)(direction < 0 ? Math.ceil(line): Math.floor(line));
            int targetY = MARGIN_Y + targetLine * lineHeight + 1;

            return Math.abs(currentY - targetY);
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    // end Scrollable


    /**
     * Get the currently displayed code attribute.
     * @return the code attribute.
     */
    public CodeAttribute getCodeAttribute() {
        return codeAttribute;
    }

    /**
     * Get the current line count.
     * @return the line count.
     */
    public int getLineCount() {
        return lines.size();
    }

    /**
     * Get the current line height.
     * @return the line height.
     */
    public int getLineHeight() {
        return lineHeight;
    }

    /**
     * Get the current line ascent.
     * @return the line ascent.
     */
    public int getAscent() {
        return ascent;
    }

    /**
     * Set the code attribute that is to be displayed.
     * @param codeAttribute the code attribute.
     * @param classFile the class file of the code attribute.
     */
    public void setCodeAttribute(CodeAttribute codeAttribute, ClassFile classFile) {
        this.codeAttribute = codeAttribute;
        this.classFile = classFile;
        frc = ((Graphics2D)getGraphics()).getFontRenderContext();
        setupTextLayouts();
        invalidate();
    }

    /**
     * Perform a link operation at a given point. Does nothing if there is no
     * link at this point.
     * @param point the point.
     */
    public void link(Point point) {

        BytecodeLink link = getLink(point);
        if (link == null) {
            return;
        }
        updateHistory(link.sourceOffset);

        if (link instanceof ConstantPoolLink) {
            ConstantPoolHyperlinkListener.link(detailPane.getBrowserServices(), ((ConstantPoolLink)link).cpIndex);
        } else if (link instanceof OffsetLink) {
            int targetOffset = ((OffsetLink)link).targetOffset;
            scrollToOffset(targetOffset);
            updateHistory(targetOffset);
        }
    }

    /**
     * Returns whether there is a link below the given point.
     * @param point the point.
     * @return the value.
     */
    public boolean isLink(Point point) {
        return getLink(point) != null;
    }

    /**
     * Scroll the view to a given bytecode offset.
     * @param offset the bytecode offset.
     */
    public void scrollToOffset(int offset) {

        Integer line = offsetToLine.get(offset);
        if (line == null) {
            return;
        }
        Rectangle target = new Rectangle(0, line * lineHeight + MARGIN_Y + 1, 10, getParent().getHeight());
        scrollRectToVisible(target);
    }

    /**
     * Copy the view text to the clipboard.
     */
    public void copyToClipboard() {
        StringSelection stringSelection = new StringSelection(getClipboardText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }

    public String getClipboardText() {
        StringBuilder buffer = new StringBuilder();
        for (String line : textLines) {
            buffer.append(line);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    protected void paintComponent(Graphics graphics) {

        if (lineHeight == 0) {
            return;
        }

        Graphics2D g = (Graphics2D)graphics;
        g.translate(MARGIN_X, MARGIN_Y);
        Rectangle clipBounds = graphics.getClipBounds();
        Paint oldPaint = g.getPaint();
        g.setPaint(Color.WHITE);
        g.fill(clipBounds);
        g.setPaint(oldPaint);
        int startLine = Math.max(0, clipBounds.y / lineHeight - 1);
        int endLine = Math.min(lines.size(), (clipBounds.y + clipBounds.height) / lineHeight + 1);
        for (int i = startLine; i < endLine; i++) {
            TextLayout textLayout = getOrCreateTextLayout(i);
            textLayout.draw(g, 0, i * lineHeight + textLayout.getAscent());
        }

        g.translate(-MARGIN_X, -MARGIN_Y);
    }

    private TextLayout getOrCreateTextLayout(int i) {

        TextLayout textLayout = textLayouts[i];
        if (textLayout == null) {
            textLayout = textLayouts[i] = new TextLayout((lines.get(i)).getIterator(), frc);
        }
        return textLayout;
    }

    private void setupComponent() {

        setBorder(BORDER);
        setDoubleBuffered(false);
        setOpaque(false);
    }

    private void setupEventHandlers() {
    }

    private BytecodeLink getLink(Point point) {

        if (lineHeight == 0) {
            return null;
        }
        int x = point.x - MARGIN_X;
        int y = point.y - MARGIN_Y;
        int line = y / lineHeight;
        BytecodeLink link = lineToLink.get(line);
        if (link == null) {
            return null;
        }

        TextLayout textLayout = getOrCreateTextLayout(line);
        TextHitInfo textHitInfo = textLayout.hitTestChar(x, y - line * lineHeight);
        int charIndex = textHitInfo.getCharIndex();
        if (charIndex >= link.startCharIndex && charIndex < link.endCharIndex) {
            return link;
        } else {
            return null;
        }
    }

    private void updateHistory(int offset) {

        BrowserServices services = detailPane.getBrowserServices();
        TreePath treePath = services.getBrowserComponent().getTreePane().getTree().getSelectionPath();

        BrowserHistory history = services.getBrowserComponent().getHistory();
        history.updateHistory(treePath, offset);
    }

    private void setupTextLayouts() {

        lineHeight = 0;
        currentHeight = 0f;
        currentWidth = 0f;
        textLines.clear();
        lines.clear();
        textLayouts = null;
        offsetToLine.clear();
        lineToLink.clear();
        invalidBranches.clear();


        byte[] code = codeAttribute.getCode();

        try {
            ArrayList<Instruction> instructions = ByteCodeReader.readByteCode(code);
            verifyOffsets(instructions);

            calculateOffsetWidth(instructions);
            detailPane.setCurrentInstructions(instructions);

            for (Instruction instruction : instructions) {
                addInstructionToDocument(instruction);
            }
            textLayouts = new TextLayout[lines.size()];
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setPreferredSize(new Dimension((int)currentWidth + 2 * MARGIN_X, (int)currentHeight + 2 * MARGIN_Y));
    }

    private void verifyOffsets(ArrayList<Instruction> instructions) {
        int instructionsLength = instructions.size();
        for (int i = 0; i < instructionsLength; i++) {
            Instruction instruction = instructions.get(i);
            if (instruction instanceof AbstractBranchInstruction) {
                int branchOffset = ((AbstractBranchInstruction)instruction).getBranchOffset();
                int targetDistance = 0;
                if (branchOffset > 0) {
                    branchOffset -= instruction.getSize();
                    while (branchOffset > 0 && i + targetDistance + 1 < instructionsLength) {
                        ++targetDistance;
                        branchOffset -= (instructions.get(i + targetDistance)).getSize();
                    }
                } else {
                    while (branchOffset < 0 && i + targetDistance > 0) {
                        --targetDistance;
                        branchOffset += (instructions.get(i + targetDistance)).getSize();
                    }
                }
                if (branchOffset != 0) {
                    invalidBranches.add(instruction);
                }
            }
        }
    }

    private void calculateOffsetWidth(java.util.List<Instruction> instructions) {

        int numberOfInstructions = instructions.size();

        if (numberOfInstructions > 0) {
            Instruction lastInstruction = instructions.get(numberOfInstructions - 1);
            offsetWidth = String.valueOf(lastInstruction.getOffset()).length();
        } else {
            offsetWidth = 1;
        }
        StringBuilder buffer = new StringBuilder(offsetWidth);
        for (int i = 0; i  < offsetWidth; i++) {
            buffer.append(' ');
        }
        offsetBlank = buffer.toString();
    }


    private void addInstructionToDocument(Instruction instruction) {

        int offset = instruction.getOffset();

        addOffsetReference(offset);

        appendString(getPaddedValue(offset, offsetWidth),
                STYLE_OFFSET);

        appendString(" " + instruction.getOpcode().getVerbose(),
                STYLE_INSTRUCTION);

        addOpcodeSpecificInfo(instruction);

        newLine();

    }

    private void addOffsetReference(int offset) {
        offsetToLine.put(offset, getCurrentLine());
    }

    private void addOpcodeSpecificInfo(Instruction instruction) {

        if (instruction instanceof ImmediateByteInstruction) {
            addImmediateByteSpecificInfo((ImmediateByteInstruction)instruction);
        } else if (instruction instanceof ImmediateShortInstruction) {
            addImmediateShortSpecificInfo((ImmediateShortInstruction)instruction);
        } else if (instruction instanceof AbstractBranchInstruction) {
            addBranchSpecificInfo((AbstractBranchInstruction)instruction);
        } else if (instruction instanceof TableSwitchInstruction) {
            addTableSwitchSpecificInfo((TableSwitchInstruction)instruction);
        } else if (instruction instanceof LookupSwitchInstruction) {
            addLookupSwitchSpecificInfo((LookupSwitchInstruction)instruction);
        }
    }

    private void addImmediateByteSpecificInfo(ImmediateByteInstruction instruction) {

        Opcode opcode = instruction.getOpcode();
        int sourceOffset = instruction.getOffset();
        int immediateByte = instruction.getImmediateByte();

        if (opcode == Opcode.LDC) {
            addConstantPoolLink(immediateByte, sourceOffset);
        } else if (opcode == Opcode.NEWARRAY) {
            String verbose;
            try {
                verbose = NewArrayType.Companion.getFromTag(immediateByte).getVerbose();
            } catch (InvalidByteCodeException e) {
                verbose = "invalid array type";
            }
            appendString(" " + immediateByte + " (" + verbose + ")",
                    STYLE_IMMEDIATE_VALUE);

        } else if (opcode == Opcode.BIPUSH) {
            appendString(" " + (byte)immediateByte, STYLE_IMMEDIATE_VALUE);

        } else {
            appendString(" " + immediateByte,
                    STYLE_IMMEDIATE_VALUE);

            if (instruction instanceof IncrementInstruction) {
                appendString(" by", STYLE_NORMAL);
                appendString(" " + ((IncrementInstruction)instruction).getIncrementConst(),
                        STYLE_IMMEDIATE_VALUE);
            }
        }
    }

    private void addImmediateShortSpecificInfo(ImmediateShortInstruction instruction) {

        Opcode opcode = instruction.getOpcode();
        int sourceOffset = instruction.getOffset();
        int immediateShort = instruction.getImmediateShort();

        if (opcode == Opcode.SIPUSH) {
            appendString(" " + immediateShort,
                    STYLE_IMMEDIATE_VALUE);
        } else {
            addConstantPoolLink(immediateShort, sourceOffset);

            if (instruction instanceof InvokeInterfaceInstruction) {
                appendString(" count " + ((InvokeInterfaceInstruction)instruction).getCount(),
                        STYLE_IMMEDIATE_VALUE);

            } else if (instruction instanceof MultianewarrayInstruction) {
                appendString(" dim " + ((MultianewarrayInstruction)instruction).getDimensions(),
                        STYLE_IMMEDIATE_VALUE);

            }
        }

    }

    private void addBranchSpecificInfo(AbstractBranchInstruction instruction) {

        int branchOffset = instruction.getBranchOffset();
        int instructionOffset = instruction.getOffset();

        addOffsetLink(branchOffset, instructionOffset);

        if (invalidBranches.contains(instruction)) {
            appendString(" [INVALID BRANCH]", STYLE_NORMAL);
        }

    }

    private void addTableSwitchSpecificInfo(TableSwitchInstruction instruction) {

        int instructionOffset = instruction.getOffset();
        int lowByte = instruction.getLowByte();
        int highByte = instruction.getHighByte();
        int[] jumpOffsets = instruction.getJumpOffsets();

        appendString(" " + lowByte + " to " + highByte, STYLE_IMMEDIATE_VALUE);
        newLine();

        for (int i = 0; i <= highByte - lowByte; i++) {
            appendString(offsetBlank + TAB_STRING + (i + lowByte) + ": ", STYLE_IMMEDIATE_VALUE);
            addOffsetLink(jumpOffsets[i], instructionOffset);
            newLine();

        }
        appendString(offsetBlank + TAB_STRING + "default: ", STYLE_IMMEDIATE_VALUE);
        addOffsetLink(instruction.getDefaultOffset(), instructionOffset);

    }

    private void addLookupSwitchSpecificInfo(LookupSwitchInstruction instruction) {

        int instructionOffset = instruction.getOffset();
        List matchOffsetPairs = instruction.getMatchOffsetPairs();
        int matchOffsetPairsCount = matchOffsetPairs.size();

        appendString(" " + matchOffsetPairsCount, STYLE_IMMEDIATE_VALUE);
        newLine();

        MatchOffsetPair matchOffsetPairEntry;
        for (Object matchOffsetPair : matchOffsetPairs) {
            matchOffsetPairEntry = (MatchOffsetPair)matchOffsetPair;
            appendString(offsetBlank + TAB_STRING + matchOffsetPairEntry.getMatch() + ": ",
                STYLE_IMMEDIATE_VALUE);
            addOffsetLink(matchOffsetPairEntry.getOffset(), instructionOffset);
            newLine();

        }
        appendString(offsetBlank + TAB_STRING + "default: ", STYLE_IMMEDIATE_VALUE);
        addOffsetLink(instruction.getDefaultOffset(), instructionOffset);

    }

    private void addConstantPoolLink(int constantPoolIndex, int sourceOffset) {

        appendString(" ", STYLE_NORMAL);
        int startCharIndex = getCurrentCharIndex();
        appendString("#" + constantPoolIndex, STYLE_LINK);
        int endCharIndex = getCurrentCharIndex();
        lineToLink.put(getCurrentLine(), new ConstantPoolLink(startCharIndex, endCharIndex, sourceOffset, constantPoolIndex));

        try {
            String name = classFile.getConstantPoolEntryName(constantPoolIndex);
            if (name.length() > 0) {
                appendString(" <" + name + ">", STYLE_SMALL);
            }
        } catch (InvalidByteCodeException ex) {
            appendString(" [INVALID]", STYLE_SMALL);
        }
    }

    private void addOffsetLink(int branchOffset, int sourceOffset) {

        int targetOffset = branchOffset + sourceOffset;

        appendString(" ", STYLE_NORMAL);
        int startCharIndex = getCurrentCharIndex();
        appendString(String.valueOf(targetOffset), STYLE_LINK);
        int endCharIndex = getCurrentCharIndex();
        lineToLink.put(getCurrentLine(), new OffsetLink(startCharIndex, endCharIndex, sourceOffset, targetOffset));

        appendString(" (" + (branchOffset > 0 ? "+" : "") + String.valueOf(branchOffset) + ")",
                STYLE_IMMEDIATE_VALUE);
    }

    private int getCurrentCharIndex() {

        int offset = 0;
        for (LineCacheEntry entry : currentLineCache) {
            offset += entry.text.length();
        }
        return offset;
    }

    private int getCurrentLine() {
        return lines.size();
    }

    private void appendString(String text, Map<TextAttribute, Object> attributes) {
        currentLineCache.add(new LineCacheEntry(text, attributes));
    }

    private void newLine() {

        String text = getCurrentLineText();
        AttributedString attrString = new AttributedString(text, STYLE_BASE);
        int startCharIndex = 0;
        for (LineCacheEntry entry : currentLineCache) {
            int endCharIndex = startCharIndex + entry.text.length();
            attrString.addAttributes(entry.attributes, startCharIndex, endCharIndex);
            startCharIndex = endCharIndex;
        }
        lines.add(attrString);
        textLines.add(text);

        if (lineHeight == 0) {
            TextLayout textLayout = new TextLayout(attrString.getIterator(), frc);
            lineHeight = (int)(textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading());
            ascent = (int)textLayout.getAscent();
            textLayout = new TextLayout("0", STYLE_BASE, frc);
            characterWidth = (int)textLayout.getAdvance();
        }
        currentHeight += lineHeight;
        currentWidth = Math.max(currentWidth, characterWidth * text.length());

        currentLineCache.clear();
    }

    private String getCurrentLineText() {

        StringBuilder buffer = new StringBuilder(getCurrentLineLength());
        for (LineCacheEntry entry : currentLineCache) {
            buffer.append(entry.text);
        }
        return buffer.toString();
    }

    private int getCurrentLineLength() {

        int length = 0;
        for (LineCacheEntry entry : currentLineCache) {
            length += entry.text.length();
        }
        return length;
    }

    private static class LineCacheEntry {

        private String text;
        private Map<TextAttribute, Object> attributes;

        private LineCacheEntry(String text, Map<TextAttribute, Object> attributes) {
            this.text = text;
            this.attributes = attributes;
        }
    }

    private static class BytecodeLink {

        private int startCharIndex;
        private int endCharIndex;
        protected int sourceOffset;

        private BytecodeLink(int startCharIndex, int endCharIndex, int sourceOffset) {
            this.startCharIndex = startCharIndex;
            this.endCharIndex = endCharIndex;
            this.sourceOffset = sourceOffset;
        }
    }

    private static class ConstantPoolLink extends BytecodeLink {

        private int cpIndex;

        private ConstantPoolLink(int startCharIndex, int endCharIndex, int sourceOffset, int cpIndex) {
            super(startCharIndex, endCharIndex, sourceOffset);
            this.cpIndex = cpIndex;
        }

    }

    private static class OffsetLink extends BytecodeLink {

        private int targetOffset;

        private OffsetLink(int startCharIndex, int endCharIndex, int sourceOffset, int targetOffset) {
            super(startCharIndex, endCharIndex, sourceOffset);
            this.targetOffset = targetOffset;
        }

    }

}
