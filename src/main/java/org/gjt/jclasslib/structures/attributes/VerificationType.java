package org.gjt.jclasslib.structures.attributes;

public enum VerificationType {
    ;

    private String verbose;

    VerificationType(String verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        return verbose;
    }
}
