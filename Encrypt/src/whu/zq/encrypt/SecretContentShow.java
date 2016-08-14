package whu.zq.encrypt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class SecretContentShow extends Activity {

    private EditText text_search;   // 搜索内容
    private ImageButton btn_search; // 启动搜索
    private ListView lv_code;       // list管理器
    private SharedPreferences sp;
    private int secLevel;           // 安全级别
    private CodeBaseAdapter codeBaseAdapter;
    private String db_table_name = "db_security_code_high";
    public static boolean checkDeleteFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_content_show);
        lv_code = (ListView) findViewById(R.id.list_view_code);
        secLevel = getIntent().getIntExtra("KeyLevel", -1);
        btn_search = (ImageButton) findViewById(R.id.btn_text_search);
        text_search = (EditText) findViewById(R.id.text_search);
        codeBaseAdapter = new CodeBaseAdapter(SecretContentShow.this, secLevel);

        cleanSearchFlag();

        lv_code.setAdapter(codeBaseAdapter);
        switch (secLevel){
            case 0:
                db_table_name = "db_security_code_low";
                break;
            case 1:
                db_table_name = "db_security_code_high";
                break;
        }

        // 设置点击监听器
        lv_code.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(SecretContentShow.this, CodeDetailReg.class);
                intent.putExtra("detail_type", "edit");
                intent.putExtra("db_row_num", l);
                intent.putExtra("sec_level", secLevel);
                startActivityForResult(intent, 0);
            }
        });
        // 设置长按监听器
        lv_code.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int db_row_num = (int) l;
                AlertDialog.Builder builder = new AlertDialog.Builder(SecretContentShow.this);
                builder.setTitle("确认");
                builder.setMessage("确认删除该条记录吗?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFromDb(db_row_num);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SecretContentShow.checkDeleteFlag = false;
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return false;
            }
        });

        // 设置搜索按钮监听器
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideOrShowInput();
                searchInDB();
//                Toast.makeText(SecretContentShow.this, "start searching...", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置输入法回车按键监听器
        text_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
//                    Toast.makeText(SecretContentShow.this, "start searching...", Toast.LENGTH_SHORT).show();
                    hideOrShowInput();
                    searchInDB();
                }
                return false;
            }
        });
    }

    /**
     * 模糊搜索
     */
    private void searchInDB(){
        SQLiteDB sqliteDb = new SQLiteDB(SecretContentShow.this, "db_code");
        SQLiteDatabase dbWriter = sqliteDb.getWritableDatabase();
        SQLiteDatabase dbReader = sqliteDb.getReadableDatabase();
        Cursor cursor;
        String searchText = text_search.getText().toString();

        // 清除search标志位
        cleanSearchFlag();

        if (searchText.equals("") || searchText == null){
            codeBaseAdapter.notifyDataSetChanged();
            return;
        }

        cursor = dbReader.query(db_table_name, new String[]{"_id"}, "name like ? OR account like ?",
                new String[]{"%"+searchText+"%","%"+searchText+"%"}, null, null, null);

        String searchedId = "";
        ContentValues conValue = new ContentValues();
        conValue.put("search", 1);
        while(cursor.moveToNext()){
            searchedId = cursor.getString(cursor.getColumnIndex("_id"));
            dbWriter.update(db_table_name, conValue, "_id=?", new String[]{searchedId});
        }

        cursor.close();
        dbReader.close();
        dbWriter.close();
        sqliteDb.close();

        // 置位显示搜索结果标志位
        CodeBaseAdapter.searchEnable = true;
        codeBaseAdapter.notifyDataSetChanged();
    }

    /**
     * 清除数据库中search字段
     */
    private void cleanSearchFlag(){
        SQLiteDB sqliteDb = new SQLiteDB(SecretContentShow.this, "db_code");
        SQLiteDatabase dbWriter = sqliteDb.getWritableDatabase();
        SQLiteDatabase dbReader = sqliteDb.getReadableDatabase();
        Cursor cursor;

        cursor = dbReader.query(db_table_name, new String[]{"_id"}, "search=?",
                new String[]{"1"}, null, null, null);

        String searchedId;
        ContentValues conValue = new ContentValues();
        conValue.put("search", 0);
        while(cursor.moveToNext()){
            searchedId = cursor.getString(cursor.getColumnIndex("_id"));
            dbWriter.update(db_table_name, conValue, "_id=?", new String[]{searchedId});
        }

        cursor.close();
        dbReader.close();
        dbWriter.close();
        sqliteDb.close();

        CodeBaseAdapter.searchEnable = false;
    }

    /**
     * 显示或隐藏软键盘
     */
    private void hideOrShowInput(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void deleteFromDb(int l){
        SQLiteDB sqliteDb = new SQLiteDB(SecretContentShow.this, "db_code");
        SQLiteDatabase dbWriter = sqliteDb.getWritableDatabase();
        SQLiteDatabase dbReader = sqliteDb.getReadableDatabase();
        Cursor cursor=null;
        if ( l>=0 ){
            switch (secLevel){
                case 0:
                    cursor = dbReader.query(db_table_name, new String[]{"_id"}, null, null, null, null, null);
                    cursor.moveToPosition(l);
                    dbWriter.delete(db_table_name, "_id=?", new String[]{cursor.getString(cursor.getColumnIndex("_id"))});
                    break;
                case 1:
                    cursor = dbReader.query(db_table_name, new String[]{"_id"}, null, null, null, null, null);
                    cursor.moveToPosition(l);
                    dbWriter.delete(db_table_name, "_id=?", new String[]{cursor.getString(cursor.getColumnIndex("_id"))});
                    break;
            }
        }
        if(cursor!=null?!cursor.isClosed():false){
            cursor.close();
        }
        sqliteDb.close();
        Toast.makeText(SecretContentShow.this, "数据删除成功", Toast.LENGTH_SHORT).show();
        codeBaseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_secret_content_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                break;
            case R.id.code_open_file:
                intent = new Intent();
                intent.setClass(this, FileBrowserActivity.class);
                startActivityForResult(intent, 0);					// 打开文件浏览器选择文件
                break;
            case R.id.code_add_data:    // 新增记录
                intent = new Intent();
                intent.setClass(this, CodeDetailReg.class);
                intent.putExtra("detail_type", "add");
                intent.putExtra("sec_level", secLevel);
                startActivityForResult(intent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String strFile, strFileName;
        if (requestCode==0){
            if ( resultCode==100 && data != null){
                strFile = data.getStringExtra("SelFilePath");
                strFileName = data.getStringExtra("SelFileName");
                if (strFile != null && strFileName != null){
                    UpdateDBFromFile(strFile, strFileName);
                }
            }else if(resultCode==101){
                codeBaseAdapter.notifyDataSetChanged(); // 修改完成，刷新列表
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UpdateDBFromFile(String strFile, String strFileName){
        try {
            FileReader fileReader = new FileReader(new File(strFile));
            BufferedReader bReader = new BufferedReader(fileReader);
            SQLiteDB sqLiteDB = new SQLiteDB(SecretContentShow.this, "db_code");
            SQLiteDatabase dbWrite = sqLiteDB.getWritableDatabase();
            ContentValues colData = null;
            String fileContent = "";
            try {
                while( (fileContent=bReader.readLine()) != null ){
                    String fileWords[] = fileContent.split("\\s+");
                    colData = new ContentValues();

                    if ( fileWords.length < 2 ){
                        continue;
                    }
                    if (fileWords.length == 2) {
                        colData.put("name", fileWords[0]);
                        colData.put("account", fileWords[0]);
                        colData.put("password", fileWords[1]);
                    }
                    if (fileWords.length > 2) {
                        colData.put("name", fileWords[0]);
                        colData.put("account", fileWords[1]);
                        colData.put("password", fileWords[2]);
                    }
                    if (fileWords.length > 3){
                        colData.put("email", fileWords[3]);
                    }
                    if (fileWords.length > 4){
                        colData.put("extra", fileWords[4]);
                    }

                    if ( colData.size() < 1 ){
                        continue;
                    }
                    switch (secLevel){
                        case 0:
                            dbWrite.insert(db_table_name, null, colData);
                            break;
                        case 1:
                            dbWrite.insert(db_table_name, null, colData);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            dbWrite.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        codeBaseAdapter.notifyDataSetChanged();

    }
}
