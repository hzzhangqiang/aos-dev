package whu.zq.encrypt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CodeDetailReg extends Activity {

    private String type;
    private int sec_level;
    private int list_id = -1;
    private int db_id = -1;
    private EditText edit_name;
    private EditText edit_account;
    private EditText edit_password;
    private EditText edit_email;
    private EditText edit_ext;
    private Button btn_commit;
    private String db_table_name="db_security_code_high";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_detail_reg);

        Intent intent = getIntent();
        type = intent.getStringExtra("detail_type");
        sec_level = intent.getIntExtra("sec_level", -1);

        switch (sec_level){
            case 0:
                db_table_name = "db_security_code_low";
                break;
            case 1:
                db_table_name = "db_security_code_high";
                break;
            default:
                Toast.makeText(this, "安全级别未知", Toast.LENGTH_SHORT).show();
                break;
        }

        edit_name = (EditText) findViewById(R.id.edit_code_detail_name);
        edit_account = (EditText) findViewById(R.id.edit_code_detail_account);
        edit_password = (EditText) findViewById(R.id.edit_code_detail_password);
        edit_email = (EditText) findViewById(R.id.edit_code_detail_email);
        edit_ext = (EditText) findViewById(R.id.edit_code_detail_extra);
        btn_commit = (Button) findViewById(R.id.btn_detail_commit);

        // 监听器
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断待写入数据库的内容是否有效
                if (checkCommitValues()){
                    Toast.makeText(CodeDetailReg.this, "数据内容不能全为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDB sqLiteDB = new SQLiteDB(CodeDetailReg.this, "db_code");
                SQLiteDatabase dbWrite = sqLiteDB.getWritableDatabase();

                ContentValues colValues = new ContentValues();
                colValues.put("name", edit_name.getText().toString());
                colValues.put("account", edit_account.getText().toString());
                colValues.put("password", edit_password.getText().toString());
                colValues.put("email", edit_email.getText().toString());
                colValues.put("extra", edit_ext.getText().toString());
                if (type.equals("edit")) {
                    if (list_id >= 0) {
                        dbWrite.update(db_table_name, colValues, "_id=?", new String[]{db_id + ""});
                        Toast.makeText(CodeDetailReg.this, "修改完成", Toast.LENGTH_SHORT).show();
                        setResult(101);
                        finish();
                    }
                }else if( type.equals("add") ) {
                    dbWrite.insert(db_table_name, null, colValues);
                    Toast.makeText(CodeDetailReg.this, "新增完成", Toast.LENGTH_SHORT).show();
                    setResult(101);
                    finish();
                }
                dbWrite.close();
                sqLiteDB.close();
            }
        } );

        // 文本初始化
        if (type.equals("edit")) {
            list_id = (int) intent.getLongExtra("db_row_num", -1);
            SQLiteDB sqLiteDB = new SQLiteDB(this, "db_code");
            SQLiteDatabase bReader = sqLiteDB.getReadableDatabase();
            String selection = null;
            String selectionArgs[] = null;
            if (CodeBaseAdapter.searchEnable) {   // 当前是否为搜索结果
                selection = "search=?";
                selectionArgs = new String[]{"1"};
            }
            Cursor cursor = bReader.query(db_table_name, null, selection, selectionArgs, null, null, null);
            if (list_id >= 0) {
                cursor.moveToPosition(list_id);
                db_id = cursor.getInt(cursor.getColumnIndex("_id"));
                edit_name.setText(cursor.getString(cursor.getColumnIndex("name")));
                edit_account.setText(cursor.getString(cursor.getColumnIndex("account")));
                edit_password.setText(cursor.getString(cursor.getColumnIndex("password")));
                edit_email.setText(cursor.getString(cursor.getColumnIndex("email")));
                edit_ext.setText(cursor.getString(cursor.getColumnIndex("extra")));
            }
            cursor.close();
            bReader.close();
            sqLiteDB.close();
        }else if(type.equals("add")) {
            btn_commit.setText("新增");
        }else{
            Toast.makeText(this, "无法解读密码详情操作指令", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断所有EditText控件内容是否全部为空
     * @return
     */
    private  boolean checkCommitValues(){
        boolean isEmpty = true;
        List<View> edits = getAllChildViews(EditText.class);
        for (int i=0; i<edits.size(); i++){
            EditText edit = (EditText) edits.get(i);
            isEmpty &= edit.getText().toString().equals("");
        }

        return isEmpty;
    }

    /**
     * 获取所有空间类型为T的子控件
     * @param T
     */
    private List<View> getAllChildViews( Class<?> T ){
        View view = this.getWindow().getDecorView();

        List<View> views = getAllChildViews(view, T);

        return views;
    }

    /**
     * 获取所有指定类型的子控件
     * @param parent
     * @param T
     * @return
     */
    private List<View> getAllChildViews( View parent, Class<?> T ){
        List<View> childs  = new ArrayList<View>();
        if ( parent instanceof ViewGroup ){
            ViewGroup vg = (ViewGroup) parent;
            for (int i=0; i<vg.getChildCount(); i++){
                View child = vg.getChildAt(i);
                if (child.getClass().equals(T)){
                     childs.add(child);
                }
                childs.addAll(getAllChildViews(child, T));
            }
        }

        return  childs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_code_detail_reg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
