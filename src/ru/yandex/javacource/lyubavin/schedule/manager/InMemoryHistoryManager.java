package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    public static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;

    public Node<Task> linkLast(Task element) {
        Node<Task> oldTail = tail;
        tail = new Node<>(oldTail, element, null);

        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
        }
        return tail;
    }

    public void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        if (node == head) {
            head = node.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasksArray = new ArrayList<>();
        Node<Task> node = head;

        while (node != null) {
            tasksArray.add(node.data);
            node = node.next;
        }

        return tasksArray;
    }

    @Override
    public void addTaskToHistory(Task task) {

        final int taskId = task.getId();

        remove(taskId);
        historyMap.put(taskId, linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }
}
