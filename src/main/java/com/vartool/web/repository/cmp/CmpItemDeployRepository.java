package com.vartool.web.repository.cmp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.model.entity.cmp.CmpItemDeployEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpItemDeployRepository extends DefaultJpaRepository, JpaRepository<CmpItemDeployEntity, Long>, JpaSpecificationExecutor<CmpItemDeployEntity>  {

	CmpItemDeployEntity findByCmpId(String cmpId);

	Page<CmpItemDeployEntity> findAllByNameContaining(String keyword, Pageable convertSearchInfoToPage);
	
}
