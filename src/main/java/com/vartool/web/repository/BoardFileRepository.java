package com.vartool.web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.model.entity.board.BoardFileEntity;

@Repository
public interface BoardFileRepository extends DefaultJpaRepository, JpaRepository<BoardFileEntity, Long> {

	BoardFileEntity findByArticleAndFileId(BoardEntity article, long fileId);

	List<BoardFileEntity> findAllByArticleAndFileId(BoardEntity article, long fileId);

	@Transactional
	@Modifying
	@Query("delete from BoardFileEntity a where a.fileId in :ids")
	void deleteByIdInQuery(@Param("ids") List<Long> ids);
	
	@Transactional
	@Modifying
	void deleteByContId(String id);
	
	@Transactional
	@Modifying
	@Query("delete from BoardFileEntity a where a.contId in (select commentId from BoardCommentEntity a where a.articleId = :id)")
	void deleteAllCommnetFileQuery(@Param("id") long id);
	
	@Transactional
	@Modifying
	@Query("delete from BoardFileEntity a where a.contId = :id")
	void deleteByContIdQuery(@Param("id")long articleId);
	
}
