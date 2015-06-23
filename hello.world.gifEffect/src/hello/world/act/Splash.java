package hello.world.act;

import hello.world.builder.gifeffect.R;
import hello.world.domain.Mapp;
import hello.world.drawpic.DrawPic;
import hello.world.picgif.PicGif;
import hello.world.utils.StreamTools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Splash extends BaseSetupActivity {

	protected static final int URL_ERROR = 0;
	protected static final int NETWORK_ERROR = 1;
	protected static final int JSON_ERROR = 2;
	protected static final int ENTER_HOME = 3;
	protected static final int SHOW_UPDATE_DIALOG = 4;
	protected static final String TAG = null;
	private int father;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// ��ʾ�����ĶԻ���
				// showUpdateDialog();
				break;
			case ENTER_HOME:// ������ҳ��
				// enterHome();
				break;

			case URL_ERROR:// URL����
				// enterHome();
				Toast.makeText(getApplicationContext(), "URL����", 0).show();

				break;

			case NETWORK_ERROR:// �����쳣
				// enterHome();
				Toast.makeText(app, "�����쳣", 0).show();
				break;

			case JSON_ERROR:// JSON��������
				// enterHome();
				Toast.makeText(app, "JSON��������", 0).show();
				break;

			default:
				break;
			}
		}

	};
	private Mapp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		father = getIntent().getIntExtra("father", 0);

		app = (Mapp) getApplication();
		checkUpdate();

	}

	public void draw(View v) {
		startActivity(new Intent(this, hello.world.drawpic.DrawPic.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void gif(View v) {
		startActivity(new Intent(this, hello.world.picgif.PicGif.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showNext() {
		if (father == 0) {
			startActivity(new Intent(this, DrawPic.class));
		} else {
			startActivity(new Intent(this, PicGif.class));
		}
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {

	}

	/**
	 * ����Ƿ����°汾������о�����
	 */
	private String description;
	private String apkurl;

	private void checkUpdate() {

		new Thread() {

			public void run() {
				// URLhttp://192.168.1.254:8080/updateinfo.html

				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {

					URL url = new URL(getString(R.string.serverurl));
					// ����
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// �����ɹ�
						InputStream is = conn.getInputStream();
						// ����ת��String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "�����ɹ���" + result);
						// json����
						JSONObject obj = new JSONObject(result);
						// �õ��������İ汾��Ϣ
						String version = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// У���Ƿ����°汾
						if (getVersionName().equals(version)) {
							// �汾һ�£�û���°汾��������ҳ��
							mes.what = ENTER_HOME;
						} else {
							// ���°汾������һ�����Ի���
							mes.what = SHOW_UPDATE_DIALOG;

						}

					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					mes.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mes.what = JSON_ERROR;
				} finally {

					long endTime = System.currentTimeMillis();
					// ���ǻ��˶���ʱ��
					long dTime = endTime - startTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					handler.sendMessage(mes);
				}

			};
		}.start();

	}

	/**
	 * �õ�Ӧ�ó���İ汾����
	 */

	private String getVersionName() {
		// ���������ֻ���APK
		PackageManager pm = getPackageManager();

		try {
			// �õ�֪��APK�Ĺ����嵥�ļ�
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

}
