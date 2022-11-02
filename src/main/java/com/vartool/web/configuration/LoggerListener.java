package com.vartool.web.configuration;

import com.vartool.web.module.VartoolUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

/**
 * logback listener
* 
* @fileName	: LoggerListener.java
* @author	: ytkim
 */
public class LoggerListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {

    private boolean started = false;

    @Override
    public void start() {
        if (started) return;

        Context context = getContext();

        if(VartoolUtils.isRuntimelocal()) {
        	context.putProperty("runtime", "local");
        	context.putProperty("LOG_DIR", "c:/zzz/logs/vtool");
        }else {
        	context.putProperty("runtime", "prod");
        	context.putProperty("LOG_DIR", System.getProperty("catalina.base")+"/logs/vtool");
        }

        started = true;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }
}