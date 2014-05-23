package github.rutvijshah.apps.simpletodo.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;
import static github.rutvijshah.apps.simpletodo.app.Constants.*;

/***
 * Simple Todo Application's Main Activity
 *
 * @author  Rutvijkumar Shah
 *
 * Detailed documentation : https://www.dropbox.com/s/ifzym2sjfosvc6z/Installing%20Eclipse%20and%20Android%20SDK.pdf
 *
 */
public class TodoMain extends ActionBarActivity implements View.OnClickListener {

    private List<String> items ;
    private ArrayAdapter<String> itemAdapter;
    private ListView listView;
    private EditText etNewItem;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;

    //Log Tag to trace log messages for the app
    private static final String LOG_TAG = "RUTVIJ_TODOAPP";

    //File name where todos will be stored
    private static final String TODO_FILE="todo.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_main);

        readItems();//Read Items from File

        listView = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);

        //Wiring Items read from files with Adapter to keep track of changes
        itemAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemAdapter);

        //Register event listners for Edit,Remove to ListView
        setupListViewListner();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
        gestureDetector = new GestureDetector(this, new TodoSwipeDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

       // listView.setOnClickListener(TodoMain.this);
        listView.setOnTouchListener(gestureListener);
        }catch (Exception e){
            Log.e(LOG_TAG, "Received an exception", e);
        }

    }

    /***
     * Setup Listner to List View
     *
     */
    private void setupListViewListner() {
        //Listner for Remove todo Item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long row_id) {
                items.remove(pos);
                itemAdapter.notifyDataSetChanged();
                saveItems();
                return true;
            }
        });

        //Listner for Edit todo item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long row_id) {
                Intent intent=new Intent(TodoMain.this,EditItemActivity.class);
                intent.putExtra(EDIT_ITEM, items.get(pos));
                intent.putExtra(EDIT_ITEM_INDEX, pos);
                startActivityForResult(intent,Constants.REQUEST_EDIT_ITEM);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_EDIT_ITEM && data !=null){
            int index=data.getIntExtra(EDIT_ITEM_INDEX,-1);
            String item=data.getStringExtra(EDIT_ITEM);
            if(index !=-1 && item!=null){
                items.set(index,item);
                itemAdapter.notifyDataSetChanged();
                saveItems();
            }
        }
    }



    /***
     * Callback method will be called when add button is clicked
     * @param view
     */
    public void addTodoItem(View view) {
        String itemTobeAdded=etNewItem.getText().toString();

        //Allow only valid values non-null & non-empty
        if(itemTobeAdded!=null && !"".equals(itemTobeAdded.trim())){
            itemAdapter.add(etNewItem.getText().toString());
            Log.d(LOG_TAG,"#items_size="+items.size());
            etNewItem.setText("");
            saveItems();
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etNewItem.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * File IO Operations
     */
    private void readItems(){
        try{
        File filesDir=getFilesDir();
        File todoFile=new File(filesDir,"todo.txt");
        items=new ArrayList<String>(FileUtils.readLines(todoFile));
        }catch (Exception ex){
            items=new ArrayList<String>();
            Log.e(LOG_TAG,"Exception while reading todo itmes from file",ex);
        }

    }
    private void saveItems(){
        File fileDir=getFilesDir();
        File todoFile=new File(fileDir,"todo.txt");
        try{
            FileUtils.writeLines(todoFile,items);
        }catch (IOException e){
            Log.e(LOG_TAG,"Exception while saving todo itmes to file",e);
        }
    }

    @Override
    public void onClick(View view) {

    }

    /***
     * Detects Swipe Gesture
     */
    class TodoSwipeDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                makeText(TodoMain.this, "Left Swipe", LENGTH_SHORT).show();



            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //makeText(TodoMain.this, "Right Swipe", LENGTH_SHORT).show();
                int id = listView.pointToPosition((int) e1.getX(), (int) e1.getY());

//                TextView row = (TextView)listView.getItemAtPosition(id);
//                row.setPaintFlags(row.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            return false;
        }
    }
}
