package com.vartool.web.app.handler.log.reader;
import static java.lang.Math.max;
import static java.lang.Thread.sleep;
import static java.nio.ByteBuffer.allocate;
import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.READ;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.utils.StringUtils;
import com.vartool.web.app.handler.log.stream.LogComponentOutputStream;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.module.FileServiceUtils;
import com.vartool.web.module.LogFilenameUtils;

/**
 * tail 
 * file read
* 
* @fileName	: FileReader.java
* @author	: ytkim
 */
public class FileReader implements LogReader {

    private static final int AWAIT_FILE_ROTATION_MILLIS = 1000;
    private static final int TAIL_CHECK_INTERVAL_MILLIS = 500;
    
    private static final byte LINE_FEED = 0x0A;
    private static final byte CARRIAGE_RETURN = 0x0D;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String fileNamePattern;
    private final long bytesToTail;

    private boolean stopped = false;
    private boolean isIncludeDatePattern = false;
    private final LogComponentOutputStream output; 
    
    private String currentYMD; 
    
    final static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
    
    private Charset logCharset = Charset.defaultCharset();

    private File file;
    
    public FileReader(ReadLogInfo readLogInfo) {
    	String fileNamePattern = readLogInfo.getLogPath();
    	
    	if (fileNamePattern == null) {
            throw new IllegalArgumentException("constructor parameter file must not be null");
        }
    	
    	long bytesToTail = 50000;
    	String charset = readLogInfo.getCharset();
    	
    	this.bytesToTail = bytesToTail;
        this.fileNamePattern = fileNamePattern;
        this.output = new LogComponentOutputStream();
        this.isIncludeDatePattern = LogFilenameUtils.isDatePattern(fileNamePattern);
        
        if(this.isIncludeDatePattern) {
        	this.file = new File(FileServiceUtils.getLogFileName(this.fileNamePattern));
        }else {
        	this.file = new File(fileNamePattern);
        }
        
        currentYMD = new DateTime().toString(dateFormatter);
        
        if(!StringUtils.isBlank(charset)) {
        	logCharset = Charset.forName(charset);
        }
        
        logger.info("tail charset : {}, filePath : {}, logCharset : {}", charset, fileNamePattern, logCharset);
	}
    
	@Override
    public void run() {
        SeekableByteChannel channel = null;
        
        try {
            channel = getChannel(channel);
            
            long position = -1;
            
            if(!this.file.exists()) {
            	logger.info("tail start file does not exists path : {}, absolute path : {} ", this.fileNamePattern, FileServiceUtils.logFile(this.fileNamePattern).getAbsolutePath() );
            }else {
            	logger.info("tail start path : {}, readFile : {}" ,this.fileNamePattern, this.file.getAbsoluteFile() );
            	position = getStartInByte() ;
            }
            
            if(channel != null) {
            	channel.position(position);
            }
            
            int BUFFER_SIZE = 4096; 
            // Read up to this amount of data from the file at once.
            ByteBuffer readBuffer = allocate(BUFFER_SIZE);
            
            ByteBuffer afterBuffer = allocate(BUFFER_SIZE);
            String afterLog = "";
            boolean fileNotFound = false; 
            while (!this.stopped) {
            	
            	String newYMD = new DateTime().toString(dateFormatter);
            	if(!currentYMD.equals(newYMD)){
            		if(this.isIncludeDatePattern ) {
	            		if(!currentYMD.equals(newYMD)){
	            			File newFile = FileServiceUtils.logFile(this.fileNamePattern);
	            			if(newFile.exists()) {
	            				this.file = newFile;
	            				this.currentYMD = newYMD;
	            				channel = getChannel(channel);
	            			}
	            		}
	            	}else {
	            		
	            	}
            	}

                // file check
                if (!this.file.exists()) {
                	
                	sleep(AWAIT_FILE_ROTATION_MILLIS);
                	
            		if(!fileNotFound) {
                		this.output.processLine("file not found : "+this.file.getAbsolutePath());
                	}
                	fileNotFound = true; 
                    continue;
                }
                
                int read = -1;
                
                if(fileNotFound) {
                	channel = getChannel(channel);
                	position = 0;
                	read = channel.read(readBuffer);
                }else {
                	read = channel.read(readBuffer);
                	
                	if(read ==-1 && position != this.file.length()){
                		channel = getChannel(channel);
                    	position = 0;
                    	read = channel.read(readBuffer);
                	}
                }
                
                fileNotFound = false;

                if (read == -1) {
                    sleep(TAIL_CHECK_INTERVAL_MILLIS);
                   	continue;
                }
                
                boolean needCompact = true; 
                int currPosition = readBuffer.position();
                int newPosition = readBuffer.position();
                //beforeBuffer = allocate(4096);
                byte readByte = 0;
                while(newPosition > 0) {
                	readByte = readBuffer.get(--newPosition); 
                	if(readByte == LINE_FEED) {
                		if(newPosition+1== read) {
                			needCompact = false; 
                		}
                		++newPosition;
                		break; 
                	}
                }
                
                //System.out.println(needCompact +" ;; " +read+ " :: "+readByte + " :: " + newPosition +" ;; "+ readBuffer.limit() +" readBuffer.capacity() " +readBuffer.capacity());
                position = channel.position();
                
                String logInfo;
                if(needCompact && newPosition > 0) {
                	java.util.Arrays.fill(afterBuffer.array(), (byte)0);
                	int limitLen = currPosition-newPosition; 
                	readBuffer.position(0);
                	afterBuffer.put(readBuffer.array(), newPosition, limitLen);
                	
                	// new line before read
                	readBuffer.limit(newPosition);
                	logInfo = afterLog+ logCharset.decode(readBuffer).toString();
                	
                	// new line after read
                	afterBuffer.position(0);
                	afterBuffer.limit(limitLen);
                	afterLog = logCharset.decode(afterBuffer).toString();
                    afterBuffer.clear();
                }else {
                	readBuffer.position(0);
                	readBuffer.limit(currPosition);
                	logInfo = afterLog+ logCharset.decode(readBuffer).toString();
                	afterLog ="";
                }
                
                this.output.processLine(logInfo);
                
                readBuffer.flip();
                readBuffer.clear();
               
            }
        } catch (IOException e) {
            logger.error("Unable to tail " + this.file.getAbsolutePath() + ".", e);
        } catch (InterruptedException e) {
            if (!this.stopped) {
                logger.error("Stopped tailing " + this.file.getAbsolutePath() + ", got interrupted.", e);
            }
        } finally {
            closeQuietly(channel);
        }
    }

	private SeekableByteChannel getChannel(SeekableByteChannel channel) throws IOException {
		if(!this.file.exists()) {
			return channel;
		}
    	closeQuietly(channel);
		return newByteChannel(this.file.toPath(), READ);
	}
    
	private long getStartInByte() {
    	long availableInByte = this.file.length();
        long startingFromInByte = max(availableInByte - this.bytesToTail, 0);
         
    	return startingFromInByte;
    }

	@Override
	public void stop() {
        this.stopped = true;
    }
    
	@Override
    public LogComponentOutputStream getOutputStream() {
    	return output; 
    }
    
	@Override
    public boolean isStop() {
    	return this.stopped;
    }
}