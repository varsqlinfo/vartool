package com.vartool.web.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.vartech.common.utils.FileUtils;
import com.vartool.web.model.entity.base.AbstractRegAuditorModel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Entity
@Table(name = FileInfoEntity._TB_NAME)
public class FileInfoEntity extends AbstractRegAuditorModel{
	private static final long serialVersionUID = 1L;

	public final static String _TB_NAME="VT_FILE";

	@Id
	@Column(name ="FILE_ID")
	private String fileId; 

	@Column(name ="FILE_DIV")
	private String fileDiv; 

	@Column(name ="FILE_CONT_ID")
	private String fileContId; 

	@Column(name ="FILE_FIELD_NAME")
	private String fileFieldName; 

	@Column(name ="FILE_NAME")
	private String fileName; 

	@Column(name ="FILE_PATH")
	private String filePath; 

	@Column(name ="FILE_EXT")
	private String fileExt; 
	
	@Column(name ="FILE_SIZE")
	private long fileSize;
	
	@Column(name ="CONT_GROUP_ID")
	private String contGroupId;

	@Builder
	public FileInfoEntity (String fileId ,String fileDiv ,String fileContId ,String fileFieldName ,String fileName ,String filePath ,String fileExt ,String regId ,String regDt ,long fileSize, String contGroupId) {
		this.fileId = fileId;
		this.fileDiv = fileDiv;
		this.fileContId = fileContId;
		this.fileFieldName = fileFieldName;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileExt = fileExt;
		this.fileSize = fileSize;
		this.contGroupId = contGroupId;
	}

	public final static String FILE_ID="fileId";
	public final static String FILE_DIV="fileDiv";
	public final static String FILE_CONT_ID="fileContId";
	public final static String FILE_FIELD_NAME="fileFieldName";
	public final static String FILE_NAME="fileName";
	public final static String FILE_PATH="filePath";
	public final static String FILE_EXT="fileExt";
	public final static String REG_ID="regId";
	public final static String REG_DT="regDt";
	public final static String FILE_SIZE="fileSize";
	
	public String getDisplaySize() {
		return FileUtils.displaySize(fileSize);
	}
}