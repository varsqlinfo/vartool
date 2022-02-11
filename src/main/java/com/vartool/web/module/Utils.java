package com.vartool.web.module;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

public final class Utils {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static String UUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String UUID(String val) {
		try {
			return UUID.nameUUIDFromBytes(val.getBytes("utf-8")).toString().replaceAll("-", "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String replaceDirectoryPath(String path) {
		return path.replaceAll("\\\\", "/");
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

	/**
	 * 
	 * @Method Name : getOsCommandExtension
	 * @Method 설명 : os command extension
	 * @작성자 : ytkim
	 * @작성일 : 2020. 2. 6.
	 * @변경이력 :
	 * @return
	 */
	public static String getOsCommandExtension() {
		if (isWindows()) {
			return ".bat";
		} else if (isMac()) {
			return ".sh";
		}

		return ".sh";
	}

	public static String[] getOsCommand() {
		if (isWindows()) {
			return new String[] { "cmd", "/c", "call" };
		} else if (isMac()) {
			return null;
		}

		return null;
	}

	/**
	 * 
	 * @Method Name : getLogUid
	 * @Method 설명 : log uid
	 * @작성자 : ytkim
	 * @작성일 : 2020. 2. 7.
	 * @변경이력 :
	 * @param uid
	 * @param mode
	 * @return
	 */
	public static String getLogUid(String uid, String mode) {
		return uid + "_" + mode;
	}
	

	public static Charset getCharset(String charset) {
		if(charset != null  && !"".equals(charset)) {
    		return Charset.forName(charset);
    	}
		
		return Charset.defaultCharset();
	}
}
