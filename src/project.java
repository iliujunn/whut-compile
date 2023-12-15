public class project implements Comparable<project> {
    String left;
    String right;

    // 重写 compareTo 方法
    @Override
    public int compareTo(project other) {
        // 首先比较 left 字段
        int leftCompare = this.left.compareTo(other.left);
        // 如果 left 字段相等，则比较 right 字段
        if (leftCompare == 0) {
            return this.right.compareTo(other.right);
        }
        return leftCompare;
    }

    // project 类的其他部分...
}
