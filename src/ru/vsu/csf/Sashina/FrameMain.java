package ru.vsu.csf.Sashina;

import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

public class FrameMain extends JFrame{

    private JPanel Main;
    private JPanel paintArea;
    private JTextField treeField;
    private JButton paintTree;
    private JLabel paintTreeMist;
    private JButton deleteLeaf;
    private JLabel searchTreeMist;
    private JButton nodeRout;
    private JLabel nodeRoutLabel;
    private JButton clearButton;

    private JPanel paint = null;

    private void repaintTree () {
        paint.repaint();
    }

    private void showSystemOut(Runnable action) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos, true, "UTF-8"));

            action.run();

            nodeRoutLabel.setText(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            SwingUtils.showErrorMessageBox(e);
        }
        System.setOut(oldOut);
    }

    BinaryTree<Integer> Tree = new SimpleBinaryTree<>();
    BinaryTree<Integer> startTree = new SimpleBinaryTree<>();
    int deletedLeaves;

    public FrameMain() {
        this.setTitle("Деревья");
        this.setContentPane(Main);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        paint = new JPanel() {
            private Dimension paintSize = new Dimension(0, 0);

            @Override
            public void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                paintSize = TreePainter.paint(Tree, gr);
                if (!paintSize.equals(this.getPreferredSize())) {
                    SwingUtils.setFixedSize(this, paintSize.width, paintSize.height);
                }
            }
        };
        JScrollPane paintJScrollPane = new JScrollPane(paint);
        paintArea.add(paintJScrollPane);

        paintTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SimpleBinaryTree<Integer> tree = new SimpleBinaryTree<>(Integer::parseInt);
                    tree.fromBracketNotation(treeField.getText());
                    SimpleBinaryTree<Integer> tree1 = new SimpleSearchTree<>(Integer::parseInt);
                    tree1.fromBracketNotation(treeField.getText());
                    startTree = tree1;
                    Tree = tree;
                    repaintTree();
                } catch (Exception ex) {
                   paintTreeMist.setText("Невозможно построить дерево");
                }
            }
        });

        deleteLeaf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SimpleSearchTree<Integer> tree = new SimpleSearchTree<>(Integer::parseInt);
                    BinaryTree.TreeNode<Integer> node = Tree.getRoot();
                    Set<Integer> delLeave = new HashSet<>();
                    Optional<Integer> min = Optional.empty();
                    Optional<Integer> max = Optional.empty();
                    delLeave = tree.isBSTree(node, min, max, delLeave);
                    if (delLeave != null) {
                        searchTreeMist.setText("Такой лист найден");
                        Iterator<Integer> it = delLeave.iterator();
                        deletedLeaves = it.next();
                        int[] values = Algorithms.getValues(treeField.getText(), deletedLeaves);
                        tree.clear();
                        for (int v : values) {
                            tree.put(v);
                        }
                        Tree = tree;
                        repaintTree();
                    } else {
                        searchTreeMist.setText("Такого листа нет или такой лист не один");
                    }
                } catch (Exception exp) {
                    searchTreeMist.setText("Такого листа нет");
                }
            }
        });

        nodeRout.addActionListener(actionEvent -> {
            showSystemOut(() -> {
                Algorithms.preOrderVisit(startTree.getRoot(), (value, move) -> {
                    System.out.println(move + " ");
                }, deletedLeaves);
            });
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tree = new SimpleBinaryTree<>();
                startTree = new SimpleBinaryTree<>();
                deletedLeaves = 0;
                paintTreeMist.setText("");
                treeField.setText("");
                searchTreeMist.setText("");
                nodeRoutLabel.setText("");
            }
        });
    }
}
