package com.vartool.web.configuration.websocket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.vartech.common.utils.StringUtils;
import com.vartool.web.constants.WebSocketConstants;

/**
 * web socket broker
* 
* @fileName	: WebSocketBrokerConfig.java
* @author	: ytkim
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
	
	private final Logger logger = LoggerFactory.getLogger(WebSocketBrokerConfig.class);
	
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	String [] destinationPrefixes= Arrays.asList(WebSocketConstants.Type.values()).stream().map(item -> item.getPrefix()).toArray(String[]::new); 
    	
    	if(logger.isDebugEnabled()) {
	    	logger.debug("WebSocketBrokerConfig configureMessageBroker start");
	    	logger.debug("enableSimpleBroker : {}", StringUtils.join(destinationPrefixes));
	    	logger.debug("applicationDestinationPrefixes : {}", WebSocketConstants.APP_DESTINATION_PREFIX);
	    	logger.debug("userDestinationPrefix : {}", WebSocketConstants.CLIENT_DESTINATION_PREFIX);
	    	logger.debug("WebSocketBrokerConfig configureMessageBroker end");
    	}
    	
        config.enableSimpleBroker(destinationPrefixes);
        config.setApplicationDestinationPrefixes(WebSocketConstants.APP_DESTINATION_PREFIX);
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
    	for(WebSocketConstants.Type type : WebSocketConstants.Type.values()) {
    		if(type.getEndPoint() != null) {
    			logger.debug("registerStompEndpoints : {}", type.getEndPoint());
    			registry.addEndpoint(type.getEndPoint()).withSockJS();
    		}
    	}
    }

}