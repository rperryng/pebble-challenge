package com.rperryng.pebblechallenge.models;

public abstract class ColorCommand {

    public enum Type {
        ABSOLUTE("Absolute"),
        RELATIVE("Relative");

        private String mName;

        private Type(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private Type mType;
    private boolean mActive;

    protected ColorCommand(Type type) {
        mType = type;
        mActive = false;
    }

    public Type getType() {
        return mType;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public abstract String getCommandString();

}
