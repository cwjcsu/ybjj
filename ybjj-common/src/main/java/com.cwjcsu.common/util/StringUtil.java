package com.cwjcsu.common.util;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;


public final class StringUtil {

    public static final Random RANDOM = new Random();

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * The empty String <code>""</code>.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";
    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private StringUtil() {

    }

    public static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static char[] PASSWORD_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',//
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
//            '~', '!', '@', '$', '%', '&', '*', '(', ')', '_', '+', '-', ',', '.', '[', ']', '{', '}', '<', '>', '?'};

    /**
     * 生成指定长度由小写字母和数字组成的随机字符串
     *
     * @param length
     * @return
     */
    public static String randomStr(int length) {
        char[] arr = new char[length];
        for (int i = 0; i < length; i++) {
            // 生成下标的随机数
            Random random = new Random();
            int index = random.nextInt(chars.length);
            char tempChar = chars[index];
            arr[i] = tempChar;
        }
        return new String(arr);
    }

    public static String randomPasswordStr(int length) {
        char[] arr = new char[length];
        for (int i = 0; i < length; i++) {
            // 生成下标的随机数
            Random random = new Random();
            int index = random.nextInt(PASSWORD_CHARS.length);
            char tempChar = PASSWORD_CHARS[index];
            arr[i] = tempChar;
        }
        return new String(arr);
    }

    public static String randomNumStr(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += RANDOM.nextInt(10);
        }
        return result;
    }

    /**
     * Returns a random value in the range [0 , range)
     */
    public static int random(int range) {
        return RANDOM.nextInt(range);
    }

    /**
     * Returns a random value in the range [from ,from + range)
     */
    public static int random(int from, int range) {
        return from + RANDOM.nextInt(range);
    }

    public static String bytesToHex(byte[] b) {
        return bytesToHex(b, 0, b.length);
    }

    public static String bytesToHex(byte[] bytes, int offset, int len) {
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            sb.append(HEX_CHAR[(bytes[offset + i] & 0xf0) >>> 4]);
            sb.append(HEX_CHAR[bytes[offset + i] & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        int k = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte high = (byte) (Character.digit(hex.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hex.charAt(k + 1), 16) & 0xff);
            bytes[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return bytes;
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * 参数列表里有任何一个String满足 str==null || "".equals(str.trim())即返回true
     *
     * @param strs
     * @return
     */
    public static boolean isAnyEmpty(String... strs) {
        if (strs.length == 0) {
            return true;
        }
        for (int i = 0; i < strs.length; i++) {
            if (isEmpty(strs[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    public static String joinPath(String path, String file) {
        boolean a = path.charAt(path.length() - 1) == '/';
        boolean b = file.charAt(0) == '/';
        if (a && b) {
            return path + file.substring(1);
        } else if (!a && !b) {
            return path + "/" + file;
        } else {
            return path + file;
        }
    }

    public static String joinPath(String path, String... paths) {
        for (String p : paths) {
            path = joinPath(path, p);
        }
        return path;
    }

    public static boolean equals(String str1, String str2) {
        return (str1 == str2) || (str1 != null && str1.equals(str2));
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return (str1 == str2) || (str1 != null && str1.equalsIgnoreCase(str2));
    }

    public static boolean in(Object thiz, Object... params) {
        for (Object p : params) {
            if (thiz == null && p == null)
                return true;
            if (thiz != null && thiz.equals(p))
                return true;
        }
        return false;
    }

    public static boolean inIgnoreCase(String thiz, String... params) {
        for (String p : params) {
            if (thiz == null && p == null)
                return true;
            if (thiz != null && thiz.equalsIgnoreCase(p))
                return true;
        }
        return false;
    }

    public static String repeat(char ch, int count) {
        char[] array = new char[count];
        for (int i = 0; i < count; ++i)
            array[i] = ch;
        return new String(array);
    }

    public static int countTrue(boolean... trues) {
        int i = 0;
        for (boolean t : trues) {
            if (t)
                i++;
        }
        return i;
    }

    public static String formatSocket(Socket socket) {
        if (socket == null) {
            throw new NullPointerException("socket");
        }

        final StringBuilder builder = new StringBuilder();

        final InetAddress inetAddress = socket.getInetAddress();

        if (inetAddress instanceof Inet6Address) {
            builder.append("[");
            builder.append(inetAddress.getHostAddress());
            builder.append("]");
        } else {
            builder.append(inetAddress.getHostAddress());
        }
        builder.append(":");
        builder.append(socket.getPort());

        return builder.toString();
    }

    /**
     * Performs the logic for the <code>split</code> and
     * <code>splitPreserveAllTokens</code> methods that do not return a
     * maximum array length.
     *
     * @param str               the String to parse, may be <code>null</code>
     * @param separatorChar     the separate character
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     *                          treated as empty token separators; if <code>false</code>, adjacent
     *                          separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.</p>
     * <p/>
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the StrTokenizer class.</p>
     * <p/>
     * <p>A <code>null</code> input String returns <code>null</code>.</p>
     * <p/>
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str           the String to parse, may be null
     * @param separatorChar the character used as the delimiter
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.0
     */
    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    // 替换json中的xss字符
    public static String htmlEncode(String value) {
        if (value != null) {
            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
//        	value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
            value = value.replaceAll("\\\"", "&#34;");
            value = value.replaceAll("'", "&#39;");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
            value = value.replaceAll("script", "");
        }
        return value;
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     * <p/>
     * <p>No separator is added to the joined String.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     * <p/>
     * <pre>
     * StringUtils.join(null)            = null
     * StringUtils.join([])              = ""
     * StringUtils.join([null])          = ""
     * StringUtils.join(["a", "b", "c"]) = "abc"
     * StringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array the array of values to join together, may be null
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * <p/>
     * ends of this String returning an empty String ("") if the String
     * <p/>
     * is empty ("") after the trim or if it is <code>null</code>.
     * <p/>
     * <p/>
     * <p/>
     * <p>The String is trimmed using {@link String#trim()}.
     * <p/>
     * Trim removes start and end characters &lt;= 32.
     * <p/>
     * To strip whitespace use.</p>
     * <p/>
     * <p/>
     * <p/>
     * <pre>
     *
     * StringUtils.trimToEmpty(null)          = ""
     *
     * StringUtils.trimToEmpty("")            = ""
     *
     * StringUtils.trimToEmpty("     ")       = ""
     *
     * StringUtils.trimToEmpty("abc")         = "abc"
     *
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     *
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     * <p/>
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Collection array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array.toArray(), separator, 0, array.size());
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     * <p/>
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     * <p/>
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.  It is
     *                   an error to pass in an end index past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is
     *                   an error to pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return EMPTY;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                + separator.length());

        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * 按字节数截取字符串，一个中文长度为2
     * 将字符编码GBK改为UTF-8，则每个中文长度按3个字符计算
     *
     * @param str
     * @param subSLength
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String subStr(String str, int subSLength)
            throws UnsupportedEncodingException {
        if (str == null)
            return "";
        else {
            int tempSubLength = subSLength;//截取字节数
            String subStr = str.substring(0, str.length() < subSLength ? str.length() : subSLength);//截取的子串
            int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度
            //int subStrByetsL = subStr.getBytes().length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字
            while (subStrByetsL > tempSubLength) {
                int subSLengthTemp = --subSLength;
                subStr = str.substring(0, subSLengthTemp > str.length() ? str.length() : subSLengthTemp);
                subStrByetsL = subStr.getBytes("GBK").length;
                //subStrByetsL = subStr.getBytes().length;
            }
            return subStr;
        }
    }

    public static String toString(Collection<?> datas, String separator) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object o : datas) {
            sb.append(o);
            if (i++ < datas.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 返回集合A与B的差集，即包含只属于A不属于B的所有元素的集合。
     *
     * @param setA
     * @param setB
     * @return
     */
    public static List<String> subtract(Collection<String> setA, Collection<String> setB) {
        List<String> subtracted = new ArrayList<String>();
        for (String ele : setA) {
            if (!setB.contains(ele)) {
                subtracted.add(ele);
            }
        }
        return subtracted;
    }

    public static String encodeURL(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    public static String decodeURL(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    /**
     * 返回省略字符串，使得字符串的byte长度为len。假设中文占2和英文占1。
     *
     * @param src
     * @param len
     * @return
     */
    public static String ellipsisAs(String src, int len) {
        StringBuilder sb = new StringBuilder();
        int l = 0;
        for (int i = 0; i < src.length(); i++) {
            if (l >= len) {
                break;
            }
            char ch = src.charAt(i);
            if (isChinese(ch)) {
                if (l == len - 1) {
                    break;
                }
                l += 2;
            } else {
                l++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static boolean isChChar(char ch) {
        return ch > 255 || ch < 0;
    }

    /**
     * 截取一段子串（从start字节到end字节，假设一个ascii字符1字节，一个非ascii字符2个字节），子串的字节长度为end-start
     *
     * @param string
     * @param start
     * @param end
     * @return
     */
    public static String subCHString(String string, int start, int end) {
        if (string != null) {
            int len = 0;
            StringBuilder strs = new StringBuilder();
            int D = 0;
            for (int i = 0; i < string.length(); i++) {
                int d = isChChar(string.charAt(i)) ? 2 : 1;
                len += d;
                if (end < len || D >= (end - start))
                    return strs.toString();
                else if (start < len) {
                    strs.append(string.charAt(i));
                    D += d;
                }
            }
            return new String(strs);
        }
        return string != null ? string.substring(start, end) : null;
    }

    public static String subCHString(String string, int start) {
        return subCHString(string, start, Integer.MAX_VALUE);
    }

    /**
     * 截取字符串（从start字节截取length个字节,假设一个ascii字符1字节，一个非ascii字符2个字节）
     *
     * @param string
     * @param start
     * @param length
     * @return
     */
    public static String subCHStringByLenth(String string, int start, int length) {
        return subCHString(string, start, start + length);
    }

    public static int lengthCH(String string) {
        if (string != null) {
            int len = 0;
            for (int i = 0; i < string.length(); i++) {
                if (isChChar(string.charAt(i))) len += 2;
                else len++;
            }
            return len;
        }
        return 0;
    }
}
