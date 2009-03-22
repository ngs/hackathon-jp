package android.scouter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 最初に起動されるアクティビティ
 * 
 * @since 2008/03/20
 */
public class MainActivity extends Activity {

	// デバッグ用情報
	private static final String TAG_NAME = "MainActivity";
	private static final boolean DEBUG_FLG = true;

	private AlarmManager alermManager = null;
	
	// 画面の構成要素
	private TextView tvIPAddress;
	private TextView tvPortNumber;
	private TextView tvDeviceID;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView
		setContentView(R.layout.display_view);

		// 画面の要素の取得
		tvIPAddress = (TextView) findViewById(R.id.layout_textview01);
		tvPortNumber = (TextView) findViewById(R.id.layout_textview02);
		tvDeviceID = (TextView) findViewById(R.id.layout_textview03);

		// AlarmManagerの取得
		alermManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// AlarmManagerが実行するIntnet
		PendingIntent pendingIntent = PendingIntent.getService( MainActivity.this, 0,
				new Intent( MainActivity.this, IntervalPingService.class), 0 );

		// 開始時間の取得
		long firstTime = SystemClock.elapsedRealtime();

		// アラーム設定
		alermManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				firstTime, 30 * 1000, pendingIntent);
        
		// Toast 表示 - 起動確認用
		Toast.makeText(MainActivity.this, R.string.repeating_scheduled,
				Toast.LENGTH_LONG).show();

		// サービスの開始
		Intent intent = new Intent(this, ScouterInterfaceService.class);
		this.startService(intent);
		
	}

	/**
	 * 画面情報を更新
	 * 
	 * @param ipAddr IPアドレス
	 * @param port ポート番号
	 * @param deviceId デバイスID
	 */
	public void upDateDisplay(String ipAddr, String port, String deviceId) {
		tvIPAddress.setText(ipAddr);
		tvPortNumber.setText(port);
		tvDeviceID.setText(deviceId);

	}

}
