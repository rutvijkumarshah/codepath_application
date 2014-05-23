package github.rutvijshah.apps.simpletodo.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static github.rutvijshah.apps.simpletodo.app.Constants.*;

/***
 * Edit Item Screen
 * @author  Rutvijkumar Shah
 *
 */
public class EditItemActivity extends ActionBarActivity {

    private EditText editText;
    private Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo_item);

        //Binding UI elements
        editText=(EditText)findViewById(R.id.eExistingItem);
        saveBtn=(Button)findViewById(R.id.btnSaveItem);

        //this is child activity always created by navigated from main activity.
        //Getting contextual information from intent.
        Intent intent=getIntent();
        final int item_index=intent.getIntExtra(EDIT_ITEM_INDEX,-1);
        String  itemToEdit=intent.getStringExtra(EDIT_ITEM);
        editText.setText(itemToEdit);

        //Setting click listner on button which returns modified todo item to main activity
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedItem=editText.getText().toString();
                Intent intent=new Intent();
                intent.putExtra(EDIT_ITEM_INDEX,item_index);
                intent.putExtra(EDIT_ITEM,editedItem);
                setResult(REQUEST_EDIT_ITEM,intent);
                finish();//finishing activity
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_todo_item, menu);
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

}
