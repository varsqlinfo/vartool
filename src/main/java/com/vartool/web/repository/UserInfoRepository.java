package com.vartool.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.model.entity.user.UserEntity;

@Repository
public interface UserInfoRepository extends JpaRepository<UserEntity, String> {

	Page<UserEntity> findByUserRoleAndUnameContaining(String userRole, String keyword, Pageable convertSearchInfoToPage);

	UserEntity findByViewid(String viewid);
	
	
}