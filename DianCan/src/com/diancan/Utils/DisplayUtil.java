package com.diancan.Utils;

import android.R.integer;

public class DisplayUtil {
	public static  float SCALE;
	public static  int DPWIDTH;
	public static  int DPHEIGHT;
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		return (int) (dpValue * SCALE + 0.5f);
	}
	 
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		return (int) (pxValue / SCALE + 0.5f);
	}
}
