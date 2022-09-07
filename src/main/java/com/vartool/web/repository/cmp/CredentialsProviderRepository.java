package com.vartool.web.repository.cmp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;
import com.vartool.web.repository.DefaultJpaRepository;

@Repository
public interface CredentialsProviderRepository extends DefaultJpaRepository, JpaRepository<CredentialsProviderEntity, Long>, JpaSpecificationExecutor<CredentialsProviderEntity>  {

	CredentialsProviderEntity findByCredId(String credId);

	Page<CredentialsProviderEntity> findAllByCredNameContaining(String keyword, Pageable convertSearchInfoToPage);
}
