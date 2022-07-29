package com.vartool.web.dto.websocket;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
*-----------------------------------------------------------------------------
* 
* @NAME		: MessageDTO.java
* @DESC		: web socket message
* @AUTHOR	: ytkim
*-----------------------------------------------------------------------------
  DATE			AUTHOR			DESCRIPTION
*-----------------------------------------------------------------------------
*2020. 10. 19. 			ytkim			최초작성

*-----------------------------------------------------------------------------
 */
@Getter
@Setter
@ToString
public class LogMessageDTO{

	private String cmpId;
	private String log;
	private int state;
	
	private Map<String,Object> item;

	@Builder
	public LogMessageDTO(String log, String cmpId, int state) {
		this.log = log;
		this.cmpId = cmpId;
		this.state = state;
	}

	public LogMessageDTO addItem(String key, Object object) {
		if(item==null) {
			item = new HashMap<String,Object>();
		}
		item.put(key, object);

		return this;
	}
}