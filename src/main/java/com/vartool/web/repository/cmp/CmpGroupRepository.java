package com.vartool.web.repository.cmp;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO;
import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpGroupRepository extends DefaultJpaRepository, JpaRepository<CmpGroupEntity, Long>, JpaSpecificationExecutor<CmpGroupEntity>  {
	Page<CmpGroupEntity>findByNameContaining(String name, Pageable page);

	CmpGroupEntity findByGroupId(String groupId);

	@Query(value = "select new com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO(m."+CmpGroupEntity.GROUP_ID+", \'\', \'\', m."+CmpGroupEntity.NAME+", \'\', m."+CmpGroupEntity.LAYOUT_INFO+") "
			+ "from CmpGroupEntity m order by m.name asc")
	List<CmpGroupUserMappingResponseDTO> findAllGroup();
}
