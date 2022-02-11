/*
**
*ytkim
*varsql ui js
 */
if (typeof window != "undefined") {
    if (typeof window.VARTOOLUI == "undefined") {
        window.VARTOOLUI = {};
    }
}else{
	if(!VARTOOLUI){
		VARTOOLUI = {};
	}
}

(function(VARTOOL, $) {
'use strict';

var _$base = {
	_version:'0.1'
	,author:'ytkim'
};

var defaultPopupPosition = {
	align : 'top'
	,topMargin : 10
	,top:2
	,left:2
	,ieDualCenter : false
	,browser : {
		msie : 40
		,mozilla : 70
		,chrome :70
		,'default' : 70
		,safari : 70
	}
}

/**
 * dialog
 */
_$base.dialog = {
	open : function (selector ,opt){
		return $(selector).dialog(opt);
	}
}

/**
 * confirm 창
 */
_$base.confirm = {
	template : function (opt){
		var strHtm = [];
		strHtm.push('<div class="confirm-area">');
		strHtm.push('	<div class="message-area">');
		strHtm.push('		<div>#message#</div>');
		strHtm.push('	</div>');
		strHtm.push('	<div class="button-area">');
		strHtm.push('		<div>#button#</div>');
		strHtm.push('	</div>');
		strHtm.push('</div>');
		return strHtm.join('');
	}
	,open : function (selector ,opt){
		return $(selector).dialog(opt);
	}
}

/**
 * alert 창
 */
_$base.alert = {
	template : function (opt){

	}
	,open : function (opt){

		var msg = opt;
		if(VARTOOL.isObject(opt) && !VARTOOL.isUndefined(opt.key)){
			msg = VARTOOL.messageFormat(opt.key, opt);
		}

		return alert(msg);
	}
}

_$base.toast = {
	options : {
		info :  {
			icon : 'info'
			, bgColor: '#7399e6'
		}
		,error : {
			icon : 'error'
			, bgColor: '#ee8777'
		}
		,warning :{
			icon : 'warning'
			, bgColor: '#d9b36c'
		}
		,success:{
			icon : 'success'
			, bgColor: '#7399e6'
		}
		,alert : {
			icon: 'warning'
			, bgColor: '#7399e6'
		}
		,text :{
			bgColor: '#7399e6'
		}
	}
	/**
	 * @method _$base.dialog.view
	 * @param opt {Object} toast option
	 * @description toast view
	 */
	,open :function (option){

		if(typeof option !=='object'){
			option = {text: option};
		}

		var opt = this.options[option.icon];
		opt = opt?opt : this.options['info'];

		// 기본 옵션 셋팅
		var setOpt = $.extend({}, {
			 hideAfter: 2000
			, loader :false
			, position: {left:"50%",top:"50%"}
			, textColor: '#fff'
			, stack:false
			, showHideTransition: 'fade'
			//, beforeShow : function(){$('.jq-toast-wrap').css("margin" , "0 0 0 -155px") }
		},opt);

		var tmpP = window ;

		if(parent){
			if(typeof tmpP.$ === 'undefined' && typeof tmpP.$.toast === 'undefined' ){
				tmpP = parent;
			}
		}

		tmpP.$.toast($.extend(true,setOpt, option));
	}
}

_$base.popup = {
	open : function (url, opt){
		opt = opt ||{};
		var targetId = VARTOOL.generateUUID()
			, tmpParam = opt.param?opt.param:{}
			, tmpMethod = opt.method ? opt.method : 'get'
			, tmpIsNewYn = opt.isNewYn=='Y' ? 'Y' : 'N'
			, tmpPopOption = opt.viewOption?opt.viewOption:''
			, tmpPosition =  $.extend({},defaultPopupPosition,( $.isPlainObject(opt.position)?opt.position:{align:opt.position} ))
			, tmpName = (opt.name?( escape(opt.name).replace(/[ \{\}\[\]\/?.,;:|\)*~`!^\-+┼<>@\#$%&\'\"\\(\=]/gi,'') ):targetId.replace(/-/g,''));

		var urlIdx = url.indexOf('?');
		var openUrl = urlIdx > -1 ?url.substring(0,urlIdx):url;

		tmpPopOption = tmpPopOption.replace(/\s/gi,'');

		if(tmpPopOption != ''){

			var popupOptArr = tmpPopOption.split(',');
			var tmpItem ,_t=0 , _l=0 ,_w=1050, _h=0, addFlag ,tmpOpt='',addScrollbarOpt = true, addStatusOpt=false, addResizeableOpt=true;

			for(var i = 0 ;i < popupOptArr.length; i++){
				tmpItem = popupOptArr[i];
				addFlag = true;
				if(tmpItem.toLowerCase().indexOf('width=')==0){
					_w= tmpItem.replace(/[^0-9]/g,'');
					addFlag = false;
				}
				if(tmpItem.toLowerCase().indexOf('height=')==0){
					_h= tmpItem.replace(/[^0-9]/g,'');
					addFlag = false;
				}
				if(tmpItem.toLowerCase().indexOf('top=')==0){
					_t= tmpItem.replace(/[^0-9]/g,'');
					addFlag = false;
				}
				if(tmpItem.toLowerCase().indexOf('left=')==0){
					_l= tmpItem.replace(/[^0-9]/g,'');
					addFlag = false;
				}
				if(tmpItem.toLowerCase().indexOf('scrollbars=')==0){
					addScrollbarOpt= false;
				}

				if(tmpItem.toLowerCase().indexOf('resizable=')==0){
					addResizeableOpt= false;
				}

				tmpOpt += (addFlag ? ( (tmpOpt==''?'':',') + tmpItem ) : '');
			}

			tmpOpt += addScrollbarOpt?',scrollbars=yes':'';
			tmpOpt += addResizeableOpt?',resizable=yes':'';

			if(typeof opt.position=='undefined' && _h !=0 ){
				tmpPosition.align='center';
			}
			var tmpTopMargin = tmpPosition.topMargin
				,tmpLeftMargin = tmpPosition.leftMargin;

			var heightMargin = tmpPosition.browser['default'];
			_h= ( _h==0 ? (screen.availHeight-heightMargin- tmpTopMargin) :_h);

			_h =addStatusOpt?_h-23:_h;
			var _viewPosition = {};

			if(tmpPosition.align=='top'){

				_viewPosition = popupPosition(_w,_h, tmpPosition.top, tmpPosition.left, tmpName , tmpPosition.ieDualCenter);

				_viewPosition.top = typeof screen['availTop']!=='undefined' ?screen['availTop'] : (window.screenTop || screen.top);
				if(_t!=0){
					_viewPosition.top = (_viewPosition.top > 0? _viewPosition.top- _t : _viewPosition.top+_t);
				}else{
					_viewPosition.top = (_viewPosition.top > 0? _viewPosition.top- tmpTopMargin : _viewPosition.top+tmpTopMargin);
				}

			}else if(tmpPosition.align=='left'){
				_viewPosition = popupPosition(_w,_h, tmpPosition.top, tmpPosition.left, tmpName , tmpPosition.ieDualCenter);
				_viewPosition.left =0;
			}else if(tmpPosition.align=='right'){
				_viewPosition = popupPosition(_w,_h, tmpPosition.top, tmpPosition.left, tmpName , tmpPosition.ieDualCenter);
				_viewPosition.left = window.screen.availWidth-_w;
			}else if(tmpPosition.align=='bottom'){
				_viewPosition = popupPosition(_w,_h, tmpPosition.top, tmpPosition.left, tmpName , tmpPosition.ieDualCenter);
				_viewPosition.top = window.screen.availHeight-_h;
			}else{
				_viewPosition = popupPosition(_w,_h, tmpPosition.top, tmpPosition.left, tmpName , tmpPosition.ieDualCenter);
			}

			_viewPosition.top = isNaN(tmpTopMargin)?_viewPosition.top:_viewPosition.top+tmpTopMargin;
			_viewPosition.left = isNaN(tmpLeftMargin)?_viewPosition.left:_viewPosition.left+tmpLeftMargin

			tmpPopOption = tmpOpt+', width=' + _w + 'px, height=' + _h + 'px, top=' + _viewPosition.top + 'px, left=' + _viewPosition.left+'px';
		}
		tmpParam=VARTOOL.util.getParameter(url , tmpParam);

		var winObj;
		if(tmpIsNewYn=='N'){
			winObj = window.open('', tmpName, tmpPopOption);

			if(winObj && winObj.VARTOOL){
				winObj.focus();
				return winObj;
			}

			if(openUrl ==''){
				return winObj;
			}
		}

		// get method
		if('get' ==tmpMethod){

			tmpParam=VARTOOL.util.paramToArray(tmpParam);

			if(tmpParam.length > 0)	openUrl =openUrl+'?'+tmpParam.join('&');

			return window.open(openUrl, tmpName, tmpPopOption);
		}else{  // post method
			var inputStr = [];
			inputStr.push('<!doctype html><head>');
			inputStr.push('<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /><meta charset="UTF-8" /></head>');
			inputStr.push('<form action="'+openUrl+'" method="post" id="'+targetId+'" name="'+targetId+'">');

			var tmpVal;
			for(var key in tmpParam){
				tmpVal = tmpParam[key];

				inputStr.push('<input type="hidden" name="'+key+'" value=\''+((typeof tmpVal==='string')?tmpVal:JSON.stringify(tmpVal))+'\'/>');
			}
			inputStr.push('</form>');
			inputStr.push('<script async type="text/javascript">try{document.charset="utf-8";}catch(e){}document.'+targetId+'.submit();</'+'script>');

			var tmpPopupObj=window.open('about:blank', tmpName, tmpPopOption);

			try{
				try{tmpPopupObj.document.open();}catch(e){console.log(e)}
				tmpPopupObj.document.write(inputStr.join(''));
				tmpPopupObj.focus();
				try{tmpPopupObj.document.close();}catch(e){console.log(e)}
			}catch(e){
				tmpPopupObj=window.open('about:blank', tmpName+targetId, tmpPopOption);
				try{
					try{tmpPopupObj.document.open();}catch(e){console.log(e)}
					tmpPopupObj.document.write(inputStr.join(''));
					tmpPopupObj.focus();

					try{tmpPopupObj.document.close();}catch(e){console.log(e)}
				}catch(e1){
					console.log(e1);
				}
			}

			return tmpPopupObj;
		}
	}
}

function popupPosition(_w,_h , tr , lr, tmpName ,ieDualCenter){
	_h=  parseInt(_h,10);
	_w = parseInt(_w,10);
	tr = parseInt(tr,10);
	lr = parseInt(lr,10);
	tr =tr? tr :2;
	lr =lr? lr :2;

	var dualScreenLeft = window.screenX || window.screenLeft || screen.left
		,dualScreenTop = window.screenY || window.screenTop || screen.top
		, width = window.innerWidth || document.documentElement.clientWidth || screen.width
		, height = window.innerHeight || document.documentElement.clientHeight || screen.height;

	var ua = window.navigator.userAgent;
	var old_ie= ua.indexOf('MSIE');
	var new_ie= ua.indexOf('Trident/');
	var _top = 0 , _left = 0 ;

	if(old_ie >-1 || new_ie >-1){
		if(ieDualCenter){

			var dualScreenLeft = window.screenLeft || screen.left
				,dualScreenTop = window.screenTop || screen.top
				,width = window.innerWidth || document.documentElement.clientWidth || screen.width
				,height = window.innerHeight || document.documentElement.clientHeight || screen.height;

			_left = ((width / 2) - (_w / 2)) + dualScreenLeft;
			_top = ((height / 2) - (_h / 2)) + dualScreenTop;
		}else{
			width=window.screen.availWidth;
			height=window.screen.availHeight;
			dualScreenTop= 0 ;
			dualScreenLeft =0 ;
			_top = ((height / tr) - (_h / tr))+ dualScreenTop;
			_top = _top < 0 ? 0 :_top;
			_left = ((width / lr) - (_w / lr)) + dualScreenLeft;
		}
	}else{
		width=window.screen.availWidth;
		height=window.screen.availHeight;
		_top = ((height / tr) - (_h / tr))+ window.screen.availTop;
		_left = ((width / lr) - (_w / lr))+window.screen.availLeft;
	}

	return {
		top : _top
		,left :  _left
	}
}



var fileComponentBtnIdx = 0;
function FileComponent(opt){
	this.init(opt);
	return this; 
}

function getAcceptExtensions (exts){
	if(exts=='all'){
		return '*.*';
	}
	
	return '.'+ VARTOOL.str.allTrim(exts).split(',').join(',.');
}

FileComponent.prototype = {
	_$fileObj : {}
	,opts : {}
	,btnClassName : ''
	,defaultParams : {}
	,init : function(opt){
		var _this =this; 

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

		opt = VARTOOL.util.objectMerge({
			mode : 'basic' // basic, template
			,extensions : ''
			,files : []
			,successAfterReset : true
			,callback : {
				fail : function (file, resp){}
				,complete : function (file ,resp){}
				,addFile : function (file){}
				,removeFile : function (file){}
			}
			,options : {}
		}, opt);

		var dropzoneOpt = VARTOOL.util.objectMerge({
			url: "http://www.varsql.com", // upload url
			thumbnailWidth : 50,
			thumbnailHeight : 50,
			parallelUploads : 20,
			uploadMultiple : true,
			maxFilesize : VARTOOL.getFileMaxUploadSize(),
			headers : VARTOOL.req.getCsrf(),
			autoQueue : false,
			previewTemplate :  opt.previewTemplate||strHtm.join(''),
			clickable : false
		}, opt.options);

		_this.defaultParams = dropzoneOpt.params;

		if(opt.extensions != ''){
			dropzoneOpt.acceptedFiles = getAcceptExtensions(opt.extensions);
		}

		if(opt.mode=='template'){
			
		}else{
			var btnClass = 'b-'+VARTOOL.generateUUID();
			this._btnClear(opt);
			var btnHtml = '<div class="file-add-btn-area wrapper-'+btnClass+'">첨부할 파일을 끌어다 놓거나<a href="javascript:;" class="file-add-btn '+btnClass+'">파일찾기</a>버튼을 클릭하세요</div>'; 
			if(opt.btn == 'top'){
				$(opt.el).before(btnHtml);
			}else{
				$(opt.el).after(btnHtml);
			}
			
			if(dropzoneOpt.clickable === false){
				dropzoneOpt.clickable = '.'+btnClass;
			}else if(VARTOOL.isArray(dropzoneOpt.clickable)){
				dropzoneOpt.clickable.push('.'+btnClass);
			}

			this.btnClassName = 'wrapper-'+btnClass;
		}
		this.opts = opt;

		var dropzone = new Dropzone(opt.el, dropzoneOpt);

		dropzone.on("addedfile", function(file) {
			opt.callback.addFile(file);
			file.previewElement.querySelector('.file-name').title = file.name;
		});

		dropzone.on("removedfile", function(file) {
			opt.callback.removeFile(file);
		});

		dropzone.on("maxfilesexceeded", function(file) {
			this.removeFile(file);
		});

		if(dropzoneOpt.uploadMultiple === true){
			dropzone.on('successmultiple', function(file, resp){
				return _this._fileUploadSuccess(opt, file, resp);
			});
			dropzone.on('errormultiple', function(file, resp){
				opt.callback.fail(file);
			});
		}else{
			dropzone.on('success', function(file, resp){
				return _this._fileUploadSuccess(opt, file, resp);
			});

			dropzone.on('error', function(file, resp){
				opt.callback.fail(file);
			});
		}

		if(VARTOOL.isArray(opt.files)){
			var fileLen = opt.files.length; 
			for(var i =0 ;i < fileLen; i++){
				var fileItem = opt.files[i];
				fileItem.accepted = true;
				dropzone.emit('addedfile', fileItem);
				dropzone.emit('thumbnail', fileItem);
				dropzone.emit('complete', fileItem);
				dropzone.files.push(fileItem);
			}
		};
		this._$fileObj = dropzone
		return this; 
	}
	,_getFileObj : function (){
		return this._$fileObj;
	}
	,_getFiles : function (){
		return this._$fileObj.files;
	}
	,_setExtensions : function (exts){
		this._$fileObj.hiddenFileInput.setAttribute("accept", getAcceptExtensions(exts));
		this._$fileObj.options.acceptedFiles = getAcceptExtensions(exts);
	}
	,_clearFiles : function (){
		this._$fileObj.removeAllFiles(true);
	}
	,_fileUploadSuccess : function (opt, file, resp){
		if(VARTOOL.reqCheck(resp)){
			if(opt.successAfterReset === true){
				this._clearFiles(true);
			}
			this._$fileObj.removeFile(file);
			opt.callback.complete(file, resp);
		}else{
			this._$fileObj.emit('error', file, resp.message);
		}
		return false;
	}
	,getRejectedFiles : function (){
		return this._$fileObj.getRejectedFiles();
	}
	,save : function (param, btnClear){
		var _this = this; 
		var dropzoneObj = this._$fileObj; 
		dropzoneObj.options.params = function (){
			return VARTOOL.util.objectMerge({},_this.defaultParams,param);
		}; 

		var addFiles = dropzoneObj.getFilesWithStatus(Dropzone.ADDED); 
		
		if(addFiles.length > 0){
			dropzoneObj.enqueueFiles(addFiles);
			//dropzoneObj.processQueue();
		}else{
			var blob = new Blob();
			blob.upload = {'chunked' : dropzoneObj.defaultOptions.chunking};
			dropzoneObj.uploadFile(blob);
		}

		if(btnClear===true){
			this._btnClear(this.opts);
		}
	}
	,emptyFileSave : function(param, btnClear){
		var _this = this; 
		var dropzoneObj = this._$fileObj; 
		dropzoneObj.options.params = function (){
			return VARTOOL.util.objectMerge({},_this.defaultParams, param);
		}
		var blob = new Blob();
		blob.upload = {'chunked' : dropzoneObj.defaultOptions.chunking};
		dropzoneObj.uploadFile(blob);

		if(btnClear===true){
			this._clear();
		}
	}
	,_btnClear : function (opt){
		if(this.btnClassName){
			$('.'+this.btnClassName).remove();
		}
	}
	,destroy :function (){
		this._btnClear();
		this._$fileObj.files = [];
		this._$fileObj.destroy();
	}
}

_$base.file = {
	allObj : {}
	,create : function (el, opt, isNew){
		if(isNew === true && typeof this.allObj[el] !== 'undefined'){
			
			this.allObj[el].destroy();
			
			delete this.allObj[el]; 
		}

		if(typeof this.allObj[el] === 'undefined'){
			if(typeof opt['el'] === 'undefined'){
				opt['el'] = el; 
			}
			this.allObj[el] = new FileComponent(opt);
		}

		return this.allObj[el]; 
	}
	,forElement : function (el){
		return this.allObj[el]; 
	}
};

window.VARTOOLUI = _$base;
})(VARTOOL, jQuery);