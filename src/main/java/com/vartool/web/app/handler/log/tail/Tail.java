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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.comparator.NameFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    
    private String currentDate;
    
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
        
        currentDate = sdf.format(new Date());
        
        if(charset != null && !"".equals(charset)) {
        	defCharset = Charset.forName(charset);
        }
        
        logger.info("tail charset : {} , filePath : {}, defCharset : {}" ,charset ,filePath, defCharset);
    }

    @Override
    public void run() {
        SeekableByteChannel channel = null;
        
        try {
            channel = getChannel();
            
            logger.info("tail start path : {}, readFile : {}" ,this.filePath, this.file.getAbsoluteFile() );
            long position = getStartInByte() ;
            
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

                // The file might be temporarily gone during rotation. Wait, then decide
                // whether the file is considered gone permanently or whether a rotation has occurred.
                if (!this.file.exists()) {
                    sleep(AWAIT_FILE_ROTATION_MILLIS);
                }
                if (!this.file.exists()) {
                	if(!fileNotFound) {
                		this.output.handle("file not found : "+filePath);
                	}
                	fileNotFound = true; 
                    continue; 
                }

                if (fileNotFound || position > this.file.length()) {
                    position = 0;
                    closeQuietly(channel);
                    channel = getChannel();
                }
                
                int read = channel.read(readBuffer);
                
                if (read == -1) {
                    sleep(TAIL_CHECK_INTERVAL_MILLIS);
                    
                    if(this.isIncludeDatePattern && !currentDate.equals(sdf.format(new Date()))) {
                    	currentDate = sdf.format(new Date());
                    	position = 0;
						closeQuietly(channel);
						channel = getChannel();
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

    private SeekableByteChannel getChannel() throws IOException {
    	String newFilePath = LogFilenameUtils.name(filePath);
    	
    	if(LogFilenameUtils.isIncludeIdxStr(newFilePath)) {
    		
    		String prefix =  newFilePath.substring(0 , newFilePath.indexOf(LogFilenameUtils.FILENAME_INDEX_STR));
    		
    		File[] files = new File(new File(newFilePath).getParent()).listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(prefix);
				}
			});
    		
    		if(files.length > 0) {
    			Arrays.sort(files , NameFileComparator.NAME_REVERSE);
    			newFilePath = files[0].getAbsolutePath();
    		}
    	}
    	
    	file = new File(newFilePath);
    	if (!this.file.exists()) {
            return null;
        }
    	
    	SeekableByteChannel channel =newByteChannel(this.file.toPath(), READ);
		return channel;
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