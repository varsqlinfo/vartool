package com.vartool.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.board.BoardEntity;

@Repository
public interface BoardRepository extends DefaultJpaRepository, JpaRepository<BoardEntity, Long>, JpaSpecificationExecutor<BoardEntity>  {
	
	public BoardEntity findByArticleId(long articleId);
}
