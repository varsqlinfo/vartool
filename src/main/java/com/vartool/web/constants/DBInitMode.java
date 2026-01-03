package com.vartool.web.constants;

public enum DBInitMode {
	DDL,DATA,NONE;
	
	public static DBInitMode getInitMode(String mode) {
		for (DBInitMode initMode: DBInitMode.values()) {
			if(initMode.name().equalsIgnoreCase(mode)) {
				return initMode;
			}
		}
		
		return DBInitMode.NONE;
	}
}
