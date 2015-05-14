package com.raddle.tools.line;

public class EmptyLine implements Line {

    @Override
    public String getLine() {
        return "";
    }

    @Override
    public String toString() {
        return getLine();
    }
}
