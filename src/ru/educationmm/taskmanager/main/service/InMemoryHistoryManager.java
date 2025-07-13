package ru.educationmm.taskmanager.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ru.educationmm.taskmanager.main.model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;
    private int historySize = 0;

    class Node {
        Node prev;
        Node next;
        Task data;

        public Node(Task data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    Node linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(task, oldTail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historySize++;
        return newNode;
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        Node node = head;
        while (node != null) {
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

    void removeNode(Node node) {
        if (head == tail) {
            head = tail = null;
        } else {
            if (node.prev == null) {
                head = node.next;
                node.next.prev = null;
            } else if (node.next == null) {
                tail = node.prev;
                node.prev.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }
        historySize--;
    }
}
