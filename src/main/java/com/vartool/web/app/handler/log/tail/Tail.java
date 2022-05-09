package com.vartool.web.app.handler.log.tail;
import static java.lang.Math.max;
import static java.lang.Thread.sleep;
import static java.nio.ByteBuffer.allocate;
import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.READ;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.comparator.NameFileComparator;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.utils.DateUtils;
import com.vartool.web.module.FileServiceUtils;
import com.vartool.web.module.LogFilenameUtils;


public class Tail implements Runnable {

    private static final int AWAIT_FILE_ROTATION_MILLIS = 1000;
    private static final int TAIL_CHECK_INTERVAL_MILLIS = 500;
    
    private static final byte LINE_FEED = 0x0A;
    private static final byte CARRIAGE_RETURN = 0x0D;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String filePath;
    private final long bytesToTail;

    private boolean stopped = false;
    private boolean isIncludeDatePattern = false;
    private final TailOutputStream output; 
    
    final static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
    
    private DateTime currentDate;
    
    private Charset defCharset = Charset.defaultCharset();

    private File file;
    
    /**
     * @param remoteEndpoint must not be <code>null</code>.
     * @param file must not be <code>null</code>.
     * @param bytesToTail the number of bytes up to which are immediately read from the file.
     */
    public Tail(String filePath, long bytesToTail, TailOutputStream output) {
    	this(filePath, bytesToTail, output, null);
    }
    
    public Tail(String filePath, long bytesToTail, TailOutputStream output, String charset) {
        if (filePath == null) {
            throw new IllegalArgumentException("constructor parameter file must not be null");
        }

        this.bytesToTail = bytesToTail;
        this.filePath = filePath;
        this.output = output;
        this.isIncludeDatePattern = LogFilenameUtils.isDatePattern(filePath);
        
        currentDate = new DateTime();
        
        if(charset != null && !"".equals(charset)) {
        	defCharset = Charset.forName(charset);
        }
        
        logger.info("tail charset : {} , filePath : {}, defCharset : {}" ,charset ,filePath, defCharset);
    }

    @Override
    public void run() {
        SeekableByteChannel channel = null;
        
        try {
            channel = getChannel(channel);
            
            long position = -1;
            
            if(this.file==null) {
            	logger.info("tail start file does not exists path : {}, absolute path : {} ", this.filePath, FileServiceUtils.logFile(this.filePath).getAbsolutePath() );
            }else {
            	logger.info("tail start path : {}, readFile : {}" ,this.filePath, this.file.getAbsoluteFile() );
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

                // file check
                if (this.file==null || !this.file.exists()) {
                	
                	sleep(AWAIT_FILE_ROTATION_MILLIS);
                	
                	this.file = FileServiceUtils.logFile(this.filePath); 
                	
                	if(!fileNotFound) {
                		this.output.handle("file not found : "+this.file.getAbsolutePath());
                	}
                	fileNotFound = true; 
                    continue; 
                }

                if (fileNotFound || position > this.file.length()) {
                    position = 0;
                    channel = getChannel(channel);
                }
                
                int read = channel.read(readBuffer);
                
                if (read == -1) {
                    sleep(TAIL_CHECK_INTERVAL_MILLIS);
                    
                    DateTime chkDate = new DateTime();
                    
                    //System.out.println(file.getAbsolutePath()+ " :: "+this.isIncludeDatePattern + " :: " + currentDate.toString(dateFormatter)+ " :: " + chkDate.toString(dateFormatter) + " :: "+ new Period(currentDate, chkDate, PeriodType.days()).getDays());
                    
                    if(new Period(currentDate, chkDate, PeriodType.days()).getDays() != 0) {
                    	
                    	logger.info("file info last file path :{} , date : {} , current date  :{} ", this.file.getAbsolutePath(), new DateTime(this.file.lastModified()).toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")), currentDate.toString(dateFormatter));
                    	
                    	if(this.isIncludeDatePattern ) {
                        	position = 0;
    						channel = getChannel(channel);
                        }else {
                        	channel = reloadByteChannel(channel);
                        }
                    }else {
                    	continue;
                    }
                }
                fileNotFound = false;
                
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
                	logInfo = afterLog+ defCharset.decode(readBuffer).toString();
                	
                	// new line after read
                	afterBuffer.position(0);
                	afterBuffer.limit(limitLen);
                	afterLog = defCharset.decode(afterBuffer).toString();
                    afterBuffer.clear();
                }else {
                	readBuffer.position(0);
                	readBuffer.limit(currPosition);
                	logInfo = afterLog+ defCharset.decode(readBuffer).toString();
                	afterLog ="";
                }
                
                this.output.handle(logInfo);
                
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
    	File newFile = FileServiceUtils.logFile(this.filePath);// new File(newFilePath);
    	if (!newFile.exists()) {
            return channel;
        }
    	currentDate = new DateTime();
    	
    	closeQuietly(channel);
    	this.file = newFile;
    	
		return newByteChannel(this.file.toPath(), READ);
	}
    
    private SeekableByteChannel reloadByteChannel(SeekableByteChannel channel) throws IOException {
    	currentDate = new DateTime();
    	closeQuietly(channel);
    	return newByteChannel(this.file.toPath(), READ);
    }
    

	private long getStartInByte() {
    	long availableInByte = this.file.length();
        long startingFromInByte = max(availableInByte - this.bytesToTail, 0);
         
    	return startingFromInByte;
    }

	public void stop() {
        this.stopped = true;
    }
    
    public TailOutputStream getOutputStream() {
    	return output; 
    }
    
    public boolean isStop() {
    	return this.stopped;
    }
}