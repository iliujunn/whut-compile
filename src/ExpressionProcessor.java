public class ExpressionProcessor {
    public static String processExpression(String expression) throws Exception {
        StringBuilder processed = new StringBuilder();
        StringBuilder token = new StringBuilder();
        boolean isInvalid = false;
        boolean digitStart = false;  // 用于标记token是否以数字开头

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isLetter(ch)) {
                if (digitStart) {
                    isInvalid = true;
                    break;
                }
                token.append('l');
            } else if (Character.isDigit(ch)) {
                if (token.length() == 0) {
                    digitStart = true;  // 标记token以数字开头
                }
                token.append('d');
            } else if (ch == '.') {
                isInvalid = true;
                break;
            } else {
                if (token.length() > 0) {
                    processed.append(token);
                    token.setLength(0);
                }
                digitStart = false;  // 重置digitStart标记
                processed.append(ch);
            }
        }

        if (token.length() > 0) {
            processed.append(token);
        }
        if(isInvalid){
            throw new Exception("Invalid token in expression.");
        }
        return  processed.toString();
    }

    public static void main(String[] args) throws Exception {
        String expression = "1var = 123 + abc";
        System.out.println(processExpression(expression));
    }
}
