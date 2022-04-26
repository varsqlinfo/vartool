package com.vartool.web.app.handler.deploy.git;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
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
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vartool.web.app.handler.deploy.AbstractDeploy;
import com.vartool.web.dto.response.CmpDeployResponseDTO;
import com.vartool.web.dto.websocket.LogMessageDTO;
import com.vartool.web.exception.DeployException;
import com.vartool.web.module.LogFilenameUtils;

public class GitSource {

	private final static Logger logger = LoggerFactory.getLogger(GitSource.class);

	private AbstractDeploy deployAbstract; 
	private LogMessageDTO msgData; 
	private String recvId;
	
	private static final String MESSAGE_PULLED_FAILED = "Pull failed.";
    private static final String MESSAGE_PULLED_FAILED_WITH_STATUS = "Pull failed, status '%s'.";

	private Git git;
	
	final private String localMaster="refs/heads/master";
	final private String orginMater="refs/remotes/origin/master";
	
	private GitProgressMoniter moniter;

	public GitSource(AbstractDeploy deployAbstract, LogMessageDTO msgData, String recvId) {
		this.deployAbstract = deployAbstract;
		this.msgData = msgData;
		this.moniter = new GitProgressMoniter(deployAbstract, msgData, recvId);
		this.recvId = recvId; 
	}

	public static void main(String[] args) throws Exception {
		
		CmpDeployResponseDTO dto = new CmpDeployResponseDTO();
		
		dto.setScmUrl("http://admin@localhost:9000/admin/ep_portlet");
		dto.setScmId("admin");
		dto.setScmPw("1234");
		
		//new GitSource(null, null, null).gitPull(deployItemEntity, false);
	}
	
	
	public static void main1(String[] args) throws Exception {
	    Repository repository = new FileRepositoryBuilder()
	            .setGitDir(new File("c:/zzz/test/WebResources/.git")).build();
	    // Here we get the head commit and it's first parent.
	    // Adjust to your needs to locate the proper commits.
	    RevCommit headCommit = getHeadCommit(repository);
	    RevCommit diffWith = headCommit.getParent(0);
	    FileOutputStream stdout = new FileOutputStream(FileDescriptor.out);
	    try (DiffFormatter diffFormatter = new DiffFormatter(stdout)) {
	        diffFormatter.setRepository(repository);
	        for (DiffEntry entry : diffFormatter.scan(diffWith, headCommit)) {
	        	System.out.println(entry.getChangeType() +" ::"+entry.getNewPath() +" : "+ entry.getOldPath());
	            //diffFormatter.format(diffFormatter.toFileHeader(entry));
	        }
	    }
	}

	private static RevCommit getHeadCommit(Repository repository) throws Exception {
	    try (Git git = new Git(repository)) {
	        Iterable<RevCommit> history = git.log().setMaxCount(1).call();
	        return history.iterator().next();
	    }
	}
	
	public String checkGitRepo (String gitURI, String username, String password){
		String msg = "success"; 
		UsernamePasswordCredentialsProvider loginAuth = new UsernamePasswordCredentialsProvider( username,password );
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
	
	
	public void gitPull(CmpDeployResponseDTO dto, boolean onlyPull) throws Exception {
		
		String gitURI = dto.getScmUrl();
		String username = dto.getScmId();
		String password =dto.getScmPw();
		
		File sourceDir = LogFilenameUtils.getDeploySourcePath(dto);
		
		if(!sourceDir.exists()) {
			sourceDir.mkdir();
		}
		
		UsernamePasswordCredentialsProvider loginAuth = new UsernamePasswordCredentialsProvider(username, password);
		
		logger.info("start");
		
		Git git =null;
		
		String checkMsg =checkGitRepo(gitURI, username, password);
		
		if(!"success".equals(checkMsg)){
			throw new Exception(checkMsg);
		}
		
		boolean isFileExists = false; 
		try (FileRepository fileRepository = new FileRepository(sourceDir.getAbsolutePath() +File.separator +".git")){
			isFileExists= fileRepository.getObjectDatabase().exists();
			fileRepository.close();
		}catch(Exception e) {
			isFileExists = false; 
		}
		
		logger.info("FileRepository isExists : {} ,git repository {}",isFileExists,sourceDir.getAbsolutePath() );
		
		if(!isFileExists) {
			git = Git.cloneRepository()
					.setURI(gitURI)
					.setCredentialsProvider( loginAuth)
					.setDirectory(sourceDir)
					.setProgressMonitor(moniter).call();
			try{
				git.checkout().setName("master").call();
			}catch(Exception e){
				logger.error("execute is :", e.getMessage());
			}finally {
				try {
					git.close();
				}catch(Exception e) {
					logger.error("git.close(): ", e.getMessage(), e);
				};
			}
		}
		
		logger.info("gitPull {}", git);
		
		if(git ==null){
			gitPull(sourceDir, loginAuth, onlyPull);
		}
		
		logger.info("end");
	}

	private boolean gitPull(File localPath, UsernamePasswordCredentialsProvider loginAuth, boolean onlyPull) throws Exception {
		
		try {
			Repository localRepo = new FileRepository(localPath.getAbsolutePath()+File.separator + ".git");
			
			git = new Git(localRepo);
			
			boolean pullFlag = false;
			
			ArrayList<ChangeInfo> changeInfoList = populateDiff(localPath,loginAuth);
			
			if(onlyPull) {
				pullFlag = true; 
			}else {
				if (!changeInfoList.isEmpty()) {
					pullFlag = true; 
				} else {
					pullFlag = false;
				}
			}
			
			logger.info("gitPull pullFlag : {}", pullFlag);
			
			if(pullFlag) {
			
				PullCommand pullCmd = git.pull().setRebase(Boolean.TRUE);
				
				if(onlyPull) {
					pullCmd.setProgressMonitor(moniter);
				}
				
				PullResult pullResult = pullCmd.setCredentialsProvider(loginAuth).call();
				
				logger.info("pullResult.isSuccessful() : {}", pullResult.isSuccessful());
				
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
			}
			
			return true;

		} catch (GitAPIException | IOException ex) {
			logger.error("gitPull : ", ex);
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
    
    private ArrayList<ChangeInfo> populateDiff(File localPath, UsernamePasswordCredentialsProvider loginAuth) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
    	git.fetch()
		.setCredentialsProvider(loginAuth)
		.call();
    	
    	Repository repo = git.getRepository();
    	
    	ArrayList<ChangeInfo> changeInfoList = new ArrayList<ChangeInfo>();
    	
        try(DiffFormatter diffFormatter = new DiffFormatter(NullOutputStream.INSTANCE);
        		RevWalk walk = new RevWalk(repo);){
	        
	        diffFormatter.setRepository(repo);
	        
	        RevCommit fromCommit = walk.parseCommit(repo.resolve(localMaster));
	        RevCommit toCommit = walk.parseCommit(repo.resolve(orginMater));
	        RevTree fromTree = fromCommit.getTree();
	        RevTree toTree = toCommit.getTree();
	        List<DiffEntry> list = diffFormatter.scan(fromTree, toTree);
	        
	        list.forEach((diffEntry) -> {
	        	changeInfoList.add(new ChangeInfo(diffEntry.getChangeType().name(), diffEntry.getOldPath(), diffEntry.getNewPath()));
	        	
	        	this.msgData.setLog(diffEntry.getChangeType() +" : " + diffEntry.getOldPath() +" -> " + diffEntry.getNewPath());
	        	
				this.deployAbstract.sendLogMessage(this.msgData, this.recvId);
				
	        });
	        walk.dispose();
        }
        
        return changeInfoList;
    }

	/**
	 * Populate all the files to update, if the system should update.
	 * @param localPath 
	 * @param loginAuth 
	 */
	private void populateDiff2(File localPath, UsernamePasswordCredentialsProvider loginAuth) {
		try {
			git.fetch()
			.setCredentialsProvider(loginAuth)
			.call();
			
			Repository repo = git.getRepository();
			ObjectId fetchHead = repo.resolve("FETCH_HEAD^{tree}");
			ObjectId head = repo.resolve("HEAD^{tree}");

			ObjectReader reader = repo.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, head);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, fetchHead);
			List<DiffEntry> diffs = git.diff()
					.setNewTree(newTreeIter)
					.setOldTree(oldTreeIter).call();
			
			checkDiffEmpty(diffs);

		} catch (GitAPIException | IOException ex) {
			logger.error("populateDiff : ", ex);
		}
	}

	private void checkDiffEmpty(List<DiffEntry> diffs) {
		if (diffs.isEmpty()) {
			logger.info("checkDiffEmpty : no diff");
		} else {
			for (DiffEntry entry : diffs) {
				this.msgData.setLog(entry.getChangeType() +" : " + entry.getOldPath() +" -> " + entry.getNewPath());
				this.deployAbstract.sendLogMessage(this.msgData, this.recvId);
			}
		}
	}
	
	public void close(){
		if(git!=null){
			git.close();
		}
	}

}