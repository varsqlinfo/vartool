<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="display-off" id="vueArea">
	<h1>
		<spring:message code="writing" text="글쓰기"/>
	</h1>
	<div class="pull-right">
		<a @click="save()" class="btn btn-success"><spring:message code="save" text="저장"/></a>
		<a @click="cancel()" class="btn btn-default"><spring:message code="cancel" text="취소"/></a>
	</div>
	<div style="clear:both;padding-top: 15px;"></div>
	<div class="col-lg-12">
		<form id="writeForm" name="writeForm" role="form" class="form-horizontal bv-form">
			<div class="form-group">
				<input type="text" ref="titleInput" v-model="articleInfo.title" placeholder="제목" class="form-control">
			</div>
			<div class="form-group">
				<label for="noticeId"><input type="checkbox" id="noticeId" v-model="articleInfo.noticeYn" true-value="Y" false-value="N">공지사항</label>
			</div>
			<div id="articleWrapper">
				<div id="fileUploadPreview" class="file-upload-area"></div>
				
				<div class="form-group">
					<div id="articleContent" style="width:100%;margin-top:10px;"></div>
				</div>
			</div>
		</form>
	</div>
	
</div>
 
<script>

VARTOOL.loadResource(['fileupload',"toast.editor"]);

VartoolAPP.vueServiceBean({
	el: '#vueArea'
	,data: {
		param :{
			boardCode : '<c:out value="${param.boardCode}"/>'
			,articleId : '<c:out value="${param.articleId}"/>'
		},
		articleInfo : {
			title : ''
			,contents : ''
			,noticeYn : 'N'
			,removeFiles:[]
			,fileList : []
		}
		,fileUploadObj : {}
		,editor : false
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
			});
			
			this.$nextTick(() => {
				this.$refs.titleInput.focus();
			});
			
			setTimeout(()=>{
				this.editor = new toastui.Editor({
		            el: document.querySelector('#articleContent'),
		            height: '500px',                        
		            initialEditType: 'markdown',
		            autofocus:false,
		            initialValue: '',
		            linkAttributes:{target:"_blank"},
		            //previewStyle: 'vertical'
		            plugins: [
	                    [toastui.Editor.plugin.chart],
	                    toastui.Editor.plugin.tableMergedCell,
	                    toastui.Editor.plugin.colorSyntax,
	                    [
	                        toastui.Editor.plugin.uml,
	                        { rendererURL: "http://www.plantuml.com/plantuml/svg/" }
	                    ]
	                ],
		            hooks: {
		                async addImageBlobHook(blob, callback) { // 이미지 업로드 로직 커스텀
		                    try {
		                        const formData = new FormData();
		                        formData.append('image', blob);

		                        const response = await fetch(VARTOOL.url('/file/imageUpload'), {
		                            method : 'POST',
		                            body : formData,
		                        });

		                        const responseJson = await response.json();
		                        const fileInfo = responseJson.item;
		                        const fileContId = fileInfo.fileContId;
		                        
		                        // 4. addImageBlobHook의 callback 함수를 통해, 디스크에 저장된 이미지를 에디터에 추가
		                        const imageUrl = VARTOOL.url( '/imageView/'+fileContId);
		                        callback(imageUrl, fileInfo.fileName || 'image');

		                    } catch (error) {
		                        console.error('업로드 실패 : ', error);
		                    }
		                }
		            }
		        });
				
				if(!VARTOOL.isBlank(this.param.articleId)){
					
					this.$ajax({
						url: '<vartool:boardUrl addUrl="contents" contextPath="false"/>'
						,data: {
							'articleId' : this.param.articleId
							,boardCode : this.param.boardCode
						}
						,success: (resData) => {
							this.articleInfo =  VARTOOL.util.objectMerge(this.articleInfo, resData.item);
							this.editor.setMarkdown(resData.item.contents)
						}
					})
				}
				
			},100);
			
			this.fileUploadObj = VARTOOLUI.file.create('#articleWrapper',{
				files: files
				,btn:'top'
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
			
			var contents = this.editor.getMarkdown(); 
			if (contents.length < 1) {
				VARTOOLUI.toast.open({text : '내용을 입력 하세요.'});
			    return ; 
			}
			
			saveInfo.contents = contents;
			
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