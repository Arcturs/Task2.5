package ru.vsu.csf.Sashina;

import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.Set;

public interface BinaryTree<T> {

    interface TreeNode<T> {

        T getValue();

        boolean isLeaf();

        default TreeNode<T> getLeft() {
            return null;
        }

        default TreeNode<T> getRight() {
            return null;
        }

        default Color getColor() {
            return Color.BLACK;
        }

        default String toBracketStr() {
            return Algorithms.toBracketStr(this);
        }
    }

    TreeNode<T> getRoot();

    Set<T> leaves ();

    int amountOfLeaves (int sum);

    void deleteLeaves(T value) throws Exception;

    default String toBracketStr() {
        return this.getRoot().toBracketStr();
    }
}
