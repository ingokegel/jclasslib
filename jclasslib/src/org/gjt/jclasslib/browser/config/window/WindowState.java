/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window;

/**
    Complete serializable state of a <tt>BrowserComponent</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class WindowState {

    private String fileName;
    private BrowserPath browserPath;

    /**
     * Constructor.
     * @param fileName the file name for the displayed class.
     * @param browserPath the browser path that should be selected. May be <tt>null</tt>.
     */
    public WindowState(String fileName, BrowserPath browserPath) {
        this.fileName = fileName;
        this.browserPath = browserPath;
    }

    /**
     * Constructor.
     * @param fileName the file name for the displayed class.
     */
    public WindowState(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Constructor.
     */
    public WindowState() {
    }

    /**
     * Get the file name of the displayed class.
     * @return the file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name of the displayed class.
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the browser path.
     * @return the browser path.
     */
    public BrowserPath getBrowserPath() {
        return browserPath;
    }

    /**
     * Set the browser path.
     * @param browserPath the browser path.
     */
    public void setBrowserPath(BrowserPath browserPath) {
        this.browserPath = browserPath;
    }

    public boolean equals(Object other) {

        if (fileName == null || other == null || !(other instanceof WindowState)) {
            return false;
        }
        return fileName.equals(((WindowState)other).fileName);
    }

    public int hashCode() {
        return fileName.hashCode();
    }
}
