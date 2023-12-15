import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;


public class Main{

    public static void main(String[] args) throws IOException {
        AnalysisTable analysisTable = new AnalysisTable("../rule3.txt");;
//        计算所有状态
        analysisTable.init();



        //获取Vt,Vn,start,productions
        List<String> Vt = analysisTable.getVt();
        List<String> Vn = analysisTable.getVn();
        String start = analysisTable.getStart();
        List<project>productions = analysisTable.getProductions();

        GrammarAnalyzer analyzer = new GrammarAnalyzer(productions,start);
        //计算follow集合
        analyzer.calculateSets();

        analyzer.printFirstSets();

        analyzer.printFollowSets();

        //获取follow集
        Map<String,Set<String>> followSets = analyzer.getFollowSets();

        //构造分析表
        analysisTable.buildTable(followSets);

        HashMap<Integer,HashMap<String,String>> Action = analysisTable.getAction();
        HashMap<Integer,HashMap<String,Integer>> Goto = analysisTable.getGoto();

        List<TreeSet<project>>I = analysisTable.getI();
        //打印产生式
//        PrintProductions(productions);
//        //打印状态
//        PrintState(I);
////        打印终结符
//        PrintVt(Vt);
        //Follow集

        //从expression.in文件中读取多条表达式

        BufferedReader bf = new BufferedReader(new FileReader("../expression.in"));
        String expression;
        Integer count = 1 ;
        while((expression = bf.readLine() )!= null){
            //去除空格
            expression = expression.replaceAll(" ","");
            try{
                //转化
                expression = ExpressionProcessor.processExpression(expression);
            }catch (Exception e){
                System.out.println("第"+count+"条表达式错误");
                count++;
                System.out.println(e.getMessage());
                continue;
            }

            System.out.println("第"+count+"条表达式为：" + expression);
            SLRAnalyzer slrAnalyzer = new SLRAnalyzer(expression, Vt, Vn, productions,start ,Action, Goto);
            Integer index = slrAnalyzer.Analyzer();
            System.out.println("第"+count+"条表达式的分析结果：");
            StringBuffer sb = slrAnalyzer.getSb();
            if(index == -1){

                System.out.println("分析成功");
            }else{
                System.out.println("分析失败");
                System.out.println("错误位置：" + index);
            }
            System.out.println(sb.toString());
            count++;
        }


        bf.close();

        //打印分析表
        PrintTable(Action,Goto,Vt,Vn);

    }

    //打印状态
    public static void PrintState(List<TreeSet<project>> I){
        for(int i = 0 ; i < I.size() ; i++){
            System.out.println("I" + i + ":");
            Print(I.get(i));
        }
    }
    //打印终结符
    public static void PrintVt(List<String> Vt){
        System.out.println("终结符：");
        for(String s : Vt){
            System.out.print(s + " ");
        }
        System.out.println();
    }


    //打印产生式
    public static void PrintProductions(List<project> productions){
        System.out.println("产生式：");
        for(project p : productions){
            System.out.println(p.left + "->" + p.right);
        }
    }

    public static void Print(TreeSet<project> I){
        System.out.println("{");
        for(project p : I){
            System.out.println("\t"+p.left + "->" + p.right + " " );
        }
        System.out.println("}");

    }

    //打印分析表
    public static void PrintTable(HashMap<Integer,HashMap<String,String>> Action,HashMap<Integer,HashMap<String,Integer>> Goto,List<String>Vt,List<String>Vn){

        Vt.add("#");
        Vn.remove("S");
        System.out.println("分析表：");
        System.out.print("状态\t");
        for(String s : Vt){
            System.out.print(s + "\t");
        }
        for(String s : Vn){
            System.out.print(s + "\t");
        }
        System.out.println();
        for(int i = 0 ; i < Action.size() ; i++){
            System.out.print(i + "\t");
            for(String s : Vt){
                if(Action.get(i).get(s) == null){
                    System.out.print("\t");
                }else{
                    System.out.print(Action.get(i).get(s) + "\t");
                }
            }
            for(String s : Vn){
                if(Goto.get(i)== null){
                    System.out.print("\t");
                    continue;
                }
                if(Goto.get(i).get(s) == null){
                    System.out.print("\t");
                }else{
                    System.out.print(Goto.get(i).get(s) + "\t");
                }
            }
            System.out.println();
        }

    }
}
