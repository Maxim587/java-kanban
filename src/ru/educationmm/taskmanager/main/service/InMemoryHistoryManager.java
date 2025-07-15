package ru.educationmm.taskmanager.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ru.educationmm.taskmanager.main.model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private final Node head;
    private final Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        head = new Node(null, null, null);
        tail = new Node(null, head, null);
        head.next = tail;
    }

    private static class Node {
        private final Task data;
        private Node prev;
        private Node next;

        private Node(Task data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task, tail.prev, tail);
        tail.prev.next = newNode;
        tail.prev = newNode;
        return newNode;
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        Node node = head.next;
        while (!node.equals(tail)) {
            history.add(node.data);
            node = node.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        history.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        history.remove(id);
    }

    private void removeNode(Node node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }
}
