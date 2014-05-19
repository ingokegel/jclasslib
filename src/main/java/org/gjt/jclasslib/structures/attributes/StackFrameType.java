package org.gjt.jclasslib.structures.attributes;

public enum StackFrameType {
    ;

    private String verbose;

    StackFrameType(String verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        return verbose;
    }
}
