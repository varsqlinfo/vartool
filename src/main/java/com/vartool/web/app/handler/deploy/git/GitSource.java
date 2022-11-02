package com.vartool.web.app.handler.deploy.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartech.common.utils.StringUtils;
import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.dto.DeployInfo;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.exception.DeployException;
import com.vartool.web.module.LogFilenameUtils;

/**
 * git source pull 
* 
* @fileName	: GitSource.java
* @author	: ytkim
 */
public class GitSource {

	private final static Logger logger = LoggerFactory.getLogger(GitSource.class);

	private AbstractDeploy deployAbstract; 
	private LogMessageDTO msgData; 
	private String recvId;
	
	private static final String MESSAGE_PULLED_FAILED = "Pull failed.";
    private static final String MESSAGE_PULLED_FAILED_WITH_STATUS = "Pull failed, status '%s'.";

	private ProgressMonitor moniter;

	public GitSource(AbstractDeploy deployAbstract, LogMessageDTO msgData, String recvId) {
		this.deployAbstract = deployAbstract;
		this.msgData = msgData;
		this.moniter = new GitProgressMonitor(deployAbstract, msgData, recvId);
		this.recvId = recvId; 
	}

	private static RevCommit getHeadCommit(Repository repository) throws Exception {
	    try (Git git = new Git(repository)) {
	        Iterable<RevCommit> history = git.log().setMaxCount(1).call();
	        return history.iterator().next();
	    }
	}
	
	public String checkGitRepo (String gitURI, String username, String password){
		if(StringUtils.isBlank(username)) {
			return "username empty"; 
		}
		
		return checkGitRepo(gitURI, new UsernamePasswordCredentialsProvider(username, password));
	}
	public String checkGitRepo (String gitURI, UsernamePasswordCredentialsProvider loginAuth ){
		String msg = "success"; 
		
		try {
			Git.lsRemoteRepository().setRemote(gitURI).setCredentialsProvider(loginAuth).call();
		} catch (InvalidRemoteException e) {
			msg = "InvalidRemoteException uri : [" + gitURI + "] errorMsg "+ e.getMessage();
		} catch (TransportException e) {
			msg = "TransportException uri : [" + gitURI + "] errorMsg "+ e.getMessage();
		} catch (GitAPIException e) {
			msg = "GitAPIException uri : [" + gitURI + "] errorMsg "+ e.getMessage();
		}
		return msg; 
	}
	
	
	public void gitPull(DeployInfo dto, boolean onlyPull) throws Exception {
		
		String gitURI = dto.getScmUrl();
		String username = dto.getScmId();
		UsernamePasswordCredentialsProvider loginAuth = null;
		
		logger.info("start");
		
		if(!StringUtils.isBlank(username)) {
			loginAuth = new UsernamePasswordCredentialsProvider(username, PasswordCryptionFactory.getInstance().decrypt(dto.getScmPw()));
			
			String checkMsg =checkGitRepo(gitURI, loginAuth);
			
			if(!"success".equals(checkMsg)){
				throw new Exception(checkMsg);
			}
		}
		
		File sourceDir = LogFilenameUtils.getDeploySourcePath(dto);	
		
		if(!sourceDir.exists()) {
			sourceDir.mkdir();
		}
		
		boolean isFileExists = false; 
		
		String localBranch = "";
		try (FileRepository fileRepository = new FileRepository(sourceDir.getAbsolutePath() +File.separator +".git")){
			isFileExists= fileRepository.getObjectDatabase().exists();
			localBranch = fileRepository.getBranch();
		}catch(Exception e) {
			isFileExists = false; 
		}
		
		logger.info("FileRepository isExists : {} , File Repository {}", isFileExists, sourceDir.getAbsolutePath());
		
		if(!isFileExists) {
			try(Git git = Git.cloneRepository()
					.setURI(gitURI)
					.setCredentialsProvider(loginAuth)
					.setDirectory(sourceDir)
					.setBranch(dto.getScmBranch())
					.setProgressMonitor(moniter).call();){
				git.checkout().setName(dto.getScmBranch()).call();
			}catch(Exception e){
				logger.error("execute is :{} ", e.getMessage() , e);
			}
		}else {
			logger.info("gitPull localBranch: {} ", localBranch);
			gitPull(dto, sourceDir, loginAuth, onlyPull);
		}
		
		logger.info("end");
	}

	private boolean gitPull(DeployInfo dto, File localPath, UsernamePasswordCredentialsProvider loginAuth, boolean onlyPull) throws Exception {
		
		Repository localRepo = new FileRepository(localPath.getAbsolutePath()+File.separator + ".git");
		
		try (Git git = new Git(localRepo);){
			
			Ref head = localRepo.getRefDatabase().findRef("HEAD");
			
			logger.info("local repo head : {}", head.getName());

			PullCommand pullCmd = git.pull()
					.setRebase(Boolean.TRUE);
			
			//pullCmd.setProgressMonitor(moniter);
			
			PullResult pullResult = pullCmd.setCredentialsProvider(loginAuth).call();
			
			logger.info("pullResult.isSuccessful() : {}", pullResult.isSuccessful());
			
			
			Ref newHEAD = localRepo.getRefDatabase().findRef("HEAD");

	        if (!head.toString().equals(newHEAD.toString())) {
	            ObjectId oldHead = localRepo.resolve(head.getObjectId().getName() + "^{tree}");
	            ObjectId newHead = localRepo.resolve(newHEAD.getObjectId().getName() + "^{tree}");

	            ObjectReader reader = localRepo.newObjectReader();

	            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
	            oldTreeIter.reset(reader, oldHead);

	            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
	            newTreeIter.reset(reader, newHead);

	            List<DiffEntry> entries = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
	            
	            ArrayList<ChangeInfo> changeInfoList = new ArrayList<ChangeInfo>();
	            
            	entries.forEach((diffEntry) -> {
		        	changeInfoList.add(new ChangeInfo(diffEntry.getChangeType().name(), diffEntry.getOldPath(), diffEntry.getNewPath()));
		        	
		        	this.msgData.setLog(diffEntry.getChangeType() +" : " + diffEntry.getOldPath() +" -> " + diffEntry.getNewPath());
		        	
					this.deployAbstract.sendLogMessage(this.msgData, this.recvId);
					
		        });
	        }else {
            	this.msgData.setLog("no change file");
            	this.deployAbstract.sendLogMessage(this.msgData, this.recvId);
	        }
	        
			
			RebaseResult rebaseResult = pullResult.getRebaseResult();

			if (!pullResult.isSuccessful()) {
				
                FetchResult fetchResult = pullResult.getFetchResult();
                
                logger.error("fetchResult : {}", fetchResult);
                logger.error("fetchResult.getTrackingRefUpdates() : {}", fetchResult.getTrackingRefUpdates());
                validateTrackingRefUpdates(MESSAGE_PULLED_FAILED, fetchResult.getTrackingRefUpdates());
                
                if(pullResult.getMergeResult() !=null) {
                	MergeStatus mergeStatus = pullResult.getMergeResult().getMergeStatus();
                	
	                if (!mergeStatus.isSuccessful()) {
	                        throw new DeployException(String.format(MESSAGE_PULLED_FAILED_WITH_STATUS, mergeStatus.name()));
	                }
                }
                
                if(rebaseResult.getStatus() == RebaseResult.Status.CONFLICTS) {
                    logger.warn("Git `pull` reported conflicts - will reset and try again next pass!");
                    git.reset().setMode(ResetCommand.ResetType.HARD).call();
                    return true;
                }
			}
			
			return true; 
		} catch (GitAPIException | IOException ex) {
			logger.error("gitPull : {}", ex.getMessage(), ex);
			throw ex; 
		}
	}
	
	/**
     * Check references updates for any errors
     *
     * @param errorPrefix The error prefix for any error message
     * @param refUpdates A collection of tracking references updates
     */
    public static void validateTrackingRefUpdates(String errorPrefix, Collection<TrackingRefUpdate> refUpdates) {
            for (TrackingRefUpdate refUpdate : refUpdates) {
                    RefUpdate.Result result = refUpdate.getResult();

                    if (result == RefUpdate.Result.IO_FAILURE ||
                        result == RefUpdate.Result.LOCK_FAILURE ||
                        result == RefUpdate.Result.REJECTED ||
                        result == RefUpdate.Result.REJECTED_CURRENT_BRANCH ) {
                            throw new DeployException(String.format("%s - Status '%s'", errorPrefix, result.name()));
                    }
            }
    }
}