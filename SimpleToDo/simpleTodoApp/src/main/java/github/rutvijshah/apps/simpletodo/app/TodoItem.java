package github.rutvijshah.apps.simpletodo.app;

/**
 * Created by sharu03 on 5/9/14.
 */
public class TodoItem {

    private int id;
    private String todo;
    private boolean done;

    public TodoItem(){

    }
    public TodoItem(int id,String todo, boolean done) {
        this.id=id;
        this.todo = todo;
        this.done = done;
    }

    public TodoItem(String todo, boolean done) {
        this.todo = todo;
        this.done = done;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoItem todoItem = (TodoItem) o;

        if (done != todoItem.done) return false;
        if (id != todoItem.id) return false;
        if (!todo.equals(todoItem.todo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + todo.hashCode();
        result = 31 * result + (done ? 1 : 0);
        return result;
    }
}
