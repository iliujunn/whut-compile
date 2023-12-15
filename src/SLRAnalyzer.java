import java.util.*;

public class SLRAnalyzer {
    private String expression;
    private List<String> Vt = new ArrayList<>();
    private List<String> Vn = new ArrayList<>();

    //符号栈
    private Deque<String> symbolStack = new ArrayDeque<>();
    //状态栈
    private Deque<Integer> stateStack = new ArrayDeque<>();

    private HashMap<Integer, HashMap<String, String>> Action = new HashMap<>();
    private HashMap<Integer, HashMap<String, Integer>> Goto = new HashMap<>();
    //输入流
    private Deque<String> input = new ArrayDeque<>();
    private String start ;
    private List<project> productions = new ArrayList<>();
    String format = "%-20s %-35s %-35s %-35s %-25s %-1s"; // 调整每列的宽度

    private static StringBuffer Reverse(String S){
        //翻转从1 到 length-2
        StringBuffer rs = new StringBuffer();
        rs.append(S.charAt(0));
        for(int i = S.length()-2 ; i >= 1 ; i--){
            rs.append(S.charAt(i));
        }
        rs.append(S.charAt(S.length()-1));
        return rs;
    }

    public SLRAnalyzer(String expression, List<String> Vt, List<String> Vn, List<project>productions, String start, HashMap<Integer, HashMap<String, String>> Action, HashMap<Integer, HashMap<String, Integer>> Goto) {
        this.expression = expression;
        this.Vt = Vt;
        this.Vn = Vn;
        this.Action = Action;
        this.Goto = Goto;
        this.start = start;
        this.productions = productions;
        stateStack.push(0);
        //压入左界符
        symbolStack.push("#");
        //初始化输入流
        input.push("#");
        for(int i = expression.length()-1 ; i >= 0 ; i--){
            input.push(expression.substring(i,i+1));
        }

        sb.append(String.format(format,"步骤","状态栈","符号栈","输入串","分析动作","下一状态"));
        sb.append("\n");
    }

    // -1 正常
    //大于等于0 在返回值处出错
    public Integer Analyzer(){
        String X ;
        Integer index = 0;
        Integer step = 1 ;
        while(true){
            X = input.peek();
            Integer s = stateStack.peek();
            String action = Action.get(s).get(X);
            if(action == null){
               sb.append(String.format(format,step,Reverse(stateStack.toString()),Reverse(symbolStack.toString()),input.toString(),"error"+"("+s+","+X+")","")+ "\n");
                return index;
            }else{
                if(action.charAt(0) == 's') {
                    //移进
                    sb.append(String.format(format,step,Reverse(stateStack.toString()),Reverse(symbolStack.toString()),input.toString(),action+"("+s+","+X+")","")+ "\n");
                    stateStack.push(Integer.parseInt(action.substring(1)));
                    symbolStack.push(X);
                    input.pop();
                    index++;


                }else if(action.charAt(0) == 'r'){
                    //规约
                    sb.append(String.format(format,step,Reverse(stateStack.toString()),Reverse(symbolStack.toString()),input.toString(),action+"("+s+","+X+")","GOTO["+stateStack.peek()+","+X+"]="));
                    Integer num = Integer.parseInt(action.substring(1));
                    project p = productions.get(num);
                    String right = p.right;
                    int length = right.length();
                    for(int i = 0 ; i < length ; i++){
                        stateStack.pop();
                        symbolStack.pop();
                    }
                    String A = p.left;
                    symbolStack.push(A);
                    Integer t = stateStack.peek();
                    Integer s1 = Goto.get(t).get(A);
                    sb.append(s1+"\n");
                    if(s1 == null){
                        sb.append(String.format(format,step,Reverse(stateStack.toString()),Reverse(symbolStack.toString()),input.toString(),action+"("+s+","+X+")","出错")+ "\n");
                        return index;
                    }
                    stateStack.push(s1);
                }else if(action.equals("acc")){
                    sb.append(String.format(format,step,Reverse(stateStack.toString()),Reverse(symbolStack.toString()),input.toString(),"acc","")+ "\n");
                    return -1;
                }
            }
            ++ step ;
        }
//        return -1 ;
    }
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getVt() {
        return Vt;
    }

    public void setVt(List<String> vt) {
        Vt = vt;
    }

    public List<String> getVn() {
        return Vn;
    }

    public void setVn(List<String> vn) {
        Vn = vn;
    }

    public StringBuffer getSb() {
        return sb;
    }

    public void setSb(StringBuffer sb) {
        this.sb = sb;
    }

    //分析过程
    private StringBuffer sb = new StringBuffer();

}
