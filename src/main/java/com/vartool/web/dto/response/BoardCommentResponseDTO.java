package com.vartool.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.vartool.web.model.entity.board.BoardCommentEntity;
import com.vartool.web.module.SecurityUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class BoardCommentResponseDTO {

	private long articleId;
	private long commentId;
	private long parentCommentId;
	
	private String contents; 
	private String authorName; 
	
	private boolean delYn;
	
	private boolean isModifyAuth; 
	
	private int indent;
	
	private LocalDateTime regDt; 
	
	private List<BoardFileResponseDTO> fileList; 
	
	public static BoardCommentResponseDTO toDto(BoardCommentEntity comment) {
		BoardCommentResponseDTO brd = new BoardCommentResponseDTO();
		
		brd.setCommentId(comment.getCommentId());
		brd.setArticleId(comment.getArticleId());
		brd.setParentCommentId(comment.getParentCommentId());
		
		if(!comment.isDelYn()) {
			brd.setContents(comment.getContents());
			brd.setIndent(comment.getIndent());
			brd.setAuthorName(comment.getAuthorName());
			brd.setRegDt(comment.getRegDt());
		}
		brd.setDelYn(comment.isDelYn());
		
		brd.setModifyAuth((SecurityUtils.isAdmin() || comment.getRegId().equals(SecurityUtils.userViewId())));
		
		brd.setFileList(comment.getFileList().stream().map(item->{
			return BoardFileResponseDTO.toDto(item);
		}).collect(Collectors.toList()));
		
		return brd;
	}
}