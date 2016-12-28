package tw.lursun.sinwunca;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by KUO on 2016/8/26.
 */
public class Send {
    static String money_="";
    static Socket socket;
    static Thread read;
    static SQLiteDatabase db;
    static boolean flag=true;
    public static void init(){
        new Thread(){
            @Override
            public void run() {
                try{
                    Cursor c = db.rawQuery("Select * From setting", null);
                    c.moveToFirst();
                    socket = new Socket(c.getString(c.getColumnIndex("IP")), 8081);
                }catch (Exception e){
                    e=e;
                }
            }
        }.start();


    }
    public static void init(final Context  context){
        Thread t=new Thread() {
            @Override
            public void run() {
                SQLite sql = new SQLite(context);
                db = sql.getReadableDatabase();
                Cursor c = db.rawQuery("Select * From setting", null);
                c.moveToFirst();
                try {
                    socket = new Socket(c.getString(c.getColumnIndex("IP")), 8081);
                    //socket = new Socket("192.168.123.100", 8081);
                } catch (Exception e) {
                    e = e;
                }
            }


        };
        t.start();
        try{
            t.join();
        }
        catch (Exception e){
            e=e;
        }
    }
    public static byte[] link(byte[]... B) {
        int length = 0;
        for (byte[] b : B) {
            length += b.length;
        }
        ByteBuffer bb = ByteBuffer.allocate(length);
        for (byte[] b : B) {
            bb.put(b);
        }
        return bb.array();
    }

    public static synchronized void readTCP() {
        read=new Thread("read") {
            @Override
            public synchronized void run() {

                    try {
                        byte[] word = {};
                        InputStream is = socket.getInputStream();

                        while (true) {
                            byte[] word2 = {(byte) is.read()};
                            if(word2[0]==0x06){
                                sleep(500);
                                continue;
                            }
                            if (word2[0] != -1) {
                                word = link(word, word2);
                                if (word.length >= 147) break;
                            } else
                                break;
                        }
                        byte[] b={0x06,0x06};
                        socket.getOutputStream().write(b);

                        for (int i = 0; i < word.length; i++) {
                            byte j = (byte) (word[i] % 64);
                            word[i] = (byte) (j < 0 ? j + 64 : j);
                        }
    ////判斷RESPONSE回傳碼
                        String s = "", result = null;
                        try {
                            s = new String(word, "ASCII");
                        } catch (Exception e) {
                            e = e;
                        }
                        Pattern p = Pattern.compile("000\\d");
                        Matcher m = p.matcher(s);
                        if (m.find()) {
                            switch (m.group()) {
                                case "0000":
                                    result = "交易成功，金額" + money_;
                                    break;
                                case "0001":
                                    result = "交易失敗，交易拒絕";
                                    break;
                                case "0002":
                                    result = "交易失敗，請查詢銀行";
                                    break;
                                case "0003":
                                    result = "交易失敗，連線逾時";
                                    break;
                                default:
                                    result = "ERROR";
                                    break;
                            }
                        }
                        flag=true;


                        Message msg = new Message();
                        msg.obj = result;
                        BootService.alert.sendMessage(msg);
                        socket.close();
                        init();
                    } catch (Exception e) {
                        e = e;
                    }
                }

        };
        read.start();
    };




    public void send(final String _money) {
        Thread t=new Thread(){
            @Override
            public void run() {
                try {
                    String money =money_= _money;

                    byte[] byteArray = {
                            (byte) 2,
                            (byte) 48, (byte) 49, (byte) 48, (byte) 49, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) '0', (byte) '0', (byte) '0', (byte) '0', (byte) '0',
                            (byte) '0', (byte) '0', (byte) '0', (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 32, (byte) 32, (byte) 32, (byte) 32,
                            (byte) 3,
                            (byte) 2};
                    byte[] typevalue = money.getBytes();

                    Cursor c = db.rawQuery("Select * From setting", null);
                    c.moveToFirst();
                    String SELLERID=c.getString(c.getColumnIndex("SELLERID"));
                    byte[] SELLERIDBytes=SELLERID.getBytes();

                    int i = 0;
                    while(i<SELLERIDBytes.length){
                        byteArray[106+i]=SELLERIDBytes[i];
                        i++;
                    }
                    i=0;
                    while (i < typevalue.length) {
                        byteArray[i + 44 - typevalue.length] = typevalue[i];
                        i++;
                    }
                    byte checkbyte = (byte) 48;
                    i = 2;
                    while (i < 146) {
                        checkbyte = (byte) (checkbyte ^ byteArray[i]);
                        i++;
                    }
                    byteArray[146] = checkbyte;

                    socket.getOutputStream().write(byteArray);
                    Send.readTCP();
                    flag=false;


                }catch (Exception e){
                    e=e;
                }
            }
        };
        t.start();



    }
}
