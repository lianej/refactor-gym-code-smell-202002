public class UnicodeTransfer {
    /**
     * 将包含unicode的字符串 转 中文字符串
     * 将每个unicode编码计算出其值，再强转成char类型，然后将这个字符存储到字符串中
     */
    public static String unicode2String(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0;i < str.length();) {
            if (str.charAt(i) == '\\' && str.charAt(i + 1) == 'u') {
                String unicode = str.substring(i + 2, i + 6);
                if (unicode.matches("[0-9a-fA-F]{4}")) {
                    char ch = (char) Integer.parseInt(unicode, 16);
                    result.append(ch);
                    i += 6;
                } else {
                    result.append("\\u");
                    i += 2;
                }
            } else {
                result.append(str.charAt(i));
                i++;
            }
        }

        return result.toString();
    }
}