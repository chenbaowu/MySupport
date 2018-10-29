package com.cbw.utils;


/**
 * Created by cbw on 2018/9/25.
 */

public class PercentUtil
{
	/**
	 * 宽720为标准，获取百分比位置
	 * @return
	 */
	public static int WidthPxToPercent(int size)
	{
		return (int)(size* ShareData.m_screenRealWidth/720f+0.5f);
	}

	/**
	 *高1280为标准,获取百分比位置
	 */
	public static int HeightPxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenRealHeight/1280f+0.5f);
	}

	/**
	 * 宽1080为标准，获取百分比位置
	 * @return
	 */
	public static int WidthPxxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenRealWidth/1080f+0.5f);
	}

	/**
	 *高1920为标准,获取百分比位置
	 */
	public static int HeightPxxToPercent(int size)
	{
		return (int)(size*ShareData.m_screenRealHeight/1920f+0.5f);
	}

	/** 1080 设计稿
	 * 以9比16 为标准 ，获取百分比位置
	 */
	public static int HeightPxxToPercent2(int size)
	{
		return (int)(size*(ShareData.m_screenRealWidth * 16.0f/ 9.0f)/1920f+0.5f);
	}

	/** 720 设计稿
	 * 以9比16 为标准 ，获取百分比位置
	 */
	public static int HeightPxToPercent2(int size)
	{
		return (int)(size*(ShareData.m_screenRealWidth * 16.0f/ 9.0f)/1280f+0.5f);
	}
}
