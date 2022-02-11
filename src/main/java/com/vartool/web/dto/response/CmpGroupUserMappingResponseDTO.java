package com.vartool.web.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmpGroupUserMappingResponseDTO {

	private String groupId;
	private String viewid;
	private String uname;
	private String uid;
	private String groupName;
	private String layoutInfo;
	
	public CmpGroupUserMappingResponseDTO(String groupId, String viewid, String uname, String groupName) {
		this(groupId, viewid, uname, groupName, null);
	}
	
	public CmpGroupUserMappingResponseDTO(String groupId, String viewid, String uname, String groupName, String uid) {
		this(groupId, viewid, uname, groupName, uid, null);
	}
	
	public CmpGroupUserMappingResponseDTO(String groupId, String viewid, String uname, String groupName, String uid, String layoutInfo) {
		this.groupId = groupId;
		this.viewid = viewid;
		this.uname = uname; 
		this.groupName = groupName; 
		this.uid = uid; 
		this.layoutInfo = layoutInfo;
	}
}