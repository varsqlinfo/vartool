package com.vartool.web.model.mapper.cmp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.vartool.web.dto.response.CredentialsProviderResponseDTO;
import com.vartool.web.model.entity.cmp.CredentialsProviderEntity;
import com.vartool.web.model.mapper.GenericMapper;
import com.vartool.web.model.mapper.IgnoreUnmappedMapperConfig;

@Mapper(componentModel = "spring", config = IgnoreUnmappedMapperConfig.class)
public interface CredentialsProviderMapper extends GenericMapper<CredentialsProviderResponseDTO, CredentialsProviderEntity> {
	CredentialsProviderMapper INSTANCE = Mappers.getMapper( CredentialsProviderMapper.class );
}