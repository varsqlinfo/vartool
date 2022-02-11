package com.vartool.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.user.UserPreferencesEntity;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferencesEntity, String> {
	
	UserPreferencesEntity findByGroupIdAndViewidAndPrefKey(@Param(UserPreferencesEntity.GROUP_ID) String groupId
			, @Param(UserPreferencesEntity.VIEWID) String viewid, @Param(UserPreferencesEntity.PREF_KEY) String prefKey);
	
	
}