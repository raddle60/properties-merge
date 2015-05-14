package com.raddle.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;

import com.raddle.swing.layout.LayoutUtils;
import com.raddle.textdiff.TextDiffResult;
import com.raddle.tools.compare.CompareResult;
import com.raddle.tools.compare.LineState;
import com.raddle.tools.compare.PropertyHolder;
import com.raddle.tools.line.Line;
import com.raddle.tools.line.PropertyLine;
import com.raddle.tools.util.TextdiffUtil;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class MergeMain extends javax.swing.JFrame {

    {
        // Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = 1L;
    private JTextField        sourceTxt;
    private JTextField        targetTxt;
    private JScrollPane       jScrollPane1;
    private JScrollPane       jScrollPane2;
    private JButton helpBtn;
    private JButton targetReloadBtn;
    private JButton sourceReloadBtn;
    private JButton           compareBtn;
    private JScrollPane       jScrollPane3;
    private JTextPane         diffResultPane;
    private JButton           toSourceBtn;
    private JButton           toTargetBtn;
    private JButton           sourceSaveBtn;
    private JButton targetEditBtn;
    private JButton sourceEditBtn;
    private JButton           targetSaveBtn;
    private JList             targetList;
    private JList             sourceList;
    private JButton           targetBtn;
    private JButton           sourceBtn;
    private PropertyHolder    source;
    private PropertyHolder    target;
    private Properties        properties       = new Properties();
    private File              mergePropFile    = null;
    //$hide>>$
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MergeMain inst = new MergeMain();
                inst.setDefaultCloseOperation(EXIT_ON_CLOSE);
                inst.setBounds(200, 200, 1050, 600);
                inst.setVisible(true);
            }
        });
    }
    //$hide<<$
    public MergeMain(){
        super();
        initGUI();
        //$hide>>$
        initData();
        initLayout();
        //$hide<<$
    }
    //$hide>>$
    private void initLayout() {
        // ------------------------------------------------左边
        // 打开按钮
        LayoutUtils.anchorRelativeDrift(this, sourceBtn).anchorRight(0.5, 5);
        // 打开路径
        LayoutUtils.anchorBorderFollow(sourceTxt, sourceBtn).followRight(5);
        // 保存按钮
        LayoutUtils.anchorRelativeDrift(this, sourceSaveBtn).anchorRight(0.5, 5);
        // 属性框
        LayoutUtils.anchorBorderFollow(jScrollPane1, sourceBtn).followRight(5);
        LayoutUtils.anchorFixedBorder(this, jScrollPane1).anchorBottom(40);
        // 比较按钮
        LayoutUtils.anchorRelativeDrift(this, compareBtn).anchorRight(0.5, 5);
        // 往左
        LayoutUtils.anchorRelativeDrift(this, toSourceBtn).anchorRight(0.5, 5);
        // 往右
        LayoutUtils.anchorRelativeDrift(this, toTargetBtn).anchorRight(0.5, 5);
        // 帮助
        LayoutUtils.anchorRelativeDrift(this, helpBtn).anchorRight(0.5, 5);
        // ------------------------------------------------右边
        // 打开按钮
        LayoutUtils.anchorFixedDrift(this, targetBtn).anchorRight(12);
        // 保存按钮
        LayoutUtils.anchorFixedDrift(this, targetSaveBtn).anchorRight(12);
        // 重新载入
        LayoutUtils.anchorDriftFollow(targetReloadBtn, targetSaveBtn).followRight();
        // 编辑文件
        LayoutUtils.anchorDriftFollow(targetEditBtn, targetSaveBtn).followRight();
        // 打开路径
        LayoutUtils.anchorRelativeBorder(this, targetTxt).anchorLeft(0.5, 10);//左边
        LayoutUtils.anchorBorderFollow(targetTxt, targetBtn).followRight(5);//右边
        // 属性框
        LayoutUtils.anchorRelativeBorder(this, jScrollPane2).anchorLeft(0.5, 10);//左边
        LayoutUtils.anchorBorderFollow(jScrollPane2, targetBtn).followRight(5);//右边
        LayoutUtils.anchorFixedBorder(this, jScrollPane2).anchorBottom(40);//下边
        // ------------------------------------------------比较
        LayoutUtils.anchorBorderFollow(jScrollPane3, targetBtn).followRight(5);
    }

    private void initData() {
        setCellRenderer(sourceList);
        setCellRenderer(targetList);
        // 最后一次的打开内容
        String homeDir = System.getProperty("user.home");
        if (homeDir != null && new File(homeDir).isDirectory()) {
            File dirMerge = new File(new File(homeDir), ".prop-merge");
            if (!dirMerge.exists()) {
                dirMerge.mkdir();
            }
            mergePropFile = new File(dirMerge, "prop-merge.properties");
            if (mergePropFile.exists()) {
                try {
                    properties.load(new InputStreamReader(new FileInputStream(mergePropFile), "utf-8"));
                    File sFile = new File(properties.getProperty("left.file"));
                    if (sFile.exists()) {
                        source = new PropertyHolder(sFile, "utf-8");
                        sourceTxt.setText(sFile.getAbsolutePath());
                    }
                    File tFile = new File(properties.getProperty("right.file"));
                    if (tFile.exists()) {
                        target = new PropertyHolder(tFile, "utf-8");
                        targetTxt.setText(tFile.getAbsolutePath());
                    }
                    compare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //$hide<<$
    private void initGUI() {
        try {
            {
                this.setBounds(0, 0, 1050, 600);
                getContentPane().setLayout(null);
                this.setTitle("\u5c5e\u6027\u6587\u4ef6\u6bd4\u8f83");
                {
                    sourceTxt = new JTextField();
                    getContentPane().add(sourceTxt);
                    sourceTxt.setBounds(12, 12, 373, 22);
                }
                {
                    sourceBtn = new JButton();
                    getContentPane().add(sourceBtn);
                    sourceBtn.setText("\u6253\u5f00");
                    sourceBtn.setBounds(406, 12, 74, 22);
                    sourceBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            JFileChooser fileChooser = new JFileChooser(); // 文件选择器
                            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("属性文件", "properties"));
                            File curFile = new File(sourceTxt.getText());
                            if (curFile.exists()) {
                                fileChooser.setCurrentDirectory(curFile.getParentFile());
                            }
                            int result = fileChooser.showOpenDialog(MergeMain.this);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                File selected = fileChooser.getSelectedFile();
                                source = new PropertyHolder(selected, "utf-8");
                                sourceTxt.setText(selected.getAbsolutePath());
                                properties.setProperty("left.file", selected.getAbsolutePath());
                                savePropMergeFile();
                                compare();
                            }
                        }
                    });
                }
                {
                    targetTxt = new JTextField();
                    getContentPane().add(targetTxt);
                    targetTxt.setBounds(496, 12, 419, 22);
                }
                {
                    targetBtn = new JButton();
                    getContentPane().add(targetBtn);
                    targetBtn.setText("\u6253\u5f00");
                    targetBtn.setBounds(935, 12, 81, 22);
                    targetBtn.setSize(74, 22);
                    targetBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            JFileChooser fileChooser = new JFileChooser(); // 文件选择器
                            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("属性文件", "properties"));
                            File curFile = new File(targetTxt.getText());
                            if (curFile.exists()) {
                                fileChooser.setCurrentDirectory(curFile.getParentFile());
                            }
                            int result = fileChooser.showOpenDialog(MergeMain.this);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                File selected = fileChooser.getSelectedFile();
                                target = new PropertyHolder(selected, "utf-8");
                                targetTxt.setText(selected.getAbsolutePath());
                                properties.setProperty("right.file", selected.getAbsolutePath());
                                savePropMergeFile();
                                compare();
                            }
                        }
                    });
                }
                {
                    jScrollPane1 = new JScrollPane();
                    getContentPane().add(jScrollPane1);
                    jScrollPane1.setBounds(12, 127, 373, 413);
                    {
                        ListModel sourceListModel = new DefaultComboBoxModel(new String[] {});
                        sourceList = new JList();
                        jScrollPane1.setViewportView(sourceList);
                        sourceList.setAutoscrolls(true);
                        sourceList.setModel(sourceListModel);
                        sourceList.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent evt) {
                                if(evt.getKeyCode() == KeyEvent.VK_DELETE){
                                    PropertyLine v = (PropertyLine) sourceList.getSelectedValue();
                                    if(v != null){
                                        int ret = JOptionPane.showConfirmDialog(MergeMain.this, "您确定要删除"+v.getKey()+"吗？");
                                        if(ret == JOptionPane.YES_OPTION){
                                            v.setState(LineState.deleted);
                                            compare();
                                            sourceList.setSelectedValue(v, true);
                                        }
                                    }
                                }
                            }
                        });
                        sourceList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent evt) {
                                if(evt.getClickCount() == 2){
                                    Object v = sourceList.getSelectedValue();
                                    updatePropertyLine((PropertyLine) v);
                                    sourceList.setSelectedValue(v, true);
                                }
                            }
                        });
                        sourceList.addListSelectionListener(new ListSelectionListener() {

                            @Override
                            public void valueChanged(ListSelectionEvent evt) {
                                if (sourceList.getSelectedValue() != null) {
                                    PropertyLine pl = (PropertyLine) sourceList.getSelectedValue();
                                    if (target != null) {
                                        PropertyLine p = target.getLine(pl.getKey());
                                        if (p != null) {
                                            TextDiffResult rt = TextdiffUtil.getDifferResult(p.toString(), pl.toString());
                                            diffResultPane.setText("左：" + rt.getTargetHtml() + "<br/>右：" + rt.getSrcHtml());
                                            selectLine(targetList, p);
                                            return;
                                        }
                                    }
                                    TextDiffResult rt = TextdiffUtil.getDifferResult("", pl.toString());
                                    diffResultPane.setText("左：" + rt.getTargetHtml() + "<br/>右：" + rt.getSrcHtml());

                                }
                            }
                        });
                    }
                }
                {
                    jScrollPane2 = new JScrollPane();
                    getContentPane().add(jScrollPane2);
                    jScrollPane2.setBounds(496, 127, 419, 413);
                    {
                        ListModel targetListModel = new DefaultComboBoxModel(new String[] {});
                        targetList = new JList();
                        jScrollPane2.setViewportView(targetList);
                        targetList.setAutoscrolls(true);
                        targetList.setModel(targetListModel);
                        targetList.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent evt) {
                                if(evt.getKeyCode() == KeyEvent.VK_DELETE){
                                    PropertyLine v = (PropertyLine) targetList.getSelectedValue();
                                    if(v != null){
                                        int ret = JOptionPane.showConfirmDialog(MergeMain.this, "您确定要删除"+v.getKey()+"吗？");
                                        if(ret == JOptionPane.YES_OPTION){
                                            v.setState(LineState.deleted);
                                            compare();
                                            targetList.setSelectedValue(v, true);
                                        }
                                    }
                                }
                            }
                        });
                        targetList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent evt) {
                                if(evt.getClickCount() == 2){
                                    Object v = targetList.getSelectedValue();
                                    updatePropertyLine((PropertyLine) v);
                                    targetList.setSelectedValue(v, true);
                                }
                            }
                        });
                        targetList.addListSelectionListener(new ListSelectionListener() {

                            @Override
                            public void valueChanged(ListSelectionEvent evt) {
                                if (targetList.getSelectedValue() != null) {
                                    PropertyLine pl = (PropertyLine) targetList.getSelectedValue();
                                    if (source != null) {
                                        PropertyLine s = source.getLine(pl.getKey());
                                        if (s != null) {
                                            TextDiffResult rt = TextdiffUtil.getDifferResult(pl.toString(), s.toString());
                                            diffResultPane.setText("左：" + rt.getTargetHtml() + "<br/>右：" + rt.getSrcHtml());
                                            selectLine(sourceList, s);
                                            return;
                                        }
                                    }
                                    TextDiffResult rt = TextdiffUtil.getDifferResult(pl.toString(), "");
                                    diffResultPane.setText("左：" + rt.getTargetHtml() + "<br/>右：" + rt.getSrcHtml());
                                }
                            }
                        });
                    }
                }
                {
                    sourceSaveBtn = new JButton();
                    getContentPane().add(sourceSaveBtn);
                    sourceSaveBtn.setText("\u4fdd\u5b58");
                    sourceSaveBtn.setBounds(406, 45, 74, 22);
                    sourceSaveBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
							int result = JOptionPane.showConfirmDialog(MergeMain.this, "确定要保存左属性文件吗？\n" + source.getPropertyFile().getAbsolutePath());
                            if (result == JOptionPane.YES_OPTION) {
                                source.saveFile();
                                JOptionPane.showMessageDialog(MergeMain.this, "保存成功");
                                clearState(source);
                                compare();
                            }
                        }
                    });
                }
                {
                    targetSaveBtn = new JButton();
                    getContentPane().add(targetSaveBtn);
                    targetSaveBtn.setText("\u4fdd\u5b58");
                    targetSaveBtn.setBounds(935, 45, 81, 22);
                    targetSaveBtn.setSize(74, 22);
                    targetSaveBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
							int result = JOptionPane.showConfirmDialog(MergeMain.this, "确定要保存右属性文件吗？\n" + target.getPropertyFile().getAbsolutePath());
                            if (result == JOptionPane.YES_OPTION) {
                                target.saveFile();
                                JOptionPane.showMessageDialog(MergeMain.this, "保存成功");
                                clearState(target);
                                compare();
                            }
                        }
                    });
                }
                {
                    toTargetBtn = new JButton();
                    getContentPane().add(toTargetBtn);
                    toTargetBtn.setText("->");
                    toTargetBtn.setBounds(406, 221, 74, 22);
                    toTargetBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            Object[] oo = sourceList.getSelectedValues();
                            for (Object selected : oo) {
                                PropertyLine s = (PropertyLine) selected;
                                if (s != null && target != null) {
                                    PropertyLine t = target.getLine(s.getKey());
                                    if (t == null) {
                                        PropertyLine n = s.clone();
                                        n.setState(LineState.added);
                                        target.addPropertyLineAtSuitedPosition(n);
                                    } else if(!t.getValue().equals(s.getValue())){
                                        t.setState(LineState.updated);
                                        t.setValue(s.getValue());
                                    } else if(t.getState() == LineState.deleted){
                                        if(t.getValue().equals(t.getOriginalValue())){
                                            t.setState(LineState.original);
                                        }else{
                                            t.setState(LineState.updated);
                                        }
                                    }
                                    compare();
                                }
                            }
                        }
                    });
                }
                {
                    toSourceBtn = new JButton();
                    getContentPane().add(toSourceBtn);
                    toSourceBtn.setText("<-");
                    toSourceBtn.setBounds(406, 255, 74, 22);
                    toSourceBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            Object[] oo = targetList.getSelectedValues();
                            for (Object selected : oo) {
                                PropertyLine t = (PropertyLine) selected;
                                if (t != null && source != null) {
                                    PropertyLine s = source.getLine(t.getKey());
                                    if (s == null) {
                                        PropertyLine n = t.clone();
                                        n.setState(LineState.added);
                                        source.addPropertyLineAtSuitedPosition(n);
                                    } else if(!s.getValue().equals(t.getValue())){
                                        s.setState(LineState.updated);
                                        s.setValue(t.getValue());
                                    } else if(s.getState() == LineState.deleted){
                                        if(s.getValue().equals(s.getOriginalValue())){
                                            s.setState(LineState.original);
                                        }else{
                                            s.setState(LineState.updated);
                                        }
                                    }
                                    compare();
                                }
                            }
                        }
                    });
                }
                {
                    jScrollPane3 = new JScrollPane();
                    getContentPane().add(jScrollPane3);
                    jScrollPane3.setBounds(12, 73, 903, 42);
                    {
                        diffResultPane = new JTextPane();
                        jScrollPane3.setViewportView(diffResultPane);
                        diffResultPane.setBounds(12, 439, 903, 63);
                        diffResultPane.setContentType("text/html");
                        diffResultPane.setPreferredSize(new java.awt.Dimension(901, 42));
                    }
                }
                {
                    compareBtn = new JButton();
                    getContentPane().add(compareBtn);
                    compareBtn.setText("\u6bd4\u8f83");
                    compareBtn.setBounds(406, 139, 74, 22);
                    compareBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            compare();
                        }
                    });
                }
                {
                    sourceReloadBtn = new JButton();
                    getContentPane().add(sourceReloadBtn);
                    sourceReloadBtn.setText("\u91cd\u65b0\u8f7d\u5165");
                    sourceReloadBtn.setBounds(12, 40, 64, 29);
                    sourceReloadBtn.setSize(90, 22);
                    sourceReloadBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            if(sourceTxt.getText().length() > 0){
                                File curFile = new File(sourceTxt.getText().trim());
                                if(curFile.exists()){
                                    source = new PropertyHolder(curFile, "utf-8");
                                    sourceTxt.setText(curFile.getAbsolutePath());
                                    properties.setProperty("left.file", curFile.getAbsolutePath());
                                    savePropMergeFile();
                                    compare();
                                }else{
                                    JOptionPane.showMessageDialog(MergeMain.this, "属性文件："+curFile.getAbsolutePath()+"不存在");
                                }
                            }
                        }
                    });
                }
                {
                    targetReloadBtn = new JButton();
                    getContentPane().add(targetReloadBtn);
                    targetReloadBtn.setText("\u91cd\u65b0\u8f7d\u5165");
                    targetReloadBtn.setBounds(839, 45, 90, 22);
                    targetReloadBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            if(targetTxt.getText().length() > 0){
                                File curFile = new File(targetTxt.getText().trim());
                                if(curFile.exists()){
                                    target = new PropertyHolder(curFile, "utf-8");
                                    targetTxt.setText(curFile.getAbsolutePath());
                                    properties.setProperty("right.file", curFile.getAbsolutePath());
                                    savePropMergeFile();
                                    compare();
                                }else{
                                    JOptionPane.showMessageDialog(MergeMain.this, "属性文件："+curFile.getAbsolutePath()+"不存在");
                                }
                            }
                        }
                    });
                }
                {
                	helpBtn = new JButton();
                	getContentPane().add(helpBtn);
                	helpBtn.setText("\u5e2e\u52a9");
                	helpBtn.setBounds(405, 338, 38, 29);
                	helpBtn.setSize(74, 22);
                	helpBtn.addActionListener(new ActionListener() {
                		@Override
                        public void actionPerformed(ActionEvent evt) {
                			StringBuilder sb = new StringBuilder();
                			sb.append("双击查看明细和编辑").append("\n");
                			sb.append("del键删除").append("\n");
                			sb.append("配置文件在使用过程中自动生成").append("\n");
                			sb.append("文件存放: 用户目录/.prop-merge/prop-merge.properties").append("\n");
                			JOptionPane.showMessageDialog(MergeMain.this, sb.toString());
                		}
                	});
                }
                {
                	sourceEditBtn = new JButton();
                	getContentPane().add(sourceEditBtn);
                	sourceEditBtn.setText("\u7f16\u8f91\u6587\u4ef6");
                	sourceEditBtn.setBounds(108, 40, 90, 22);
                	sourceEditBtn.addActionListener(new ActionListener() {
                		@Override
                        public void actionPerformed(ActionEvent evt) {
                			if(sourceTxt.getText().length() > 0){
                                File curFile = new File(sourceTxt.getText());
                                editFile(curFile);
                            }
                		}
                	});
                }
                {
                	targetEditBtn = new JButton();
                	getContentPane().add(targetEditBtn);
                	targetEditBtn.setText("\u7f16\u8f91\u6587\u4ef6");
                	targetEditBtn.setBounds(743, 45, 90, 22);
                	targetEditBtn.addActionListener(new ActionListener() {
                		@Override
                        public void actionPerformed(ActionEvent evt) {
                			if(targetTxt.getText().length() > 0){
                                File curFile = new File(targetTxt.getText());
                                editFile(curFile);
                            }
                		}
                	});
                }
            }
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //$hide>>$
    private void compare() {
        if (source != null && target != null) {
            // source
            sourceList.setModel(new DefaultComboBoxModel(diffLines(source, target).toArray()));
            // target
            targetList.setModel(new DefaultComboBoxModel(diffLines(target, source).toArray()));
        } else if (source != null) {
            sourceList.setModel(new DefaultComboBoxModel(source.getProperties().toArray()));
        } else if (target != null) {
            targetList.setModel(new DefaultComboBoxModel(target.getProperties().toArray()));
        }
    }

    private void clearState(PropertyHolder holder) {
        for (PropertyLine line : holder.getProperties()) {
            line.setState(LineState.original);
        }
    }

    private List<Line> diffLines(PropertyHolder sourceLines, PropertyHolder targetLines) {
        List<Line> updateDiffList = new ArrayList<Line>();
        List<Line> addDiffList = new ArrayList<Line>();
        List<Line> equalList = new ArrayList<Line>();
        List<Line> diffList = new ArrayList<Line>();
        for (PropertyLine line : sourceLines.getProperties()) {
            line.setCompareResult(null);
            if (line.getState() != LineState.deleted) {
                PropertyLine t = targetLines.getLine(line.getKey());
                if (t != null && t.getState() != LineState.deleted) {
                    if (!StringUtils.equals(t.getValue(), line.getValue())) {
                        line.setCompareResult(CompareResult.different);
                        updateDiffList.add(line);
                    } else {
                        equalList.add(line);
                    }
                } else {
                    line.setCompareResult(CompareResult.extra);
                    addDiffList.add(line);
                }
            } else {
                equalList.add(line);
            }
        }
        diffList.addAll(updateDiffList);
        diffList.addAll(addDiffList);
        diffList.addAll(equalList);
        return diffList;
    }

    private void selectLine(JList list, PropertyLine line) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) list.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            PropertyLine mLine = (PropertyLine) model.getElementAt(i);
            if (mLine.getKey().equals(line.getKey())) {
                list.setSelectedValue(mLine, true);
                return;
            }
        }
    }

    private void setCellRenderer(JList list) {
        list.setCellRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    PropertyLine line = (PropertyLine) value;
                    boolean isInSelected = false;
                    for (int selectedIndex : list.getSelectedIndices()) {
                        if (index == selectedIndex) {
                            isInSelected = true;
                        }
                    }
                    if (!isInSelected) {
                        if (CompareResult.extra == line.getCompareResult()) {
                            c.setBackground(new Color(0xFFC48E));
                        } else if (CompareResult.different == line.getCompareResult()) {
                            c.setBackground(new Color(0xBBBBFF));
                        }
                    }
                    if (line.getState() != null) {
                        if (LineState.added == line.getState()) {
                            c.setForeground(new Color(0xCC0033));
                        } else if (LineState.updated == line.getState()) {
                            c.setForeground(new Color(0x0066CC));
                        } else if (LineState.deleted == line.getState()) {
                            if(line.getOriginalValue() == null){
                                c.setForeground(new Color(0xAAAAAA));
                            }else{
                                c.setForeground(new Color(0x666666));
                            }
                            Map<Attribute, Object> map = new HashMap<Attribute, Object>();
                            map.put(TextAttribute.FONT, c.getFont());
                            map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                            c.setFont(Font.getFont(map));
                        }
                    }
                }
                return c;
            }

        });
    }

    private void savePropMergeFile() {
        if (mergePropFile != null) {
            try {
                properties.store(new FileWriterWithEncoding(mergePropFile, "utf-8"), "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePropertyLine(PropertyLine pl) {
           if(pl != null){
                if (pl.getState() == LineState.deleted) {
                    JOptionPane.showMessageDialog(MergeMain.this, pl.getKey()+"已删除，不能修改");
                } else {
                	StringBuilder sb = new StringBuilder();
                	sb.append("注释: ");
                	if(pl.getComment() != null){
                		sb.append(pl.getComment());
                	}
                	sb.append('\n');
                	sb.append("修改: " + pl.getKey()).append('\n');;
                	sb.append("原值: " + StringUtils.defaultString(pl.getOriginalValue()));
                    String v = JOptionPane.showInputDialog(MergeMain.this, sb.toString(), pl.getValue());
                    if (v != null && !v.trim().equals(pl.getValue())) {
                        if(pl.getState() != LineState.added && pl.getState() != LineState.deleted){
                            pl.setState(LineState.updated);
                        }
                        pl.setValue(v);
                        if(pl.getValue().equals(pl.getOriginalValue())){
                            pl.setState(LineState.original);
                        }
                        compare();
                    }
                }
           }
    }
    private void editFile(File curFile) {
    	if(curFile.exists()){
    		String cmd = properties.getProperty("editor.cmd");
    		if(StringUtils.isBlank(cmd)){
    			cmd = inputEditorCmd();
    		}
    		try {
    			Runtime.getRuntime().exec(MessageFormat.format(cmd, curFile.getAbsolutePath()));
    		} catch (IOException e) {
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(MergeMain.this, "打开文件失败："+e.getMessage());
    			inputEditorCmd();
    		}
    	}else{
    		JOptionPane.showMessageDialog(MergeMain.this, "属性文件："+curFile.getAbsolutePath()+"不存在");
    	}
    }
    private String inputEditorCmd() {
		String cmd = properties.getProperty("editor.cmd");
		if(StringUtils.isBlank(cmd)){
			String osName = System.getProperties().getProperty("os.name");
			if (osName.toLowerCase().indexOf("windows") != -1) {
				cmd = "notepad {0}";
			}else{
				cmd = "gedit {0}";
			}
		}
    	String input = JOptionPane.showInputDialog(MergeMain.this, "请输入编辑器命令行,{0}是文件路径", cmd);
    	if(StringUtils.isNotBlank(input)){
    		properties.setProperty("editor.cmd", input);
    		savePropMergeFile();
    	}
    	return input;
    }
    //$hide<<$
}
