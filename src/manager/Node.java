package manager;

public class Node<T> {
    public Node<T> prev;
    public Node<T> next;
    public T data;

    public Node(T data) {
        this.prev = null;
        this.next = null;
        this.data = data;
    }
}
