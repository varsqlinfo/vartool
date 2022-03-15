package com.vartool.web.module;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Stack;

import com.vartech.common.utils.FileUtils;
import com.vartool.web.app.config.VartoolConfiguration;
import com.vartool.web.dto.response.CmpDeployResponseDTO;

public class LogFilenameUtils {
	public final static char START_CHAR = '%';
	public final static String FILENAME_INDEX_STR = ";idx;";
	
	public interface NamePattern {
		String pattern(String val);
	}

	static enum PATTERN implements NamePattern {
		DATE("d{", "}") {
			@Override
			public String pattern(String val) {
				SimpleDateFormat sdf = new SimpleDateFormat(val);
				return sdf.format(new Date());
			}
		},
		INDEX("i", "", FILENAME_INDEX_STR);
		
		String prefix;
		char[] prefixChars;
		int prefixLen;
		String suffix;
		char[] suffixChars;
		int suffixLen;
		String replaceStr;
		

		PATTERN(String prefix, String suffix) {
			this(prefix, suffix, "");
		}

		PATTERN(String prefix, String suffix, String replaceStr) {
			this.prefix = prefix; 
			this.prefixChars = prefix.toCharArray();
			this.prefixLen = this.prefixChars.length;
			
			this.suffix = suffix;
			this.suffixChars = suffix.toCharArray();
			this.suffixLen = this.suffixChars.length;
			this.replaceStr = replaceStr;
		}

		@Override
		public String pattern(String val) {
			return val;
		}

		public boolean patternStart(char... val) {
			if (prefixLen == val.length) {
				return Arrays.equals(val, prefixChars);
			} else if (prefixLen < val.length) {
				return Arrays.equals(Arrays.copyOf(val, prefixLen), prefixChars);
			}
			return false;
		}

		public boolean patternEnd(char... val) {
			if (suffixLen == val.length) {
				return Arrays.equals(val, suffixChars);
			} else if (suffixLen < val.length) {
				return Arrays.equals(Arrays.copyOf(val, suffixLen), suffixChars);
			}
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(LogFilenameUtils.name("C:/app/apache-tomcat-9.0.58/logs/catalina.%d{yyyy-MM-dd}.log"));
		System.out.println(LogFilenameUtils.isDatePattern("C:/app/apache-tomcat-9.0.58/logs/catalina.%d{yyyy-MM-dd}.log"));
	}

	private LogFilenameUtils() {
	}

	/**
	 * 
	 * @Method Name  : name
	 * @Method 설명 : file name 변환
	 * @작성자   : ytkim
	 * @작성일   : 2020. 3. 6. 
	 * @변경이력  :
	 * @param fileName
	 * @return
	 */
	public static String name(final String fileName) {
		final StringBuilder dest = new StringBuilder();
		final Stack<PATTERN> states = new Stack<>();

		int startIdx = -1;
		int fileNameLen = fileName.length();
		char c1, c2, c3;

		PATTERN[] patterns = PATTERN.values();
		for (int index = 0; index < fileNameLen; index++) {

			if (states.empty()) {
				int startChIdx = fileName.indexOf(START_CHAR, index);
				if (startChIdx > -1) {
					dest.append(fileName.substring(index, startChIdx));

					index = startChIdx;
					c1 = fileName.charAt(index);
					c2 = (index + 1) < fileNameLen ? fileName.charAt(index + 1) : '0';
					c3 = (index + 2) < fileNameLen ? fileName.charAt(index + 2) : '0';

					// start char
					PATTERN startPattern = null;
					// pattern check
					for (PATTERN pattern : patterns) {
						if (pattern.patternStart(c2, c3)) {
							startPattern = pattern;
							break;
						}
					}

					if (startPattern != null) {
						index = index + startPattern.prefixLen;

						if (startPattern.suffixLen > 0) {
							states.push(startPattern);
							startIdx = index + 1;
						} else {
							dest.append(startPattern.replaceStr);
						}
					} else {
						dest.append(c1);
					}
				} else {
					dest.append(fileName.substring(index));
					break;
				}
			} else {
				PATTERN statePattern = states.peek();

				int suffixIdx = fileName.indexOf(statePattern.suffix, startIdx);

				if (suffixIdx > -1) {
					index = suffixIdx;
					dest.append(statePattern.pattern(fileName.substring(startIdx, suffixIdx)));
					states.pop();
				} else {
					dest.append(fileName.substring(startIdx - statePattern.prefixLen, startIdx));
					states.pop();
				}
			}
		}
		return dest.toString();
	}
	
	
	public static boolean isIncludePattern(String fileName) {
		return isDatePattern(fileName) || isIncludeIdxStr(fileName);
	}
	

	/**
	 * 
	 * @Method Name  : isIncludeIdxStr
	 * @Method 설명 : index str 포함 여부. 
	 * @작성자   : ytkim
	 * @작성일   : 2020. 3. 6. 
	 * @변경이력  :
	 * @param fileName
	 * @return
	 */
	public static boolean isIncludeIdxStr(String fileName) {
		return fileName.indexOf(FILENAME_INDEX_STR) > -1;
	}

	public static String replaceIndexStr(String fileName, String idx) {
		return fileName.replace(FILENAME_INDEX_STR, idx);
	}

	public static boolean isDatePattern(String fileName) {
		return fileName.indexOf(START_CHAR+PATTERN.DATE.prefix) > -1;
	}
	
	public static File getDeploySourcePath(CmpDeployResponseDTO dto) {
		return new File(FileUtils.pathConcat(VartoolConfiguration.getInstance().getDeployConfig().getSourcePath(), dto.getCmpId(),"source"));
	}

	public static File getDeployBuildPath(CmpDeployResponseDTO dto) {
		return new File(FileUtils.pathConcat(VartoolConfiguration.getInstance().getDeployConfig().getSourcePath(), dto.getCmpId(),"build"));
	}

}
