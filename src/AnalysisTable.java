import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

public class AnalysisTable {

    String path;
    private String start ;
    List<project> projects = new ArrayList<>();

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
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

    //    终结符
    private List<String>Vt = new ArrayList<>();
//    非终结符
    private List<String>Vn = new ArrayList<>();

    public List<project> getProductions() {
        return productions;
    }

    public void setProductions(List<project> productions) {
        this.productions = productions;
    }

    private List<project>productions = new ArrayList<>();

    public List<TreeSet<project>> getI() {
        return I;
    }

    public void setI(List<TreeSet<project>> i) {
        I = i;
    }

    private List<TreeSet<project>>I = new ArrayList<>();      // 状态表
    //状态转移 go[Ii][X] = Ij
    HashMap<Integer , HashMap<String,Integer>>go = new HashMap<>();

    public HashMap<Integer, HashMap<String, String>> getAction() {
        return action;
    }

    public void setAction(HashMap<Integer, HashMap<String, String>> action) {
        this.action = action;
    }

    public HashMap<Integer, HashMap<String, Integer>> getGoto() {
        return Goto;
    }

    public void setGoto(HashMap<Integer, HashMap<String, Integer>> aGoto) {
        Goto = aGoto;
    }

    // Action表
    private HashMap<Integer , HashMap<String , String>>action = new HashMap<>();
// Goto表
    private HashMap<Integer , HashMap<String , Integer>>Goto = new HashMap<>();

    AnalysisTable(String path) throws IOException {
        this.path = path;
        //读取
        BufferedReader bf = new BufferedReader(new FileReader(path));
        String line = null;
        while ((line = bf.readLine()) != null) {
            //去除空格
            line = line.replaceAll(" ", "");
            String[] split = line.split("->");
            String left = split[0];
            //初始化Vn
            //左部作为非终结符，如果Vn中没有则添加
            if (!Vn.contains(left)) {
                Vn.add(left);
            }
            String right = split[1];
            //初始化Vt
            //右部中非大写字母作为终结符，如果Vt中没有则添加
            for (int i = 0; i < right.length(); i++) {
                if (!Character.isUpperCase(right.charAt(i))) {
                    if (!Vt.contains(right.charAt(i) + "")) {
                        Vt.add(right.charAt(i) + "");
                    }
                }
            }
            //添加产生式
            project p = new project();
            p.left = left;
            p.right = right;
            productions.add(p);


        }
        bf.close();
        start = productions.get(0).left;

    }

    //求closure集合
    TreeSet<project> closure(TreeSet<project> P){
        List<project>closureSet = new ArrayList<>(P);
        HashMap<String, Boolean>isIn = new HashMap<>();
        for(int i = 0 ; i  < closureSet.size() ; ++ i ){
            project p1 = closureSet.get(i);
            String right = p1.right;
            int index = right.indexOf(".");
            if(index == right.length() - 1){
                continue;
            }
            String next = right.charAt(index + 1) + "";
            if(Vn.contains(next) && !isIn.containsKey(next)){
                isIn.put(next , true);
                for(project p2 : productions){
                    if(p2.left.equals(next)){
                        project p3 = new project();
                        p3.left = p2.left;
                        p3.right = "." + p2.right;
                        if(!closureSet.contains(p3)){
                            closureSet.add(p3);
                        }
                    }
                }
            }
        }
        TreeSet<project>closure_Set = new TreeSet<>(closureSet);
        return closure_Set;
    }

    //go方法
    TreeSet GO(TreeSet<project> Ii , String X)  {
        TreeSet<project> J = new TreeSet<>();
        for(project p : Ii){

            String right = p.right;
            int index = right.indexOf(".");
            if(index == right.length() - 1){
                continue;
            }
            String next = right.charAt(index + 1) + "";
            if(!X.equals(next)){
                continue;
            }
            project p1 = new project();
            p1.left = p.left;
            p1.right = p.right.substring(0 , index) + next + "." + p.right.substring(index + 2);
            J.add(p1);
        }

//        int k = I.indexOf(Ii);
        TreeSet<project>J1 = closure(J);
        return J1;
    }

    //初始化，利用go求所有状态
    void init(){
        TreeSet<project> I0 = new TreeSet<>();
        for(project p : productions){
                project p1 = new project();
                p1.left = p.left;
                p1.right = "." + p.right;
                I0.add(p1);

        }
        I.add(I0);
        for(int i = 0 ; i < I.size() ; ++ i){
            TreeSet<project> Ii = I.get(i);

            for(project p1 : Ii){
                String right = p1.right;
                int index = right.indexOf(".");
                if(index == right.length() - 1){
                    continue;
                }
                String next = right.charAt(index + 1) + "";


                TreeSet ts = GO(Ii , next);

                if(ts.size() >0 ){
//                    System.out.println("*");
                    if(!I.contains(ts)){
                        I.add(ts);
                    }
                    if(null == go.get(i)) go.put(i , new HashMap<>());
                    go.get(i).put(next , I.indexOf(ts));
                }

            }

        }

    }

    //构造分析表

    void buildTable(Map<String,Set<String>> followSets){
        for(int i = 0 ; i < I.size() ; ++i){
            HashMap<String,Integer>mp = go.get(i);
            if(null != mp){
                for(String X : mp.keySet()){
                    if(Vt.contains(X)){
                        if(null == action.get(i))action.put(i , new HashMap<>());
                        action.get(i).put(X , "s" + mp.get(X));

                    }else if(Vn.contains(X)){
                        if(null == Goto.get(i)) Goto.put(i , new HashMap<>());
                        Goto.get(i).put(X , mp.get(X));
                    }
                }

            }
            //分割线
            TreeSet<project> Ii = I.get(i);
            for(project p : Ii){
                String right = p.right;
                int index = right.indexOf(".");
                if(index == right.length() - 1){
                    if(p.left.equals(start)){
                        if(null == action.get(i)) action.put(i , new HashMap<>());
                        action.get(i).put("#" , "acc");
                    }else{
                        //此处可以优化，空间换时间
                        int k = -1  ;
                        for(int j = 0 ; j < productions.size() ; ++ j){
                            project p1 = productions.get(j);
                            String Left = p1.left;
                            String Right = p1.right+".";
                            if(Left.equals(p.left) && Right.equals(p.right)){
                                k = j;
                                break;

                            }
                        }
                        if(k != -1 ){
                            if(null == action.get(i)) action.put(i , new HashMap<>());
                            List<String> V = new ArrayList<>(Vt);
                            V.add("#");
                            for(String X : V){
                                Set<String> set = followSets.get(p.left);
                                if(set.contains(X)) action.get(i).put(X , "r" + k);
                            }
                        }
                    }
                }
            }
        }

    }

}



