package com.vartool.web.configuration.websocket;
import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import com.vartool.web.constants.WebSocketConstants;

/**
 * web socket security
* 
* @fileName	: WebSocketSecurityConfig.java
* @author	: ytkim
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    	
    	String[] destMatchers = (String[]) Arrays.asList(WebSocketConstants.Type.values()).stream().map(item -> item.getDestMatcher()).toArray(String[]::new);
    	
        messages
                .simpDestMatchers(destMatchers).authenticated()
                .anyMessage().authenticated();
    }
}