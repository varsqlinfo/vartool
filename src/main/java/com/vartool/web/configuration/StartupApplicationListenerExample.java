package com.vartool.web.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.vartool.core.config.VartoolConfiguration;

@Component
public class StartupApplicationListenerExample implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StartupApplicationListenerExample.class);

    @Override 
    public void onApplicationEvent(ApplicationStartedEvent event) {
    	File file = new File(VartoolConfiguration.getConfigRootPath(), VartoolConfiguration.getInstance().getInitializedFileName());
    	
    	if(!file.exists()) {
    		try {
    			IOUtils.write((System.currentTimeMillis()+"").getBytes(), new FileOutputStream(file));
			}catch (IOException e) {
				LOG.error("initialized.dat : {} ", e.getMessage(), e);
				;
			}
    	}
    }
}