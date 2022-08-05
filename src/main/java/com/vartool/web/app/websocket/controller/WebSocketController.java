package com.vartool.web.app.websocket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vartool.web.app.common.controller.AbstractController;
import com.vartool.web.app.websocket.service.WebSocketServiceImpl;



/**
 * web socket
* 
* @fileName	: WebSocketController.java
* @author	: ytkim
 */
@Controller
@RequestMapping("/websocket")
public class WebSocketController extends AbstractController {

	private final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

	private WebSocketServiceImpl webSocketServiceImpl;

	
}
