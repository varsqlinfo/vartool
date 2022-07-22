package com.vartool.web.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.user.UserEntity;

@Repository
public interface UserMgmtRepository extends JpaRepository<UserEntity, String> {

	Page<UserEntity> findByUserRoleNotAndUnameContaining(String userRole, String keyword, Pageable convertSearchInfoToPage);
	
	Page<UserEntity> findByUserRoleAndUnameContaining(String userRole, String keyword, Pageable convertSearchInfoToPage);

	UserEntity findByViewid(String viewid);

	List<UserEntity> findByViewidIn(List<String> ids);

}