package ru.vsu.csf.Sashina;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Algorithms {

    @FunctionalInterface
    public interface Visitor<T> {
        void visit(T value, String move);
    }

    public static <T> void preOrderVisit(BinaryTree.TreeNode<T> treeNode, Visitor<T> visitor, T value) {

        class Inner {
            boolean f = false;
            void preOrderVisit(BinaryTree.TreeNode<T> node, Visitor<T> visitor, String move, T value) {
                if (node == null) {
                    return;
                }
                if (node.getValue() == value) {
                    f = true;
                    return;
                }
                visitor.visit(node.getValue(), move);
                preOrderVisit(node.getLeft(), visitor, "L", value);
                if (!f) {
                    preOrderVisit(node.getRight(), visitor, "R", value);
                }
            }
        }

        new Inner().preOrderVisit(treeNode, visitor, "", value);
    }

    public static <T> String toBracketStr(BinaryTree.TreeNode<T> treeNode) {

        class Inner {
            void printTo(BinaryTree.TreeNode<T> node, StringBuilder sb) {
                if (node == null) {
                    return;
                }
                sb.append(node.getValue());
                if (node.getLeft() != null || node.getRight() != null) {
                    sb.append(" (");
                    printTo(node.getLeft(), sb);
                    if (node.getRight() != null) {
                        sb.append(", ");
                        printTo(node.getRight(), sb);
                    }
                    sb.append(")");
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        new Inner().printTo(treeNode, sb);
        return sb.toString();
    }

    public static int[] getValues (String tree, int deletedLeaf) {
        try {
            String array = "";
            char [] chArr = tree.toCharArray();
            for (int i = 0; i < chArr.length; i++) {
                if (chArr[i] >= '0' && chArr[i] <= '9') {
                    array += chArr[i];
                } else if (chArr[i] != ' '){
                    array += " ";
                }
            }
            int l = 0;
            chArr = array.toCharArray();
            array = "";
            for (int i = 0; i < chArr.length; i++) {
                if (chArr[i] >= '0' && chArr[i] <= '9') {
                    array += chArr[i];
                    l = 0;
                } else if (chArr[i] == ' ' && l == 0) {
                    l++;
                    array += chArr[i];
                }
            }
            String [] arr = array.split(" ");
            int[] arrInt = new int[arr.length - 1];
            int j = 0;
            for (int i = 0; i < arr.length; i++) {
                if (Integer.parseInt(arr[i]) != deletedLeaf) {
                    arrInt[j] = Integer.parseInt(arr[i]);
                    j++;
                }
            }
            return arrInt;
        } catch (Exception exp) {
            return null;
        }
    }

    public static int getDeletedLeave (Set<Integer> set0, Set<Integer> set1) {
        Iterator<Integer> it = set0.iterator();
        while (it.hasNext()) {
            int next = it.next();
            if (!set1.contains(next)) {
                return next;
            }
        }
        return -1;
    }
}
