package com.vartool.web.app.handler.log.reader;

import com.vartool.web.app.handler.log.stream.LogComponentOutputStream;

public interface LogReader extends Runnable {
	public void stop();
    
    public LogComponentOutputStream getOutputStream();
    
    public boolean isStop();
}
