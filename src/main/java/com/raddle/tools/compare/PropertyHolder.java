package com.raddle.tools.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.raddle.tools.line.CommentLine;
import com.raddle.tools.line.EmptyLine;
import com.raddle.tools.line.Line;
import com.raddle.tools.line.PropertyLine;

public class PropertyHolder {
    private File propertyFile;
    private String encoding;
    private List<Line> lineObjs = new ArrayList<Line>();

    @SuppressWarnings("unchecked")
    public PropertyHolder(File propertyFile, String encoding) {
        this.propertyFile = propertyFile;
        this.encoding = encoding;
        if (propertyFile == null) {
            JOptionPane.showMessageDialog(null, "属性文件null不存在", "加载属性文件", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!propertyFile.exists()) {
            JOptionPane.showMessageDialog(null, "属性文件" + propertyFile.getAbsolutePath() + "不存在", "加载属性文件",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<String> lines = FileUtils.readLines(propertyFile, encoding);
            for (String line : lines) {
                String trimed = line.trim();
                if (trimed.startsWith(CommentLine.COMMENT_PREFIX)) {
                    lineObjs.add(new CommentLine(trimed.substring(1).trim()));
                } else if (trimed.indexOf(PropertyLine.PROPERTY_SEPARATOR) != -1) {
                    int index = trimed.indexOf(PropertyLine.PROPERTY_SEPARATOR);
                    int commentIndex = trimed.indexOf(CommentLine.COMMENT_PREFIX);
                    PropertyLine p = new PropertyLine();
                    p.setKey(trimed.substring(0, index).trim());
                    if (commentIndex != -1) {
                        if (index != commentIndex - 1) {
                            p.setValue(trimed.substring(index + 1, commentIndex).trim());
                        }
                        p.setComment(new CommentLine(trimed.substring(commentIndex + 1).trim()));
                    } else {
                        p.setValue(trimed.substring(index + 1).trim());
                    }
                    p.setOriginalValue(p.getValue());
                    if (isExist(p)) {
                        JOptionPane.showMessageDialog(null, "属性" + p.getKey() + "已存在,忽略重复的属性", "重复属性验证",
                                JOptionPane.WARNING_MESSAGE);
                        continue;
                    } else {
                        lineObjs.add(p);
                    }
                } else if (trimed.length() == 0) {
                    lineObjs.add(new EmptyLine());
                } else {
                    lineObjs.add(new EmptyLine());
                    JOptionPane.showMessageDialog(null, "忽略非法的属性" + trimed, "加载属性文件", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "属性文件" + propertyFile.getAbsolutePath() + "加载失败，因为：" + e.getMessage(),
                    "加载属性文件", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void addPropertyLineAtSuitedPosition(PropertyLine line) {
        if (line == null) {
            return;
        }
        if (isExist(line)) {
            JOptionPane.showMessageDialog(null, "属性" + line.getKey() + "已存在", "重复属性验证", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int suitedIndex = -1;
        int mostMatchCount = 0;
        for (int i = 0; i < lineObjs.size(); i++) {
            Line l = lineObjs.get(i);
            if (l instanceof PropertyLine) {
                PropertyLine pl = (PropertyLine) l;
                if (pl.getKey() != null && line.getKey() != null) {
                    int length = Math.min(pl.getKey().length(), line.getKey().length());
                    int matchCount = 0;
                    for (int j = 0; j < length; j++) {
                        if (pl.getKey().charAt(j) == line.getKey().charAt(j)) {
                            matchCount++;
                        } else {
                            break;
                        }
                    }
                    // 最后一个最匹配的
                    if (matchCount >= mostMatchCount) {
                        suitedIndex = i;
                        mostMatchCount = matchCount;
                    }
                }
            }
        }
        if (suitedIndex != -1 && suitedIndex < lineObjs.size() - 1) {
            // 插入到最匹配的后面
            lineObjs.add(suitedIndex + 1, line);
        } else {
            //没有匹配到，加到末尾
            lineObjs.add(line);
        }
    }

    public void addLastLine(Line line) {
        if (line == null) {
            return;
        }
        if (isExist(line)) {
            if (line instanceof PropertyLine) {
                JOptionPane.showMessageDialog(null, "属性" + ((PropertyLine) line).getKey() + "已存在", "重复属性验证",
                        JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        lineObjs.add(line);
    }

    private boolean isExist(Line line) {
        if (line != null && line instanceof PropertyLine) {
            for (int i = 0; i < lineObjs.size(); i++) {
                Line l = lineObjs.get(i);
                if (l instanceof PropertyLine) {
                    PropertyLine pl = (PropertyLine) l;
                    if (StringUtils.equals(pl.getKey(), ((PropertyLine) line).getKey())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeLine(Line line) {
        if (line == null) {
            return;
        }
        lineObjs.remove(line);
    }

    public List<PropertyLine> getProperties() {
        List<PropertyLine> l = new ArrayList<PropertyLine>();
        for (Line line : lineObjs) {
            if (line instanceof PropertyLine) {
                l.add((PropertyLine) line);
            }
        }
        return l;
    }

    public Line getLine(int start, String key) {
        for (int i = start; i < lineObjs.size(); i++) {
            Line l = lineObjs.get(i);
            if (l instanceof PropertyLine && key.equals(((PropertyLine) l).getKey())) {
                return l;
            }
        }
        return null;
    }

    public PropertyLine getLine(String key) {
        for (int i = 0; i < lineObjs.size(); i++) {
            Line l = lineObjs.get(i);
            if (l instanceof PropertyLine && key.equals(((PropertyLine) l).getKey())) {
                return (PropertyLine) l;
            }
        }
        return null;
    }

    public void saveFile() {
        saveAsFile(propertyFile);
    }

    public void saveAsFile(File File) {
        // 去掉已删除的行
        for (Iterator<Line> iterator = lineObjs.iterator(); iterator.hasNext();) {
            Line l = iterator.next();
            if(l instanceof PropertyLine && ((PropertyLine) l).getState() == LineState.deleted){
                iterator.remove();
            }
        }
        try {
            FileUtils.writeLines(File, encoding, toStringList());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "属性文件" + propertyFile.getAbsolutePath() + "保存失败，因为：" + e.getMessage(),
                    "保存属性文件", JOptionPane.WARNING_MESSAGE);
        }
    }

    private List<String> toStringList() {
        List<String> sl = new ArrayList<String>();
        for (Line line : lineObjs) {
            sl.add(line.getLine());
        }
        return sl;
    }

    public File getPropertyFile() {
        return propertyFile;
    }
}
