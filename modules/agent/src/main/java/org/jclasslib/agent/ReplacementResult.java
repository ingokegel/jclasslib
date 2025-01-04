/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.jclasslib.agent;

import java.io.Serializable;

public class ReplacementResult implements Serializable {
    public static final ReplacementResult SUCCESS = new ReplacementResult(null);

    private final String errorMessage;

    public ReplacementResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
