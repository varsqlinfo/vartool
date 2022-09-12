package com.vartool.web.repository.cmp;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vartool.web.dto.response.CmpResponseDTO;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpRepository extends DefaultJpaRepository, JpaRepository<CmpEntity, Long>, JpaSpecificationExecutor<CmpEntity>  {
	
	CmpEntity findByCmpId(String cmpId);
	
	@Query(value = "select new com.vartool.web.dto.response.CmpResponseDTO("+CmpEntity.CMP_ID+", "+CmpEntity.NAME+", "+CmpEntity.CMP_TYPE+","+CmpEntity.DESCRIPTION+","+CmpEntity.CMP_CREDENTIAL+") from CmpEntity m")
	List<CmpResponseDTO> findAllByNameContaining(Sort sort);
}
