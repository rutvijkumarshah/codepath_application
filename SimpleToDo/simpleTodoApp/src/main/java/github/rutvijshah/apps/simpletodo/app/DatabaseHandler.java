package github.rutvijshah.apps.simpletodo.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todos";

    // Contacts table name
    private static final String TABLE_TODO_ITEMS = "todo_items";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TODO= "todo";
    private static final String KEY_IS_DONE = "done";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO_ITEMS + "("
                + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + KEY_TODO + " TEXT,"
                + KEY_IS_DONE + " BOOLEAN DEFAULT 'false' NOT NULL" + ")";
        db.execSQL(CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }


    public void add(TodoItem todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TODO, todo.getTodo());
        values.put(KEY_IS_DONE, todo.isDone());
        db.insert(TABLE_TODO_ITEMS, null, values);
        db.close(); // Closing database connection

    }

    // Getting single contact
    public TodoItem getById(int id) {
        TodoItem item=null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery="select id,todo,done from todo_items where id="+id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            item=new TodoItem(cursor.getInt(0),cursor.getString(1),Boolean.valueOf(cursor.getString(2)));
        }
        return item;
    }


    public List<TodoItem> getAll() {
        List<TodoItem> items=new ArrayList<TodoItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery="select id,todo,done from todo_items ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            cursor.moveToFirst();
            do{
                items.add(new TodoItem(cursor.getInt(0),cursor.getString(1),Boolean.valueOf(cursor.getString(2))));
            }while (cursor.moveToNext());

        }
        return items;
    }


    public int update(TodoItem todo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO, todo.getTodo());
        values.put(KEY_IS_DONE, todo.isDone());

        // updating row
        return db.update(TABLE_TODO_ITEMS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getId()) });
    }


    public void delete(TodoItem todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO_ITEMS, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getId()) });
        db.close();
    }
}
