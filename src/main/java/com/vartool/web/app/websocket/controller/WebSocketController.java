package com.vartool.web.app.websocket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.handler.log.LogCmpManager;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;
import com.vartool.web.dto.websocket.LogMessageDTO;



/**
 * web socket
* 
* @fileName	: WebSocketController.java
* @author	: ytkim
 */
@Controller
public class WebSocketController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

	private WebSocketServiceImpl webSocketServiceImpl;

	
	
}
