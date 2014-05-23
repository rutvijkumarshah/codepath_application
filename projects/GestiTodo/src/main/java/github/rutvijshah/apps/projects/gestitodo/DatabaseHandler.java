package github.rutvijshah.apps.projects.gestitodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/****
 * DatabaeHandler class provide utility methods to interact with SQLLite db.
 * It provide abstraction and support CRUD operations for TodoItem object.
 * As TodoItem is only primary object for persistence using SQL Query over using ORM.
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todos";

    // table name
    private static final String TABLE_TODO_ITEMS = "todo_items";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TODO= "todo";
    private static final String KEY_IS_DONE = "done";

    public DatabaseHandler(Context context) {
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


    /****
     * Add TodoItem  to database.
     *
     * @param todo
     * @return inserted records id
     */
    public int add(TodoItem todo) {
        SQLiteDatabase db=null;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_TODO, todo.getTodo());
            values.put(KEY_IS_DONE, todo.isDone());
            return (int)db.insertOrThrow(TABLE_TODO_ITEMS, null, values);
        }finally {
            closeConnection(null,db);
        }
    }

    /***
     * Return all TodoItems from Database
     *
     * @return
     */
    public List<TodoItem> getAll() {
        String selectQuery="select id,todo,done from todo_items ";
        return executeQuery(selectQuery);
    }

    /***
     * Return all TodoItems which are marked done
     * @return
     */
    public List<TodoItem> getAllDoneTodos(){
        String selectQuery="select id,todo,done from todo_items where "+KEY_IS_DONE+"='true'";
        return executeQuery(selectQuery);
    }

    /****
     * Helper method to execute query and return resulted TodoItems if any.
     * @param query
     * @return
     */
    private List<TodoItem> executeQuery(final String query){
        SQLiteDatabase db=null;
        Cursor cursor=null;
        try{
            List<TodoItem> items=new ArrayList<TodoItem>();
            db = this.getReadableDatabase();
            cursor = db.rawQuery(query, null);
            if (cursor != null){
                cursor.moveToFirst();
                if(cursor.getCount() >0 ){
                    do{
                        items.add(new TodoItem(cursor.getInt(0),cursor.getString(1),Boolean.valueOf(cursor.getString(2))));
                    }while (cursor.moveToNext());
                }
            }
            return items;
        }finally {
            closeConnection(cursor,db);
        }

    }

    /***
     * Update isDone flag for TodoItem identified by todoId
     * @param todoId
     * @param isDone
     *
     */
    public void updateIsDone(Integer todoId,Boolean isDone){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateIsDone="update todo_items  set "+KEY_IS_DONE+"='"+isDone.toString().toLowerCase()+"' where "+KEY_ID+"="+todoId;
        executeQuery(updateIsDone);

    }

    /****
     * Update TodoItem in database from todo object
     * @param todo
     * @return no of records updated ( Always 1 )
     */

    public int update(TodoItem todo) {
        SQLiteDatabase db=null;
        try{
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_TODO, todo.getTodo());
            values.put(KEY_IS_DONE, String.valueOf(todo.isDone()));

            // updating row
            return db.update(TABLE_TODO_ITEMS, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(todo.getId()) });
        }finally {
            closeConnection(null,db);
        }

    }

    /****
     * Delete all TodoItems which are marked done.
     *
     */
    public void deleteAllDoneTodos(){
        String deleteQuery="delete from todo_items where "+KEY_IS_DONE+"='true'";
        executeQuery(deleteQuery);
    }


    /****
     * Helper method to to close Cursor and Db connection
     * in every method as finally step
     * @param cursor
     * @param db
     */
    private void closeConnection(Cursor cursor,SQLiteDatabase db){
        if(cursor!=null){
            try{
                if(!cursor.isClosed()){
                    cursor.close();
                }
            }catch (Exception e){

            }
        }
        if(db !=null){
            try{
                if(db.isOpen()){
                    db.close();
                }
            }catch (Exception e){

            }
        }

    }

    /****
     * Utility method for debugging
     */
    public void printDB(){
        StringBuilder sb=new StringBuilder();
        List<TodoItem> items = getAll();
        sb.append("\n================================================================================");
        sb.append("\nID   |   DONE   |   TODO   | ");
        sb.append("\n================================================================================");
        for(TodoItem item:items){
            sb.append("\n----------------------------------------------------------------------------------------");
            sb.append("\n"+String.format("\n   %d   |   %s   |   %s   | ",item.getId(),String.valueOf(item.isDone()),item.getTodo()));
        }
        sb.append("\n================================================================================");

        Log.d("#TABLE#\n", sb.toString());
    }

}
