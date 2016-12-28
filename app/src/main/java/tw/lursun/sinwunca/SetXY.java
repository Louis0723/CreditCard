package tw.lursun.sinwunca;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

/**
 * Created by admin on 2016/9/5.
 */
public class SetXY extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent=new Intent(this,BootService.class);
        (this).startService(intent);
        SQLite sqlite=new SQLite(this);
        SQLiteDatabase db=sqlite.getReadableDatabase();
        Cursor c=db.rawQuery("Select * From setting",null);
        c.moveToFirst();
        ((EditText)findViewById(R.id.IP)).setText(c.getString(c.getColumnIndex("IP")));
        ((EditText)findViewById(R.id.SELLERID)).setText(c.getString(c.getColumnIndex("SELLERID")));



    }
    public void Onsave(View view)
    {
        SQLite sqlite=new SQLite(this);
        SQLiteDatabase db=sqlite.getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("IP",((EditText)findViewById(R.id.IP)).getText().toString());
        cv.put("SELLERID",Integer.parseInt(((EditText)findViewById(R.id.SELLERID)).getText().toString()));
        db.update("setting",cv,"",null);
    }
}
