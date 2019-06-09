package SimpleEditor;

import javax.swing.*;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 一个工具类，用于支持 AutoComplete 类的实现
 */
class Utility {
    private static Set<String> getSpaceSet() {
        return spaceSet;
    }

    private static Set<String> spaceSet = new SupportedKeywords().getSpaceSet();

    /**
     * 用于寻找结尾在position位置的单词的开头的前一位的位置
     *
     * @param position 单词末尾位置
     * @param content  总文本
     * @return 该单词的第一个元素（包括字母、数字、-）的前一位
     */
    static int getStartOfTypingWord(int position, String content) {
        int len = content.length();
        int start = position >= len ? len - 1 : position;
        for (; start >= 0; start--) {
            if (!Character.isLetter(content.charAt(start)) &&
                    !Character.isDigit(content.charAt(start)) &&
                    content.charAt(start) != '-') {
                break;
            }
        }
        return start;
    }
    static int getEndOfTypingWord(int position, String content) {
        int len = content.length();
        int end = position >= len ? len - 1 : position;
        for (; end <= len - 1; end++) {
            if (!Character.isLetter(content.charAt(end)) &&
                    !Character.isDigit(content.charAt(end)) &&
                    content.charAt(end) != '-') {
                break;
            }
        }
        return end;
    }


    /**
     * 判断某个字符串是否左括号
     *
     * @param s          需要判断是否左大/中/小括号的字符串
     * @param bracketMap 括号映射，key为左大/中/小括号，value分别对应为大/中/小括号对
     * @return 字符串s是否左大/中/小括号
     */
    static boolean isBracket(String s, Map<String, String> bracketMap) {
        return bracketMap.keySet().contains(s);
    }


    /**
     * 分离文本为一个个单词
     * TODO 当前有个缺陷就是只能根据空白字符分隔单词，但实际上还应该算上=、&&、||、|等保留符号
     *
     * @param content 需要分离单词的文本
     * @return String数组，包含文本中的所有的单词（单词的界定由空白字符决定）
     */
    static String[] getAllWords(String content) {
        return content.split("\\s+");
    }

    /**
     * 用于将选中的单词插入到JTextArea中
     *
     * @param txtInput JTextArea
     * @param cbInput  JComBox，候选框
     */
    static void setText(JTextArea txtInput, JComboBox cbInput) {
        int position = txtInput.getCaretPosition();

        /*
        position = position
            + 1                      // the length of the space
            + Objects.requireNonNull(cbInput.getSelectedItem()).toString().length()
            - getPrefixForComplete(txtInput.getText(), position).length();
        */

        txtInput.setText(insertCompletion(txtInput, cbInput));

        position = getEndOfTypingWord(position, txtInput.getText()) + 1;
        txtInput.setCaretPosition(position);
    }



    /**
     * 用于获得光标所指 word 的前缀
     * 这里的前缀是指word开头到光标的子串
     *
     * @param content  总文本，避免重复读取文本造成效率降低
     * @param position 一个位置，指向某一个可见字母
     * @return 光标所指 word 的前缀
     */
    static String getPrefixForInsert(String content, int position) {
        int start = Utility.getStartOfTypingWord(position, content);
        String prefix = content.substring(start + 1, position + 1 >= start + 1 ? position + 1 : start + 1);       // 分离出来便于调试
        return prefix;
    }

    static String getPrefixForComplete(String content, int position) {
        int start = Utility.getStartOfTypingWord(position, content);
        String prefix = content.substring(start + 1, position >= start + 1 ? position : start + 1);       // 分离出来便于调试
        return prefix;
    }

    static String getPrefixForRemove(String content, int position) {
        int start = Utility.getStartOfTypingWord(position, content);
        String prefix = content.substring(start + 1, position - 1 >= start + 1 ? position - 1 : start + 1);       // 分离出来便于调试
        return prefix;
    }

    /**
     * 利用关键词生成新的文本框文本，用于支持关键词的补全操作
     *
     * @param txtInput JTextArea，文本输入区域
     * @param cbInput  JComboBox，候选框
     * @return 新的文本
     */
    private static String insertCompletion(JTextArea txtInput, JComboBox cbInput) {
        int position = txtInput.getCaretPosition();
        String content = txtInput.getText();
        int start = Utility.getStartOfTypingWord(position - 1, content);

        return generateNewContext(content,
                Objects.requireNonNull(cbInput.getSelectedItem()).toString() + " ",
                start,
                position);
    }

    /**
     * 用于生成新的文本区域文本，由于补全关键词和括号时候，实现逻辑不同，因此将此功能提取成一个独立的method
     *
     * @param content       原始文本
     * @param insertString  要补全的文本
     * @param startOfWord   要补全的文本在原始文本中已有的前缀的起始位置
     * @param caretPosition 光标位置
     * @return 补全文本后的新的总文本
     */
    private static String generateNewContext(String content, String insertString, int startOfWord, int caretPosition) {
        return content.substring(0, startOfWord + 1) +
                insertString +
                content.substring(caretPosition);
    }

    /**
     * 用于判断光标左右是否都是空白字符
     *
     * @param content  总文本
     * @param position 光标位置
     * @return 光标左右是否都是空白字符，若是则返回true，否则返回false
     */
    static boolean isEmpty(String content, int position) {
        int len = content.length();
        if (position < 0 || position == len) {
            return true;
        } else {
            return Utility.getSpaceSet().contains(content.substring(position, position + 1));
        }
    }

    /**
     * 判断程序是否正在调整候选框内容
     *
     * @param cbInput JComboBox 候选框
     * @return 候选框内容是否正在调整
     */
    static boolean isAdjusting(JComboBox cbInput) {
        if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
            return (Boolean) cbInput.getClientProperty("is_adjusting");
        }
        return false;
    }

    /**
     * 设置候选框的是否正在调整
     *
     * @param cbInput   JComboBox 候选框
     * @param adjusting 是否正在调整候选框内容
     */
    static void setAdjusting(JComboBox cbInput, boolean adjusting) {
        cbInput.putClientProperty("is_adjusting", adjusting);
    }
}
