package tw.lursun.sinwunca;


import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class BootService extends Service {
	private static Context context=null;
	public static boolean lock=false;
	private static WindowManager windowManager;
	private static RelativeLayout moneyView ,onoffView ;
	private static WindowManager.LayoutParams onoffParms;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	public static Handler hsetxy=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			HashMap<String,Float> value=(HashMap<String,Float>)(msg.obj);
			onoffParms.x= (value.get("X").intValue());
			onoffParms.y= (value.get("Y").intValue());
			windowManager.updateViewLayout(onoffView,onoffParms);
		}
	};
	public static Handler alert=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			AlertDialog.Builder builder=new AlertDialog.Builder(context).setMessage((String)msg.obj).setPositiveButton("確認",null);
			final AlertDialog dialog = builder.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}
	};

	private void handleStart() {
		if(context==null) {
			Send.init(getApplicationContext());

			context = getApplicationContext();
			windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
			final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

			final View.OnClickListener vocl = new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					TextView tv = (TextView) ((ViewGroup) v.getParent().getParent()).findViewById(R.id.text);
					String temp = tv.getText().toString();
					if (temp.indexOf("交") > -1)
						temp = "$0";
					temp += ((Button) v).getText().toString();
					temp = temp.replaceAll("\\$0", "\\$");
					tv.setText(temp);
				}
			};

			onoffView = (RelativeLayout) inflater.inflate(R.layout.onoff, null);
			onoffParms = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);
			onoffParms.gravity = Gravity.TOP | Gravity.LEFT;
			SQLite sql = new SQLite(this);
			SQLiteDatabase db = sql.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM XY", null);
			c.moveToFirst();
			onoffParms.x = c.getInt(1);
			onoffParms.y = c.getInt(2);
			try {
				windowManager.addView(onoffView, onoffParms);
			} catch (Exception e) {
				e = e;
			}
			onoffView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					moneyView = (RelativeLayout) inflater.inflate(R.layout.activity_custom_dialog, null);

					moneyView.findViewById(R.id.btn_0).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_1).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_2).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_3).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_4).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_5).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_6).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_7).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_8).setOnClickListener(vocl);
					moneyView.findViewById(R.id.btn_9).setOnClickListener(vocl);


					AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.dialog).setView(moneyView).setPositiveButton("送出", null).setNeutralButton("取消", null);
					final AlertDialog dialog = builder.create();
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					dialog.show();
					Button submit = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
					submit.setText("送出");
					submit.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							TextView tv =(TextView)moneyView.findViewById(R.id.text);
							String money=tv.getText().toString();
							money=money.replace("$","");
							new Send().send(money);
						}
					});
					submit.setTextSize(30);
					Button cancle = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
					cancle.setText("取消");
					cancle.setTextSize(30);

					moneyView.findViewById(R.id.X).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

				}
			});
			onoffView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					lock = true;
					return false;
				}
			});
			onoffView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					try {
						if (lock == true) {
							if (event.getAction() == MotionEvent.ACTION_MOVE) {
								Message msg = new Message();
								HashMap<String, Float> value = new HashMap<String, Float>();
								value.put("X", event.getRawX() - 45);
								value.put("Y", event.getRawY() - 45);
								msg.obj = value;
								hsetxy.sendMessage(msg);
							}
							if (event.getAction() == MotionEvent.ACTION_UP) {
								lock = false;
								SQLite sql = new SQLite(getApplicationContext());
								SQLiteDatabase db = sql.getReadableDatabase();
								ContentValues values = new ContentValues();
								values.put("X", event.getRawX() - 45);
								values.put("Y", event.getRawY() - 45);
								db.update("XY", values, null, null);
							}
						}
					} catch (Exception e) {
						e = e;
					}
					return false;
				}
			});
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

			handleStart();
			return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
