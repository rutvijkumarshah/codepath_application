package github.rutvijshah.apps.projects.gestitodo;

import android.content.Context;
import android.graphics.Paint;
import android.util.*;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements Custom ArrayAdapter for TodoItems
 * Created by Rutvij on 5/20/14.
 *
 * Todos
 * 1. Performance
 * 2. Infinite Scroll
 * 3. Gesture based
 */
public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {

    //list of todos on which arrayAdapter operates
    final private ArrayList<TodoItem> todos;

    //In-memory cache for todoitems <ID,OBJECT>
    final private HashMap<Integer, TodoItem> todoMap;

    //databse handler for persisting TodoItems
    final private DatabaseHandler db;

    public TodoItemsAdapter(Context context, ArrayList<TodoItem> todos) {
        super(context, R.layout.item_todos, todos);
        this.todos = todos;
        this.todoMap = new HashMap<Integer, TodoItem>();
        this.db = new DatabaseHandler(context);
        refresh();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TodoItem todo = getItem(position);
        final TodoItemsAdapter _this = this;
        final ViewHolder viewHolder; // view lookup cache stored in tag

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todos, parent, false);
            viewHolder.todoText = (TextView) convertView.findViewById(R.id.todo_item);
            viewHolder.todoSel = (CheckBox) convertView.findViewById(R.id.todo_item_sel);
            viewHolder.todoSel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton checkbtn, boolean isChecked) {
                    Integer todoId = (Integer) checkbtn.getTag();
                    updateIsDone(todoId, isChecked);
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.todoText.setText(todo.getTodo());
        viewHolder.todoSel.setTag(todo.getId());
        viewHolder.todoSel.setChecked(todo.isDone());

        todoMap.put(todo.getId(), todo);
        if (todo.isDone()) {
            viewHolder.todoText.setPaintFlags(viewHolder.todoText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.todoText.setPaintFlags(viewHolder.todoText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }
        // Return the completed view to render on screen
        return convertView;
    }


    // View lookup cache
    private static class ViewHolder {
        TextView todoText;
        CheckBox todoSel;
    }

    private void populateTodoMap() {
        this.todoMap.clear();
        for (TodoItem item : todos) {
            todoMap.put(item.getId(), item);
        }

    }
    public void refresh() {
        this.todos.clear();
        this.todos.addAll(db.getAll());
        populateTodoMap();
        this.notifyDataSetChanged();
    }

    /*********
     *
     *  Following methods provide convient CRUD methods for UI.
     *
     *  These methods encapsulate persistence, adapter & in-memory cache and provide simple interface
     *  For UI to operate on adapter.
     *
     * */

    public TodoItem getById(int todoId){
        return this.todoMap.get(todoId);
    }

    public void addTodo(TodoItem todo) {
        int id = (int) db.add(todo);
        todo.setId(id);
        this.todos.add(todo);
        this.todoMap.put(id, todo);
        this.notifyDataSetChanged();
    }

    public void update(Integer id, String todoText) {
        TodoItem todo = todoMap.get(id);

        if (todo != null) {
            todo.setTodo(todoText);
            db.update(todo);
            this.notifyDataSetChanged();

        }

    }

    private void updateIsDone(Integer todoId, Boolean isDone) {
        final TodoItem todoItem = todoMap.get(todoId);
        if (todoItem.isDone() != isDone) {
            todoItem.setDone(isDone);
            db.update(todoItem);
            this.notifyDataSetChanged();
        }
    }

    public void deleteSelected() {
        db.deleteAllDoneTodos();
        refresh();
    }


}
