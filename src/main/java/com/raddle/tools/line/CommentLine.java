package com.raddle.tools.line;

public class CommentLine implements Line {
    public final static String COMMENT_PREFIX = "#";
    private String comment;

    public CommentLine(String comment) {
        this.comment = comment;
    }

    @Override
    public String getLine() {
        if (comment != null) {
            return COMMENT_PREFIX + " " + comment;
        } else {
            return COMMENT_PREFIX;
        }
    }

    @Override
    public String toString() {
        return getLine();
    }

}
