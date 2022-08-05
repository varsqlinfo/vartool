package com.vartool.web.app.handler.deploy.git;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.Test;

import com.vartool.core.crypto.PasswordCryptionFactory;
import com.vartool.web.dto.DeployInfo;

class GitSourceTest {

	@Test
	void gitCheckTest() {
		
		DeployInfo dto = new DeployInfo();
		
		dto.setScmUrl("http://ytkim@165.186.170.189:12311/ytkim/vai-system");
		dto.setScmId("ytkim");
		try {
			dto.setScmPw("1234!");
			
			String result =new GitSource(null, null, null).checkGitRepo(dto.getScmUrl(), dto.getScmId(), dto.getScmPw());
			
			assertTrue("git check result : "+ result, "success".equals(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage() ,false);
		}
		
		
	}
}
