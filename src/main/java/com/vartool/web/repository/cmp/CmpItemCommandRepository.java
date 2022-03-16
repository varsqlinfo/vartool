package com.vartool.web.repository.cmp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.cmp.CmpItemCommandEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpItemCommandRepository extends DefaultJpaRepository, JpaRepository<CmpItemCommandEntity, Long>, JpaSpecificationExecutor<CmpItemCommandEntity>  {

	Page<CmpItemCommandEntity> findAllByNameContaining(String keyword, Pageable convertSearchInfoToPage);

	CmpItemCommandEntity findByCmpId(String cmpId);
	
}
