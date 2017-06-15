package net.fortytwo.smsn.brain.model.entities;

public interface TreeNode<T> extends Node {

    T getValue();

    void setValue(T value);

    ListNode<TreeNode<T>> getChildren();

    void setChildren(ListNode<TreeNode<T>> children);
}
