package com.vartool.web.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.vartech.common.app.beans.SearchParameter;
import com.vartool.web.model.entity.cmp.CmpEntity;

public class CmpSpec extends DefaultSpec {

	public static Specification<CmpEntity> componentList(SearchParameter param) {
		
		return (root, query, cb) -> {
			return cb.and(
				cb.like(root.get(CmpEntity.NAME), contains(param.getKeyword()))
			);
		};
	}	
}