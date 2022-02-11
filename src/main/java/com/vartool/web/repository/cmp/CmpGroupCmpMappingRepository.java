package com.vartool.web.repository.cmp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vartool.web.dto.response.CmpGroupCmpMappingResponseDTO;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.cmp.CmpGroupMappingEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpGroupCmpMappingRepository extends DefaultJpaRepository, JpaRepository<CmpGroupMappingEntity, Long>, JpaSpecificationExecutor<CmpGroupMappingEntity>  {

	List<CmpGroupMappingEntity> findAllByGroupId(String groupId);
	
	@Query(value = "select new com.vartool.web.dto.response.CmpGroupCmpMappingResponseDTO(m."+CmpGroupMappingEntity.GROUP_ID+", m."+CmpGroupMappingEntity.CMP_ID+", c."+CmpEntity.NAME+", c."+CmpEntity.CMP_TYPE+") "
			+ "from CmpGroupMappingEntity m join m.cmpEntity c where m.groupId = :groupId order by c."+CmpEntity.NAME+" asc")
	List<CmpGroupCmpMappingResponseDTO> findMappingInfo(@Param(CmpGroupMappingEntity.GROUP_ID) String groupId);


	@Query(value = "select new com.vartool.web.dto.response.CmpGroupCmpMappingResponseDTO"
			+ "(m."+CmpGroupMappingEntity.GROUP_ID+", m."+CmpGroupMappingEntity.CMP_ID+", c."+CmpEntity.NAME+", c."+CmpEntity.CMP_TYPE+", c."+CmpEntity.DESCRIPTION+") "
			+ "from CmpGroupMappingEntity m join m.cmpEntity c where m.groupId = :groupId order by c."+CmpEntity.CMP_TYPE+" asc, c."+CmpEntity.NAME+" asc")
	List<CmpGroupCmpMappingResponseDTO> findComponentList(@Param(CmpGroupMappingEntity.GROUP_ID)String groupId);
	
}
