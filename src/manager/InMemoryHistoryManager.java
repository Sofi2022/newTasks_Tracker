package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {


    protected Map<Integer, Node<Task>> history = new HashMap<>();

    protected Node<Task> first;

    protected Node<Task> last;


    private static class Node<T> {
        T item;
        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T element, Node<T> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    void linkLast(Task task) {
        final Node<Task> oldTail = last;
        final Node<Task> newTail = new Node<>(last, task, null);

        history.put(task.getId(), newTail);

        last = newTail;

        if (oldTail == null) {
            first = newTail;
        } else {
            oldTail.next = newTail;
        }
    }


    @Override
    public void add(Task task) {
        linkLast(task);
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            final Node<Task> prevNode = node.prev;
            final Node<Task> nextNode = node.next;

            if (node == first) {
                first = first.next;
                first.prev = null;
                history.remove(node.item.getId());
                return;
            } else if (node == last) {
                last = node.prev;
                last.next = null;
                history.remove(node.item.getId());
                return;
            } else {
                nextNode.prev = prevNode;
                prevNode.next = nextNode;
                history.remove(node.item.getId());
                return;
            }
        }
    }

    @Override
    public void remove(int id) {
        final Node<Task> node = history.get(id);
        removeNode(node);
    }


    public List<Task> getTasks() {
        final List<Task> historyList = new ArrayList<>();
        Node<Task> node = first;
        while (node != null) {

            historyList.add(node.item);
            if (node.next == null) {
                break;
            }
            node = node.next;
        }

        return historyList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
