package whu.zq.encrypt;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Kubbi on 2015/11/1.
 */
public class CodeBaseAdapter extends BaseAdapter{

    public static boolean searchEnable = false;

    private Context context = null;
    private int secLevel;

    public CodeBaseAdapter(Context context, int secLevel){
        this.context = context;
        this.secLevel = secLevel;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout linearLayout = null;
        if( view != null ){
            linearLayout = (LinearLayout)view;
        }else{
            linearLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.list_code_data_cell, null);
        }

        TextView tv_name = (TextView) linearLayout.findViewById(R.id.code_name);
        TextView tv_account = (TextView) linearLayout.findViewById(R.id.code_account);
        TextView tv_pwd = (TextView) linearLayout.findViewById(R.id.code_password);

        Cursor cursor = getItem(i);
        tv_name.setText(cursor.getString(cursor.getColumnIndex("name")));
        tv_account.setText(cursor.getString(cursor.getColumnIndex("account")));
        tv_pwd.setText(cursor.getString(cursor.getColumnIndex("password")));

        if(!cursor.isClosed()){
            cursor.close();
        }

        Random random = new Random();
        int rgb = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        linearLayout.setBackgroundColor( rgb );
//        tv_name.setBackgroundColor(rgb);
//        tv_account.setBackgroundColor(rgb);
//        tv_pwd.setBackgroundColor(rgb);

        return linearLayout;
    }

    @Override
    public int getCount() {
        int count;
        SQLiteDB sqLiteDB = new SQLiteDB(context, "db_code");
        SQLiteDatabase dbReader = sqLiteDB.getReadableDatabase();
        Cursor cursor = null;
        String selection = null;
        String selectionArgs[] = null;
        if (searchEnable) {   // 显示搜索结果
            selection = "search=?";
            selectionArgs = new String[]{"1"};
        }

        switch (secLevel){
            case 0:
                cursor = dbReader.query("db_security_code_low", null, selection, selectionArgs, null, null, null);
                break;
            case 1:
                cursor = dbReader.query("db_security_code_high", null, selection, selectionArgs, null, null, null);
                break;
        }
        count = cursor.getCount();
        if (!cursor.isClosed()){
            cursor.close();
        }
        dbReader.close();
        return count;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Cursor getItem(int i) {
        SQLiteDB sqLiteDB = new SQLiteDB(context, "db_code");
        SQLiteDatabase dbReader = sqLiteDB.getReadableDatabase();
        Cursor cursor = null;
        String selection = null;
        String selectionArgs[] = null;
        if (searchEnable) {   // 显示搜索结果
            selection = "search=?";
            selectionArgs = new String[]{"1"};
        }
        switch (secLevel) {
            case 0:
                cursor = dbReader.query("db_security_code_low", null, selection, selectionArgs, null, null, null);
                break;
            case 1:
                cursor = dbReader.query("db_security_code_high", null, selection, selectionArgs, null, null, null);
                break;
        }
        cursor.moveToPosition(i);
        dbReader.close();

        return cursor;
    }
}

























