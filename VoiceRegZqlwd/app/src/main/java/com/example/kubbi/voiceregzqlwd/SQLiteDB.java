package com.example.kubbi.voiceregzqlwd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Kubbi on 2015/11/2.
 */
public class SQLiteDB extends SQLiteOpenHelper {
    Context context = null;

    public SQLiteDB(Context context, String name) {
        super(context, name, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE db_bill_record (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT DEFAULT \"\"," +
                "detail TEXT DEFAULT \"\"," +
                "money FLOAT DEFAULT \"\","+
                "belongto TEXT DEFAULT \"\","+
                "data TEXT DEFAULT \"\","+
                "createdtime BIGINT DEFAULT \"\"," +
                "updatedtime BIGINT DEFAULT \"\"," +
                "ext TEXT DEFAULT \"\" )" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        // 改名
//        sqLiteDatabase.execSQL("alter table db_security_code_low rename to db_security_code_low_tmp");
//        sqLiteDatabase.execSQL("alter table db_security_code_high rename to db_security_code_high_tmp");
//
//        // 新创建表
//        sqLiteDatabase.execSQL("CREATE TABLE db_security_code_low (" +
//                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "name TEXT DEFAULT \"\"," +
//                "account TEXT DEFAULT \"\"," +
//                "password TEXT DEFAULT \"\"," +
//                "email TEXT DEFAULT \"\","+
//                "extra TEXT DEFAULT \"\"," +
//                "search INTEGER DEFAULT 0 )" );
//
//        sqLiteDatabase.execSQL("CREATE TABLE db_security_code_high (" +
//                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "name TEXT DEFAULT \"\"," +
//                "account TEXT DEFAULT \"\"," +
//                "password TEXT DEFAULT \"\"," +
//                "email TEXT DEFAULT \"\","+
//                "extra TEXT DEFAULT \"\"," +
//                "search INTEGER DEFAULT 0 )" );
//
//        // 拷贝数据
//        sqLiteDatabase.execSQL("insert into db_security_code_low (name, account, password, email, extra) " +
//                "select name, account, password, email, extra from db_security_code_low_tmp");
//        sqLiteDatabase.execSQL("insert into db_security_code_high (name, account, password, email, extra) " +
//                "select name, account, password, email, extra from db_security_code_high_tmp");
//
//        // 丢弃临时表
//        sqLiteDatabase.execSQL("drop table db_security_code_low_tmp");
//        sqLiteDatabase.execSQL("drop table db_security_code_high_tmp");
//
//        Toast.makeText(context, "The DataBase upgrade success", Toast.LENGTH_SHORT).show();
    }
}
