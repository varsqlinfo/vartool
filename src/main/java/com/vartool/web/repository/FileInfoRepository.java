package com.vartool.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.FileInfoEntity;

@Repository
public interface FileInfoRepository extends DefaultJpaRepository, JpaRepository<FileInfoEntity, String> {
	
}
