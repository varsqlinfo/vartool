package com.vartool.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.user.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
	
	UserEntity findByUemail(String uemail);
	
	UserEntity findByUid(String uname);
	
	UserEntity findByViewid(String viewId);

	long countByUid(String uid);
	long countByUemail(String email);
}