package com.example.jobseeker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the AVL tree, with a location key and associated job listings.
 * @author Jiaqi Zhuang
 */
class Node {
    String key; // Location key of the node
    List<JSONObject> value; // List of job listings for the location
    int height;
    Node left, right;

    Node(String key, JSONObject job) {
        this.key = key;
        this.value = new ArrayList<>();
        this.value.add(job);
        height = 1;
    }
}

/**
 * Represents an AVL tree which stores job listings by location.
 * This class is a singleton, ensuring only one instance can exist.
 * @author Jiaqi Zhuang
 */
class AVLTree {
    private Node root;
    // Declare a private static instance of the AVLTree
    private static AVLTree instance;
    // Make the constructor private to prevent instantiation
    private AVLTree() {}


    /**
     * Returns the single instance of the AVLTree. If not created, it initializes it.
     *
     * @return The single instance of the AVLTree
     */
    public static AVLTree getInstance() {
        if (instance == null) {
            synchronized (AVLTree.class) {
                if (instance == null) {
                    instance = new AVLTree();
                }
            }
        }
        return instance;
    }

    /**
     * Returns the height of the given node.
     *
     * @param N Node whose height is to be determined
     * @return Height of the node
     */
    private int height(Node N) {
        if (N == null) return 0;
        return N.height;
    }

    /**
     * Computes the balance factor of the given node.
     *
     * @param N Node whose balance factor is to be determined
     * @return Balance factor of the node
     */
    private int getBalance(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    /**
     * Right-rotates the subtree rooted with the given node.
     *
     * @param y The root node of the subtree to be right-rotated
     * @return New root of the subtree after rotation
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    /**
     * Left-rotates the subtree rooted with the given node.
     *
     * @param x The root node of the subtree to be left-rotated
     * @return New root of the subtree after rotation
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    /**
     * Inserts a new job listing for the specified location into the AVL tree.
     *
     * @param location Location of the job listing
     * @param job Job listing details
     */
    public void insert(String location, JSONObject job) {
        root = insert(root, location, job);
    }

    /**
     * Recursively inserts a new job listing for the specified location in the subtree rooted at the given node.
     *
     * @param node The root node of the subtree where insertion is to occur
     * @param location Location of the job listing
     * @param job Job listing details
     * @return Root node of the subtree after insertion
     */
    private Node insert(Node node, String location, JSONObject job) {
        if (node == null) return new Node(location, job);

        int comparison = location.compareTo(node.key);
        if (comparison < 0) node.left = insert(node.left, location, job);
        else if (comparison > 0) node.right = insert(node.right, location, job);
        else node.value.add(job);

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && location.compareTo(node.left.key) < 0) return rotateRight(node);
        if (balance < -1 && location.compareTo(node.right.key) > 0) return rotateLeft(node);
        if (balance > 1 && location.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && location.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    /**
     * Searches for job listings associated with the specified location.
     *
     * @param location Location of the job listings to be searched
     * @return List of job listings for the given location, or null if not found
     */
    public List<JSONObject> search(String location) {
        Node node = search(root, location);
        if (node != null) return node.value;
        else return null;
    }

    /**
     * Recursively searches for a node with the specified location in the subtree rooted at the given node.
     *
     * @param root The root node of the subtree where search is to occur
     * @param location Location to be searched
     * @return Node with the given location, or null if not found
     */
    private Node search(Node root, String location) {
        if (root == null || root.key.toLowerCase().equals(location.toLowerCase())) return root;

        if (location.toLowerCase().compareTo(root.key.toLowerCase()) < 0) {
            return search(root.left, location);
        } else {
            return search(root.right, location);
        }
    }
}