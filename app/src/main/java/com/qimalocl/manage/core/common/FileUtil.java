/*************************************************************************************************
 * 版权所有 (C)2012,  深圳市康佳集团股份有限公司 
 * 
 * 文件名称：FileUtil.java
 * 内容摘要：文件工具类
 * 当前版本：
 * 作         者： hexiaoming
 * 完成日期：2012-12-26
 * 修改记录：
 * 修改日期：
 * 版   本  号：
 * 修   改  人：
 * 修改内容：
 ************************************************************************************************/
package com.qimalocl.manage.core.common;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

/**
 * 类描述：FileUtil
 *  @author hexiaoming
 *  @version  
 */
public class FileUtil {
	
	public static File updateDir = null;
	public static File updateFile = null;
	/***********保存升级APK的目录***********/
	public static final String llyjzApplication = "001兼职";
	
	public static boolean isCreateFileSucess;

	/** 
	* 方法描述：createFile方法
	* @param   String app_name
	* @return 
	* @see FileUtil
	*/
	public static void createFile(String app_name) {
		
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			isCreateFileSucess = true;
			
			updateDir = new File(Environment.getExternalStorageDirectory()+ "/" + llyjzApplication +"/");
			updateFile = new File(updateDir + "/" + app_name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}

		}else{
			isCreateFileSucess = false;
		}
	}
}