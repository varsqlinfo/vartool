package com.vartool.web.repository.cmp;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.vartool.web.constants.LogType;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.dto.CredentialInfo;
import com.vartool.web.dto.ReadLogInfo;
import com.vartool.web.dto.response.CmpLogResponseDTO;
import com.vartool.web.model.entity.cmp.CmpEntity;
import com.vartool.web.model.entity.cmp.CmpItemLogEntity;
import com.vartool.web.model.entity.cmp.CmpItemLogExtensionsEntity;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;
import com.vartool.web.model.entity.cmp.QCmpEntity;
import com.vartool.web.model.entity.cmp.QCmpItemLogEntity;
import com.vartool.web.model.entity.cmp.QCmpItemLogExtensionsEntity;
import com.vartool.web.model.entity.cmp.QCredentialsProviderEntity;
import com.vartool.web.repository.DefaultJpaRepository;

import lombok.Getter;

@Repository
public interface CmpItemLogRepository extends DefaultJpaRepository, JpaRepository<CmpItemLogEntity, Long>, JpaSpecificationExecutor<CmpItemLogEntity>, CmpItemLogEntityCustom  {

	CmpItemLogEntity findByCmpId(String cmpId);

	
	@Transactional(readOnly = true , value=ResourceConfigConstants.APP_TRANSMANAGER)
	public class CmpItemLogEntityCustomImpl extends QuerydslRepositorySupport implements CmpItemLogEntityCustom {

		public CmpItemLogEntityCustomImpl() {
			super(CmpItemLogEntity.class);
		}

		@Override
		public Page<CmpLogResponseDTO> findAllByNameContaining(String keyword, Pageable convertSearchInfoToPage) {
			final QCmpEntity cmpEntity = QCmpEntity.cmpEntity;
			final QCmpItemLogEntity cmpItemLogEntity = QCmpItemLogEntity.cmpItemLogEntity;
			final QCmpItemLogExtensionsEntity cmpItemLogExtensionsEntity  = QCmpItemLogExtensionsEntity.cmpItemLogExtensionsEntity;
			
			JPQLQuery<CmpItemLogEntityCustomResultVO> query = from(cmpEntity).innerJoin(cmpItemLogEntity).on(cmpEntity.cmpId.eq(cmpItemLogEntity.cmpId))
			.leftJoin(cmpItemLogExtensionsEntity).on(cmpEntity.cmpId.eq(cmpItemLogExtensionsEntity.cmpId))
			.select(Projections.constructor(CmpItemLogEntityCustomResultVO.class, cmpEntity, cmpItemLogEntity, cmpItemLogExtensionsEntity))
			.where(cmpEntity.name.contains(keyword));
			
			
			long totalCount = query.fetchCount();
			
			List<CmpItemLogEntityCustomResultVO> results = getQuerydsl().applyPagination(convertSearchInfoToPage, query).fetch();
			
			return new PageImpl<>(results.stream().map(item->{
				CmpLogResponseDTO dto = new CmpLogResponseDTO();
				
				dto.setCmpId(item.getCmpEntity().getCmpId());
				dto.setName(item.getCmpEntity().getName());
				dto.setCmpCredential(item.getCmpEntity().getCmpCredential());
				dto.setLogPattern(item.getCmpEntity().getLogPattern());
				dto.setDescription(item.getCmpEntity().getDescription());
				
				dto.setLogType(item.getCmpItemLogEntity().getLogType());
				dto.setCharset(item.getCmpItemLogEntity().getLogCharset());
				dto.setLogPath(item.getCmpItemLogEntity().getLogPath());
				
				CmpItemLogExtensionsEntity cile = item.getCmpItemLogExtensionsEntity();
				
				if(cile != null) {
					dto.setCommand(cile.getCommand());
					dto.setRemoteHost(cile.getRemoteHost());
					dto.setRemotePort(cile.getRemotePort());
				}
				
				return dto;
			}).collect(Collectors.toList()), convertSearchInfoToPage, totalCount);
		}

		@Override
		public ReadLogInfo findReadLogInfo(String cmpId) {
			final QCmpEntity cmpEntity = QCmpEntity.cmpEntity;
			final QCmpItemLogEntity cmpItemLogEntity = QCmpItemLogEntity.cmpItemLogEntity;
			final QCmpItemLogExtensionsEntity cmpItemLogExtensionsEntity  = QCmpItemLogExtensionsEntity.cmpItemLogExtensionsEntity;
			
			CmpItemLogEntityCustomResultVO customVo = from(cmpEntity).innerJoin(cmpItemLogEntity).on(cmpEntity.cmpId.eq(cmpItemLogEntity.cmpId))
			.leftJoin(cmpItemLogExtensionsEntity).on(cmpEntity.cmpId.eq(cmpItemLogExtensionsEntity.cmpId))
			.select(Projections.constructor(CmpItemLogEntityCustomResultVO.class, cmpEntity, cmpItemLogEntity, cmpItemLogExtensionsEntity))
			.where(cmpEntity.cmpId.contains(cmpId)).fetchOne();
			
			ReadLogInfo readLogInfo = new ReadLogInfo();
			
			CmpEntity customCmpEntity = customVo.getCmpEntity();
			CmpItemLogEntity costomCmpItemLogEntity = customVo.getCmpItemLogEntity();
			
			readLogInfo.setCmpId(customCmpEntity.getCmpId());
			readLogInfo.setName(customCmpEntity.getName());
			readLogInfo.setCmpCredential(customCmpEntity.getCmpCredential());
			readLogInfo.setLogPattern(customCmpEntity.getLogPattern());
			readLogInfo.setDescription(customCmpEntity.getDescription());
			
			readLogInfo.setLogType(costomCmpItemLogEntity.getLogType());
			readLogInfo.setCharset(costomCmpItemLogEntity.getLogCharset());
			readLogInfo.setLogPath(costomCmpItemLogEntity.getLogPath());
			
			CmpItemLogExtensionsEntity cile = customVo.getCmpItemLogExtensionsEntity();
			
			if(cile != null) {
				readLogInfo.setCommand(cile.getCommand());
				readLogInfo.setRemoteHost(cile.getRemoteHost());
				readLogInfo.setRemotePort(cile.getRemotePort());
			}
			
			if(!LogType.FILE.name().equals(readLogInfo.getLogType())) {
				QCredentialsProviderEntity credentialsProviderEntity = QCredentialsProviderEntity.credentialsProviderEntity;
				
				CredentialsProviderEntity entity = from(credentialsProviderEntity).where(credentialsProviderEntity.credId.eq(readLogInfo.getCmpCredential())).fetchOne();
				
				if(entity != null) {
					readLogInfo.setCredentialInfo(CredentialInfo.toDto(entity));
				}
			}
			return readLogInfo;
		}
	}
	
	@Getter
	public class CmpItemLogEntityCustomResultVO{
		private CmpEntity cmpEntity;
		private CmpItemLogEntity cmpItemLogEntity;
		private CmpItemLogExtensionsEntity cmpItemLogExtensionsEntity;
		
		public CmpItemLogEntityCustomResultVO(CmpEntity cmpEntity, CmpItemLogEntity cmpItemLogEntity, CmpItemLogExtensionsEntity cmpItemLogExtensionsEntity) {
			this.cmpEntity = cmpEntity; 
			this.cmpItemLogEntity = cmpItemLogEntity; 
			this.cmpItemLogExtensionsEntity = cmpItemLogExtensionsEntity;
		}
	}
}

interface CmpItemLogEntityCustom {
	Page<CmpLogResponseDTO> findAllByNameContaining(String keyword, Pageable convertSearchInfoToPage);
	
	ReadLogInfo findReadLogInfo(String cmpId);
}