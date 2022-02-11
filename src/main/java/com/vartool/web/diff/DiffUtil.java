package com.vartool.web.diff;

import java.util.LinkedList;
import java.util.Optional;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 yona diff_match_patch 참고 할것. 
 */
public class DiffUtil {
	/*
  public static final int EQUAL_TEXT_ELLIPSIS_SIZE = 100;
  
  public static final int EQUAL_TEXT_BASE_SIZE = 50;
  
  public static final int DIFF_EDITCOST = 8;
  
  public static String getDiffText(String oldValue, String newValue) {
    String oldVal = Optional.<String>ofNullable(oldValue).orElse("");
    String newVal = Optional.<String>ofNullable(newValue).orElse("");
    diff_match_patch dmp = new diff_match_patch();
    dmp.Diff_EditCost = 8;
    StringBuilder sb = new StringBuilder();
    LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(oldVal, newVal);
    dmp.diff_cleanupEfficiency(diffs);
    for (diff_match_patch.Diff diff : diffs) {
      String deleteStyle;
      int textLength;
      String insertStyle, str1;
      diff_match_patch.Diff diff1 = diff;
      diff_match_patch.Operation operation = null;
      operation = diff1.getOperation();
      switch (operation) {
        case DELETE:
          deleteStyle = "<span style='background-color: #fda9a6;padding: 2px 0;'>";
          sb.append(addDiffStyle(diff, deleteStyle));
        case EQUAL:
          diff1 = diff;
          operation = null;
          str1 = diff1.getText();
          textLength = str1.length();
          if (textLength > 100) {
            sb.append(addHeadOfDiff(diff));
            sb.append(addEllipsis());
            sb.append(addTailOfDiff(diff));
            continue;
          } 
          sb.append(addAllDiff(diff));
        case INSERT:
          insertStyle = "<span style='background-color: #abdd52;padding: 2px 0;'>";
          sb.append(addDiffStyle(diff, insertStyle));
      } 
    } 
    return sb.toString().replaceAll("\n", "&nbsp<br/>\n");
  }
  
  public static String getDiffPlainText(String oldValue, String newValue) {
    String oldVal = Optional.<String>ofNullable(oldValue).orElse("");
    String newVal = Optional.<String>ofNullable(newValue).orElse("");
    diff_match_patch dmp = new diff_match_patch();
    dmp.Diff_EditCost = 8;
    StringBuilder sb = new StringBuilder();
    LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(oldVal, newVal);
    dmp.diff_cleanupEfficiency(diffs);
    for (diff_match_patch.Diff diff : diffs) {
      String deleteText;
      int textLength;
      String insertText, str1;
      diff_match_patch.Diff diff1 = diff;
      diff_match_patch.Operation operation = null;
      operation = diff1.getOperation();
      switch (operation) {
        case DELETE:
          deleteText = "--- ";
          sb.append(addDiffText(diff, deleteText));
        case EQUAL:
          diff1 = diff;
          operation = null;
          str1 = diff1.getText();
          textLength = str1.length();
          if (textLength > 100) {
            sb.append(addHeadOfDiff(diff))
              .append(addEllipsisText())
              .append(addTailOfDiff(diff));
          } else {
            sb.append(addAllDiff(diff));
          } 
          sb.append("\n");
        case INSERT:
          insertText = "+++ ";
          sb.append(addDiffText(diff, insertText));
      } 
    } 
    return sb.toString();
  }
  
  private static String addHeadOfDiff(diff_match_patch.Diff diff) {
    diff_match_patch.Diff diff1 = diff;
    String str = null;
    str = diff1.getText();
    return StringEscapeUtils.escapeHtml4(str.substring(0, 50));
  }
  
  private static String addTailOfDiff(diff_match_patch.Diff diff) {
    diff_match_patch.Diff diff1 = diff;
    String str = null;
    str = diff1.getText();
    diff1 = diff;
    str = null;
    str = diff1.getText();
    return StringEscapeUtils.escapeHtml4(str.substring(str.length() - 50));
  }
  
  private static String addAllDiff(diff_match_patch.Diff diff) {
    diff_match_patch.Diff diff1 = diff;
    String str = null;
    str = diff1.getText();
    return StringEscapeUtils.escapeHtml4(str);
  }
  
  private static String addEllipsis() {
    return "<span style='color: #bdbdbd;font-size: 16px;font-family: serif;'>...\n......\n......\n...</span>";
  }
  
  private static String addDiffStyle(diff_match_patch.Diff diff, String style) {
    diff_match_patch.Diff diff1 = diff;
    String str = null;
    str = diff1.getText();
    return style + 
      StringEscapeUtils.escapeHtml4(str) + "</span>";
  }
  
  private static String addDiffText(diff_match_patch.Diff diff, String text) {
    diff_match_patch.Diff diff1 = diff;
    String str = null;
    str = diff1.getText();
    return text + 
      StringEscapeUtils.escapeHtml4(str) + "\n";
  }
  
  private static String addEllipsisText() {
    return "......\n......\n...\n";
  }
  
  */
}
