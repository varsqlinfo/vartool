<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="display-off" id="vueArea">
	<h1>
		작성
	</h1>
	<div class="pull-right">
		<a @click="save()" class="btn btn-default">저장</a>
		<a @click="cancel()" class="btn btn-default">취소</a>
	</div>
	<div style="clear:both;padding-top: 15px;"></div>
	<div class="col-lg-12">
		<form id="writeForm" name="writeForm" role="form" class="form-horizontal bv-form">
			<div class="form-group">
				<input v-model="articleInfo.title" placeholder="제목" class="form-control">
			</div>
			<div class="form-group">
				<label for="noticeId"><input type="checkbox" id="noticeId" v-model="articleInfo.noticeYn" true-value="Y" false-value="N">공지사항</label>
			</div>
			<div class="form-group">
				<textarea id="fileDropArea" v-model="articleInfo.contents" rows="5" style="width:100%"></textarea>
			</div>
		</form>
	</div>
	
	<div id="fileUploadPreview" class="file-upload-area"></div>
</div>
 
<script>

VartoolAPP.vueServiceBean({
	el: '#vueArea'
	,data: {
		list_count :10
		,searchVal : ''
		,articleInfo : VARTOOL.util.objectMerge({
				title : ''
				,contents : ''
				,noticeYn : 'N'
				,removeFiles:[]
				,fileList : []
			},${articleInfo}
		)
		,fileUploadObj : {}
	}
	,methods:{
		init : function (){
			var _this =this; 
			
			var files = [];
			this.articleInfo.fileList.forEach(function (item){
				files.push({
					name : item.fileName
					,size : item.fileSize
					,fileId : item.fileId
				})
			})
			
			this.fileUploadObj = VARTOOLUI.file.create('#fileDropArea',{
				files: files
				,options : {
					url : '<vartool:boardUrl addUrl="save"/>'
					,params : {
						div : 'board'
						, fileExtensions : ''
						, contGroupId : '<vartool:boardCode/>'
					}
					,previewsContainer :'#fileUploadPreview'
				}
				,callback : {
					complete : function (file, resp){
						_this.listPage();
					}
					,removeFile : function (file){
						_this.articleInfo.removeFiles.push(file.fileId);
					}
				}
			});
			
		}
		,save : function(){
			
			var saveInfo = VARTOOL.util.objectMerge({}, this.articleInfo);
			
			if(VARTOOL.isBlank(saveInfo.title)){
				VARTOOLUI.toast.open({text : '제목을 입력 하세요.'});
				return ; 
			}
			
			var fileUploadObj = this.fileUploadObj; 
			
			var rejectedFiles = fileUploadObj.getRejectedFiles();
			
			if(rejectedFiles.length > 0){
				VARTOOLUI.toast.open({text : 'rejected files : '+JSON.stringify(rejectedFiles)});
				return ; 
			}
			
			saveInfo.removeFileIds = saveInfo.removeFiles.join(',');
			
			fileUploadObj.save(saveInfo);
		}
		,cancel :function (){
			if(VARTOOL.isBlank(this.articleInfo.articleId)){
				location.href='<vartool:boardUrl />';
			}else{
				location.href='<vartool:boardUrl addUrl="view"/>?articleId='+this.articleInfo.articleId;  
			}
		}
		,listPage : function(){
			location.href='<vartool:boardUrl />';
		}
	}
});


</script>