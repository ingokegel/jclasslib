/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import java.util.List;

/**
    Serializable configuration object for the window state of an MDI frame.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 07:59:17 $
*/
public class MDIConfig {

    private List internalFrameDescs;
    private InternalFrameDesc activeFrameDesc;

    /**
     * Get the list of internal frame descriptions.
     * @return the list.
     */
    public List getInternalFrameDescs() {
        return internalFrameDescs;
    }

    /**
     * Set the list of internal frame descriptions.
     * @param internalFrameDescs the list.
     */
    public void setInternalFrameDescs(List internalFrameDescs) {
        this.internalFrameDescs = internalFrameDescs;
    }

    /**
     * Get the internal frame description of the active internal frame.
     * @return the internal frame description.
     */
    public InternalFrameDesc getActiveFrameDesc() {
        return activeFrameDesc;
    }

    /**
     * Set the internal frame description of the active internal frame.
     * @param activeFrameDesc the internal frame description.
     */
    public void setActiveFrameDesc(InternalFrameDesc activeFrameDesc) {
        this.activeFrameDesc = activeFrameDesc;
    }

    /**
     * Serializable description object for the state of an internal frame.
     */
    public static class InternalFrameDesc {

        private String className;
        private Object initParam;
        private int x;
        private int y;
        private int width;
        private int height;
        private boolean maximized;
        private boolean iconified;

        /**
         * Get the class name of the internal frame.
         * @return the class name.
         */
        public String getClassName() {
            return className;
        }

        /**
         * Set the class name of the internal frame.
         * @param className the class name.
         */
        public void setClassName(String className) {
            this.className = className;
        }

        /**
         * Get the initialization parameter for the internal frame.
         * @return the initialization parameter
         */
        public Object getInitParam() {
            return initParam;
        }

        /**
         * Set the initialization parameter for the internal frame.
         * @param initParam the initialization parameter
         */
        public void setInitParam(Object initParam) {
            this.initParam = initParam;
        }

        /**
         * Get the x-position of the internal frame on the desktop.
         * @return the x-position.
         */
        public int getX() {
            return x;
        }

        /**
         * Set the x-position of the internal frame on the desktop.
         * @param x the x-position.
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Get the y-position of the internal frame on the desktop.
         * @return the y-position.
         */
        public int getY() {
            return y;
        }

        /**
         * Set the y-position of the internal frame on the desktop.
         * @param y the y-position.
         */
        public void setY(int y) {
            this.y = y;
        }

        /**
         * Get the width of the internal frame on the desktop.
         * @return the width.
         */
        public int getWidth() {
            return width;
        }

        /**
         * Set the width of the internal frame on the desktop.
         * @param width the width.
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Get the height of the internal frame on the desktop.
         * @return the height.
         */
        public int getHeight() {
            return height;
        }

        /**
         * Set the height of the internal frame on the desktop.
         * @param height the height.
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * Returns whether the internal frame is maximized.
         * @return the value.
         */
        public boolean isMaximized() {
            return maximized;
        }

        /**
         * Sets whether the internal frame is maximized.
         * @param maximized the value.
         */
        public void setMaximized(boolean maximized) {
            this.maximized = maximized;
        }

        /**
         * Returns whether the internal frame is iconified.
         * @return the value.
         */
        public boolean isIconified() {
            return iconified;
        }

        /**
         * Sets whether the internal frame is iconified.
         * @param iconified the value.
         */
        public void setIconified(boolean iconified) {
            this.iconified = iconified;
        }
    }

}
