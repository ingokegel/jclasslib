/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.attributes.*;

import java.util.*;

/**
    Contains all information necessary to insert code into a
    method. Allows for pre and post insertions. The core method
    to perform code insertions is the static <tt>apply</tt> method.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.9 $ $Date: 2005-06-22 13:56:37 $
*/
public class CodeInsertion {


    /**
        Merge two code insertions into one.
        @param position the position of the resulting <tt>CodeInsertion</tt>
        @param shiftTarget should offsets of branch instructions pointing to 
                           the position of the resulting code insertion
                           be shifted or point to the beginning of the 
                           inserted code.
        @param inner the inner <tt>CodeInsertion</tt>
        @param outer the outer <tt>CodeInsertion</tt>
        @return the resulting <tt>CodeInsertion</tt>
    */
    public static CodeInsertion merge(int position,
                                      boolean shiftTarget,
                                      CodeInsertion inner,
                                      CodeInsertion outer)
    {

        if (outer == null) {
            return inner;
        }
        if (inner == null) {
            return outer;
        }

        AbstractInstruction[] preInstructions = mergeInstructions(outer.preInstructions, inner.preInstructions);
        AbstractInstruction[] postInstructions = mergeInstructions(inner.postInstructions, outer.postInstructions);

        CodeInsertion codeInsertion =
            new CodeInsertion(position,
                              preInstructions,
                              postInstructions,
                              shiftTarget);

        return codeInsertion;
    }

    /**
        Merge two arrays of instructions into one.
        @param firstInstructions the head array of type <tt>AbstractInstruction</tt>
        @param lastInstructions the tail array of type <tt>AbstractInstruction</tt>
        @return the merged array of type <tt>AbstractInstruction[]</tt>
    */
    public static AbstractInstruction[] mergeInstructions(AbstractInstruction[] firstInstructions,
                                                          AbstractInstruction[] lastInstructions)
    {
        if (firstInstructions== null) {
            return lastInstructions;
        }
        if (lastInstructions == null) {
            return firstInstructions;
        }

        AbstractInstruction[] mergedInstructions = new AbstractInstruction[firstInstructions.length + lastInstructions.length];
        System.arraycopy(firstInstructions, 0, mergedInstructions, 0, firstInstructions.length);
        System.arraycopy(lastInstructions, 0, mergedInstructions, firstInstructions.length, lastInstructions.length);

        return mergedInstructions;
    }

    /**
        Apply a list of <tt>CodeInsertion</tt>s to a list of instructions
        such as the one supplied by a <tt>ByteCodeReader</tt>. Offsets
        of branch instructions will be adapted to point to the original
        instructions. Exception and line number tables in the associated 
        <tt>CodeAttribute</tt>will also be updated.
     
        @param instructions the list of instructions which is to be treated
                            with the <tt>CodeInsertion</tt>s. This list
                            and the resulting instructions will not be usable
                            after the method is finished. If you need to 
                            reuse the original instructions, you have to pass
                            a deep-cloned list into this method.
        @param codeInsertions the list of codeInsertions which is to be applied to
                              the list of instructions.
        @param codeAttribute the <tt>CodeAttribute</tt> pertaining to the supplied 
                             list of instructions.
        @return the resulting list of instructions
        @throws InvalidByteCodeException
     */
    public static List apply(List instructions,
                             List codeInsertions,
                             CodeAttribute codeAttribute)

        throws InvalidByteCodeException
    {
        int instructionCount = instructions.size();
        int[] transformedIndices = new int[instructionCount];
        for (int i = 0; i < instructionCount; i++) {
            transformedIndices[i] = i;
        }

        List newInstructions = insertCode(instructions,
                                          codeInsertions,
                                          transformedIndices);

        int[] oldOffsets = new int[instructionCount];
        for (int i = 0; i < instructionCount; i++) {
            oldOffsets[i] = ((AbstractInstruction)instructions.get(i)).getOffset();
        }
        int[] newOffsets = new int[newInstructions.size()];
        calculateOffsets(newInstructions, newOffsets);


        adjustOffsets(instructions,
                      newInstructions,
                      oldOffsets,
                      newOffsets,
                      transformedIndices);

        if (codeAttribute != null) {
            adjustExceptionTable(oldOffsets,
                                 newOffsets,
                                 transformedIndices,
                                 codeAttribute);

            adjustLineNumberTable(oldOffsets,
                                  newOffsets,
                                  transformedIndices,
                                  codeAttribute);
        }

        applyOffsets(newInstructions, newOffsets);
        return newInstructions;
    }

    private static List insertCode(List instructions,
                                   List codeInsertions,
                                   int[] transformedIndices)
    {
        int instructionCount = instructions.size();
        int insertionCount = codeInsertions.size();

        int newSize = calculateNewSize(instructions, codeInsertions);
        List newInstructions = new ArrayList(newSize);

        int currentInsertionIndex = 0;
        CodeInsertion currentInsertion = (CodeInsertion)codeInsertions.get(0);
        for (int i = 0; i < instructionCount; i++) {
            if (currentInsertion.getPosition() < i && currentInsertionIndex < insertionCount - 1) {
                ++currentInsertionIndex;
                currentInsertion = (CodeInsertion)codeInsertions.get(currentInsertionIndex);
            }
            int addedBefore = 0;
            int addedAfter = 0;
            if (currentInsertion.getPosition() == i) {
                addedBefore = addInstructions(newInstructions, currentInsertion.getPreInstructions());
            }
            newInstructions.add(instructions.get(i));
            if (currentInsertion.getPosition() == i) {
                addedAfter = addInstructions(newInstructions, currentInsertion.getPostInstructions());
            }
            if (addedBefore > 0 || addedAfter > 0) {
                shiftIndices(i, addedBefore, addedAfter, transformedIndices, currentInsertion.isShiftTarget());
            }
        }

        return newInstructions;
    }

    private static int calculateNewSize(List instructions, List codeInsertions) {

        int insertionCount = codeInsertions.size();
        int newSize = instructions.size();
        for (int i = 0; i < insertionCount; i++) {
            CodeInsertion insertion = (CodeInsertion)codeInsertions.get(i);

            AbstractInstruction[] preInstructions = insertion.getPreInstructions();
            if (preInstructions != null) {
                newSize += preInstructions.length;
            }
            AbstractInstruction[] postInstructions = insertion.getPostInstructions();
            if (postInstructions != null) {
                newSize += postInstructions.length;
            }
        }
        return newSize;
    }

    private static void shiftIndices(int currentIndex,
                                     int addedBefore,
                                     int addedAfter,
                                     int[] transformedIndices,
                                     boolean shiftTarget)
    {
        if (!shiftTarget) {
            transformedIndices[currentIndex] += addedBefore;
        }
        for (int i = currentIndex + 1; i < transformedIndices.length; i++) {
            transformedIndices[i] += addedBefore + addedAfter;
        }
    }

    private static int addInstructions(List newInstructions,
                                       AbstractInstruction[] insertedInstructions)
    {
        if (insertedInstructions != null) {
            for (int i = 0; i < insertedInstructions.length; i++) {
                newInstructions.add(insertedInstructions[i]);
            }
            return insertedInstructions.length;
        } else {
            return 0;
        }
    }

    private static void calculateOffsets(List instructions,
                                         int[] offsets)
    {
        int instructionCount = instructions.size();
        int currentOffset = 0;
        for (int i = 0; i < instructionCount; i++) {
            offsets[i] = currentOffset;

            AbstractInstruction instr = (AbstractInstruction)instructions.get(i);

            int currentSize;
            if (instr instanceof PaddedInstruction) {
                currentSize = ((PaddedInstruction)instr).getPaddedSize(currentOffset);
            } else {
                currentSize = instr.getSize();
            }
            currentOffset += currentSize;
        }
    }

    private static void applyOffsets(List instructions, int[] offsets) {
        int instructionCount = instructions.size();
        for (int i = 0; i < instructionCount; i++) {
            AbstractInstruction instr = (AbstractInstruction)instructions.get(i);
            instr.setOffset(offsets[i]);
        }
    }

    private static void adjustOffsets(List instructions,
                                      List newInstructions,
                                      int[] oldOffsets,
                                      int[] newOffsets,
                                      int[] transformedIndices)
        throws InvalidByteCodeException
    {
        int instructionCount = instructions.size();
        for (int sourceIndex = 0; sourceIndex < instructionCount; sourceIndex++) {
            AbstractInstruction currentInstruction = (AbstractInstruction)instructions.get(sourceIndex);
            int branchOffset = getBranchOffset(currentInstruction);
            if (branchOffset == 0) {
                continue;
            }
            if (currentInstruction instanceof TableSwitchInstruction) {
                int[] jumpOffsets = ((TableSwitchInstruction)currentInstruction).getJumpOffsets();
                for (int i = 0; i < jumpOffsets.length; i++) {
                    int targetIndex = getBranchTargetIndex(instructions, sourceIndex, jumpOffsets[i]);
                    jumpOffsets[i] =
                        calculateNewBranchOffset(newInstructions, sourceIndex, targetIndex, transformedIndices, newOffsets);
                }
            } else if (currentInstruction instanceof LookupSwitchInstruction) {
                List matchOffsetPairs = ((LookupSwitchInstruction)currentInstruction).getMatchOffsetPairs();
                for (int i = 0; i < matchOffsetPairs.size(); i++) {
                    MatchOffsetPair matchOffsetPair =
                        (MatchOffsetPair)matchOffsetPairs.get(i);
                    int targetIndex = getBranchTargetIndex(instructions, sourceIndex, matchOffsetPair.getOffset());
                    matchOffsetPair.setOffset(
                        calculateNewBranchOffset(newInstructions, sourceIndex, targetIndex, transformedIndices, newOffsets)
                    );
                }
            }
            int targetIndex = getBranchTargetIndex(instructions, sourceIndex, branchOffset);

            setBranchOffset(
                currentInstruction,
                calculateNewBranchOffset(newInstructions, sourceIndex, targetIndex, transformedIndices, newOffsets)
            );

        }
    }

    private static int calculateNewBranchOffset(List newInstructions,
                                                int sourceIndex,
                                                int targetIndex,
                                                int[] transformedIndices,
                                                int[] newOffsets)
{
            int transformedSourceIndex = transformedIndices[sourceIndex];
            int transformedTargetIndex = transformedIndices[targetIndex];

            int newBranchOffset = newOffsets[transformedTargetIndex] - newOffsets[transformedSourceIndex];

            return newBranchOffset;
    }


    private static int getBranchOffset(AbstractInstruction instruction) {

        int branchOffset = 0;
        if (instruction instanceof TableSwitchInstruction) {
            branchOffset = ((TableSwitchInstruction)instruction).getDefaultOffset();
        } else if (instruction instanceof LookupSwitchInstruction) {
            branchOffset = ((LookupSwitchInstruction)instruction).getDefaultOffset();
        } else if (instruction instanceof AbstractBranchInstruction) {
            branchOffset = ((AbstractBranchInstruction)instruction).getBranchOffset();
        }
        return branchOffset;
    }

    private static void setBranchOffset(AbstractInstruction instruction,
                                        int branchOffset)
    {
        if (instruction instanceof TableSwitchInstruction) {
            ((TableSwitchInstruction)instruction).setDefaultOffset(branchOffset);
        } else if (instruction instanceof LookupSwitchInstruction) {
            ((LookupSwitchInstruction)instruction).setDefaultOffset(branchOffset);
        } else if (instruction instanceof AbstractBranchInstruction) {
            ((AbstractBranchInstruction)instruction).setBranchOffset(branchOffset);
        }
    }

    private static int getBranchTargetIndex(List instructions,
                                            int sourceIndex,
                                            int branchOffset)
        throws InvalidByteCodeException
    {
        int instructionsCount = instructions.size();
        int startOffset = ((AbstractInstruction)instructions.get(sourceIndex)).getOffset();
        int step = branchOffset > 0 ? 1 : -1;
        for (int i = sourceIndex + step; i >= 0 && i < instructionsCount; i += step) {
            int targetOffset = ((AbstractInstruction)instructions.get(i)).getOffset();
            if (targetOffset - startOffset == branchOffset) {
                return i;
            }
        }
        throw new InvalidByteCodeException("Invalid branch target");
    }

    private static void adjustExceptionTable(int[] oldOffsets,
                                             int[] newOffsets,
                                             int[] transformedIndices,
                                             CodeAttribute codeAttribute)
        throws InvalidByteCodeException
    {

        ExceptionTableEntry[] exceptionTable = codeAttribute.getExceptionTable();
        if (exceptionTable == null) {
            return;
        }

        for (int i = 0; i < exceptionTable.length; i++) {
            ExceptionTableEntry currentEntry = exceptionTable[i];
            int startPcIndex = Arrays.binarySearch(oldOffsets, currentEntry.getStartPc());
            int endPcIndex = Arrays.binarySearch(oldOffsets, currentEntry.getEndPc());
            int handlerPcIndex = Arrays.binarySearch(oldOffsets, currentEntry.getHandlerPc());
            if (startPcIndex < 0 || endPcIndex < 0 || handlerPcIndex < 0 ||
                startPcIndex == oldOffsets.length ||
                endPcIndex == oldOffsets.length ||
                handlerPcIndex == oldOffsets.length)
            {
                throw new InvalidByteCodeException("Invalid exception table");
            }
            currentEntry.setStartPc(newOffsets[transformedIndices[startPcIndex]]);
            currentEntry.setEndPc(newOffsets[transformedIndices[endPcIndex]]);
            currentEntry.setHandlerPc(newOffsets[transformedIndices[handlerPcIndex]]);
        }

   }

    private static void adjustLineNumberTable(int[] oldOffsets,
                                              int[] newOffsets,
                                              int[] transformedIndices,
                                              CodeAttribute codeAttribute)
        throws InvalidByteCodeException
    {

        LineNumberTableAttribute lineNumberTableAttribute =
            (LineNumberTableAttribute)codeAttribute.findAttribute(LineNumberTableAttribute.class);
        if (lineNumberTableAttribute == null) {
            return;
        }
        LineNumberTableEntry[] lineNumberTable = lineNumberTableAttribute.getLineNumberTable();

        for (int i = 0; i < lineNumberTable.length; i++) {
            LineNumberTableEntry currentEntry = lineNumberTable[i];
            int startPcIndex = Arrays.binarySearch(oldOffsets, currentEntry.getStartPc());
            if (startPcIndex < 0 || startPcIndex == oldOffsets.length) {
                throw new InvalidByteCodeException("Invalid line number table " + currentEntry.getStartPc());
            }
            currentEntry.setStartPc(newOffsets[transformedIndices[startPcIndex]]);
        }

   }

    private int position;
    private AbstractInstruction[] preInstructions;
    private AbstractInstruction[] postInstructions;
    private boolean shiftTarget;

    /**
        Create a code insertion.
        @param position the instruction number to which this <tt>CodeInsertion</tt>
                        is to be applied. Corresponds to the index in the list
                        of instructions such as the one returned by a 
                        <tt>ByteCodeReader</tt>.
        @param preInstructions the instructions to be inserted <b>before</b>
                               the insertion point.
        @param postInstructions the instructions to be inserted <b>after</b>
                                the insertion point.
        @param shiftTarget should offsets of branch instructions pointing to 
                           the position of this code insertion
                           be shifted or point to the beginning of the 
                           code inserted via the <tt>preInstructions</tt>
                           parameters.
     */
    public CodeInsertion(int position,
                         AbstractInstruction[] preInstructions,
                         AbstractInstruction[] postInstructions,
                         boolean shiftTarget)
    {
        this.position = position;
        this.preInstructions = preInstructions;
        this.postInstructions  = postInstructions;
        this.shiftTarget = shiftTarget;
    }

    /**
        Get the insertion position, i.e the instruction number to which
        this <tt>CodeInsertion</tt> is to be applied. Corresponds to the
        index in the list of instructions such as the one returned by a 
        <tt>ByteCodeReader</tt>.
        @return the insertion position
     */
    public int getPosition() {
        return position;
    }

    /**
        Set the insertion position, i.e the instruction number to which
        this <tt>CodeInsertion</tt> is to be applied. Corresponds to the
        index in the list of instructions such as the one returned by a 
        <tt>ByteCodeReader</tt>.
        @param position the insertion position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
        Get the instructions to be inserted <b>before</b> the insertion point.
        @return array of instructions
     */
    public AbstractInstruction[] getPreInstructions() {
        return preInstructions;
    }

    /**
        Set the instructions to be inserted <b>before</b> the insertion point.
        @param preInstructions array of instructions
     */
    public void setPreInstructions(AbstractInstruction[] preInstructions) {
        this.preInstructions = preInstructions;
    }

    /**
        Get the instructions to be inserted <b>after</b> the insertion point.
        @return array of instructions
     */
    public AbstractInstruction[] getPostInstructions() {
        return postInstructions;
    }

    /**
        Set the instructions to be inserted <b>after</b> the insertion point.
        @param postInstructions array of instructions
     */
    public void setPostInstructions(AbstractInstruction[] postInstructions) {
        this.postInstructions = postInstructions;
    }

    /**
        Get whether offsets of branch instructions pointing to the position of
        the resulting code insertion should be shifted to the first pre-instruction
        or continue to point at the original instruction.
        @return the boolean value
     */
    public boolean isShiftTarget() {
        return shiftTarget;
    }

    /**
        Set whether offsets of branch instructions pointing to the position of
        the resulting code insertion should be shifted to the first pre-instruction
        or continue to point at the original instruction.
        @param shiftTarget the boolean value.
     */
    public void setShiftTarget(boolean shiftTarget) {
        this.shiftTarget = shiftTarget;
    }
}
