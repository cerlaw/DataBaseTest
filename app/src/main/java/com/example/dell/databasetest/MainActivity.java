package com.example.dell.databasetest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabaseHelper = new MyDatabaseHelper(this,"Bookstore.db",null,2);
        Button button = (Button) findViewById(R.id.create_database);
        Button adddata = (Button) findViewById(R.id.add_data);
        Button updata = (Button) findViewById(R.id.updatea_data);
        Button delete = (Button) findViewById(R.id.delete_data);
        Button query = (Button) findViewById(R.id.query_data);
        Button replace = (Button) findViewById(R.id.replace_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseHelper.getWritableDatabase();
                //要使用root了的手机才能使用adb shell来查看/data中数据库的创建
                //应该有不需要使用root的手机就可以查看的方法，待定。。
            }
        });
        adddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name","The Da Vinci Code");
                values.put("author","Dan Brown");
                values.put("page",454);
                values.put("price",15.96);
                db.insert("book",null,values);//插入第一条数据
                values.clear();
                values.put("name","The Lost Symbol");
                values.put("author","Dan Brown");
                values.put("page",530);
                values.put("price",19.46);
                db.insert("book",null,values);//插入第二条数据
                Toast.makeText(MainActivity.this,"add data succeeded!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price",10.99);
                db.update("book",values,"name = ?",new String[] {"The Da Vinci" +
                        " Code"});
                Toast.makeText(MainActivity.this,"update succeeded!",Toast.
                        LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        MainActivity.this
                );
                dialog.setTitle("Warning!");
                dialog.setMessage("Are you sure to delete the book's page  is " +
                        "> 500");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete("book","page > ?",new String[] {"500"});
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.
                        TYPE_TOAST);
                alertDialog.show();
            }
        });
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                Cursor cursor = db.query("book",null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        String name = cursor.getString(cursor.getColumnIndex(
                                "name"));
                        String author = cursor.getString(cursor.getColumnIndex
                                ("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex(
                                "page"));
                        double price = cursor.getDouble(cursor.getColumnIndex(
                                 "price"));
                        Log.d("MainActivity","book name is:" + name);
                        Log.d("MainActivity","book author is:" + author);
                        Log.d("MainActivity","book page is:" + pages);
                        Log.d("MainActivity","book price is:" + price);
                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
        });
        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
                db.beginTransaction();    //开启事务
                try{
                    db.delete("book",null,null);
                    ContentValues values = new ContentValues();
                    values.put("name","Game of Thrones");
                    values.put("author","George Martin");
                    values.put("page",720);
                    values.put("price",20.85);
                    db.insert("book",null,values);
                    db.setTransactionSuccessful();   //事务已经执行成功
                }catch (NullPointerException e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
            }
        });
    }
}
