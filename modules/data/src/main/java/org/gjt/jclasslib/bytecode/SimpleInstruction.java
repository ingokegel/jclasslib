/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

/**
    Describes an instructions with no immediate arguments.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class SimpleInstruction extends AbstractInstruction {

    /**
        Constructor.
        @param opcode the opcode.
     */
    public SimpleInstruction(Opcode opcode) {
        super(opcode); 
    }
    
    
}
