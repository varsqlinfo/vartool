package com.vartool.web.repository.cmp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpItemLogRepository extends DefaultJpaRepository, JpaRepository<CmpItemLogEntity, Long>, JpaSpecificationExecutor<CmpItemLogEntity>  {

	CmpItemLogEntity findByCmpId(String cmpId);

	Page<CmpItemLogEntity> findAllByNameContaining(String keyword, Pageable convertSearchInfoToPage);
}
