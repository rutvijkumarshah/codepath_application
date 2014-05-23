package github.rutvijshah.apps.projects.gestitodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/****
 *
 * AllTask is main activity for TodoApp.
 *
 */
public class AllTasks extends ActionBarActivity {

    private TodoItemsAdapter itemAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        listView = (ListView) findViewById(R.id.lvItems);

        //Wiring Items read from files with Adapter to keep track of changes
        itemAdapter = new TodoItemsAdapter(this, new ArrayList<TodoItem>());
        listView.setAdapter(itemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long row_id) {
                TodoItem item = (TodoItem)adapterView.getItemAtPosition(pos);
                showDialog(true,item.getId());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_add :
                    showDialog(false,-1);
                    break;
            case R.id.action_delete:
                itemAdapter.deleteSelected();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    /***
     * Shows dialog for Add/Edit ToodItem
     * @param isEdit Open Dialog for Add or Edit
     * @param todoId is mandator only when isEdit is true
     */
    private void showDialog(final boolean isEdit,final int todoId){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.add_edit_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.add_edit_item);
        final TextView textView = (TextView) promptView.findViewById(R.id.add_edit_lable);
        TodoItem item=null;

        if(isEdit){
            textView.setText("Edit:");
             item=itemAdapter.getById(todoId);
            input.setText(item.getTodo());
        }else{
            textView.setText("Add:");
        }

        alertDialogBuilder.setCancelable(true);

        final AlertDialog alertD = alertDialogBuilder.create();
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    TodoItem todo=null;
                    String inputString=input.getText().toString();
                    if(isEdit){
                        itemAdapter.update(todoId,inputString);

                    }else{
                        todo=new TodoItem();
                        todo.setTodo(inputString);
                        itemAdapter.addTodo(todo);
                    }
                    alertD.cancel();
                    return true;
                }else{
                    return false;
                }
            }
        });

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertD.show();

    }
}
