package com.vartool.web.app.user.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vartech.common.app.beans.ResponseResult;
import com.vartech.common.app.beans.SearchParameter;
import com.vartech.common.utils.StringUtils;
import com.vartool.web.app.common.service.FileUploadService;
import com.vartool.web.constants.ResourceConfigConstants;
import com.vartool.web.constants.UploadFileType;
import com.vartool.web.dto.request.BoardCommentRequestDTO;
import com.vartool.web.dto.request.BoardRequestDTO;
import com.vartool.web.dto.response.BoardCommentResponseDTO;
import com.vartool.web.dto.response.BoardResponseDTO;
import com.vartool.web.exception.BoardNotFoundException;
import com.vartool.web.exception.BoardPermissionException;
import com.vartool.web.exception.PermissionDeniedException;
import com.vartool.web.model.entity.board.BoardCommentEntity;
import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.model.entity.board.BoardFileEntity;
import com.vartool.web.module.NumberUtils;
import com.vartool.web.module.SecurityUtils;
import com.vartool.web.module.VartoolUtils;
import com.vartool.web.repository.BoardCommentRepository;
import com.vartool.web.repository.BoardFileRepository;
import com.vartool.web.repository.BoardRepository;
import com.vartool.web.repository.spec.BoardSpec;

/**
 * 게시판
* 
* @fileName	: BoardService.java
* @author	: ytkim
 */
@Service
public class BoardService{
	private final static Logger logger = LoggerFactory.getLogger(BoardService.class);
	
	@Autowired
	private BoardRepository boardEntityRepository;
	
	@Autowired
	private BoardCommentRepository boardCommentEntityRepository;
	
	@Autowired
	private BoardFileRepository boardFileEntityRepository;
	
	@Autowired
	private FileUploadService fileUploadService;
	
	private final String BOARD_CONT_TYPE = "board";
	private final String COMMENT_CONT_TYPE = "comment";
	
	/**
	 * 목록
	 *
	 * @method : list
	 * @param boardCode
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult list(String boardCode, SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.DESC, BoardEntity.NOTICE_YN).and(Sort.by(Sort.Direction.DESC, BoardEntity.REG_DT));
		
		Page<BoardEntity> result = boardEntityRepository.findAll(
			BoardSpec.boardSearch(boardCode, searchParameter)
			, VartoolUtils.convertSearchInfoToPage(searchParameter, sort)
		);
		
		return VartoolUtils.getResponseResult(result.getContent().stream().map(item->{
			 return BoardResponseDTO.toDto(item);
		}).collect(Collectors.toList()), result.getTotalElements(), searchParameter);
	}
	
	/**
	 * 게시물 정보 저장. 
	 *
	 * @method : saveBoardInfo
	 * @param boardRequestDTO
	 * @return
	 */
	@Transactional(transactionManager=ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor=Throwable.class)
	public ResponseResult saveBoardInfo(BoardRequestDTO boardRequestDTO) {
		
		BoardEntity boardEntity;
		
		if(NumberUtils.isNullOrZero(boardRequestDTO.getArticleId())) {
			boardEntity = boardRequestDTO.toEntity();
			boardEntity.setAuthorName(SecurityUtils.loginInfo().getFullname());
			boardEntity.setFileList(new ArrayList<BoardFileEntity>());
		}else {
			boardEntity = boardEntityRepository.findByArticleId(boardRequestDTO.getArticleId());
			
			if(!isModify(boardEntity)) {
				throw new BoardPermissionException("no permission");
			}
			
			boardEntity.setTitle(boardRequestDTO.getTitle());
			boardEntity.setContents(boardRequestDTO.getContents());
			
			String fileIds = boardRequestDTO.getRemoveFileIds();
			
			if(!StringUtils.isBlank(fileIds)) {
				boardFileEntityRepository.deleteByIdInQuery(Arrays.asList(fileIds.split(",")).stream().map(item->{
					return Long.parseLong(item);
				}).collect(Collectors.toList()));
			}
		}
		
		if(boardRequestDTO.getFile() !=null && boardRequestDTO.getFile().size() > 0) {
			List<BoardFileEntity> boardFileList= boardEntity.getFileList();
			fileUploadService.uploadFiles(UploadFileType.BOARD, boardRequestDTO.getFile(), boardRequestDTO.getArticleId()+"", boardRequestDTO.getBoardCode(),"file", false, false).forEach(item->{
				BoardFileEntity entity= BoardFileEntity.toBoardFileEntity(item);
				entity.setArticle(boardEntity);
				entity.setContType(BOARD_CONT_TYPE);
				boardFileList.add(entity);
			});
			boardEntity.setFileList(boardFileList);
		}
		
		boardEntityRepository.save(boardEntity);
		
		ResponseResult result = VartoolUtils.getResponseResultItemList(new ArrayList());
		result.setItemOne(1);
		
		return result;
	}
	
	/**
	 * 글 삭제.
	 *
	 * @method : deleteBoardInfo
	 * @param boardCode
	 * @param articleId
	 * @return
	 */
	@Transactional(transactionManager=ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor=Throwable.class)
	public ResponseResult deleteBoardInfo(String boardCode, long articleId) {
		
		logger.debug("deleteBoardInfo boardCode : {} , articleId:{} ", boardCode, articleId);
		
		BoardEntity board = boardEntityRepository.findByArticleId(articleId);
		
		if(board == null || !isModify(board)) {
			throw new PermissionDeniedException("no permission");
		}
		
		boardFileEntityRepository.deleteByContIdQuery(board.getArticleId());
		boardFileEntityRepository.deleteAllCommnetFileQuery(board.getArticleId());
		boardCommentEntityRepository.deleteByArticleIdQuery(board.getArticleId());
		boardEntityRepository.delete(board);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 상세보기
	 *
	 * @method : viewBoardInfo
	 * @param boardCode
	 * @param articleId
	 * @return
	 */
	public BoardResponseDTO viewBoardInfo(String boardCode, long articleId) {
		BoardEntity boardEntity = boardEntityRepository.findByArticleId(articleId); 
		
		if(boardEntity == null) {
			throw new BoardNotFoundException("not found : "+ articleId);
		}
		
		BoardResponseDTO  dto = BoardResponseDTO.toDto(boardEntity, true);
		dto.setModifyAuth(isModify(boardEntity));

		return dto;
	}
	
	/**
	 * 코멘트 저장. 
	 *
	 * @method : commentSave
	 * @param articleId
	 * @param boardCommentRequestDTO
	 * @return
	 */
	@Transactional(transactionManager=ResourceConfigConstants.APP_TRANSMANAGER, rollbackFor=Throwable.class)
	public ResponseResult commentSave(long articleId, BoardCommentRequestDTO boardCommentRequestDTO) {
		
		BoardEntity boardEntity = boardEntityRepository.findByArticleId(articleId);
		
		if(boardEntity == null) {
			throw new BoardNotFoundException("article id not found : "+ articleId);
		}
		
		BoardCommentEntity boardCommentEntity;
		boolean isNew = NumberUtils.isNullOrZero(boardCommentRequestDTO.getCommentId()); 
		boolean isReComment = false; 
		
		if(isNew) {
			
			boardCommentEntity = boardCommentRequestDTO.toEntity();
			boardCommentEntity.setArticleId(articleId);
			boardCommentEntity.setAuthorName(SecurityUtils.loginInfo().getFullname());
			boardCommentEntity.setFileList(new ArrayList<BoardFileEntity>());
			
			isReComment = !NumberUtils.isNullOrZero(boardCommentRequestDTO.getParentCommentId()); // 대댓글
			if(isReComment) {
				
				BoardCommentEntity parentBoardCommentEntity = boardCommentEntityRepository.findByArticleIdAndCommentId(articleId, boardCommentRequestDTO.getParentCommentId());
				
				if(parentBoardCommentEntity== null) {
					throw new BoardNotFoundException("parent comments not found : "+ boardCommentRequestDTO.getParentCommentId());
				}
				boardCommentEntity.setIndent(parentBoardCommentEntity.getIndent() +1);
				boardCommentEntity.setGrpCommentId(parentBoardCommentEntity.getGrpCommentId());
				boardCommentEntity.setGrpSeq(boardCommentEntityRepository.findByGrpSeqMaxQuery(parentBoardCommentEntity.getGrpCommentId()));
			}else {
				
			}
		}else {
			boardCommentEntity = boardCommentEntityRepository.findByArticleIdAndCommentId(articleId, boardCommentRequestDTO.getCommentId());
			
			if( !isCommentModify(boardCommentEntity)){
				throw new BoardPermissionException("no permission");
			}
			
			boardCommentEntity.setContents(boardCommentRequestDTO.getContents());
			
			String fileIds = boardCommentRequestDTO.getRemoveFileIds();
			
			if(!StringUtils.isBlank(fileIds)) {
				boardFileEntityRepository.deleteByIdInQuery(Arrays.asList(fileIds.split(",")).stream().map(item->{
					return Long.parseLong(item);
				}).collect(Collectors.toList()));
			}
		}
		
		if(boardCommentRequestDTO.getFile().size() > 0) {
			List<BoardFileEntity> boardFileList= boardCommentEntity.getFileList();
			fileUploadService.uploadFiles(UploadFileType.BOARD, boardCommentRequestDTO.getFile(), boardCommentRequestDTO.getCommentId()+"", boardCommentRequestDTO.getBoardCode(),"file", false, false).forEach(item->{
				BoardFileEntity entity= BoardFileEntity.toBoardFileEntity(item);
				entity.setComment(boardCommentEntity);
				entity.setContType(COMMENT_CONT_TYPE);
				boardFileList.add(entity);
			});
			boardCommentEntity.setFileList(boardFileList);
		}
		
		BoardCommentEntity saveEntity = boardCommentEntityRepository.save(boardCommentEntity);
		
		if(isNew) {
			
			if(boardEntity.getCommentCnt() > 0) {
				boardEntity.setCommentCnt(boardEntity.getCommentCnt() +1);
			}else {
				boardEntity.setCommentCnt(1);
			}
			
			boardEntityRepository.save(boardEntity);
			
			if(!isReComment){
				saveEntity.setGrpCommentId(saveEntity.getCommentId());
				boardCommentEntityRepository.save(saveEntity);
			}else {
				boardCommentEntityRepository.updateGrpSeqQuery(saveEntity.getArticleId(), saveEntity.getGrpCommentId(), saveEntity.getCommentId(), saveEntity.getGrpSeq());
			}
		}
		
		ResponseResult result = VartoolUtils.getResponseResultItemList(new ArrayList());
		result.setItemOne(1);
		
		return result;
	}
	
	/**
	 * 댓글 목록. 
	 *
	 * @method : commentList
	 * @param boardCode
	 * @param acticleId
	 * @param searchParameter
	 * @return
	 */
	public ResponseResult commentList(String boardCode, long acticleId, SearchParameter searchParameter) {
		Sort sort =Sort.by(Sort.Direction.ASC, BoardCommentEntity.GRP_COMMENT_ID).and(Sort.by(Sort.Direction.ASC, BoardCommentEntity.GRP_SEQ));
		
		return VartoolUtils.getResponseResultItemList(boardCommentEntityRepository.findByArticleId(acticleId, sort).stream().map(item->{
			return BoardCommentResponseDTO.toDto(item);
		}).collect(Collectors.toList()));
	}

	/**
	 * 댓글 삭제. 
	 *
	 * @method : commentDelete
	 * @param articleId
	 * @param commentId
	 * @return
	 */
	public ResponseResult commentDelete(long articleId, long commentId) {
		
		BoardEntity boardEntity = boardEntityRepository.findByArticleId(articleId);
		
		if(boardEntity == null) {
			throw new BoardNotFoundException("article id not found : "+ articleId);
		}
		
		BoardCommentEntity boardCommentEntity = boardCommentEntityRepository.findByArticleIdAndCommentId(articleId, commentId);
		
		if( !isCommentModify(boardCommentEntity)){
			throw new BoardPermissionException("no permission");
		}
		
		if(boardCommentEntity.getChildren().size() > 0) {
			boardCommentEntity.setDelYn(true);
			boardCommentEntityRepository.save(boardCommentEntity);
		}else {
			boardCommentEntityRepository.delete(boardCommentEntity);
			
            BoardCommentEntity parentCommentEntity = boardCommentEntity.getParent(); 
			// 상위 comment 삭제 처리
			if(parentCommentEntity != null && parentCommentEntity.isDelYn() && parentCommentEntity.getChildren().size() ==1 && parentCommentEntity.getChildren().get(0).getCommentId() ==boardCommentEntity.getCommentId() ) {
				boardCommentEntityRepository.delete(parentCommentEntity);
			}
		}
		
		if(boardEntity.getCommentCnt() > 0) {
			boardEntity.setCommentCnt(boardEntity.getCommentCnt() - 1);
		}else {
			boardEntity.setCommentCnt(0);
		}
		
		boardEntityRepository.save(boardEntity);
		
		return VartoolUtils.getResponseResultItemOne(1);
	}
	
	/**
	 * 파일 목록. 
	 *
	 * @method : findFileList
	 * @param articleId
	 * @param fileId
	 * @return
	 */
	public List<BoardFileEntity> findFileList(long articleId, long fileId) {
		return boardFileEntityRepository.findAllByArticleAndFileId(BoardEntity.builder().articleId(articleId).build(), fileId);
	}
	
	private boolean isModify(BoardEntity boardEntity) {
		return SecurityUtils.isAdmin() || SecurityUtils.isManager() || boardEntity.getRegId().equals(SecurityUtils.userViewId());
	}

	private boolean isCommentModify(BoardCommentEntity boardCommentEntity) {
		return SecurityUtils.isAdmin() || SecurityUtils.isManager() || boardCommentEntity.getRegId().equals(SecurityUtils.userViewId());
	}
	
}
