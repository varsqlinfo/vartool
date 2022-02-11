package com.vartool.web.repository.cmp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO;
import com.vartool.web.model.entity.cmp.CmpGroupEntity;
import com.vartool.web.model.entity.cmp.CmpGroupUserMappingEntity;
import com.vartool.web.model.entity.user.UserEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CmpGroupUserMappingRepository extends DefaultJpaRepository, JpaRepository<CmpGroupUserMappingEntity, String>, JpaSpecificationExecutor<CmpGroupUserMappingEntity>  {

	List<CmpGroupUserMappingEntity> findAl1lByGroupId(String groupId);
	
	@Query(value = "select new com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO(m."+CmpGroupUserMappingEntity.GROUP_ID+", m."+CmpGroupUserMappingEntity.VIEWID+", c."+UserEntity.UNAME+", \'\', c."+UserEntity.UID+") "
			+ "from CmpGroupUserMappingEntity m join m.userEntity c where m.groupId = :groupId order by c.uname asc")
	List<CmpGroupUserMappingResponseDTO> findGroupUserMappingInfo(@Param(CmpGroupUserMappingEntity.GROUP_ID) String groupId);
	
	@Query(value = "select new com.vartool.web.dto.response.CmpGroupUserMappingResponseDTO(m."+CmpGroupUserMappingEntity.GROUP_ID+", m."+CmpGroupUserMappingEntity.VIEWID+", \'\', c."+CmpGroupEntity.NAME+", \'\', c."+CmpGroupEntity.LAYOUT_INFO+") "
			+ "from CmpGroupUserMappingEntity m join m.cmpGroupUser c where m.viewid = :viewid order by c.name asc")
	List<CmpGroupUserMappingResponseDTO> findViewIdMappingInfo(@Param(CmpGroupUserMappingEntity.VIEWID) String viewid);
	
	
}
