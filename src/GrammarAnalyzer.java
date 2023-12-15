import java.util.*;

public class GrammarAnalyzer {
    private Map<String, Set<String>> productions;

    public Map<String, Set<String>> getFollowSets() {
        return followSets;
    }

    public void setFollowSets(Map<String, Set<String>> followSets) {
        this.followSets = followSets;
    }

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;

    private String start ;

    public GrammarAnalyzer(Map<String, Set<String>> productions) {
        this.productions = productions;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
    }

    public GrammarAnalyzer(List<project>productions,String start) {
        this.start = start;
        this.productions = new HashMap<>();
        for(project p : productions){
            if(!this.productions.containsKey(p.left)){
                this.productions.put(p.left,new HashSet<>());
            }
            this.productions.get(p.left).add(p.right);
        }

        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
    }

    public void calculateSets() {
        calculateFirstSets();
        calculateFollowSets();
    }

    private void calculateFirstSets() {
        for (String nonTerminal : productions.keySet()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, Set<String>> entry : productions.entrySet()) {
                for (String rule : entry.getValue()) {
                    Set<String> first = calculateFirst(rule);
                    int originalSize = firstSets.get(entry.getKey()).size();
                    firstSets.get(entry.getKey()).addAll(first);
                    if (firstSets.get(entry.getKey()).size() != originalSize) {
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    private Set<String> calculateFirst(String rule) {
        Set<String> first = new HashSet<>();
        if (rule.isEmpty() || rule.equals("ε")) {
            first.add("ε");
            return first;
        }
        int i = 0;
        boolean containsEpsilon;
        do {
            containsEpsilon = false;
            String symbol = String.valueOf(rule.charAt(i));
            if (productions.containsKey(symbol)) {
                first.addAll(firstSets.get(symbol));
                if (firstSets.get(symbol).contains("ε")) {
                    first.remove("ε");
                    containsEpsilon = true;
                }
            } else {
                first.add(symbol);
                break;
            }
            i++;
        } while (containsEpsilon && i < rule.length());
        return first;
    }

    private void calculateFollowSets() {
        String startSymbol = start; // Assuming the first non-terminal is the start symbol
        for (String nonTerminal : productions.keySet()) {
//            System.out.println(nonTerminal);
            followSets.put(nonTerminal, new HashSet<>());
        }

        followSets.get(startSymbol).add("#");

        boolean changed;
        do {
            changed = false;
            for (Map.Entry<String, Set<String>> entry : productions.entrySet()) {
                for (String rule : entry.getValue()) {
                    for (int i = 0; i < rule.length(); i++) {
                        String symbol = String.valueOf(rule.charAt(i));
                        if (productions.containsKey(symbol)) {
                            Set<String> follow = calculateFollow(rule.substring(i + 1), entry.getKey());
                            int originalSize = followSets.get(symbol).size();
                            followSets.get(symbol).addAll(follow);
                            if (followSets.get(symbol).size() != originalSize) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    private Set<String> calculateFollow(String beta, String lhs) {
        Set<String> follow = new HashSet<>();
        if (beta.isEmpty()) {
            follow.addAll(followSets.get(lhs));
        } else {
            Set<String> firstOfBeta = calculateFirst(beta);
            follow.addAll(firstOfBeta);
            if (firstOfBeta.contains("ε")) {
                follow.remove("ε");
                follow.addAll(followSets.get(lhs));
            }
        }
        return follow;
    }

    public void printFirstSets() {
        for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
            System.out.println("FIRST(" + entry.getKey() + ") = " + entry.getValue());
        }
    }

    public void printFollowSets() {
        for (Map.Entry<String, Set<String>> entry : followSets.entrySet()) {
            System.out.println("FOLLOW(" + entry.getKey() + ") = " + entry.getValue());
        }
    }
}

