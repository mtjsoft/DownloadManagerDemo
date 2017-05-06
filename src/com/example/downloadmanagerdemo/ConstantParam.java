package com.example.downloadmanagerdemo;

import android.annotation.SuppressLint;
import android.os.Environment;

public class ConstantParam {

	public static final String PACKAGE_NAME = "com.example.downloadmanagerdemo";// 包名

	/**
	 * 缓存的文件的名字
	 */
	private static final String CACHR_DIR_NAME = "DownloadDemo";

	public static final String SAVE_DOWNLOAD = getBaseCacheDir() + "下载测试/";

	/**
	 * 获取基本的缓存的路径
	 * 
	 * @return
	 */
	@SuppressLint("SdCardPath")
	private static String getBaseCacheDir() {
		// TODO Auto-generated method stub
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + CACHR_DIR_NAME + "/";
	}
}
