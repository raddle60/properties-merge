/**
 *
 */
package com.raddle.tools.util;

import org.apache.commons.lang.StringUtils;

import com.raddle.textdiff.TextDiff;
import com.raddle.textdiff.TextDiffBaseChar;
import com.raddle.textdiff.TextDiffResult;

/**
 * 生成比较结果
 *
 * @author xurong
 *
 */
public class TextdiffUtil {
	public static final String DEFAULT_ADD_STYLE_PREFIX = "<span style='background-color:#FFC48E;'>";
	public static final String DEFAULT_ADD_STYLE_SUFFIX = "</span>";
	public static final String DEFAULT_CHANGE_NEW_STYLE_PREFIX = "<span style='background-color:#BBBBFF;'>";
	public static final String DEFAULT_CHANGE_NEW_STYLE_SUFFIX = "</span>";
	public static final String DEFAULT_CHANGE_OLD_STYLE_PREFIX = "<span style='background-color:#BBBBFF;'>";
	public static final String DEFAULT_CHANGE_OLD_STYLE_SUFFIX = "</span>";
	public static final String DEFAULT_DEL_STYLE_PREFIX = "<span style='background-color:#C0F7FE;text-decoration:line-through;'>";
	public static final String DEFAULT_DEL_STYLE_SUFFIX = "</span>";
	public static final int DEFAULT_MERGE_MODE = 1;
	private static TextDiff textdiff = new TextDiffBaseChar();

	static {
		textdiff.setAddStylePrefix(DEFAULT_ADD_STYLE_PREFIX);
		textdiff.setAddStyleSuffix(DEFAULT_ADD_STYLE_SUFFIX);
		textdiff.setChangeNewStylePrefix(DEFAULT_CHANGE_NEW_STYLE_PREFIX);
		textdiff.setChangeNewStyleSuffix(DEFAULT_CHANGE_NEW_STYLE_SUFFIX);
		textdiff.setChangeOldStylePrefix(DEFAULT_CHANGE_OLD_STYLE_PREFIX);
		textdiff.setChangeOldStyleSuffix(DEFAULT_CHANGE_OLD_STYLE_SUFFIX);
		textdiff.setDelStylePrefix(DEFAULT_DEL_STYLE_PREFIX);
		textdiff.setDelStyleSuffix(DEFAULT_DEL_STYLE_SUFFIX);
		textdiff.setMergeMode(DEFAULT_MERGE_MODE);
	}

	public static String getDifferMergedHtml(String src, String dest) {
		return getDifferResult(src, dest).getMergedHtml();
	}

	public static TextDiffResult getDifferResult(String src, String dest) {
		TextDiffResult result = textdiff.diffString(src, dest);
		TextDiffResult retResult = new TextDiffResult(StringUtils.replace(result.getSrcHtml(), "&nbsp;", " "), StringUtils.replace(result.getTargetHtml(), "&nbsp;", " "), StringUtils.replace(result.getMergedHtml(), "&nbsp;", " "), result.getDiffCount(), result.getDiffCharNumber());
		return retResult;
	}
}