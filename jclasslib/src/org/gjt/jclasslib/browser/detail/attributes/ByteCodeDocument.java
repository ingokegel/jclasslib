/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.bytecode.*;
import org.gjt.jclasslib.io.ByteCodeReader;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;

import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
    Document type for the bytecode in <tt>Code</tt> attributes.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-07-08 14:04:28 $
*/
public class ByteCodeDocument extends DefaultStyledDocument
                              implements Opcodes
{

    /** Name of the style which contains document links */
    public static final String ATTRIBUTE_NAME_LINK = "attributeLink";

    /** Style for normal text */
    public static final MutableAttributeSet STYLE_NORMAL;
    /** Style for small text */
    public static final MutableAttributeSet STYLE_SMALL;
    /** Style for hyperlinks */
    public static final MutableAttributeSet STYLE_LINK;
    /** Style for bytecode offsets */
    public static final MutableAttributeSet STYLE_OFFSET;
    /** Style for instruction names */
    public static final MutableAttributeSet STYLE_INSTRUCTION;
    /** Style for immediate values */
    public static final MutableAttributeSet STYLE_IMMEDIATE_VALUE;
    /** Style for line numbers */
    public static final MutableAttributeSet STYLE_LINE_NUMBER;

    private static final int LINE_NUMBERS_FONT_DIFF = 2;

    static {
        STYLE_NORMAL = new SimpleAttributeSet();

        STYLE_LINK = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_LINK, new Color(0,128,0));
        StyleConstants.setBold(STYLE_LINK, true);
        StyleConstants.setUnderline(STYLE_LINK, true);

        STYLE_OFFSET = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_OFFSET, new Color(128,0,0));

        STYLE_INSTRUCTION = new SimpleAttributeSet();
        StyleConstants.setBold(STYLE_INSTRUCTION, true);

        STYLE_IMMEDIATE_VALUE = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_IMMEDIATE_VALUE, Color.magenta);
        StyleConstants.setBold(STYLE_IMMEDIATE_VALUE, true);

        STYLE_LINE_NUMBER = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_LINE_NUMBER, new Color(128,128,128));

        StyleConstants.setFontSize(STYLE_LINE_NUMBER, StyleConstants.getFontSize(STYLE_LINE_NUMBER) -
                                LINE_NUMBERS_FONT_DIFF);

        STYLE_SMALL = new SimpleAttributeSet();
        StyleConstants.setFontSize(STYLE_SMALL, StyleConstants.getFontSize(STYLE_SMALL) -
                                1);

    }

    private StyleContext styles;
    private CodeAttribute attribute;
    private ClassFile classFile;

    private HashMap offsetToPosition = new HashMap();
    private DefaultStyledDocument opcodeCounterDocument;
    private int opcodeCounterWidth;
    private int offsetWidth;

    /**
        Construct a bytecode document with a specified style cache,
        an associated <tt>Code</tt> attribute and a parent class file.
        @param styles the style cache
        @param attribute the <tt>Code</tt> attribute
        @param classFile the class file
     */
    public ByteCodeDocument(StyleContext styles,
                            CodeAttribute attribute,
                            ClassFile classFile)
    {
        super(styles);
        this.styles = styles;
        this.attribute = attribute;
        this.classFile = classFile;
        putProperty("tabSize", new Integer(4));

        setupDocument();

    }

    /**
        Get the document containing the opcode counters.
        @return the document
     */
    public DefaultStyledDocument getOpcodeCounterDocument() {
        return opcodeCounterDocument;
    }

    /**
        Get the width of the document containing the opcode counters.
        @return the width
     */
    public int getOpcodeCounterWidth() {
        return opcodeCounterWidth;
    }

    /**
        Get the position in the document which corresponds to a bytecode offset.
        @param offset the offset
        @return the position
     */
    public int getPosition(int offset) {
        Integer position = (Integer)offsetToPosition.get(new Integer(offset));
        if (position == null) {
            return 0;
        } else {
            return position.intValue();
        }
    }

    private void setupDocument() {

        byte[] code = attribute.getCode();

        try {
            java.util.List instructions = ByteCodeReader.readByteCode(code);

            calculateOffsetWidth(instructions);

            int[] linesPerOpcode = new int[instructions.size()];

            Iterator it = instructions.iterator();
            AbstractInstruction currentInstruction;
            int instructionCount = 0;
            while (it.hasNext()) {
                currentInstruction = (AbstractInstruction)it.next();
                linesPerOpcode[instructionCount++] = addInstructionToDocument(currentInstruction);
            }

            createOpcodeCounterDocument(linesPerOpcode);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void calculateOffsetWidth(java.util.List instructions) {

        int numberOfInstructions = instructions.size();

        if (numberOfInstructions > 0) {
            AbstractInstruction lastInstruction = (AbstractInstruction)instructions.get(numberOfInstructions - 1);
            offsetWidth = String.valueOf(lastInstruction.getOffset()).length();
        } else {
            offsetWidth = 1;
        }
    }

    private int addInstructionToDocument(AbstractInstruction instruction) {

        int offset = instruction.getOffset();

        addOffsetReference(offset);

        appendString(getPaddedValue(offset, offsetWidth),
                     STYLE_OFFSET);

        appendString(" " + instruction.getOpcodeVerbose(),
                     STYLE_INSTRUCTION);

        int additionalLines = addOpcodeSpecificInfo(instruction);

        appendString("\n", STYLE_NORMAL);

        return additionalLines + 1;
    }

    private void addOffsetReference(int offset) {

        offsetToPosition.put(new Integer(offset),
                             new Integer(getLength()));
    }

    private int addOpcodeSpecificInfo(AbstractInstruction instruction) {

        int additionalLines = 0;

        if (instruction instanceof ImmediateByteInstruction) {
            additionalLines +=
                addImmediateByteSpecificInfo((ImmediateByteInstruction)instruction);
        } else if (instruction instanceof ImmediateShortInstruction) {
            additionalLines +=
                addImmediateShortSpecificInfo((ImmediateShortInstruction)instruction);
        } else if (instruction instanceof ImmediateIntInstruction) {
            additionalLines +=
                addImmediateIntSpecificInfo((ImmediateIntInstruction)instruction);
        } else if (instruction instanceof BranchInstruction) {
            additionalLines +=
                addBranchSpecificInfo((BranchInstruction)instruction);
        } else if (instruction instanceof TableSwitchInstruction) {
            additionalLines +=
                addTableSwitchSpecificInfo((TableSwitchInstruction)instruction);
        } else if (instruction instanceof LookupSwitchInstruction) {
            additionalLines +=
                addLookupSwitchSpecificInfo((LookupSwitchInstruction)instruction);
        }

        return additionalLines;
    }

    private int addImmediateByteSpecificInfo(ImmediateByteInstruction instruction) {

        int opcode = instruction.getOpcode();
        int sourceOffset = instruction.getOffset();
        int immediateByte = instruction.getImmediateByte();

        if (opcode == OPCODE_LDC) {
            addConstantPoolLink(immediateByte, sourceOffset);
        } else if (opcode == OPCODE_NEWARRAY) {
            String verbose = OpcodesUtil.getArrayTypeVerbose(immediateByte);
            appendString(" " + immediateByte + " (" + verbose + ")",
                         STYLE_IMMEDIATE_VALUE);

        } else {
            appendString(" " + immediateByte,
                         STYLE_IMMEDIATE_VALUE);

            if (instruction instanceof IncrementInstruction) {
                appendString(" by", STYLE_NORMAL);
                appendString(" " + ((IncrementInstruction)instruction).getIncrementConst(),
                             STYLE_IMMEDIATE_VALUE);
            }
        }
        return 0;
    }

    private int addImmediateShortSpecificInfo(ImmediateShortInstruction instruction) {

        int opcode = instruction.getOpcode();
        int sourceOffset = instruction.getOffset();
        int immediateShort = instruction.getImmediateShort();

        if (opcode == OPCODE_SIPUSH) {
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

        return 0;
    }

    private int addImmediateIntSpecificInfo(ImmediateIntInstruction instruction) {

        int immediateInt = instruction.getImmediateInt();
        int sourceOffset = instruction.getOffset();

        addConstantPoolLink(immediateInt, sourceOffset);

        return 0;
    }

    private int addBranchSpecificInfo(BranchInstruction instruction) {

        int branchOffset = instruction.getBranchOffset();
        int instructionOffset = instruction.getOffset();

        addOffsetLink(branchOffset, instructionOffset);

        return 0;
    }

    private int addTableSwitchSpecificInfo(TableSwitchInstruction instruction) {

        int instructionOffset = instruction.getOffset();
        int lowByte = instruction.getLowByte();
        int highByte = instruction.getHighByte();
        int[] jumpOffsets = instruction.getJumpOffsets();

        appendString(" " + lowByte + " to " + highByte + "\n",
                     STYLE_IMMEDIATE_VALUE);

        for (int i = 0; i <= highByte - lowByte; i++) {
            appendString("\u0009" + (i + lowByte) + ": ", STYLE_IMMEDIATE_VALUE);
            addOffsetLink(jumpOffsets[i], instructionOffset);
            appendString("\n", STYLE_IMMEDIATE_VALUE);

        }
        appendString("\u0009default: ", STYLE_IMMEDIATE_VALUE);
        addOffsetLink(instruction.getDefaultOffset(), instructionOffset);

        return highByte - lowByte + 2;
    }

    private int addLookupSwitchSpecificInfo(LookupSwitchInstruction instruction) {

        int instructionOffset = instruction.getOffset();
        java.util.List matchOffsetPairs = instruction.getMatchOffsetPairs();
        int matchOffsetPairsCount = matchOffsetPairs.size();

        appendString(" " + matchOffsetPairsCount + "\n",
                     STYLE_IMMEDIATE_VALUE);

        MatchOffsetPair matchOffsetPairEntry;
        for (int i = 0; i < matchOffsetPairsCount; i++) {
            matchOffsetPairEntry = (MatchOffsetPair)matchOffsetPairs.get(i);
            appendString("\u0009" + matchOffsetPairEntry.getMatch() + ": ",
                         STYLE_IMMEDIATE_VALUE);
            addOffsetLink(matchOffsetPairEntry.getOffset(), instructionOffset);
            appendString("\n", STYLE_IMMEDIATE_VALUE);

        }
        appendString("\u0009default: ", STYLE_IMMEDIATE_VALUE);
        addOffsetLink(instruction.getDefaultOffset(), instructionOffset);

        return matchOffsetPairsCount + 1;
    }

    private void addConstantPoolLink(int constantPoolIndex, int sourceOffset) {

        AttributeSet currentLinkStyle =
                styles.addAttribute(STYLE_LINK,
                                    ATTRIBUTE_NAME_LINK,
                                    new DocumentLink(constantPoolIndex,
                                                     sourceOffset,
                                                     DocumentLink.CONSTANT_POOL_LINK));

        appendString(" ", STYLE_NORMAL);
        appendString("#" + constantPoolIndex, currentLinkStyle);
        try {
            String name = classFile.getConstantPoolEntryName(constantPoolIndex);
            if (name.length() > 0) {
                appendString(" <" + name + ">", STYLE_SMALL);
            }
        } catch (InvalidByteCodeException ex) {
        }
    }

    private void addOffsetLink(int branchOffset, int instructionOffset) {

        int totalOffset = branchOffset + instructionOffset;

        AttributeSet currentLinkStyle =
                styles.addAttribute(STYLE_LINK,
                                    ATTRIBUTE_NAME_LINK,
                                    new DocumentLink(totalOffset,
                                                     instructionOffset,
                                                     DocumentLink.OFFSET_LINK));

        appendString(" ", STYLE_NORMAL);
        appendString(String.valueOf(totalOffset), currentLinkStyle);

        appendString(" (" + (branchOffset > 0 ? "+" : "") + String.valueOf(branchOffset) + ")",
                     STYLE_IMMEDIATE_VALUE);
    }


    private void appendString(String string, AttributeSet attributes) {

        try {
            insertString(getLength(), string, attributes);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void createOpcodeCounterDocument(int[] linesPerOpcode) {

        opcodeCounterDocument = new DefaultStyledDocument(styles);
        int numberOfOpcodes = linesPerOpcode.length;

        opcodeCounterWidth = String.valueOf(numberOfOpcodes - 1).length();

        try {
            for (int i = 0; i < numberOfOpcodes; i++) {
                opcodeCounterDocument.insertString(opcodeCounterDocument.getLength(),
                                               getPaddedValue(i, opcodeCounterWidth),
                                               STYLE_LINE_NUMBER);
                for (int j = 0; j < linesPerOpcode[i]; j++) {
                    opcodeCounterDocument.insertString(opcodeCounterDocument.getLength(),
                                                   "\n",
                                                   ByteCodeDocument.STYLE_NORMAL);
                }
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

    }

    private static String getPaddedValue(int number, int width) {

        StringBuffer buffer = new StringBuffer();
        String value = String.valueOf(number);
        int valueLength = value.length();
        for (int i = valueLength; i < width; i++) {
            buffer.append(' ');
        }
        buffer.append(value);
        return buffer.toString();
    }

    /**
        A hyperlink in a bytecode document
     */
    public static class DocumentLink {

        /**
            Constant which indicated whether a document link is a
            constant pool link
         */
        public static int CONSTANT_POOL_LINK = 1;

        /**
            Constant which indicated whether a document link is a link
            to a different offset in the same bytecode document
         */
        public static int OFFSET_LINK = 2;

        private int index;
        private int sourceOffset;
        private int type;

        /**
            Constructs a <tt>DocumentLink</tt> which is either a constant pool
            link with a specified constant pool index or an offset link with a
            specified bytecode offset.

            @param index the constant pool index for constant pool links
            @param sourceOffset the bytecode offset for offset links
            @param type the type of the link, either <tt>CONSTANT_POOL_LINK</tt>
                        or <tt>OFFSET_LINK</tt>
         */
        public DocumentLink(int index, int sourceOffset, int type) {
            this.index = index;
            this.sourceOffset = sourceOffset;
            this.type = type;
        }

        /**
            Get the constant pool index for constant pool links.
            @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
            Get the bytecode offset for offset links.
            @return the offset
         */
        public int getSourceOffset() {
            return sourceOffset;
        }

        /**
            Get the type of the link.
            @return the type, either <tt>CONSTANT_POOL_LINK</tt>
                    or <tt>OFFSET_LINK</tt>
         */
        public int getType() {
            return type;
        }
    }

}

