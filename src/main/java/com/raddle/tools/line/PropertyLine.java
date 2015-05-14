package com.raddle.tools.line;

import com.raddle.tools.compare.CompareResult;
import com.raddle.tools.compare.LineState;

public class PropertyLine implements Line {

    public final static String PROPERTY_SEPARATOR = "=";
    private String             key;
    private String             value;
    private String             originalValue;
    private CommentLine        comment;
    private CompareResult      compareResult;
    private LineState          state              = LineState.original;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CommentLine getComment() {
        return comment;
    }

    public void setComment(CommentLine comment) {
        this.comment = comment;
    }

    @Override
    public String getLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(PROPERTY_SEPARATOR);
        if (value != null) {
            sb.append(value);
        }
        if (comment != null) {
            sb.append(" ").append(comment.getLine());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        sb.append(key).append(PROPERTY_SEPARATOR);
        if (value != null) {
            sb.append(value);
        }
        return sb.toString();
    }

    public CompareResult getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(CompareResult compareResult) {
        this.compareResult = compareResult;
    }

    public LineState getState() {
        return state;
    }

    public void setState(LineState state) {
        this.state = state;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    /**
     * 不复制compareResult和state还有originalValue
     */
    public PropertyLine clone() {
        PropertyLine l = new PropertyLine();
        l.setKey(this.getKey());
        l.setValue(this.getValue());
        l.setComment(this.getComment());
        return l;
    }

}
