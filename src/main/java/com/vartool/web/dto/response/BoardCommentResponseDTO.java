package com.vartool.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.vartool.web.model.entity.board.BoardCommentEntity;
import com.vartool.web.model.entity.board.BoardEntity;
import com.vartool.web.module.SecurityUtil;

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
		
		brd.setModifyAuth((SecurityUtil.isAdmin() || comment.getRegId().equals(SecurityUtil.userViewId())));
		
		brd.setFileList(comment.getFileList().stream().map(item->{
			return BoardFileResponseDTO.toDto(item);
		}).collect(Collectors.toList()));
		
		return brd;
	}
}