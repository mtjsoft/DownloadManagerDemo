package com.example.downloadmanagerdemo;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String downloadUrl = "http://issuecdn.baidupcs.com/issue/netdisk/apk/BaiduNetdisk_7.16.2.apk";
	private TextView startTextView;
	private TextView cancelTextView;
	private DownloadManager downloadManager;
	private DownloadManager.Request request;
	private long id = -1;
	private ProgressBar progressBar;
	private boolean isQueryDown = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initValues();
		initListeners();
	}

	public void initView() {
		// TODO Auto-generated method stub
		startTextView = (TextView) findViewById(R.id.tv_start);
		cancelTextView = (TextView) findViewById(R.id.tv_cancel);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
	}

	public void initValues() {
		// TODO Auto-generated method stub
		File file = new File(ConstantParam.SAVE_DOWNLOAD);
		if (!file.exists()) {
			file.mkdirs();
		}
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		request = new DownloadManager.Request(Uri.parse(downloadUrl));
		request.setDestinationInExternalPublicDir(ConstantParam.SAVE_DOWNLOAD,
				"down.apk");
		request.setTitle("测试下载");
		request.setDescription("描述描述");
		// apk类型
		request.setMimeType("application/vnd.android.package-archive");
		// 设置显示隐藏
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
	}

	public void initListeners() {
		// TODO Auto-generated method stub
		startTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isQueryDown = true;
				id = downloadManager.enqueue(request);
				startTextView.setOnClickListener(null);
				query();
			}
		});
		cancelTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (id > -1) {
					startTextView.setOnClickListener(null);
					isQueryDown = false;
					downloadManager.remove(id);
					id = -1;
				}
			}
		});
	}

	/**
	 * 查询进度
	 */
	private void query() {
		// TODO Auto-generated method stub
		final DownloadManager.Query managerQuery = new DownloadManager.Query();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isQueryDown) {
					try {
						Thread.sleep(1000);
						Cursor cursor = downloadManager.query(managerQuery
								.setFilterById(id));
						if (cursor != null && cursor.moveToFirst()) {
							if (cursor.getInt(cursor
									.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
								startTextView.setOnClickListener(null);
								isQueryDown = false;
								install();
								return;
							}
							// 标题
//							String title = cursor.getString(cursor
//									.getColumnIndex(DownloadManager.COLUMN_TITLE));
							// 已下载
							int down_ed = cursor.getInt(cursor
									.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
							// 总大小
							int toal = cursor.getInt(cursor
									.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
							int pro = (down_ed * 100) / toal;
							Message message = new Message();
							message.what = 13;
							message.obj = pro;
							handler.sendMessage(message);
						}
						cursor.close();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * 跳安装
	 */
	private void install() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType((Uri.parse("file://"
				+ ConstantParam.SAVE_DOWNLOAD + "down.apk")),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 13:
				int pro = (Integer) msg.obj;
				progressBar.setProgress(pro);
				break;
			default:
				break;
			}
		}
	};

}
