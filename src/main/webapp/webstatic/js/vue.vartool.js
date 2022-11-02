/**
 * vue.vartool.js v0.0.1
 * ========================================================================
 */
if (typeof window != "undefined") {
    if (typeof window.VartoolAPP == "undefined") {
        window.VartoolAPP = {};
    }
}else{
	if(!VartoolAPP){
		VartoolAPP = {};
	}
}

var  portalDefaultTemplate = {
	'pageNavTemplate' : '<div class="text-center"><ul class="pagination">'
		+'<li :class="((pageInfo.preP_is !== true && pageInfo.currPage <=1)? \'disabled\' :\'\')">'
		+'	<a @click="goPage(pageInfo.currPage - 1)">«</a>'
		+'</li>'
		+'<li v-for="no in range(pageInfo.currStartPage , pageInfo.currEndPage)" :class="no ==pageInfo.currPage?\'active\':\'\'">'
		+'	<a v-if="no ==pageInfo.currPage">{{no}}</a>'
		+'	<a v-if="no != pageInfo.currPage" @click="goPage(no)">{{no}}</a>'
		+'</li>'
		+'<li :class="((pageInfo.nextPage_is !== true && pageInfo.currPage ==pageInfo.currEndPage)?\'disabled\':\'\')">'
		+'	<a @click="goPage(pageInfo.currPage + 1)">»</a>'
		+'</li>'
		+'</ul></div>'

	,'grid1Template' : '<div class="text-center"><ul class="pagination">'
		+'<li :class="((pageInfo.preP_is !== true && pageInfo.currPage <=1)? \'disabled\' :\'\')">'
		+'	<a @click="goPage(pageInfo.currPage - 1)">«</a>'
		+'</li>'
		+'<li v-for="no in range(pageInfo.currStartPage , pageInfo.currEndPage)">'
		+'	<a v-if="no ==pageInfo.currPage">{{no}}</a>'
		+'	<a v-if="no != pageInfo.currPage" @click="goPage(no)">{{no}}</a>'
		+'</li>'
		+'<li :class="((pageInfo.nextPage_is !== true && pageInfo.currPage ==pageInfo.currEndPage)?\'disabled\':\'\')">'
		+'	<a @click="goPage(pageInfo.currPage + 1)">»</a>'
		+'</li>'
	+'</ul></div>'

	,stepTemplate : '<div class="process-step-area"><div class="process-step-btn-area">'
	+ '<button type="button" class="" :class="[cssClass, (step == 1 ? \'disabled\' :\'\') ]" @click="moveHandle(\'prev\')">{{buttons.prev}}</button>'
	+ '<button type="button" :class="cssClass" v-show="(step != endStep)" @click="moveHandle(\'next\')">{{buttons.next}}</button>'
	+ '<button type="button" :class="cssClass" v-show="(step == endStep)" @click="moveHandle(\'complete\')">{{buttons.complete}}</button>'
	+ '</div></div>'
};

(function( Vue ,portalDefaultTemplate, $) {

Vue.config.devtools = true;
Vue.prototype.$ajax = VARTOOL.req.ajax;

VartoolAPP.message ={
	empty : '데이타가 없습니다.'
}

// list component add
Vue.component('list-cont', {
	created :function() {
		var templateName = this.listType+'Template';
		var templateCont = portalDefaultTemplate[templateName];

		if(typeof templateCont ==='undefined'){
			templateCont  = portalDefaultTemplate['type1Template'];
		}

		this.$options.template = templateCont;
	}
	,props: {
		list : Array,
		listType : String,
		columnKey : Object
	}
	,data:function(){
		var sortOrders = {};

		var keyInfo = Vue.util.extend({
			'TITLE' : 'TITLE'
			,'AUTHOR' : 'AUTHOR'
			,'DATE' : 'VIEW_DT'
			,price : 'price'
			,active : 'active'
			,imgSrc : 'imgSrc'
		},this.columnKey);

		return {
			keyInfo : keyInfo
		}
	}
	,methods: {
		titleClick:function(item) {
			this.$parent.detailItem = item;
			//console.log(JSON.stringify(key))
		}
	}
})

// page navigation component add
Vue.component('page-navigation', {
	template: portalDefaultTemplate.pageNavTemplate,
	props: {
		pageInfo : Object
		,callback : String
	}
	,methods: {
		range : function (start, end) {

			if(typeof start ==='undefined') return [];

			var reArr = [];

			for(start ; start <= end;start++){
				reArr.push(start);
			}

			return reArr;
		}
		,goPage : function (pageNo) {

			if(pageNo < 1){
				pageNo =1;
				return ;
			}

			if(pageNo > this.pageInfo.totalPage){
				pageNo= this.pageInfo.totalPage;
				return ;
			}

			if(this.pageInfo.currPage == pageNo){
				return ;
			}
			this.pageInfo.currPage = pageNo;


			var callback = this.$parent[this.callback];

			if(typeof callback === 'undefined'){
				callback = this.$parent['search'];
			}

			callback.call(null,pageNo);
		}
	}
})

// step button component add
Vue.component('step-button', {

	template : '<div class="process-step-area"><div class="process-step-btn-area">'
	+ '<button type="button" class="" :class="[cssClass, (step == 1 ? \'disabled\' :\'\') ]" @click="moveHandle(\'prev\')">{{btnName.prev}}</button>'
	+ '<button type="button" :class="cssClass" v-show="(step != endStep)" @click="moveHandle(\'next\')">{{(btnName[step]||btnName.next)}}</button>'
	+ '<button type="button" :class="cssClass" v-show="(step == endStep)" @click="moveHandle(\'complete\')">{{btnName.complete}}</button>'
	+ '</div></div>'

	//template: portalDefaultTemplate.stepTemplate
	, props: {
		step : {type: Number, default: 1 }
		,endStep : Number
		,buttons : {type: Object, default: {} }
		,cssClass : {type: String,	default: 'btn-md' }
		,moveStep : {type: String,	default: 'moveStep' }
		,complete : {type: String,	default: 'complete' }
	}
	,data : function (){
		return {
			btnName : VARTOOL.util.objectMerge({
				prev : VARTOOL.messageFormat('step.prev')
				,next : VARTOOL.messageFormat('step.next')
				,complete :VARTOOL.messageFormat('step.complete')
			}, this.buttons)
		};
	}
	,watch :{
		buttons : function (newval){
			this.btnName = VARTOOL.util.objectMerge({
				prev : VARTOOL.messageFormat('step.prev')
				,next : VARTOOL.messageFormat('step.next')
				,complete :VARTOOL.messageFormat('step.complete')
			}, this.buttons)
		}
	}
	,methods: {
		moveHandle : function (mode){
			var step = -9999;

			if(mode=='complete'){
				var callback = this.$parent[this.complete];

				if(typeof callback === 'undefined'){
					throw 'complete function empty  setting function name : [' + this.complete +']'
				}
				callback.call(this.$parent);
				return ;
			}

			if(mode == 'prev'){
				if(this.step > 1){
					step = this.step-1;
				}
			}else if(mode == 'next'){
				if(this.step < this.endStep){
					step = this.step+1;
				}
			}

			if(step == -9999){
				return ;
			}else{
				if(step < 1){
					step = 1;
				}else if(step >= this.endStep){
					step = this.endStep;
				}
			}

			if(this.step == step) return ;

			this.move(step, mode);
		}
		,move : function(step, mode){
			if(mode != 'prev'){
				var callback = this.$parent[this.moveStep];

				if(typeof callback === 'undefined'){
					throw 'moveStep function empty  setting function name : [' + this.moveStep +']'
				}

				var result = callback.call(this.$parent,step);
				if(result===false){
					return ;
				};
			}
			this.step = step;
			this.$emit('update:step', step);
		}
	}
})

// file upload component
Vue.component('file-upload', {

	template :
	'<div :id="id" class="file-upload-area">'
	+' <div v-if="useButton" class="file-upload-btn-area">'
	+'  <button class="btn btn-success select-file-button">'
	+'		<i class="glyphicon glyphicon-plus"></i>'
	+'		<span>{{buttons.add}}</span>'
	+'	</button>'
	+'  <button @click="uploadFile()" class="btn btn-success upload-file-button">'
	+'		<i class="glyphicon glyphicon-plus"></i>'
	+'		<span>{{buttons.upload}}</span>'
	+'	</button>'
	+'  <button @click="removeFile()" class="btn btn-success cancel-file-button">'
	+'		<i class="glyphicon glyphicon-plus"></i>'
	+'		<span>{{buttons.remove}}</span>'
	+'	</button>'
	+' </div>'
	+'<div :id="id+\'_previews\'" class="file-upload-preview">'
	+'	<div v-if="fileAddMsgViewFlag" class="file-upload-preview-msg">Drop files here or click to upload.</div>'
	+'</div>'
	+'<div v-if="isViewUploadFile">'
	+'  <div>업로드된 파일</div>'
	+'  <div class="file-upload-list">'
	+'   <template v-for="(item,index) in fileList">'
	+'	  <div class="file-upload-list-item">'
	+'	   <span v-if="options.enableCheckbox">'
	+'		<input type="checkbox" :id="item.fileId" v-model="item.isCheck"> <label :for="item.fileId">{{item.fileName}}</label>'
	+'	   </span>'
	+'	   <span v-else>'
	+'		{{item.fileName}}'
	+'	   </span>'
	+'	  </div>'
	+'   </template>'
	+'  </div>'
	+'</div>'

	//template: portalDefaultTemplate.stepTemplate
	, props: {
		id: {
			type: String,
			required: true,
			default: "fileUploadEle"
		}
		,options : {
			type: Object
			, default :{}
		}
		,accept : {
			type: String
			, default :''
		}
		,callback : {
			type: Object
			, default :{
				success : function (){}
				,successmultiple : function (){}
			}
		}
		,fileList : {
			type: Array
			, default : []
		}
		,buttons :{
			type: Object
			, default :{
				add : VARTOOL.messageFormat('file.add')
				,upload : VARTOOL.messageFormat('file.upload')
				,remove : VARTOOL.messageFormat('file.remove')
			}
		}
		,isViewUploadFile : {
			type: Boolean
			, default : false
		}
		,useButton : {
			type: Boolean
			, default : false
		}
	}
	,data : function (){

		var strHtm = [];
		strHtm.push('	<ul class="file-row">');
		strHtm.push('	  <li class="file-action-btn">');
		strHtm.push('		<button data-dz-remove class="btn btn-warning cancel">');
		strHtm.push('			<i class="glyphicon glyphicon-ban-circle"></i>');
		strHtm.push('			<span>Cancel</span>');
		strHtm.push('		</button>');
		strHtm.push('	  </li>');
		strHtm.push('	  <li class="file-info">');
		strHtm.push('		<span class="file-name text-ellipsis" data-dz-name></span> <span class="file-size" data-dz-size></span>');
		strHtm.push('	  </li>');
		strHtm.push('	  <li class="file-progress">');
		strHtm.push('		<div class="error-view-area"><strong class="error text-danger" data-dz-errormessage></strong></div>');
		strHtm.push('		<div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0">');
		strHtm.push('			<div class="progress-bar progress-bar-success" style="width:0%;" data-dz-uploadprogress></div>');
		strHtm.push('		</div>');
		strHtm.push('	  </li>');
		strHtm.push('	</ul>');

		return {
			previewTemplate : strHtm.join('')
			,fileAddMsgViewFlag : true
		}
	}
	,watch :{
		accept : function (newval){
			this.dropzone.hiddenFileInput.setAttribute("accept", this.getAcceptExtensions(newval));
			this.dropzone.options.acceptedFiles = this.getAcceptExtensions(newval);
		}
		,fileList : function (){
			this.$emit('update:fileList', this.fileList);
		}
	}
	,mounted : function() {
		var $$csrf_token = $("meta[name='_csrf']").attr("content") ||'';
		var $$csrf_header = $("meta[name='_csrf_header']").attr("content") ||'';

		var headers = {};
		headers[$$csrf_header] = $$csrf_token;

		var _this = this;
		
		var clickableSelectors = [".file-upload-preview"];
		if(this.useButton){
			clickableSelectors = [".select-file-button" ,".file-upload-preview"]	
		}

		var dropzoneOpt = VARTOOL.util.objectMerge({
			url: "http://www.vartool.com", // upload url
			thumbnailWidth: 50,
			thumbnailHeight: 50,
			parallelUploads: 20,
			uploadMultiple : true,
			maxFilesize: VARTOOL.getFileMaxUploadSize(),
			autoQueue: false,
			previewTemplate :  this.previewTemplate,
			previewsContainer: "#"+this.id+"_previews",
			headers : headers,
			clickable: clickableSelectors // select file selector
		}, this.options);
		
		if(this.accept != ''){
			dropzoneOpt.acceptedFiles = this.getAcceptExtensions(this.accept);
		}

		var dropzone = new Dropzone(this.$el, dropzoneOpt);

		dropzone.on("addedfile", function(file) {
			_this.fileAddMsgViewFlag = false;
			file.previewElement.querySelector('.file-name').title = file.name;
		});

		dropzone.on("removedfile", function(file) {
			if(dropzone.files.length < 1){
				_this.fileAddMsgViewFlag = true;
			}else{
				_this.fileAddMsgViewFlag = false;
			}
		});

		function addFileList(contextThis, items){
			for(var i =0; i < items.length; i++){
				var item = items[i];
				item.isCheck = true;
				contextThis.fileList.push(item);
			}
		}

		if(dropzoneOpt.uploadMultiple === true){

			dropzone.on('successmultiple', function(files, resp){

				if(VARTOOL.reqCheck(resp)){
					for(var i =0 ;i <files.length;i++){
						this.removeFile(files[i]);
					}

					addFileList(_this, resp.list);
					_this.callback.successmultiple (files, resp);
				}else{
					for(var i =0 ;i <files.length;i++){
						this.emit('error', files[i], resp.message);
					}
					return false;
				}
			});

			dropzone.on('completemultiple', function (file) {
				//var resData = JSON.parse(file.xhr.responseText);
				//_this.fileList = _this.fileList.concat(resData.list);
	        });
		}else{
			dropzone.on('success', function(file, resp){

				if(VARTOOL.reqCheck(resp)){
					this.removeFile(file);
					addFileList(_this, resp.list);
					_this.callback.success (file,resp);
				}else{
					this.emit('error', file, resp.message);
					return false;
				}
			});

			dropzone.on('complete', function (file) {
				//var resData = JSON.parse(file.xhr.responseText);
				//_this.fileList = _this.fileList.concat(resData.list);
			});
		}

		_this.dropzone = dropzone;
	}
	,methods: {
		uploadFile : function (){
			var _this = this;

			this.dropzone.options.params = function (){
				return _this.options.params;
			};

			this.dropzone.enqueueFiles(this.dropzone.getFilesWithStatus(Dropzone.ADDED));
		}
		,removeFile : function (){
			this.dropzone.removeAllFiles(true);
		}
		,getAcceptExtensions : function (exts){
			if(exts=='all'){
				return '*.*';
			}
			
			return '.'+ VARTOOL.str.allTrim(exts).split(',').join(',.');
		}
	}
})

/**
 * @method VartoolAPP.addTemplate
 * @description 템플릿 add
 */
VartoolAPP.addTemplate  = function (template){
	if($.isPlainObject(template)){
		for(var key in template){
			if(typeof portalDefaultTemplate[key] ==='undefined'){
				portalDefaultTemplate[key]= template[key];
			}
		}
	}
}

/**
 * @method VartoolAPP.addMessage
 * @description 메시지 add
 */
VartoolAPP.addMessage = function (msg){
	for(var key in msg){
		VartoolAPP.message[key]= msg[key];
	}
}


var defaultOpt = {
	el: '#vartoolViewArea'
	,data: {}
	,methods:{
		init: function (){}
		,search: function (no){}
	}
}
/**
 * @method addMethod
 * @description vue method 추가.
 * @param prefix
 * @param opts
 * @param methodObj
 * @returns
 */
function addMethod(prefix , opts){
	var methodObj = opts[prefix];

	if(typeof methodObj !=='undefined'){
		for(var key in methodObj){
			opts.methods[prefix+'_'+key] = methodObj[key];
		}
		delete opts[prefix];
	}
	return opts;
}

function getAcceptExtensions (exts){
	if(exts=='all'){
		return '*.*';
	}
	
	return '.'+ VARTOOL.str.allTrim(exts).split(',').join(',.');
}

VartoolAPP.vueServiceBean = function (opts){
	
	var initOpt = {};
	
	opts = VARTOOL.util.objectMerge(initOpt,defaultOpt,opts);

	if(opts.validateCheck ===true){
		Vue.use(VeeValidate)
	}

	var vueObj = new Vue(opts);

	$(vueObj.$el).removeClass('display-off')

	if(VARTOOL.isFunction(vueObj.init)){
		vueObj.init();
	}
	
	if(VARTOOL.isFunction(vueObj.search)){
		vueObj.search(1);
	}

	return vueObj;
}
})(Vue , portalDefaultTemplate, jQuery);