/**
 * pubLogViewer v0.0.1
 * ========================================================================
 * Copyright 2019-2021 ytkim
 * Licensed under MIT
 * http://www.opensource.org/licenses/mit-license.php
 * url : https://github.com/ytechinfo/pub
 * demo : http://pub.moaview.com/
*/

;(function($, window, document) {
"use strict";
/**
af :  add function
ap  : add parameter
*/
var _initialized = false
,_$doc = $(document)
,_datastore = {}
,_defaults = {
	theme : 'light'
	,height: 'auto'
	,width: 'auto'
	,logFileName : 'logFileName'
	,itemMaxCount : -1	// add시 item max로 유지할 카운트
	,rowOptions:{	// 로우 옵션. 
		height: 19	// cell 높이
		,paddingPixel : 4 // row padding
	}
	,logPattern :'%d{yyyy-MM-dd HH:mm:ss} %level %c - %msg%n'
	,contextMenu : false // row(tr) contextmenu event
	,headerOptions : {
		view : true	// header 보기 여부
		,height: 30
	}
	,autoResize : {	//리사이즈 설정
		enabled : false	// 리사이즈시 그리드 리사이즈 여부.
		,responsive : true 
		,threshold :150
	}
	,scroll :{	// 스크롤 옵션
		isPreventDefault : true	// 이벤트 전파 여부.
		,topMargin : 5 			// 스크롤 이동시 container top margin
		,vertical : {
			width : 14			// 세로 스크롤 
			,bgDelay : 100		// 스크롤 빈공간 mousedown delay
			,btnDelay : 100		// 방향키 mousedown delay
			,dragDelay : 5		// 스크롤 bar drag delay
			,speed : 3			// 스크롤 스피드 row 1
			,onUpdate : function (item){	// 스크롤 업데이트. 
				return true; 
			}
			,tooltip : false		// item count tooltip
		}
		,horizontal :{
			height: 14			// 가로 스크롤 높이
			,bgDelay : 100		
			,btnDelay : 100		// 방향키 버튼 속도.
			,dragDelay : 7		// 스크롤 bar drag delay
			,speed : 1			// 스크롤 스피드
		}
	}
	,status : {
		enabled :false
		,height : 17
		,countEnabled : true
	}
	,setting : {
		lines : [1,2,3,4,5,10,15,20]
		,saveStateKey : ''
		,searchFilter : function (logText, searchVal){
			return searchVal.test(logText);
		}
		,filter :{
			text: ''
			,line : 0
		}
	}
	,i18n :{
		'setting.label' : '설정'
		,'search.button' : '찾기'
		,'search.prev' : '이전'
		,'search.stop' : 'stop'
		,'search.start' : 'start'
		,'string.not.found' : '문자열을 찾을수 없습니다.'
		,'setting.apply': 'Apply'
		,'setting.clear': 'Clear'
		,'setting.more.view': '더보기 라인'
	}
};

var hasOwnProperty = Object.prototype.hasOwnProperty;

function hasProperty(obj , key){
	return hasOwnProperty.call(obj, key);
}

function isUndefined(obj){
	return typeof obj==='undefined';
}
function isFunction(obj){
	return typeof obj==='function';
}

function intValue(val){
	return parseInt(val , 10);
}

var specialCaseWordChar = /[\"\s`~!#$%^&*\.()\[\]+|<>\\\'?,;={}]/; // 특수문자

function isWordChar(ch) {
  return !specialCaseWordChar.test(ch);
}

function escapeHtml(str){
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/\"/g, "&quot;");
};

function searchMatchText (txt , re){
	return txt;
	/*
	if(txt) {
		return txt.replace(re , ';publog-highlight-start;$1;publog-highlight-end;');
	}else{
		return txt;
	}
	*/
}
// left 값 상대적인 비율 값 계산.
function getLeftRatioValue (leftVal, chkStrW, newLineW){
	return newLineW * (leftVal/ chkStrW *100) /100;
}

function getMeasureTextWidth(ctx, str){
	if(true){
		return ctx.element.measureEl.text(str).width();
	}else{
		ctx.config.canvasContext.clearRect(0,0,ctx.config.canvasContext.width, ctx.config.canvasContext.height);
		var _w = ctx.config.canvasContext.measureText(str).width; 

		// tab 사이즈 때문에 보류.
		return _w;
	}
}

function getCharIdx(ctx, leftPos, logText, eleW){
	var leftPerVal = (leftPos/eleW*100);
	var contW = getMeasureTextWidth(ctx, logText);
	var hidLeftPos = contW * leftPerVal /100;

	var result = getContPosition(ctx, hidLeftPos, logText, contW);

	return result;
}

function getContPosition(ctx, leftPos, logText, contW){

	var len = logText.length;

	if(leftPos >= contW){
		return len-1; 
	}

	if(len <= 1){
		return 0;
	}

	if(len <= 3){
		var totVal= 0;
		for(var i=0; i <3; i++){
			totVal += getMeasureTextWidth(ctx, logText.charAt(i));	

			if(leftPos <= totVal){
				return leftPos*2 > totVal ? i+1:0; 
			}
		}
		return 2; 
	}

	var halfContLen= Math.ceil(len/2);
	var halfW = getMeasureTextWidth(ctx, logText.substring(0,halfContLen));

	if(leftPos > halfW){
		var overHalfStr = logText.substring(halfContLen); 
		return halfContLen+getContPosition(ctx, leftPos-halfW, overHalfStr, getMeasureTextWidth(ctx, overHalfStr));
	}else{
		return getContPosition(ctx, leftPos, logText.substring(0, halfContLen), halfW);
	}
}

function getHashCode (str){
	var hash = 0;
	if (str.length == 0) return hash;
	for (var i = 0; i < str.length; i++) {
		var tmpChar = str.charCodeAt(i);
		hash = ((hash<<5)-hash)+tmpChar;
		hash = hash & hash; 
	}
	return ''+hash+'99';
}
function copyStringToClipboard (prefix , copyText) {
	var isRTL = document.documentElement.getAttribute('dir') == 'rtl';

	if (!isUndefined(window.clipboardData) && !isUndefined(window.clipboardData.setData)) {
		window.clipboardData.setData("Text", copyText);
		return ; 
	}

	var _id = prefix+'_pubLogViewerCopyArea'; 
	var copyArea = document.getElementById(_id); 
	
	copyArea.value = copyText;
	copyArea.select();
	
	function handler (event){
		document.removeEventListener('copy', handler);
		copyArea = null;
	}
	document.addEventListener('copy', handler);

	document.execCommand('copy');
}
// file download
function fileDownload (fileName , content){
	var blob = new Blob([content] ,{type:"text/plain;charset=utf-8"})
	saveAs(blob , fileName);
}

function Plugin(element, options) {
	this._initialize(element, options);
	return this; 
}

function $pubSelector(selector){
	return document.querySelector(selector);
}

function $pubAllSelector(selector){
	return document.querySelectorAll(selector);
}

function objMergeRecursive(dst, src) {
			
	for (var p in src) {
		if (!src.hasOwnProperty(p)) {continue;}
		
		var srcItem = src[p] ;
		if (srcItem=== undefined) {continue;}
		
		if ( typeof srcItem!== 'object' || srcItem=== null) {
			dst[p] = srcItem;
		} else if (typeof dst[p]!=='object' || dst[p] === null) {
			dst[p] = objMergeRecursive(srcItem.constructor===Array ? [] : {}, srcItem);
		} else {
			objMergeRecursive(dst[p], srcItem);
		}
	}
	return dst;
}

function objectMerge() {
	var reval = arguments[0];
	if (typeof reval !== 'object' || reval === null) {	return reval;}
	for (var i = 1, il = arguments.length; i < il; i++) {
		objMergeRecursive(reval, arguments[i]);
	}

	return reval; 
}

function getViewLogTemplate(log, tokenList){
	
	log=(log||'');
	log = escapeHtml(log);
	if(tokenList == null || tokenList.length < 1){
		return replaceSpaceToNbsp(log);
	}

	return getLogTemplate(tokenList, log);
}

function allMatchWord(logText, schRegExp, matchType){
	if(logText && schRegExp){
		var matchInfos = [];

		var matchInfo;
		var findFlag = false; 
		while((matchInfo = schRegExp.exec(logText)) != null){
			matchInfos.push({
				matchword : matchInfo[0]
				,matchChIdx : matchInfo.index
				,matchType : matchType
			})
			findFlag = true; 
		}
		return findFlag ? matchInfos : false;
	}
	
	return false; 
}

function matchWord(idx, logText, schRegExp, startRowIdx, startChIdx, schMode){
	var substringFlag = false; 
	if(idx == startRowIdx  && startChIdx > 0){
		substringFlag = true; 
		if(schMode=='prev'){
			logText = logText.substring(0 ,startChIdx);
		}else{
			logText = logText.substring(startChIdx ,logText.length);
		}
	}

	var matchInfo = logText.match(schRegExp); 

	if(matchInfo != null){
		var matchCharIdx; 
		if(substringFlag){
			if(schMode=='prev'){
				matchCharIdx = matchInfo.index;
			}else{
				matchCharIdx = startChIdx + matchInfo.index;
			}
			
		}else{
			matchCharIdx = matchInfo.index;
		}	
		return {
			searchIdx : idx
			,matchChIdx : matchCharIdx
		};
	}
	return false; 
}

function cursorHide(thisObj, ele){
	
	clearTimeout(thisObj.config.cursorTimer);
	thisObj.config.cursorTimer = null; 
	thisObj.config.cursorShow = false;
	thisObj.config.cursorView = false;
	ele.hide();
}

function replaceSpaceToNbsp(log){
	return log.replace(/\s/g,'&nbsp;');
}

var tokenParser ={
	d : function (log, token, nextToken){
		var endIdx = token.formatLen + nextToken.delimiterLen; 
		var matchCont = log.substring(0, token.formatLen);

		if(isNaN(Date.parse(matchCont))){
			return false; 
		}

		return {
			cont : '<span class="log0">'+ replaceSpaceToNbsp(matchCont)+'</span>'+ replaceSpaceToNbsp(nextToken.delimiter)
			,log : log.substring(endIdx)
		}
	}
	,c : function (log, token, nextToken){
		var findIdx = log.indexOf(nextToken.delimiter); 
		
		var endIdx = findIdx+ nextToken.delimiterLen; 
		var matchCont = log.substring(0, findIdx);

		return {
			cont : '<span class="log7">'+ replaceSpaceToNbsp(matchCont)+'</span>'+ replaceSpaceToNbsp(nextToken.delimiter)
			,log : log.substring(endIdx)
		}
	}
	,level: function (log, token, nextToken){
		var findIdx = log.indexOf(nextToken.delimiter); 
		
		var endIdx = findIdx+ nextToken.delimiterLen; 
		var matchCont = log.substring(0, findIdx);

		var logStyleClass = {
			TRACE : 'log1'
			,DEBUG : 'log2'
			,INFO : 'log3'
			,WARN : 'log4'
			,ERROR : 'log5'
			,FATAL : 'log6'
		}

		var logClass = logStyleClass[matchCont.replace(/\s/g,'')]; 

		if(logClass){
			return {
				cont : '<span class="'+logClass+'">'+ replaceSpaceToNbsp(matchCont)+'</span>'+ replaceSpaceToNbsp(nextToken.delimiter)
				,log : log.substring(endIdx)
			}
		}

		return false; 			
	}
	,other: function (log, token, nextToken){
		var findIdx = log.indexOf(nextToken.delimiter); 

		var endIdx = findIdx+ nextToken.delimiterLen; 
		var matchCont = log.substring(0, findIdx);
		return {
			cont : matchCont+nextToken.delimiter
			,log : log.substring(endIdx)
		}
	} 
}
                
function getLogTemplate(tokenList, log){

	var tokenInfo = {}, nextToken = {};
	var viewLogInfo = [];
	var reval ={};

	if(/(^\s{0,}at\s)/.test(log)){
		return '<span class="log5">'+ replaceSpaceToNbsp(log)+'</span>'
	}

	for(var i =0, len = tokenList.length; i < len; i++){
		var tokenInfo = tokenList[i];
		var token = tokenInfo.token; 
		nextToken = i+1 < len ? tokenList[i+1] : {};
		
		if(token =='msg'){
			viewLogInfo.push(replaceSpaceToNbsp(log));
			continue; 
		}else if(token =='n'){
			break; 
		}else {
			var parserInfo = tokenParser[token];
			if(typeof parserInfo !=='undefined'){
				reval = parserInfo(log, tokenInfo, nextToken);
			}else{
				reval = tokenParser.other(log, tokenInfo, nextToken);
			}
			
			if(reval === false){
				viewLogInfo.push(log)
				return viewLogInfo.join('');
			}
			viewLogInfo.push(reval.cont)
			log = reval.log;
		}
	}
	return viewLogInfo.join('');
}

function getTokenList(format){

	if(!format){
		return null;
	}

	var regexp = new RegExp(/%(-?(\d+))?(\.(\d+))?([a-zA-Z]+)(\{([^\}]+)\})?/,'g');

	var tokenList = [];
	var match = regexp.exec(format);
	var beforeEndIdx = 0; 
	while (match != null) {
		var matchStr = match[0];
		var tokenInfo = {token:match[5], format:match[7], delimiterLen:0};
		tokenInfo.size = match[1]||0;
		tokenInfo.formatLen = typeof tokenInfo.format ==='undefined' ? 0 : tokenInfo.format.length;

		if(beforeEndIdx != 0){
			tokenInfo.delimiter = format.substring(beforeEndIdx, match.index);
			tokenInfo.delimiterLen = tokenInfo.delimiter.length;
		}

		tokenList.push(tokenInfo);
		beforeEndIdx =  match.index+matchStr.length;

		match = regexp.exec(format);
	}
	return tokenList; 
}

Plugin.prototype ={
	/**
	 * @method _initialize
	 * @description 그리드 초기화.
	 */
	_initialize :function(selector,options){
		// scroll size 
		var _this = this; 
		_this.selector = selector;

		_this.prefix = 'pubLogViewer'+getHashCode(_this.selector);
		_this.logElement = $(selector);
		
		_this.element = {};
		_this.config = {
			 container :{height : 0,width : 0, bodyHeight:0, margin : 0}
			, dataInfo : {colLen : 0, allColLen : 0, rowLen : 0, lastRowIdx : 0}
			, selection :{}
			, orginData : []
			, newAddData : []
			, header : {}
			, status : 'start'
			, search:{text : ''}
			, cursorView :false
			, filter :{text: '', line: 0, beforeAddLineCount: -1, searchOn: false}
			, tokenList : []
		};	

		_this._setSelectionRangeInfo({},true);
		_this.eleCache = {};
		_this._initScrollData();
		
		_this.setOptions(options, true);

		_this.drag ={};
		_this.addStyleTag();
		
		_this.setData(_this.options.items, 'init');
		
		_this._windowResize();

		return this;
	}
	// 스크롤 데이터 초기화
	,_initScrollData : function (){
		this.config.scroll = {containerLeft :0, before:{bodyCont: 0},top :0 , left:0, startLeft:0, endLeft : 0, viewIdx : 0, vBarPosition :0 , hBarPosition :0 , maxViewCount:0, viewCount : 0, vTrackHeight:0,hTrackWidth:0, verticalTimer : -1, horizontalTimer : -1, mouseDown :false}; 
	}
	,setOptions : function(options , firstFlag){
		var _this = this; 
		
		_this.options = objectMerge({}, _defaults, options)

		_this.options.items = options.items ? options.items : [];

		_this.config.rowHeight = _this.options.rowOptions.height;
		_this.setLogPattern(_this.options.logPattern);

		_this.config.marginLength = Math.round(this.config.rowHeight/this.options.scroll.topMargin);

		_this.config.header.height = _this.options.headerOptions.height;
		var saveStateInfo = localStorage.getItem(_this.options.setting.saveStateKey || _this.prefix);

		if(_this.options.setting.saveStateKey !== false){
			if(isUndefined((options.setting||{}).filter)){
				if(saveStateInfo){
					_this.config.filter =objectMerge({}, _this.config.filter, JSON.parse(saveStateInfo));
				}
			}else{
				_this.config.filter =objectMerge({}, _this.config.filter, options.setting.filter);
			}
			_this.config.filter.searchOn = _this.config.filter.text != '' ? true :false;
		}

		// space width
		this.config.blankSpaceWidth = 2+(this.options.blankSpaceWidth>0?this.options.blankSpaceWidth :0);

		if($.isFunction((options.setting||{}).searchFilter)){
			_this.config.filter.isCustomFn = true;
		}
		
		_this._setGridContainerWidth(_this.getGridWidth());
	}
	,setLogPattern:function(pattern){
		this.options.logPattern = pattern;
		this.config.tokenList = getTokenList(pattern);
	}
	,setItemLength : function (){
		this.config.dataInfo.orginRowLen = this.options.items.length;
		
		if(this.config.dataInfo.orginRowLen > 0){
			this.config.dataInfo.rowLen= this.config.dataInfo.orginRowLen+1;
			this.config.dataInfo.lastRowIdx= this.config.dataInfo.orginRowLen-1;
		}else{
			this.config.dataInfo.rowLen= 0;
			this.config.dataInfo.lastRowIdx= 0;
		}
	}
	,setLogItemMaxConcat : function (data, addArr, opt, concatFlag){
		var removeCount = (addArr.length+data.length)-opt.itemMaxCount;
		if(opt.itemMaxCount > 0 && removeCount > 0){
			addArr.splice(0, removeCount);	
		}
		if(concatFlag !==false)	addArr = addArr.concat(data);

		return {arr : addArr , removeCount : removeCount}; 
	}
	/**
	 * @method setData
	 * @param data {Array} - 데이타
	 * @param gridMode {String} - 그리드 모드 
	 * @param addOpt {Object} - add opt
	 * @description 데이타 그리기
	 */
	,setData :function (pdata, mode, addOpt){
		var _this = this
			,opt = _this.options
		var logData = pdata;

		addOpt = addOpt || {};
		
		mode = mode||'reDraw'; 

		var modeInfo = mode.split('_');
		var gridMode = modeInfo[0];
			
		gridMode = gridMode||'reDraw';

		if(!$.isArray(pdata)){
			logData = pdata.items;
		}

		var logDataLen = logData.length;

		if(logDataLen > opt.itemMaxCount){
			logData.splice(0, (logDataLen-opt.itemMaxCount));
			logDataLen = logData.length;
		}

		var isStop = _this._isStatusStop(); 

		var data = [];

		if(isStop){
			data = logData;
		}else{
			for (var i = 0; i < logDataLen; i++) {
				var logText = logData[i]
				logText = logText.split('\t').join('    ')

				data.push({
					orgin : logText
				});
			}
		}
		
		if(data){
			if(gridMode == 'addData'){
				if(isStop){
					_this.config.newAddData = _this.setLogItemMaxConcat(data, _this.config.newAddData, opt).arr;
				}else{
					var concatInfo = _this.setLogItemMaxConcat(data, _this.config.orginData, opt);
					_this.config.orginData =concatInfo.arr;				

					var beforeItemLen = _this.options.items.length;

					// selection cacl
					var selection = this.config.selection;
					var startCell = selection.startCell;
					var selectionFlag = startCell.startIdx > -1;

					var removeCount = concatInfo.removeCount; 
					if(_this.config.filter.text != ''){
						_this._filterData(data);
						if(selectionFlag && beforeItemLen < _this.options.items.length){
							removeCount= (_this.options.items.length - beforeItemLen);
						}
					}else{
						_this.options.items = _this.config.orginData.slice(0);
					}
					
					if(removeCount > 0){
						var startIdx = startCell.startIdx - removeCount;
						var endIdx = selection.range.endIdx - removeCount;
						
						_this._setSelectionRangeInfo({
							rangeInfo : {startIdx : startIdx, endIdx : endIdx}
							,startCell : {startIdx : startIdx}
						}, false, true);
					}
				}
			}else if(gridMode !='filter'){
				_this.config.orginData = data;
				
				if(_this.config.filter.text != ''){
					if(gridMode =='init'){
						_this.options.items = [];
					}
					_this._filterData(data);
				}else{
					_this.options.items = _this.config.orginData.slice(0);
				}
			}
		}

		if(gridMode =='filter'){
			gridMode ='reDraw';
		}
				
		_this.setItemLength();
		
		if(gridMode=='reDraw' || gridMode == 'addData'){
			_this.calcDimension(gridMode, {onlyCalc: (!isUndefined(addOpt.onlyCalc)? addOpt.onlyCalc : (isStop ? true :false))});
		}

		if(!this.isVisible()){
			return ; 
		}
		
		if(mode != 'filter' && isStop) return ; 
		
		//_this._setSelectionRangeInfo({}, true);
		
		if(addOpt.focus===true){
			_this.moveHorizontalScroll({pos: 0, drawFlag: false});
			_this.moveVerticalScroll({rowIdx: _this.config.dataInfo.orginRowLen, drawFlag: true});
		}else{
			if(mode=='filter'){
				_this.moveVerticalScroll({rowIdx: this.options.items.length, drawFlag: true});
			}else{
				_this.drawGrid(mode);
			}
		}
	}
	,drawGrid : function (pMode){
		var _this = this
			,items = _this.options.items

		pMode = pMode||'';
		var drawModeInfo =pMode.split('_');

		var drawMode = drawModeInfo[0];

		if(drawMode =='init'){
						
			var templateHtm = _$template.getTemplateHtml(_this);

			_this.logElement.empty().html(templateHtm);

			_this.element.pubLogViewer = $('#'+_this.prefix +'_pubLogViewer');
			_this.element.hidden = $('#'+_this.prefix +'_hiddenArea');
							
			_this.element.container = $('#'+_this.prefix+'_container');
			_this.element.header= $('#'+_this.prefix+'_headerContainer');
			_this.element.body = $('#'+_this.prefix +'_bodyContainer');
			_this.element.bodyCont = $('#'+_this.prefix +'_bodyCont');

			_this.element.bodyViewArea = _this.element.body.find('.pubLogViewer-body');

			_this.element.vScrollBar = $('#'+_this.prefix+'_vscroll .pubLogViewer-vscroll-bar');
			_this.element.hScrollBar = $('#'+_this.prefix+'_hscroll .pubLogViewer-hscroll-bar');
			_this.element.statusbar = $('#'+_this.prefix+'_statusbar');
			_this.element.statusbarMessage =_this.element.statusbar.find('.pubLogViewer-message-info');
			_this.element.statusbarIcon =_this.element.statusbar.find('.pubLogViewer-status-icon');
			_this.element.statusbarCount = _this.element.statusbar.find('.pubLogViewer-count-info');
			
			//cursor
			_this.element.cursorEle = $('#'+_this.prefix +'_pubLogViewerCursor');
			
			//search input box text
			_this.element.searchText = $('#'+_this.prefix +'_srh_val');
			
			// measure element
			_this.element.measureEl = $('#'+_this.prefix+'_pubLogViewerMeasure');
			
			// message element
			_this.element.bodyMessage = $('#'+_this.prefix+'_bodyMessage');
			
			//canvas
			/*
			_this.element.canvas = document.getElementById(_this.prefix+'_pubLogViewerCanvas');
			_this.config.canvasContext = _this.element.canvas.getContext('2d'); 

			var bodyStyle = window.getComputedStyle(document.getElementById(_this.prefix +'_bodyCont')); 
			_this.config.canvasContext.font = "12px Arial " + bodyStyle.fontFamily;
			_this.config.canvasContext.direction="ltr";
			*/
			_this.element.statusbar.show();

			_this.calcDimension('init');
			
			_this.scroll();
			
			_this._initHeaderEvent();
			_this._initBodyEvent();
			_this._setBodyEvent();
			_this.setTheme(_this.options.theme);

			_this.config.scroll.viewIdx =  _this.config.dataInfo.orginRowLen - _this.config.scroll.viewCount;
			_this.moveVerticalScroll({rowIdx: _this.config.dataInfo.orginRowLen, drawFlag: false});
		}

		var itemIdx = _this.config.scroll.viewIdx;
		var viewCount = _this.config.scroll.viewCount;
		
		var logItem; 
		var rowEle,selectionEle ,addEle;

		var searchInfo = _this.config.search;
		var allSchRegExp =searchInfo.currentAllSchRegExp;

		var filterRegExp =_this.config.filter.currentFilterRegExp;
		
		var startCell = this.config.selection.startCell;
		var startCursorIdx = startCell.startIdx;

		if(startCursorIdx > -1  && (itemIdx <= startCursorIdx  && startCursorIdx <= itemIdx+_this.config.scroll.insideViewCount)){
			this.setCursorPosition(((startCursorIdx - itemIdx) *_this.config.rowHeight), startCell.startLeft , true);
		}else{
			this.setCursorPosition(null,null,false);
		}

		if(itemIdx == startCursorIdx-1){
			this.setContainerMargin(0);
		}else if(itemIdx+viewCount > this.config.dataInfo.orginRowLen){
			this.setContainerMargin(0);
		}else{
			this.setContainerMargin((itemIdx % this.config.marginLength * this.options.scroll.topMargin));
		}

		for(var i =0 ; i < viewCount; i++){
			logItem = items[itemIdx] ||{};

			if(typeof logItem.viewHtmlTemplate==='undefined'){
				logItem['viewHtmlTemplate'] = getViewLogTemplate(logItem.orgin, _this.config.tokenList);
			}
						
			var overRowFlag = (itemIdx >= this.config.dataInfo.orginRowLen);

			rowEle =$pubSelector('#'+_this.prefix+'_bodyCont>.pubLog-body-cont[rowinfo="'+i+'"]');
			
			selectionEle = rowEle.querySelector('.pubLog-body-cont-overlay');
			addEle = rowEle.querySelector('.pubLog-body-cont-log');

			if(searchInfo.currentText =='' && _this.config.filter.text==''){
				this.setSelectPosition(itemIdx, selectionEle, false);
			}else{
				var matchWordInfos = []
				if(searchInfo.currentText !=''){
					var matchResult = allMatchWord(logItem.orgin, allSchRegExp, 'search'); 
					if(matchResult){
						matchWordInfos = matchWordInfos.concat(matchResult);
					}
				}

				if(_this.config.filter.text !=''){
					var matchResult = allMatchWord(logItem.orgin, filterRegExp, 'filter'); 
					if(matchResult){
						matchWordInfos = matchWordInfos.concat(matchResult);
					}
				}
				
				this.setSelectPosition(itemIdx, selectionEle, matchWordInfos);
			}
			
			if(overRowFlag){
				addEle.innerHTML='';
			}else{
				addEle.innerHTML =  logItem.viewHtmlTemplate;
			}
									
			rowEle= null; 
			itemIdx++;

			if(itemIdx > this.config.dataInfo.orginRowLen){
				_this.element.bodyCont.find('[rowinfo="'+(viewCount-1)+'"]').hide();
			}else{
				_this.element.bodyCont.find('[rowinfo="'+(viewCount-1)+'"]').show();
			}
		}

		this.setHorizontalScroll();
		
		if(_this.config.filter.searchOn===true){
			_this.element.container.find('.pubLogViewer-setting').addClass('search-on');
		}else{
			_this.element.container.find('.pubLogViewer-setting').removeClass('search-on');
		}

		if(this.options.status.enabled===true) _this._setCountInfo();
	}
	,_setCountInfo : function (){
		var startVal = this.config.scroll.viewIdx +1
			,endVal = startVal+ this.config.scroll.insideViewCount;

		endVal = endVal >= this.config.dataInfo.orginRowLen? this.config.dataInfo.orginRowLen: endVal;

		this.element.statusbarCount.empty().html(startVal+' - ' + endVal+' of '+ this.config.dataInfo.orginRowLen)
	}
	,setInfoMessage : function (msg){
		this.element.statusbarMessage.empty().html(msg);
	}
	,moveLastRow : function (statusIgnore){
		if(statusIgnore===true || !this._isStatusStop()){
			this.moveVerticalScroll({pos: 'last', drawFlag: true});
		}
	}
	,setStatusIcon : function(status){
		if(status=='stop'){
			this.element.statusbarIcon.removeClass('green').addClass('gray');
		}else if(status=='start'){
			this.element.statusbarIcon.removeClass('gray').addClass('green');
		}
		
	}
	/**
	 * @method calcDimension
	 * @param  type {String}  타입.
	 * @param  opt {Object}  옵션.
	 * @description 그리드 수치 계산 . 
	 */
	, calcDimension : function (pType, opt){

		var typeInfo =pType.split('_');
		var type = typeInfo[0];
		
		var _this = this
			,cfg = _this.config; 

		var dimension = {};

		if(this.isVisible()){
			dimension = {width : _this.getGridWidth(), height :_this.getGridHeight()};
		}else{
			dimension= {height : _this.config.eleTotHeight ,width : _this.config.eleTotWidth};
		}

		opt = objectMerge(dimension, opt);
	
		cfg.container.height = opt.height;
		var totHeight = opt.height
			, bodyHeight  = totHeight - cfg.header.height - this.options.scroll.horizontal.height - (this.options.status.enabled ===true ? this.options.status.height : 1)
			, totWidth = opt.width;
		
		if(type =='init' || type =='resize'){
			_this.element.pubLogViewer.css('height',totHeight+'px');
			_this.element.pubLogViewer.css('width',totWidth+'px');
		}

		_this._setGridContainerWidth(totWidth, type);
						
		var itemTotHeight = (cfg.dataInfo.rowLen-1) * cfg.rowHeight;
		var vScrollFlag = itemTotHeight > bodyHeight;

		_this.element.body.css('height',(bodyHeight + this.options.scroll.horizontal.height)+'px');

		var beforeViewCount = cfg.scroll.viewCount; 

		cfg.scroll.viewCount = itemTotHeight > bodyHeight ? Math.ceil(bodyHeight / cfg.rowHeight) : cfg.dataInfo.rowLen;
		cfg.scroll.bodyViewCount = Math.ceil(bodyHeight/cfg.rowHeight);
		cfg.scroll.insideViewCount = Math.floor(bodyHeight/cfg.rowHeight);
		cfg.scroll.lastItemIdx = cfg.dataInfo.orginRowLen - cfg.scroll.insideViewCount;
		cfg.container.bodyHeight = bodyHeight;

		var topVal = 0 ; 
		if(vScrollFlag && cfg.dataInfo.orginRowLen >= cfg.scroll.viewCount){
			cfg.scroll.vUse = true;
			_this.element.vScrollBar.show();
						
			var scrollH = bodyHeight - (10*2 +2); //arrow *2 + border top, bottom 2
			
			var barHeight = (scrollH*(bodyHeight/(itemTotHeight)*100))/100; 
			if(scrollH <25){
				barHeight = 1;
			}else{
				barHeight = barHeight < 25 ? 25 : ( barHeight > scrollH ?scrollH :barHeight);	
			}
			
			cfg.scroll.vHeight = scrollH; 
			cfg.scroll.vThumbHeight = barHeight; 
			cfg.scroll.vTrackHeight = scrollH - barHeight;
			cfg.scroll.oneRowMove = cfg.scroll.vTrackHeight/(cfg.dataInfo.rowLen-cfg.scroll.viewCount);
						
			topVal = cfg.scroll.vTrackHeight* cfg.scroll.vBarPosition/100;
			
			_this.element.vScrollBar.css('height',barHeight);
			if(scrollH < 25){
				_this.element.vScrollBar.hide();
			}else{
				_this.element.vScrollBar.show();
			}
		}else{
			cfg.scroll.vUse = false; 
			_this.element.vScrollBar.hide();
		}
		
		if(cfg.scroll.maxViewCount < cfg.scroll.viewCount) cfg.scroll.maxViewCount = cfg.scroll.viewCount;
						
		if(beforeViewCount != cfg.scroll.viewCount){
			_$template.bodyRowDraw(_this, type); 
		}

		if(type =='init' || type =='resize'){
			_this.config.eleTotHeight = totHeight;
			_this.config.eleTotWidth = totWidth;
		}

		if(type=='resize'){
			if(cfg.scroll.viewIdx +cfg.scroll.maxViewCount > cfg.dataInfo.orginRowLen){
				cfg.scroll.viewIdx = cfg.dataInfo.orginRowLen - cfg.scroll.maxViewCount;
				cfg.scroll.viewIdx = Math.max(cfg.scroll.viewIdx, 0);
				topVal = cfg.scroll.viewIdx * cfg.scroll.oneRowMove;
			}
		}

		if(type =='init' || opt.onlyCalc===true) return ; 

		if(type =='reDraw'){
			topVal=0; 
		}else if(type == 'addData'){
			if(cfg.scroll.vUse){
				topVal = cfg.scroll.viewIdx * cfg.scroll.oneRowMove;
			}
		}

		if(type=='resize'){
			if( !_this._isStatusStop() && opt.focus ===true){
				_this.moveVerticalScroll({pos: 'last'});
			}else{

				_this.moveVerticalScroll({pos: topVal, drawFlag: false, resizeFlag: true});
				
				if(_this.config.eleBeforeTotHeight != totHeight){
					_this.drawGrid();
				}
			}
		}else{
			_this.moveVerticalScroll({pos :topVal, drawFlag : false});
			if(beforeViewCount !=0 ){
				if(type !='reDraw'){
					_this.drawGrid();
				}
			}
		}

		_this.config.eleBeforeTotHeight = totHeight;
	}
	,_setBodyContLeftValue : function (scrollLeftVal){
		var contLeftVal = this._getBodyContainerLeft(scrollLeftVal) ; 
		this.element.bodyCont.css('left','-'+contLeftVal+'px');
		this.config.scroll.bodyLeftPixel = contLeftVal;
	}
	// 가로 스크롤 
	,setHorizontalScroll : function (){
		var _this = this
			, cfg = _this.config
			, bodyWidth = this.config.bodyWidth;

		var contW = this.getBodyContWidth(); 
		
		if(cfg.scroll.before.bodyW == this.config.container.width && cfg.scroll.before.bodyCont == contW){
			return ; 
		}else{
			this._setBodyContLeftValue(this.config.scroll.left);
		}
		
		cfg.scroll.before.bodyCont=contW;
		cfg.scroll.before.bodyW = this.config.container.width;

		var hScrollFlag =  contW >bodyWidth;
		var leftVal = 0;
		if(hScrollFlag){
			var gridContTotW = contW;

			cfg.scroll.hUse = true; 
			_this.element.hScrollBar.show();

			var hscrollW = $('#'+_this.prefix+'_hscroll').find('.pubLogViewer-hscroll-bar-area').width();

			var barWidth = (hscrollW*(bodyWidth/gridContTotW*100))/100; 
			barWidth = barWidth < 25 ? 25 :( barWidth > hscrollW ?hscrollW :barWidth);
			
			cfg.scroll.hThumbWidth = barWidth;
			cfg.scroll.hTrackWidth =hscrollW - barWidth;
			cfg.scroll.oneColMove = gridContTotW/cfg.scroll.hTrackWidth;
			leftVal = cfg.scroll.hTrackWidth* cfg.scroll.hBarPosition/100;
			_this.element.hScrollBar.css('width',barWidth);
			this.element.hScrollBar.css('left',leftVal+'px');

		}else{
			cfg.scroll.hUse= false; 
			this._setBodyContLeftValue(0);
			_this.element.hScrollBar.hide();
		}
		return leftVal; 
	}
	,_verticalScrollMove : function(ctx, pEvtY, pTop, vThumbHeight, oneRowMove){
		
		clearTimeout(ctx.config.scroll.verticalTimer);

		var loopcount =5;
		var upFlag =pEvtY> ctx.config.scroll.top ? false :true;
		
		pTop= pTop+((upFlag?-1:1) * (oneRowMove *loopcount));

		if(upFlag){
			if( pEvtY >= pTop ){
				ctx.config.scroll.mouseDown = false
				pTop = pEvtY;
			}
		}else{
			if(pEvtY <= (pTop +vThumbHeight)){
				ctx.config.scroll.mouseDown = false
				pTop = pEvtY-vThumbHeight;
			}
		}

		ctx.moveVerticalScroll({pos :pTop});

		if(ctx.config.scroll.mouseDown ){
			ctx.config.scroll.verticalTimer = setTimeout(function() {
				ctx._verticalScrollMove(ctx, pEvtY, pTop, vThumbHeight, oneRowMove * ctx.options.scroll.horizontal.speed);
			}, ctx.options.scroll.vertical.bgDelay);
		}
	}
	,_horizontalScrollMove  : function (ctx, pEvtX, pLeft, hThumbWidth, oneColMove){
		clearTimeout(ctx.config.scroll.horizontalTimer);

		var leftFlag =pEvtX > ctx.config.scroll.left ? false :true;
		var loopcount =10;

		pLeft = pLeft+((leftFlag?-1:1) * (oneColMove*loopcount)); 

		if(leftFlag){
			if( pEvtX >= pLeft ){
				ctx.config.scroll.mouseDown = false
				pLeft = pEvtX;
			}
		}else{
			if(pEvtX <= (pLeft +hThumbWidth)){
				ctx.config.scroll.mouseDown = false
				pLeft = pEvtX-hThumbWidth;
			}
		}
		
		ctx.moveHorizontalScroll({pos : pLeft});
		
		if(ctx.config.scroll.mouseDown){
			ctx.config.scroll.horizontalTimer = setTimeout(function() {
				ctx._horizontalScrollMove(ctx, pEvtX, pLeft, hThumbWidth, oneColMove);
			}, ctx.options.scroll.horizontal.bgDelay);
		}
	}
	,scroll : function (){
		var _this = this
			,_conf = _this.config;
			
		_this.element.body.off('mousewheel DOMMouseScroll');
		_this.element.body.on('mousewheel DOMMouseScroll', function(e) {
			var oe = e.originalEvent;
			var delta = 0;
		
			if (oe.detail) {
				delta = oe.detail * -40;
			}else{
				delta = oe.wheelDelta;
			};

			//delta > 0--up
			if(_this.config.scroll.vUse){
				_this.moveVerticalScroll({pos :(delta > 0? 'U' :'D') , speed : _this.options.scroll.vertical.speed});

				if(_this.options.scroll.isPreventDefault === true){
					e.preventDefault();
				}else if(_this.config.scroll.top != 0 && _this.config.scroll.top != _this.config.scroll.vTrackHeight){
					e.preventDefault();
				}
			}else{
				if(_this.config.scroll.hUse){
					_this.moveHorizontalScroll({pos :(delta > 0?'L':'R') , speed : _this.options.scroll.horizontal.speed});
					
					if(_this.options.scroll.isPreventDefault===true){
						e.preventDefault();
					}else{
						if(_this.config.scroll.left != 0 && _this.config.scroll.left != _this.config.scroll.hTrackWidth){
							e.preventDefault();
						}
					}
				}
			}
		});
				
		$('#'+_this.prefix+'_vscroll .pubLogViewer-vscroll-bar-bg').off('mousedown touchstart mouseup touchend mouseleave');
		$('#'+_this.prefix+'_vscroll .pubLogViewer-vscroll-bar-bg').on('mousedown touchstart',function(e) {
			_this.config.scroll.mouseDown = true;
			var evtY = e.offsetY;
						
			_this._verticalScrollMove(_this, evtY, _this.config.scroll.top, _this.config.scroll.vThumbHeight, _this.config.scroll.oneRowMove *_this.options.scroll.horizontal.speed);
			
		}).on('mouseup touchend mouseleave',function(e) {
			_this.config.scroll.mouseDown = false;
			clearTimeout(_this.config.scroll.verticalTimer);
		});
		
		$('#'+_this.prefix+'_hscroll .pubLogViewer-hscroll-bar-bg').off('mousedown touchstart mouseup touchend mouseleave');
		$('#'+_this.prefix+'_hscroll .pubLogViewer-hscroll-bar-bg').on('mousedown touchstart',function(e) {
			_this.config.scroll.mouseDown = true;
			var evtX = e.offsetX;
						
			_this._horizontalScrollMove(_this, evtX,_this.config.scroll.left , _this.config.scroll.hThumbWidth, _this.config.scroll.oneColMove);
			
		}).on('mouseup touchend mouseleave',function(e) {
			_this.config.scroll.mouseDown = false;
			clearTimeout(_this.config.scroll.horizontalTimer);
		});
		
		var scrollbarDragTimeer; 
		var startTime = '';
		var hDragDelay = _this.options.scroll.horizontal.dragDelay; 
		// 가로 스크롤 bar drag
		_this.element.hScrollBar.off('touchstart.pubhscroll mousedown.pubhscroll');
		_this.element.hScrollBar.on('touchstart.pubhscroll mousedown.pubhscroll',function (e){
			e.stopPropagation();

			var oe = e.originalEvent.touches;
			var ele = $(this); 
			var data = {};

			data.left = _this.config.scroll.left
			data.pageX = oe ? oe[0].pageX : e.pageX; 

			ele.addClass('active');

			$(document).on('touchmove.pubhscroll mousemove.pubhscroll', function (e){
				
				if(startTime==''){
					startTime = new Date().getTime(); 
				}

				if(new Date().getTime()-hDragDelay <= startTime){
					clearTimeout(scrollbarDragTimeer);
				}

				scrollbarDragTimeer = setTimeout(function() {
					startTime='';
					_this.horizontalScroll(data, e, 'move');
				}, hDragDelay);
			}).on('touchend.pubhscroll mouseup.pubhscroll mouseleave.pubhscroll', function (e){
				ele.removeClass('active');
				clearTimeout(scrollbarDragTimeer);
				startTime='';
				_this.horizontalScroll(data,e, 'end');
			});

			return true; 
		});
					
		var tooltipFlag = _this.options.scroll.vertical.tooltip; 
		var vDragDelay = _this.options.scroll.vertical.dragDelay; 
		var tooltipEle = _this.element.vScrollBar.find('.pubLogViewer-vscroll-bar-tip');
		// 세로 스크롤 바 .
		_this.element.vScrollBar.off('touchstart.pubvscroll mousedown.pubvscroll');
		_this.element.vScrollBar.on('touchstart.pubvscroll mousedown.pubvscroll',function (e){
			e.stopPropagation();
			var oe = e.originalEvent.touches;
			var ele = $(this); 
			var data = {};
			data.top= _this.config.scroll.top; 
			data.pageY = oe ? oe[0].pageY : e.pageY; 

			ele.addClass('active');

			$(document).on('touchmove.pubvscroll mousemove.pubvscroll', function (e){
				if(startTime==''){
					startTime = new Date().getTime(); 
				}

				if(new Date().getTime()-vDragDelay <= startTime){
					clearTimeout(scrollbarDragTimeer);
				}

				scrollbarDragTimeer = setTimeout(function() {
					startTime='';
					_this.verticalScroll( data,e , 'move');

					if(tooltipFlag){
						tooltipEle.text(_this.config.scroll.viewIdx+1);
						tooltipEle.show();
					}
				}, vDragDelay);
			}).on('touchend.pubvscroll mouseup.pubvscroll mouseleave.pubvscroll', function (e){
				ele.removeClass('active');
				clearTimeout(scrollbarDragTimeer);
				_this.verticalScroll(data, e , 'end');
				startTime='';

				if(tooltipFlag){
					tooltipEle.hide();
				}
			});

			return true; 
		}); 

		// 가로 스크롤 방향키
		var scrollBtnTimer
			,vBtnDelay = _this.options.scroll.vertical.btnDelay
			,hBtnDelay = _this.options.scroll.horizontal.btnDelay;

		$('#'+_this.prefix+'_hscroll .pubLogViewer-hscroll-btn').off('mousedown touchstart mouseup touchend mouseleave');
		$('#'+_this.prefix+'_hscroll .pubLogViewer-hscroll-btn').on('mousedown touchstart',function(e) {
			var sEle = $(this)
				,mode = sEle.attr('data-pubLogViewer-btn');

			scrollBtnTimer = setInterval(function() {
				_this.moveHorizontalScroll({pos :mode});
			}, hBtnDelay);
		}).on('mouseup touchend mouseleave',function(e) {
			clearInterval(scrollBtnTimer);
		});
		
		//세로 스크롤 방향키
		$('#'+_this.prefix+'_vscroll .pubLogViewer-vscroll-btn').off('mousedown touchstart mouseup touchend mouseleave');
		$('#'+_this.prefix+'_vscroll .pubLogViewer-vscroll-btn').on('mousedown touchstart',function(e) {
			var sEle = $(this)
				,mode = sEle.attr('data-pubLogViewer-btn');
			
			scrollBtnTimer = setInterval(function() {
				_this.moveVerticalScroll({pos :mode});
			}, vBtnDelay);
		}).on('mouseup touchend mouseleave',function(e) {
			clearInterval(scrollBtnTimer);
		});
	}
	,isVisible : function (){
		return this.logElement.is(':visible');
	}
	
	/**
	* 세로 스크롤 드래그 이동 
	*/
	,verticalScroll : function (data,e, type){
		var oe = e.originalEvent.touches
		,oy = oe ? oe[0].pageY : e.pageY;

		oy = data.top+(oy - data.pageY);
		
		this.moveVerticalScroll({pos :oy});
		if(type=='end'){
			$(document).off('touchmove.pubvscroll mousemove.pubvscroll').off('touchend.pubvscroll mouseup.pubvscroll mouseleave.pubvscroll');
		}
	}
	/**
	* @method moveVerticalScroll
	* @param  moveObj.pos {String ,Integer} 'U' or 'D' or 'last' or 'first' position
	* @param  moveObj.resizeFlag {boolean} resize flag
	* @param  moveObj.drawFlag {boolean} redraw flag
	* @param  moveObj.speed {Integer} row move count
	* @param  moveObj.rowIdx {Integer} move row idx
	* @description 세로 스크롤 이동.
	*/
	,moveVerticalScroll : function (moveObj){
		var _this =this; 

		if(!_this.config.scroll.vUse){
			_this.config.scroll.viewIdx = 0;

			if(moveObj.drawFlag === true){
				this.drawGrid('vscroll');
				return ; 
			}

			if(moveObj.resizeFlag !== true){ 
				return ; 
			}
		}

		if(moveObj.pos=='last'){
			moveObj.rowIdx = _this.config.dataInfo.orginRowLen;
		}else if(moveObj.pos=='first'){
			moveObj.rowIdx = 0;
		}
		
		var posVal=moveObj.pos 
			,speed = moveObj.speed || 1
			,rowIdx = moveObj.rowIdx;
		
		var topVal = posVal; 


		if(!isNaN(rowIdx)){
			topVal =rowIdx * _this.config.scroll.oneRowMove;
		}else{
			if(isNaN(posVal)){
				topVal =_this.config.scroll.top+((topVal=='U'?-1:1)* speed * _this.config.scroll.oneRowMove);
			}
		}

		this.moveVScrollPosition(topVal, moveObj.drawFlag, false, moveObj.isKeyDown);
	}
	/**
	*세로 스크롤 위치 이동.
	*/
	,moveVScrollPosition : function (topVal, drawFlag, updateChkFlag, isKeyDown){

		var barPos = 0 ; 

		if( topVal> 0){
			if(topVal >= this.config.scroll.vTrackHeight ){
				topVal = this.config.scroll.vTrackHeight;
			}
			barPos = topVal/this.config.scroll.vTrackHeight*100; 
		}else {
			topVal = 0;
			barPos = 0 ; 
		}

		if(drawFlag !==true && this.config.scroll.top ==topVal){
			return ; 
		}
		
		if(updateChkFlag !== false){
			var onUpdateFn = this.options.scroll.vertical.onUpdate; 
			if(drawFlag !== false && isFunction(onUpdateFn)){
				if(onUpdateFn.call(null, {scrollTop : topVal, height :  this.config.scroll.vTrackHeight, barPosition : barPos }) === false){
					return ; 
				}
			}
		}

		this._setScrollBarTopPosition(topVal);
		
		var itemIdx =0;

		if(topVal > 0){
			itemIdx = topVal/(this.config.scroll.vTrackHeight / (this.config.dataInfo.rowLen-this.config.scroll.viewCount));
			itemIdx  = Math.round(itemIdx); 
		}

		if(itemIdx >= this.config.scroll.lastItemIdx){
			this.setStatus('start', false);
		}else{
			if(!this._isStatusStop()){
				this.setStatus('stop');
			}
		}
		
		this.config.scroll.vBarPosition = barPos;

		if(drawFlag === false){
			this.config.scroll.viewIdx = itemIdx; 
			return ; 
		}

		if(drawFlag !==true && this.config.scroll.viewIdx ==itemIdx) return ;

		this.config.scroll.viewIdx = itemIdx;

		this.drawGrid('vscroll');
	}
	,setContainerMargin : function (margin){
		this.config.container.margin = margin;
		this.element.bodyViewArea.css('margin-top','-'+margin+'px');	
	}
	/**
	* 가로 스크롤 드래그 이동
	*/
	,horizontalScroll : function (data ,e, type){
		var oe = e.originalEvent.touches
		,ox = oe ? oe[0].pageX : e.pageX;
		ox = data.left+(ox - data.pageX);
		
		this.moveHorizontalScroll({pos : ox});

		if(type=='end'){
			$(document).off('touchmove.pubhscroll mousemove.pubhscroll').off('touchend.pubhscroll mouseup.pubhscroll mouseleave.pubhscroll');
		}
	}
	/**
	* @method moveHorizontalScroll
	* @param  moveObj.pos {String ,Integer} 'L' or 'R' or left position
	* @param  moveObj.resizeFlag {boolean} resize flag
	* @param  moveObj.drawFlag {boolean} redraw flag
	* @param  moveObj.speed {Integer} row move count
	* @description 가로 스크롤 이동.
	*/
	,moveHorizontalScroll : function (moveObj){
		var _this =this; 

		if(!_this.config.scroll.hUse){
			if(this.config.scroll.left >= 0){
				this.moveHScrollPosition(0, moveObj.drawFlag);
			}

			if(moveObj.resizeFlag !== true){
				return ; 
			}
		}

		var posVal = moveObj.pos;

		var leftVal= posVal;
		
		if(isNaN(posVal)){
			leftVal =_this.config.scroll.left+((posVal=='L'?-1:1) * _this.config.scroll.oneColMove);
		}
		
		this.moveHScrollPosition(leftVal, moveObj.drawFlag);
	}
	// body cont width
	,getBodyContWidth: function (){
		return this.element.bodyCont.width();
	}
	/**
	 * @method _getBodyContainerLeft
	 * @param leftVal {Integer} body left position
	 * @description 가로 스크롤바 위치 이동
	 */
	,_getBodyContainerLeft : function (leftVal){
		var overWidth = this.getBodyContWidth()+10 -this.config.bodyWidth;
		return leftVal < 1? 0:(((overWidth)*(leftVal/this.config.scroll.hTrackWidth*100))/100); 
	}
	// container left 값으로 scroll 위치값 구하기.
	,_getScrollLeft : function (leftVal, arrow){

		if(leftVal < 1){
			return 0; 
		}

		var scrollLeftVal =this.config.scroll.left;
		var overWidth = this.getBodyContWidth()+10 -this.config.bodyWidth;

		leftVal = leftVal+ (arrow =='left'? -20 : 20); 
		
		if(leftVal <this.config.scroll.bodyLeftPixel){
			if(arrow !='left'){
				leftVal =leftVal - this.config.bodyWidth;			
			}
			scrollLeftVal = (this.config.scroll.hTrackWidth*(leftVal/overWidth *100)/100);
		}else if((leftVal - (this.config.scroll.bodyLeftPixel+ this.config.bodyWidth)) > 0){
			leftVal = leftVal - this.config.bodyWidth;			
			scrollLeftVal = (this.config.scroll.hTrackWidth*(leftVal/overWidth *100)/100);
		}
						
		return scrollLeftVal > 0 ? scrollLeftVal : 0;
	}
	/**
	 * @method moveHScrollPosition
	 * @param leftVal {Integer} body left position
	 * @param drawFlag {Boolean} draw flag
	 * @param updateChkFlag {Boolean} 업데이트 여부.
	 * @description 가로 스크롤바 위치 이동
	 */
	,moveHScrollPosition : function (leftVal, drawFlag, updateChkFlag){
		var hw = this.config.scroll.hTrackWidth;
		leftVal = leftVal > 0 ? (leftVal >= hw ? hw : leftVal) : 0 ; 

		if(this.config.scroll.left ==leftVal){
			return ; 
		}
		
		this.config.scroll.left = leftVal; 
		this.config.scroll.hBarPosition = leftVal/hw*100; 

		this.element.hScrollBar.css('left',this.config.scroll.left+'px');
		this._setBodyContLeftValue(this.config.scroll.left);
	}
	/**
	* vertical scroll top postion setting
	*/
	,_setScrollBarTopPosition : function (topVal){
		this.config.scroll.top = topVal; 
		this.element.vScrollBar.css('top', topVal);
	}
	,_isStatusStop : function (){
		return this.config.status == 'stop';
	}
	,setStatus : function (statusInfo, drawFlag){
				
		if(statusInfo=='stop'){
			this.config.status = 'stop';
			this.element.header.find('.pubLogViewer-status-btn').html(this.options.i18n['search.start']);
		}else{
			this.config.status = 'start';
			this.element.header.find('.pubLogViewer-status-btn').html(this.options.i18n['search.stop']);
	
			if(this.config.newAddData.length > 0){
				this.setData(this.config.newAddData.splice(0,this.config.newAddData.length), 'addData', {focus: true});
			}else{
				if(drawFlag !== false){
					this.moveVerticalScroll({rowIdx : this.config.dataInfo.orginRowLen});
				}
			}		
		}
	}
	,_initHeaderEvent : function (){
		var _this = this; 
		var schBtnEle = this.element.header.find('.pubLogViewer-sch-btn');
		var statusBtnEle = this.element.header.find('.pubLogViewer-status-btn'); 
		var schNextBtnEle = this.element.header.find('.pubLogViewer-sch-btn[data-sch-mode="next"]');

		statusBtnEle.on('click.pubLogViewer.statusbtn',function(e) {
			_this.setStatus((_this.config.status =='start' ?'stop':'start'));
			return true; 
		});

		schBtnEle.on('click.pubLogViewer.searchbtn', function (e){
			var sEle = $(this);
			var schOrginStr = _this.element.searchText.val();
			_$search.search(_this, sEle.attr('data-sch-mode'), schOrginStr);
		})

		// keydown enter
		_this.element.searchText.on('keydown.pubLogViewer.search',function(e) {
			if(e.keyCode==13){
				_$search.search(_this, 'next');
			}
		});

		// input event
		_this.element.searchText.on('input.pubLogViewer.search',function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			schNextBtnEle.trigger('click.pubLogViewer.searchbtn');
			return false; 
		});

		// setting 버튼.
		_this.element.container.find('.pubLogViewer-setting').on('click', function (){
			var wrapperEle= $(this).closest('.pubLogViewer-setting-wrapper'); 

			if(wrapperEle.hasClass('open')){
				wrapperEle.removeClass('open');
			}else{
				wrapperEle.addClass('open');
			}
		})

		var applyBtn = _this.element.container.find('.pubLogViewer-apply-btn'); 

		// setting 적용 버튼. 
		applyBtn.on('click.publogviewer.apply', function (){
			var filterText= $('#'+_this.prefix+'_filter_val').val()
				,filterLine = $('#'+_this.prefix+'_filter_line').val(); 

			if(_this.config.filter.text == filterText && _this.config.filter.line == filterLine){
				return ;
			}

			_this.config.filter.text = filterText;
			_this.config.filter.line = parseInt(filterLine,10);
			_this.config.filter.beforeAddLineCount = -1; 

			if($.trim(filterText) != ''){
				_this.options.items = [];
				_this.config.filter.searchOn = true; 
				_this._filterData(_this.config.orginData.slice(0));
				_this.setStatus('stop');
				_this.setData([],'filter');
			}else{
				_this.config.filter.searchOn = false; 
				_this.options.items = _this.config.orginData;
				_this.setStatus('start');
				_this.setData([], 'filter', {focus: true});
			}

			if(_this.options.setting.saveStateKey !== false){
				if(typeof localStorage !=='undefined'){
					localStorage.setItem(_this.options.setting.saveStateKey || _this.prefix, JSON.stringify(_this.config.filter));
				}
			}
		})

		_this.element.container.find('.pubLogViewer-clear-btn').on('click', function (){
			$('#'+_this.prefix+'_filter_val').val('');
			applyBtn.trigger('click.publogviewer.apply');
		}) 

		// 검색
		$('#'+_this.prefix+'_filter_val').on('keydown.publogview.search',function(e) {
			if (e.keyCode == '13') {
				applyBtn.trigger('click.publogviewer.apply');
				return false;
			}
		});
		$('#'+_this.prefix+'_filter_val').val(_this.config.filter.text ||'');
		
		var lines = this.options.setting.lines ||[];

		var strHtm = [];
		strHtm.push('<option value="0">사용안함</option>');
		for(var i =0 ;i < lines.length;i++){
			var lineNum = lines[i]; 
			strHtm.push('<option value="'+lineNum+'">'+lineNum+'</option>');
		}
		
		$('#'+this.prefix+'_filter_line').empty().html(strHtm.join(''));

		$('#'+_this.prefix+'_filter_line').val(_this.config.filter.line ||'0');
	}

	,_filterData : function (data){
		var cfg = this.config; 

		var filterText = cfg.filter.text;
		var line = cfg.filter.line;
		var beforeAddLineCount = cfg.filter.beforeAddLineCount;

		var isCustomFn = cfg.filter.isCustomFn;
		var filterFn = this.options.setting.searchFilter;
		
		var schVal = filterText;
		var allSchVal;
		if(isCustomFn !== true){
			schVal = filterText.replace(/([.?*+^$[\]\\(){}])/g,"\\$1");
			allSchVal = new RegExp(schVal,'gi');
			schVal = new RegExp(schVal,'i');	
		}

		cfg.filter.currentFilterRegExp = allSchVal;
		
		var addLineCnt= beforeAddLineCount;

		for(var i =0, len=data.length; i<len; i++){
			var logItem = data[i];

			if(filterFn(logItem.orgin , schVal)){
				addLineCnt = line;
				this.options.items.push(logItem);
			}else{
				if(addLineCnt > 0){
					--addLineCnt;
					this.options.items.push(logItem);
				}
			}
		}
		this.config.filter.beforeAddLineCount = addLineCnt; 
	}
	/**
	 * @method getFindWordPostion
	 * @description get Find word position
	 */
	,getFindWordPostion : function (logText, schOrginStr, matchInfo){

		var lineStrPixel = getMeasureTextWidth(this, logText);
		return {
			lineWidth : lineStrPixel
			,startLeftPos : getLeftRatioValue(getMeasureTextWidth(this, logText.substring(0, matchInfo.matchChIdx)), lineStrPixel, lineStrPixel) 
			,endLeftPos: getLeftRatioValue(getMeasureTextWidth(this, logText.substring(0,matchInfo.matchChIdx+schOrginStr.length)), lineStrPixel, lineStrPixel)
		}
	}
	,getBodyContPosition : function (){
		var pos = this.element.bodyCont.position();
		var contLeftVal = this.config.scroll.bodyLeftPixel;
		return objectMerge(pos,{left :contLeftVal - (this.options.rowOptions.paddingPixel+2)});
	} 
	,getBodyOffset : function (){
		return this.element.body.offset();
	} 
	/**
	 * @method _initBodyEvent
	 * @description 바디 이벤트 초기화.
	 */
	,_initBodyEvent : function (){
		var _this = this;
			
		_this._setSelectionRangeInfo({isMouseDown : false});
		
		var bodyDragTimer; 
		var bodyDragDelay = 150;
		
		function dragScrollMove(){
			
			bodyDragTimer = setInterval(function() {
				if(_this.config.mouseDragDirectionY !==false){
					_this.moveVerticalScroll({pos :_this.config.mouseDragDirectionY});
				}
				
				if(_this.config.mouseScrollDirectionX !==false){
					_this.moveHorizontalScroll({pos :_this.config.mouseScrollDirectionX});
				}
			}, bodyDragDelay);
		}

		// body  selection 처리. 
		var currentMousePosition = { x: null, y: null, row : -1};
		var clickTimer;

		var clickCnt = 0, clickDelay = 400, mouseOverPixel = 5;
		
		var resetClick = function () {
			clickCnt = 0;
			currentMousePosition = {
				x: null,
				y: null
			};
		}

		// Function to wait for the next click
		function conserveClick(mousePosition) {
			currentMousePosition = mousePosition;
			clearTimeout(clickTimer);
			clickTimer = setTimeout(resetClick, clickDelay);
		}
		
		var lineSelectionFlag = false; 
		_this.element.bodyCont.on('mousedown.pubLogViewer.col','.pubLog-body-cont',function (e){
			
			if(e.which ===3){
				return true; 
			}
			
			//e.preventDefault();
			//e.stopPropagation();

			var oe = e.originalEvent.touches;
			var startPageX = oe ? oe[0].pageX : e.pageX;
			var startPageY = oe ? oe[0].pageY : e.pageY;

			var sEle = $(this)
				,rowLogEle = sEle.find('.pubLog-body-cont-log');

			var bodyOffsetPos = _this.getBodyOffset()
				,logBodycontPosition = _this.getBodyContPosition();

			var _l  = bodyOffsetPos.left
				,_r = _l + _this.config.bodyWidth;
			var _t = bodyOffsetPos.top,
				_b = _t+_this.config.container.bodyHeight;
			
			var row = intValue(sEle.attr('rowinfo'))
				,rowLogEleWidth = rowLogEle.width()
				,startLeftPos = startPageX - _l
				,endLeftPos = startLeftPos
				, selIdx = _this.config.scroll.viewIdx+row;
			
			if(row==0){
				_this.setContainerMargin(0);
			}

			var mousePosition ={x : startPageX, y : startPageY, row : row};

			var clickPosInfo = _this.getMeasureChar(startLeftPos+logBodycontPosition.left, rowLogEleWidth, selIdx);

			//console.log(rowLogEleWidth, " row : "+row ,logBodycontPosition,' clickPosInfo : ',startPageX, clickPosInfo)

			if(clickPosInfo===false) return ; 
	
			var chIdx = clickPosInfo.chIdx;
			var startChIdx = -1, endChIdx = -1; 
		
			if (clickCnt && currentMousePosition.row == row && (currentMousePosition.x-mouseOverPixel <=  mousePosition.x && mousePosition.x <= currentMousePosition.x+mouseOverPixel)) {
				++clickCnt;

				var item = _this.options.items[selIdx];
				var lineStr = item.orgin;
				var lineStrLen = lineStr.length;
				var lineStrPixel = clickPosInfo.lineStrWidth;

				if (clickCnt == 2) { //double click
					if(!e.shiftKey){
					
						startChIdx =chIdx==0 ? 0:-1;
						endChIdx = chIdx == lineStrLen-1 ? lineStrLen-1 : -1; 

						for(var i =0 ;i <=lineStrLen;i++){
							if(startChIdx == -1 && !isWordChar(lineStr.charAt(chIdx - (i+1))) ){
								startChIdx = chIdx - i;
							}
							
							if(endChIdx == -1 && !isWordChar(lineStr.charAt(chIdx + (i+1))) ) {
								endChIdx = chIdx + (i+1);
							}

							if(startChIdx != -1 && endChIdx != -1) break; 
						}

						if(endChIdx==-1){
							endChIdx = lineStrLen;
						}

						startLeftPos = getLeftRatioValue(getMeasureTextWidth(_this ,lineStr.substring(0,startChIdx)) , lineStrPixel , rowLogEleWidth);
						endLeftPos = getLeftRatioValue(getMeasureTextWidth(_this ,lineStr.substring(0,endChIdx)) , lineStrPixel , rowLogEleWidth);
					}
				} else { // triple click
					lineSelectionFlag = true; 
					startChIdx = 0;
					endChIdx = lineStrLen; 
					startLeftPos = 0; 
					endLeftPos = rowLogEleWidth;

					resetClick();
				}
				conserveClick(mousePosition);
			} else { //single click
				++clickCnt;

				startChIdx = chIdx;
				endChIdx = chIdx; 
				startLeftPos = clickPosInfo.left; 
				endLeftPos = startLeftPos;

				conserveClick(mousePosition);
			}
			var containerTop = _t - _this.config.container.margin;
			// mouse darg scroll
			$(document).on('touchmove.pubLogViewer.body.drag mousemove.pubLogViewer.body.drag', function (e1){

				var oe1 = e1.originalEvent.touches;
				var movePageX = oe1 ? oe1[0].pageX : e1.pageX;
				var movePageY = oe1 ? oe1[0].pageY : e1.pageY;

				_this.config.bodyDrag = true; 
				
				_this.config.mouseScrollDirectionX =false;
				var moveLeftPos; 
				if(movePageX < _l){
					moveLeftPos = 0 ; 
					_this.config.mouseScrollDirectionX = 'L';
				}else if(movePageX > _r){
					moveLeftPos = movePageX - _l ; 
					_this.config.mouseScrollDirectionX = 'R';
				}else{
					moveLeftPos = movePageX - _l ; 
				}
			
				_this.config.mouseDragDirectionY = false;
				
				var moveRowIdx =0; 
				if(movePageY < _t){
					moveRowIdx =0;
					_this.config.mouseDragDirectionY = 'U';
				}else if(movePageY > _b){
					moveRowIdx = _this.config.scroll.viewCount-1;
					_this.config.mouseDragDirectionY = 'D';
				}else{
					moveRowIdx = Math.floor((movePageY- containerTop) / _this.config.rowHeight);
				}
			
				if(!bodyDragTimer) dragScrollMove();

				logBodycontPosition = _this.getBodyContPosition();
				
				var moveIdx = _this.config.scroll.viewIdx + moveRowIdx
					,rowLogEleWidth = _this.element.bodyCont.find('.pubLog-body-cont[rowinfo="'+moveRowIdx+'"] .pubLog-body-cont-log').width();
				
				var movePosInfo = _this.getMeasureChar(moveLeftPos + logBodycontPosition.left, rowLogEleWidth, moveIdx);

				if(movePosInfo !== false){
					_this._setSelectionRangeInfo({
						rangeInfo :{endIdx: moveIdx, endLeft: movePosInfo.left , endChIdx : movePosInfo.chIdx}
					},false , true);
				}
			}).on('touchend.pubLogViewer.body.drag mouseup.pubLogViewer.body.drag mouseleave.pubLogViewer.body.drag', function (e1){
				$(document).off('touchmove.pubLogViewer.body.drag mousemove.pubLogViewer.body.drag').off('touchend.pubLogViewer.body.drag mouseup.pubLogViewer.body.drag mouseleave.pubLogViewer.body.drag');
				clearInterval(bodyDragTimer);
				bodyDragTimer = null; 
				_this.config.bodyDrag = false; 
			});

			if(e.shiftKey && _this.config.selection.startCell.startIdx > 0) {// shift key
				_this._setSelectionRangeInfo({
					rangeInfo :{endIdx: selIdx, endLeft: endLeftPos ,endChIdx : endChIdx}
				},false , true);

			}else{
				_this._setSelectionRangeInfo({
					rangeInfo : {startIdx : selIdx, endIdx : selIdx, startLeft : startLeftPos, endLeft: endLeftPos, startChIdx: startChIdx ,endChIdx : endChIdx}
					,isSelect : true
					,allSelect : false
					,isMouseDown : true
					,startCell : {startIdx : selIdx, startLeft : startLeftPos , startChIdx: startChIdx}
				}, true, true);
			}

			window.getSelection().removeAllRanges(); // selection area remove; 

			// hidden row up
			if(row +1 > _this.config.scroll.insideViewCount){
				_this.moveVerticalScroll({pos: 'D'});
			}
			var scrollLeftVal= _this._getScrollLeft(startLeftPos); 

			if(lineSelectionFlag !==true && scrollLeftVal < _this.config.scroll.left){
				_this.moveHorizontalScroll({pos: scrollLeftVal});
			}

			lineSelectionFlag = false; 
		
			return true;
		})

		_this.element.pubLogViewer.on('mouseup.'+_this.prefix,function (e) {
			_this._setSelectionRangeInfo({isMouseDown : false});
		}).on('mousedown.'+_this.prefix,function (e){ // focus in
			_this.config.focus = true;
		}).on('blur.'+_this.prefix,function (e){ //blur focus out
			_this.config.focus = false;
		})
		
		// focus out
		$(document).on('mousedown.'+_this.prefix, 'html', function (e) {
			if(!_this.config.focus){
				return true; 
			}

			if(e.which !==2 && $(e.target).closest('#'+_this.prefix+'_pubLogViewer').length < 1){
				_this.config.focus = false;
			}
		});
	
		// window keydown 처리.  tabindex 처리 확인 해볼것.
		$(window).on("keydown." + _this.prefix, function (e) {

			if(!_this.config.focus) return true;
			
			var evtKey = window.event ? e.keyCode : e.which;

			if(evtKey == 114){ // f3
				_$search.search(_this, 'next');
				return false; 
			}

			if(evtKey == 115){ // f4
				_$search.search(_this, 'prev');
				return false; 
			}

			var tagName = e.target.tagName;
			if(tagName.search(/(input|textarea|select)/gi) > -1){
				return true;
			}
			
			if (e.metaKey ||  e.ctrlKey) { 
				var tagName = e.target.tagName;
				
				if (evtKey == 67) { // ctrl+ c
					try{
						_this.copyData();
					}catch(e){
						console.log('Unable to copy', e);					
					}
					return false; 
				}else if(evtKey==65){ // ctrl + a
					_this.allItemSelect();
					return false; 					
				}else if(evtKey==70){ // ctrl + f
					var copyData = _this.selectionData();
					_this.element.searchText.val(copyData);
					_this.element.searchText.focus();
					return false; 	
				}
			}

			if( (32 < evtKey && evtKey <41) || evtKey == 13 || evtKey == 9){
				e.preventDefault();
				e.stopPropagation();

				_this.gridKeyCtrl(e, evtKey);
			}
		
		});
	}
	,gridKeyCtrl : function (evt, evtKey){
		var _this  =this;

		var rangeInfo = _this.config.selection.range
			,scrollInfo = _this.config.scroll
			,dataInfo =_this.config.dataInfo

		var endIdx = rangeInfo.endIdx
			,endChIdx = rangeInfo.endChIdx
			,endLeft = rangeInfo.endLeft; 
		
		if(endIdx < 0 || endIdx > _this.options.items.length) return; 

		var logText = _this.options.items[endIdx].orgin
			,logTextLen = logText.length;
		
		switch(evtKey){
			case 34 : // PageDown
			case 40 : { //down
				
				if(endIdx+1 >= dataInfo.orginRowLen){
					if((endIdx > scrollInfo.viewIdx+scrollInfo.insideViewCount)){
						_this.moveVerticalScroll({pos: 'M',rowIdx :endIdx, isKeyDown : true });
					}
					return ; 
				}

				var moveRow = (evtKey ==34 ? scrollInfo.insideViewCount: 1);  
				var moveRowIdx = (endIdx +moveRow);

				moveRowIdx = moveRowIdx >= dataInfo.orginRowLen ? dataInfo.orginRowLen-1 : moveRowIdx;

				if((moveRowIdx -scrollInfo.viewIdx) >= scrollInfo.insideViewCount){
					_this.moveVerticalScroll({pos:'D', speed: moveRow, isKeyDown : true});
				}

				var positionInfo = _this.getMeasureChar(endLeft, _this.element.bodyCont.find('[rowinfo="'+(moveRowIdx-scrollInfo.viewIdx)+'"] .pubLog-body-cont-area').width(), moveRowIdx);
				_$util.setRangeInfo(_this, evt, moveRowIdx, positionInfo.chIdx, endLeft);
			
				break;
			}
			case 33 :  //PageUp
			case 38 : { //up
				
				if(endIdx <= 0){
					if(endIdx < scrollInfo.viewIdx){
						_this.moveVerticalScroll({pos: 'M',rowIdx :endIdx, isKeyDown : true });
					}			
					return ; 
				}
				var moveRow = (evtKey ==33 ? scrollInfo.insideViewCount: 1);  
				var moveRowIdx = (endIdx - moveRow);
				
				moveRowIdx = moveRowIdx > 0 ? moveRowIdx : 0;

				if(moveRowIdx < scrollInfo.viewIdx){
					_this.moveVerticalScroll({pos:'U',speed :moveRow, isKeyDown : true});
				}

				var positionInfo = _this.getMeasureChar(endLeft, _this.element.bodyCont.find('[rowinfo="'+(moveRowIdx-scrollInfo.viewIdx)+'"] .pubLog-body-cont-area').width(), moveRowIdx);
				_$util.setRangeInfo(_this, evt, moveRowIdx, positionInfo.chIdx, endLeft);

				break; 
			}
			case 36 : // Home
			case 37 : { //left

				var moveChIdx = _$util.getMoveChIdx(_this, evt, evtKey, endChIdx, logText, 'left');
				moveChIdx = moveChIdx > logTextLen ?  logTextLen : moveChIdx;
				
				var leftPos = 0;
				var lineMoveFlag = false; 
				if(moveChIdx < 0){
					endIdx = endIdx-1;
					var newLogText = _this.options.items[endIdx].orgin; 
					moveChIdx = newLogText.length; 
					leftPos = getMeasureTextWidth(this, newLogText);
					lineMoveFlag = true; 
				}else if(moveChIdx > 0){
					leftPos = getMeasureTextWidth(this, logText.substring(0, moveChIdx));
				}

				if(endIdx < 0){
					return ; 
				}

				_this.setCursorPosition((endIdx *_this.config.rowHeight), leftPos , true);

				_this.moveHorizontalScroll({pos: _this._getScrollLeft(leftPos, lineMoveFlag ?'right': 'left'), drawFlag:false});

				_$util.setRangeInfo(_this, evt, endIdx, moveChIdx, leftPos);
				
				if(_$util.insideScrollCheck(_this, endIdx, scrollInfo, endIdx, 'left')){ // 스크롤 밖에 있을때
					return ; 
				}

				break; 
			}
			case 35 : // End
			case 39 : { //right
				
				var moveChIdx = _$util.getMoveChIdx(_this, evt, evtKey, endChIdx, logText, 'right');

				var leftPos =0;
				var moveRowIdx;
				var lineMoveFlag = false; 
				if(moveChIdx > logText.length){
					endIdx = endIdx+1;
					moveChIdx = 0; 
					moveRowIdx = endIdx +1;
					lineMoveFlag = true; 
				}else{
					leftPos = getMeasureTextWidth(this, logText.substring(0, moveChIdx));
				}

				if(moveRowIdx > dataInfo.orginRowLen){
					return ; 
				}

				_this.moveHorizontalScroll({pos: _this._getScrollLeft(leftPos, lineMoveFlag ?'left': 'right'), drawFlag:false});

				_this.setCursorPosition((endIdx *_this.config.rowHeight), leftPos , true);

				_$util.setRangeInfo(_this, evt, endIdx, moveChIdx, leftPos);
				
				if(_$util.insideScrollCheck(_this, endIdx, scrollInfo, moveRowIdx, 'right')){ // 스크롤 밖에 있을때
					return ; 
				}
						
				break; 
			}
			
			default:{
				break; 
			}
		}
	}
	,copyData : function (){
		var copyData = this.selectionData();
		copyStringToClipboard(this.prefix, copyData);
	}
	// 위치 계산
	,getMeasureChar : function (_l , elePixelW, idx){
		if(idx >= this.options.items.length){
			return false;
		}

		var item = this.options.items[idx]
			, orginText = item.orgin;
					
		var lineStrWidth = getMeasureTextWidth(this, orginText);

		if(_l >= elePixelW){
			return {
				left : getLeftRatioValue (lineStrWidth , lineStrWidth , elePixelW)
				,chIdx : orginText.length
				,lineStrWidth : lineStrWidth
			}
		}

		var charIdx = getCharIdx(this, _l, orginText, elePixelW);

		return {
			left : getLeftRatioValue (getMeasureTextWidth(this, orginText.substring(0,charIdx)), lineStrWidth, elePixelW)
			,chIdx : charIdx
			,lineStrWidth : lineStrWidth
		}
	}
	/**
	 * @method _setBodyEvent
	 * @description body event setting
	 */
	,_setBodyEvent : function (){
		var _this =this; 

		var logFileName = this.options.logFileName;

		var contextMenu =[
			{key : "removeFilter" , "name": "Filter clear"}
			,{divider : true}
			,{key : "copy" , "name": "Copy", hotkey:'Ctrl+C'}
			,{key : "allSelection" , "name": "전체선택", hotkey:'Ctrl+A'}
			,{key : "download" , "name": "다운로드"}
			,{key : "selection_download" , "name": "선택영역 다운로드"}
			,{divider : true}
			//,{key : "newWindow" , "name": "New Window View"}
			//,{divider : true}
		];

		var addContextMenu = this.options.contextMenu;
		var customMenuInfo = {};
		if($.isArray(addContextMenu)){
			for(var i =0; i <addContextMenu.length; i++){
				contextMenu.push(addContextMenu[i]);
			}
			contextMenu.push({divider : true});
		}

		contextMenu.push({key : "pageClear" , "name": "Page clear"});
		contextMenu.push({key : "clearLog" , "name": "All clear"});
			
		this.config.bodyContextMenu = $.pubContextMenu('#'+this.prefix+'_bodyContainer',{
			callback: function(key,sObj) {
			
				switch (key) {
					case 'copy': 
						_this.copyData();
						break;
					case 'download': 
						fileDownload(logFileName + ".log" ,_this.selectionData(true));
						break; 
					case 'selection_download':
						var data = _this.selectionData();

						fileDownload(logFileName + "-selection.log",data);
						break; 
					case 'allSelection': 
						_this.allItemSelect();
						break; 
					case 'pageClear': 
						_this.clearLog('page');
						break; 		
					case 'clearLog': 
						_this.clearLog();
						break; 	
					case 'removeFilter': 
						_this.element.container.find('.pubLogViewer-clear-btn').trigger('click');
						break; 	
					case 'newWindow': 
						_this.openNewWindow();
						break; 	
					default:
						break;
				}
			}
			,items: contextMenu
		});
	}
	,openNewWindow : function (){
		var contHtm = [];
		contHtm.push('<!doctype html><html dir="ltr"><head>');
		contHtm.push('<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /><meta charset="UTF-8" />');
		contHtm.push('<style>html,body{width:100%;height:100%;} body{margin:0px;padding:10px;overflow:hidden;font-size:12pt;font-family:"NotoSansR", Malgun Gothic, "맑은고딕", sans-serif;}');
		if(this.options.theme=='dark'){
			contHtm.push('pre{ background-color: #262729; color: #c5c3c3;}');
		}else{
			contHtm.push('pre{ background-color: #f3f3f3; color: #000;}');
		}

		contHtm.push('</style></head><body>');
		contHtm.push('<pre id="logViewArea" style="margin:0px;overflow: auto;position: absolute;height: calc(100% - 40px);width: calc(100% - 40px);box-shadow: .1;font-size: 13px;line-height: 18px;line-break: anywhere;border-radius: .25em;padding: 10px;white-space: pre-wrap;/* text-align: justify; */"></pre>');
		contHtm.push('<script>');
		contHtm.push('</script>');
		contHtm.push('</body></html>');

		var tmpPopupObj=window.open('about:blank', this.prefix+'_popup', 'width=1024px,height=768px');
		
		try{
			try{tmpPopupObj.document.open();}catch(e){console.log(e)}
			tmpPopupObj.document.write(contHtm.join(''));
			tmpPopupObj.document.getElementById('logViewArea').innerHTML = this.selectionData();
			tmpPopupObj.focus();
			try{tmpPopupObj.document.close();}catch(e){console.log(e)}
			
		}catch(e){
			console.log(e);

			tmpPopupObj=window.open('about:blank', this.prefix+'_popup', 'width=1024px,height=768px');
			try{
				try{tmpPopupObj.document.open();}catch(e){console.log(e)}
				tmpPopupObj.document.write(contHtm.join(''));
				tmpPopupObj.focus();
				try{tmpPopupObj.document.close();}catch(e){console.log(e)}
			}catch(e1){
				console.log(e1);
			}
		}		
	}
	,clearLog : function (mode){
		if(mode =='page'){
			var blankArr = [];
			for(var i =0 ;i <this.config.scroll.viewCount;i++ ){
				blankArr.push('');
			}
			this.setStatus('start');
			this.setData(blankArr,'addData', {focus:true});
		}else{
			this.setData([],'reDraw');
		}
	}
	,allItemSelect : function (){
		this.config.selection.allSelect = true; 
		this._setCellSelect(false);
		return ; 
	}
	// set curosr position 
	,setCursorPosition :function(top, left ,showHide){
		var cursorEle = this.element.cursorEle;

		showHide = this.config.selection.isMouseDown ? false : showHide;

		if(showHide === false){
			if(this.config.cursorShow !== false){
				cursorHide(this, cursorEle);
			}
			return ; 
		}

		var _this =this; 

		var currInfo = this.config.selection.range;
		
		if(currInfo.startIdx > 0){
			if(currInfo.startIdx == currInfo.endIdx && currInfo.startChIdx == currInfo.endChIdx){
				;
			}else{
				cursorHide(this, cursorEle);
				return ; 
			}
		}

		var cssInfo ={};
		
		if(top != null && top > -1){
			cssInfo.top = top+'px';
		}

		if(left != null && left > -1){
			cssInfo.left = left+'px';
		}

		if(showHide){
			this.config.cursorView = true;
			this.config.cursorShow = true;
			cursorEle.css(cssInfo).show();
						
			if(_this.config.cursorTimer ==null){
				_this.config.cursorTimer = setInterval(function() {
					if(_this.config.focus  && _this.config.bodyDrag !== true){
						 
						if(_this.config.cursorShow){
							cursorEle.hide();
							_this.config.cursorShow = false;
						}else{
							cursorEle.show();
							_this.config.cursorShow = true;
						}
					}else{
						clearTimeout(_this.config.cursorTimer);
						_this.config.cursorTimer = null; 
						_this.config.cursorView = false;
						cursorEle.hide();
					}
				}, 550);
			}
		}
	}
	/**
	 * @method _isAllSelect
	 * @description cell select  
	 */
	,_isAllSelect : function (){
		return this.config.selection.allSelect;
	}
	,_setSelectionRangeInfo : function(selectionInfo, initFlag, selectFlag){
		var _this =this; 
		var cfgSelect = this.config.selection;
		_this.config.focus = true; 

		if(initFlag !==true  && _this._isAllSelect()){
			return ; 
		}

		var	rangeInfo = selectionInfo.rangeInfo;
		
		if(initFlag === true){
		
			var initOpt =objectMerge({
				curr: 0
				,range: {startIdx: -1, endIdx: -1, startLeft: -1, endLeft: -1, startChIdx: -1,endChIdx: -1}
				,isSelect: false
				,isMouseDown:false
				,isFindWord: false
				,unSelectPosition: {}
				,allSelect: false
				,minIdx: -1, maxIdx: -1
				,minChIdx: -1, maxChIdx: -1
				,minLeft: -1, maxLeft: -1
				,startCell: {startIdx: -1, startLeft: -1}
				,rangeTimer: -1
			}, selectionInfo)

			cfgSelect = this.config.selection = initOpt;
		}else{
			cfgSelect = objectMerge(cfgSelect, selectionInfo)
		}
		
		var currInfo = cfgSelect.range;

		for(var key in rangeInfo){
			currInfo[key] = rangeInfo[key];
		}

		if(currInfo.startIdx > -1 && currInfo.endIdx == cfgSelect.startCell.startIdx && currInfo.endLeft == cfgSelect.startCell.startLeft){
			this.setCursorPosition(( currInfo.startIdx-_this.config.scroll.viewIdx) *_this.config.rowHeight,currInfo.startLeft , true);
		}
		
		delete cfgSelect.rangeInfo;

		if(isUndefined(rangeInfo)) return ; 
		
		currInfo.minIdx = Math.min(currInfo.startIdx, currInfo.endIdx)
		currInfo.maxIdx = Math.max(currInfo.endIdx ,currInfo.startIdx);

		if(currInfo.minIdx  == currInfo.maxIdx){

			currInfo.minChIdx = Math.min( currInfo.startChIdx , currInfo.endChIdx);
			currInfo.maxChIdx = Math.max( currInfo.startChIdx , currInfo.endChIdx);

			currInfo.minLeft = Math.min( currInfo.endLeft , currInfo.startLeft);
			currInfo.maxLeft = Math.max( currInfo.endLeft , currInfo.startLeft);
		}else if(currInfo.startIdx == currInfo.maxIdx){

			currInfo.minChIdx = currInfo.endChIdx;
			currInfo.maxChIdx = currInfo.startChIdx;
			
			currInfo.minLeft = currInfo.endLeft;
			currInfo.maxLeft = currInfo.startLeft;

		}else if(currInfo.startIdx == currInfo.minIdx){
			
			currInfo.minChIdx = currInfo.startChIdx;
			currInfo.maxChIdx = currInfo.endChIdx;

			currInfo.minLeft = currInfo.startLeft;
			currInfo.maxLeft = currInfo.endLeft;
		}

		if(selectFlag){
			clearTimeout(cfgSelect.rangeTimer);
			cfgSelect.rangeTimer= setTimeout(function() {
				_this._setCellSelect(initFlag);
			}, 2);
		}
	}
	/**
	 * @method _setCellSelect
	 * @description cell select  
	 */
	,_setCellSelect : function (initFlag) {
		var _this =this; 
		
		var itemIdx = _this.config.scroll.viewIdx;
		var viewCount = _this.config.scroll.viewCount;

		var rowEle, selectionEle;

		for(var i =0 ; i < viewCount; i++){
			rowEle =$pubSelector('#'+_this.prefix+'_bodyCont>.pubLog-body-cont[rowinfo="'+i+'"]');
			selectionEle = rowEle.querySelector('.pubLog-body-cont-overlay');

			this.setSelectPosition(itemIdx, selectionEle);
						
			rowEle= null; 
			selectionEle= null; 
			itemIdx++;
		}
	}
	/**
	 * @method setSelectPosition:
	 * @description cell 선택 여부
	 */
	,setSelectPosition: function (itemIdx, ele, matchWordInfos){
		var selectionInfo = this.config.selection; 
		var rangeInfo = selectionInfo.range; 
		
		var selectdTextEle = ele.querySelector('.selected-text'); 
		var matchWordEle = ele.querySelector('.match-word');
		if(matchWordInfos===false){
			matchWordEle.innerHTML = '';
			
		}else if(matchWordInfos){
			var strHtm = [];
			
			for(var i =0 ;i < matchWordInfos.length; i++){
				var matchItem = matchWordInfos[i];
				
				var matchWordPosition =this.getFindWordPostion(this.options.items[itemIdx].orgin, matchItem.matchword, {
					searchIdx : itemIdx
					,matchChIdx : matchItem.matchChIdx
				});

				strHtm.push('<div class="word-highlight '+matchItem.matchType+'" style="left:'+matchWordPosition.startLeftPos+'px;width:'+(matchWordPosition.endLeftPos - matchWordPosition.startLeftPos)+'px;"></div>');
			}
			matchWordEle.innerHTML = strHtm.join('');
		}

		if(this.config.selection.allSelect ===true){
			selectdTextEle.removeAttribute('style');
			selectdTextEle.setAttribute('style','left:0px;width:100%;');
			return ; 
		}

		if(rangeInfo.minIdx <= itemIdx && itemIdx <= rangeInfo.maxIdx){
			selectdTextEle.removeAttribute('style');
			
			selectdTextEle.setAttribute('style','left:0px;width:100%;');
			
			if(rangeInfo.minIdx == rangeInfo.maxIdx){
				selectdTextEle.setAttribute('style','left:'+rangeInfo.minLeft+'px;width:'+ (rangeInfo.maxLeft - rangeInfo.minLeft) +'px;');
			}else if(rangeInfo.minIdx == itemIdx){
				selectdTextEle.setAttribute('style','left:'+rangeInfo.minLeft+'px;width:calc(100% - '+ (rangeInfo.minLeft) +'px);');
			}else if(rangeInfo.maxIdx == itemIdx){
				selectdTextEle.setAttribute('style','left:0px;width:calc('+ (rangeInfo.maxLeft) +'px);');
			}	
		}else{
			selectdTextEle.removeAttribute('style');
		}
		return false; 
	}

	/**
	 * @method selectionData
	 * @description select data 구하기.
	 */
	,selectionData : function (allSelectFlag) {
		var _this = this; 

		var sIdx,eIdx, sChIdx , eChIdx;

		allSelectFlag = allSelectFlag===true ?true : _this._isAllSelect(); 
		
		if(allSelectFlag){
			sIdx= 0;
			eIdx = _this.config.dataInfo.lastRowIdx;
		}else{
			var colInfo = _this.config.selection.range;
			sIdx= colInfo.minIdx;
			eIdx =  colInfo.maxIdx;
			sChIdx = colInfo.minChIdx;
			eChIdx = colInfo.maxChIdx;
		}

		if(sIdx < 0 || eIdx < 0) return ''; 

		var returnVal = [];

		var items = _this.options.items;

		for(var i = sIdx ; i <= eIdx ; i++){
			var item = items[i];
			var log = item.orgin;
			if(allSelectFlag){
				returnVal.push(log);			
			}else{
			
				if(sIdx == eIdx){
					returnVal.push(log.substring(sChIdx, eChIdx));
				}else if(sIdx == i){
					returnVal.push(log.substring(sChIdx, item.length));
				}else if(eIdx == i){
					returnVal.push(log.substring(0, eChIdx));
				}else{
					returnVal.push(log);
				}
			}
		}
		return returnVal.join('\n');
	}
	,getGridWidth : function(){
		return this.options.width =='auto' ? this.logElement.width() : this.options.width;
	}
	,getGridHeight : function (){
		return this.options.height =='auto' ? this.logElement.height() : this.options.height;
	}
	,_setGridContainerWidth : function (width, mode){
		if(mode=='headerResize'){
			this.config.container.width = width;
		}else{
			this.config.container.width = width- this.config.blankSpaceWidth;		// border 값 빼주기.
		}

		this.config.bodyWidth = this.config.container.width-this.options.scroll.vertical.width;
	}
	/**
	 * @method addStyleTag
	 * @param options {Object} - 데이타 .
	 * @description  add style tab
	 */
	,addStyleTag : function (){
		var _this = this
			,_d = document; 
		
		var cssStr = [];
		
		var rowOptHeight = _this.options.rowOptions.height; 

		if(!isNaN(rowOptHeight)){
			cssStr.push('#'+_this.prefix+'_pubLogViewer .pub-body-td, #'+_this.prefix+'_pubLogViewer .pub-body-aside-td{height:'+rowOptHeight+'px;}');
		}
		
		var styleId = _this.prefix+'_pubLogViewerStyle'; 
		var styleTag = document.getElementById(styleId);

		if(styleTag){
			if (styleTag.styleSheet) {
				styleTag.styleSheet.cssText = cssStr.join('');
			} else {
				styleTag.innerHTML = cssStr.join('');
			}
		}else{
			styleTag = _d.createElement('style');
		
			_d.getElementsByTagName('head')[0].appendChild(styleTag);
			styleTag.setAttribute('id', styleId);
			styleTag.setAttribute('type', 'text/css');

			if (styleTag.styleSheet) {
				styleTag.styleSheet.cssText = cssStr.join('');
			} else {
				styleTag.appendChild(document.createTextNode(cssStr.join('')));
			}
		}
		styleTag = null; 
	}
	,_windowResize :function (){
		var _this = this;

		if(_this.options.autoResize.enabled === false) return ; 

		var _evt = $.event,
			_special,
			resizeTimeout,
			eventName =  _this.prefix+"pubLogViewerResize"; 

		_special = _evt.special[eventName] = {
			setup: function() {
				$( this ).on( "resize.pubLogViewer", _special.handler );
			},
			teardown: function() {
				$( this ).off( "resize.pubLogViewer", _special.handler );
			},
			handler: function( event, execAsap ) {
				// Save the context
				var context = this,
					args = arguments,
					dispatch = function() {
						// set correct event type
						event.type = eventName;
						_evt.dispatch.apply( context, args );
					};

				if ( resizeTimeout ) {
					clearTimeout( resizeTimeout );
				}

				execAsap ?
					dispatch() :
					resizeTimeout = setTimeout( dispatch, _special.threshold );
			},
			threshold: _this.options.autoResize.threshold
		};
		$(window).off(eventName);
		$(window).on(eventName, function( event ) {
			_this.resizeDraw();
		});
	}
	/**
	 * @method resizeDraw
	 * @description resize 하기
	 */
	,resizeDraw :function (opt){
		if(opt){
			if(!$.isNumeric(opt.height)){
				delete opt.height; 
			}

			if(!$.isNumeric(opt.width)){
				delete opt.width; 
			}
		}

		this.calcDimension('resize',opt);
		return ; 
	}

	/**
	 * @method setTheme
	 * @description set theme
	 */
	,setTheme : function (themeName){
		this.logElement.attr('pub-theme', themeName);
		this.options.theme = themeName;
	}
	
	/**
	 * @method getTheme
	 * @description get theme
	 */
	,getTheme : function (){
		return this.options.theme;
	}
	
	/**
	 * @method destroy
	 * @description 해제.
	 */
	,destroy:function (){
		
		$(window).off(this.prefix+"pubLogViewerResize").off("keydown." + this.prefix);

		this.logElement.find('*').off();
		$._removeData(this.logElement);

		delete _datastore[this.selector];
		$(this.selector).empty(); 
		//this = {};
	}
	,getDataStore :function (){
		return _datastore; 
	}
};

var ctrlSpecialCaseWordChar = /[\"\.\s:`~()\[<>\\\'?;={}]/; // ctrl + left or right check 특수문자.
var _$util = {
	 setRangeInfo :function(ctx, evt, endIdx, moveChIdx, leftPos){

		var startChIdx=moveChIdx, endChIdx= moveChIdx; 

		if(evt.shiftKey && ctx.config.selection.startCell.startIdx > 0) {// shift key
			ctx._setSelectionRangeInfo({
				rangeInfo :{endIdx: endIdx, endLeft: leftPos ,endChIdx : endChIdx}
			},false , true);

		}else{
			ctx._setSelectionRangeInfo({
				rangeInfo : {startIdx : endIdx, endIdx : endIdx, startLeft : leftPos, endLeft: leftPos, startChIdx: startChIdx ,endChIdx : endChIdx}
				,isSelect : true
				,allSelect : false
				,startCell : {startIdx : endIdx, startLeft : leftPos , startChIdx: startChIdx}
			}, true, true);
		}			
	}
	// cursor scroll inside check
	,insideScrollCheck : function(ctx, endIdx, scrollInfo, moveRowIdx, direction){
		var reFlag = false; 

		if(endIdx < scrollInfo.viewIdx || (moveRowIdx > scrollInfo.viewIdx+scrollInfo.insideViewCount)){ // 스크롤 밖에 있을때	
			if(direction=='right'){
				ctx.moveVerticalScroll({pos: 'D'});
			}else{
				ctx.moveVerticalScroll({pos: 'M', rowIdx : endIdx });
			}
			reFlag = true; 
			
		}
		return reFlag;
	}
	,getMoveChIdx : function (ctx, evt, evtKey, endChIdx, logText, arrow){
		if(evt.ctrlKey){
			
			if(evtKey == 37) { //left
				if(endChIdx == 0) return -1;

				for(var i =endChIdx-1; i > 0; i--){
					if(ctrlSpecialCaseWordChar.test(logText.charAt(i))){
						return i;
					}
				}
				return 0;
			}else if(evtKey == 39){ //right
				var logTextLen = logText.length; 

				if(endChIdx == logTextLen) return logTextLen+1;
				
				for(var i =endChIdx+1; i < logTextLen; i++){
					if(ctrlSpecialCaseWordChar.test(logText.charAt(i))){
						return i;
					}
				}
				return logTextLen;
			}
		}
		if(arrow=='right'){
			return (evtKey ==35 ? logText.length : endChIdx+1);
		}else if(arrow=='left'){
			return (evtKey ==36 ? 0 : endChIdx-1);
		}
	}
}

/**
 * search box 
 */
var _$search = {
    searchTimer : -1
    ,search : function (ctx, schMode, schOrginStr){
		var _this = this; 
		
		schOrginStr = isUndefined(schOrginStr) ?ctx.config.search.currentText:schOrginStr;
        
		ctx.config.search.currentText = schOrginStr;

		ctx.element.bodyMessage.hide();

		clearTimeout(_this.searchTimer);
									
		if($.trim(schOrginStr) == ''){
			
			ctx._setSelectionRangeInfo({}, true);
			ctx.drawGrid();
			return ; 
		}
		var caseSearch = false; 
		var caseSearchOpt = caseSearch == true?'' :'i';

		var schRegExp = schOrginStr.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
		var allRegExp = new RegExp('('+schRegExp + ')','g'+caseSearchOpt);

		if(schMode=='prev'){
			schRegExp = new RegExp(schRegExp+'(?!.*'+schRegExp + ')',caseSearchOpt);
		}else{
			schRegExp = new RegExp('('+schRegExp + ')',caseSearchOpt);
		}

		ctx.config.search.currentSchRegExp = schRegExp;
		ctx.config.search.currentAllSchRegExp = allRegExp;

		var items = ctx.options.items;
		var itemLen = items.length; 
			
		var startRowIdx = ctx.config.selection.startCell.startIdx
			,startChIdx  = ctx.config.selection.startCell.startChIdx;
		
		if(startRowIdx == ctx.config.search.beforeStartRowIdx && ctx.config.search.beforeTxt == schOrginStr){
			startRowIdx = ctx.config.search.currentSearchIdx;
			
			if(schMode=='prev'){
				startChIdx = ctx.config.search.matchChIdx;
			}else{
				startChIdx = ctx.config.search.matchChIdx + schOrginStr.length;
			}
		}
		
		startChIdx = startChIdx ? startChIdx :0;

		startRowIdx = startRowIdx < 0 ? 0: startRowIdx;

		var findFlag = false; 

		var logItem; 
		var matchResult; 
		if(schMode=='prev'){
			for(var i = startRowIdx ; i >= 0 ; i--){
				logItem = items[i];
				matchResult = matchWord(i, logItem.orgin ,schRegExp ,startRowIdx, startChIdx, schMode);
				if(matchResult !== false){
					findFlag = true; 
					break; 
				} 
			}

			if(!findFlag){
				for(var i = itemLen-1 ; i > startRowIdx ; i--){
					logItem = items[i];
						
					matchResult = matchWord(i, logItem.orgin ,schRegExp ,startRowIdx, startChIdx, schMode);
					if(matchResult !== false){
						findFlag = true; 
						break; 
					} 
				}	
			}
		}else{

			for(var i = startRowIdx ; i < itemLen ; i++){
				logItem = items[i];

				matchResult = matchWord(i, logItem.orgin ,schRegExp ,startRowIdx, startChIdx, schMode);
				if(matchResult !== false){
					findFlag = true; 
					break; 
				} 
			}

			if(!findFlag){
				for(var i = 0 ; i < startRowIdx ; i++){
					logItem = items[i];

					matchResult = matchWord(i, logItem.orgin ,schRegExp ,startRowIdx, startChIdx, schMode);
					if(matchResult !== false){
						findFlag = true; 
						break; 
					} 
				}
			}
		}

		if(findFlag !==true){
			ctx._setSelectionRangeInfo({}, true);
			ctx.config.search.currentSearchIdx = startRowIdx;
			ctx.config.search.matchChIdx = startChIdx;
			ctx.drawGrid();
			
			ctx.element.bodyMessage.empty().html(ctx.options.i18n['string.not.found']);
			ctx.element.bodyMessage.show();
			
			_this.searchTimer = setTimeout(function (){
				ctx.element.bodyMessage.fadeOut();
			}, 1000);
								
			return ; 
		}else{
			
			var logText = logItem.orgin;

			var viewIdx = ctx.config.scroll.viewIdx; 
			var maxViewIdx = viewIdx + ctx.config.scroll.insideViewCount; 

			ctx.config.search.currentSearchIdx = matchResult.searchIdx;
			ctx.config.search.matchChIdx = matchResult.matchChIdx;

			var currentSearchIdx = matchResult.searchIdx; 			
			
			if((viewIdx < currentSearchIdx && currentSearchIdx < maxViewIdx-1)) {
				ctx.drawGrid();
			}else if((viewIdx < currentSearchIdx && currentSearchIdx < maxViewIdx) && (currentSearchIdx+1 == itemLen || currentSearchIdx == 0)){
				ctx.drawGrid();
			}else {
				if(viewIdx < currentSearchIdx){
					if(maxViewIdx <= currentSearchIdx){
						ctx.moveVerticalScroll({rowIdx :( currentSearchIdx - (ctx.config.scroll.insideViewCount- 2)), drawFlag:false});	
					}else{								
						ctx.moveVerticalScroll({rowIdx :(viewIdx + (maxViewIdx -currentSearchIdx)), drawFlag:false});	
					}
				}else{
					if(viewIdx == currentSearchIdx){
						var matchLeftPos = getMeasureTextWidth(ctx, logText.substring(0, matchResult.matchChIdx + schOrginStr.length));	
						ctx.moveHorizontalScroll({pos: ctx._getScrollLeft(matchLeftPos), drawFlag:false});
					}else if(viewIdx > currentSearchIdx){
						ctx.moveVerticalScroll({rowIdx : currentSearchIdx -2, drawFlag:false});
					}else{							
						ctx.moveVerticalScroll({rowIdx :(viewIdx -1), drawFlag:false});	
					}
				}
				ctx.drawGrid();
			}

			var matchWordPosition =ctx.getFindWordPostion(logText, schOrginStr, matchResult);

			var startLeftPos = matchWordPosition.startLeftPos
				, endLeftPos = matchWordPosition.endLeftPos;
								
			if(endLeftPos > ctx.config.bodyWidth ){
				ctx.moveHorizontalScroll({pos: ctx._getScrollLeft(endLeftPos), drawFlag:false});
			}else{
				ctx.moveHorizontalScroll({pos: 0, drawFlag:false});
			}

			ctx._setSelectionRangeInfo({
				rangeInfo : {startIdx : currentSearchIdx, endIdx : currentSearchIdx, startLeft : startLeftPos, endLeft: endLeftPos, startChIdx: ctx.config.search.matchChIdx ,endChIdx : ctx.config.search.matchChIdx+schOrginStr.length}
				,isSelect : true
				,allSelect : false
				,isFindWord : true
				,startCell : {startIdx : currentSearchIdx, startLeft : startLeftPos , startChIdx: ctx.config.search.matchChIdx}
			}, true, true);
		}

		ctx.config.search.beforeStartRowIdx = ctx.config.selection.startCell.startIdx;
		ctx.config.search.beforeTxt= schOrginStr;
	}
}

var _$template = {
	/**
	 * @method getTemplateHtml
	 * @description header html 
	 */
	getTemplateHtml : function (ctx){
		
		var vScrollW = ctx.options.scroll.vertical.width
			,hScrollH = ctx.options.scroll.horizontal.height
			,vArrowWidth = vScrollW	- 2
			,hArrowWidth = hScrollH - 2;

		return '<div class="pubLogViewer-wrapper"><div id="'+ctx.prefix+'_pubLogViewer" class="pubLogViewer pubLogViewer-noselect" tabindex="-1"  style="outline:none !important;overflow:hidden;width:'+ctx.config.container.width+'px;">'
			+' 	<div id="'+ctx.prefix+'_container" class="pubLogViewer-container " style="overflow:hidden;">'
			+'    <div class="pubLogViewer-setting-wrapper pubLogViewer-layer" data-pubLogViewer-layer="'+ctx.prefix+'"><div class="pubLogViewer-setting"><svg version="1.1" width="'+vArrowWidth+'px" height="'+vArrowWidth+'px" viewBox="0 0 54 54" style="enable-background:new 0 0 54 54;">	'
			+'<g><path id="'+ctx.prefix+'_settingBtn" d="M51.22,21h-5.052c-0.812,0-1.481-0.447-1.792-1.197s-0.153-1.54,0.42-2.114l3.572-3.571	'
			+'		c0.525-0.525,0.814-1.224,0.814-1.966c0-0.743-0.289-1.441-0.814-1.967l-4.553-4.553c-1.05-1.05-2.881-1.052-3.933,0l-3.571,3.571	'
			+'		c-0.574,0.573-1.366,0.733-2.114,0.421C33.447,9.313,33,8.644,33,7.832V2.78C33,1.247,31.753,0,30.22,0H23.78	'
			+'		C22.247,0,21,1.247,21,2.78v5.052c0,0.812-0.447,1.481-1.197,1.792c-0.748,0.313-1.54,0.152-2.114-0.421l-3.571-3.571	'
			+'		c-1.052-1.052-2.883-1.05-3.933,0l-4.553,4.553c-0.525,0.525-0.814,1.224-0.814,1.967c0,0.742,0.289,1.44,0.814,1.966l3.572,3.571	'
			+'		c0.573,0.574,0.73,1.364,0.42,2.114S8.644,21,7.832,21H2.78C1.247,21,0,22.247,0,23.78v6.439C0,31.753,1.247,33,2.78,33h5.052	'
			+'		c0.812,0,1.481,0.447,1.792,1.197s0.153,1.54-0.42,2.114l-3.572,3.571c-0.525,0.525-0.814,1.224-0.814,1.966	'
			+'		c0,0.743,0.289,1.441,0.814,1.967l4.553,4.553c1.051,1.051,2.881,1.053,3.933,0l3.571-3.572c0.574-0.573,1.363-0.731,2.114-0.42	'
			+'		c0.75,0.311,1.197,0.98,1.197,1.792v5.052c0,1.533,1.247,2.78,2.78,2.78h6.439c1.533,0,2.78-1.247,2.78-2.78v-5.052	'
			+'		c0-0.812,0.447-1.481,1.197-1.792c0.751-0.312,1.54-0.153,2.114,0.42l3.571,3.572c1.052,1.052,2.883,1.05,3.933,0l4.553-4.553	'
			+'		c0.525-0.525,0.814-1.224,0.814-1.967c0-0.742-0.289-1.44-0.814-1.966l-3.572-3.571c-0.573-0.574-0.73-1.364-0.42-2.114	'
			+'		S45.356,33,46.168,33h5.052c1.533,0,2.78-1.247,2.78-2.78V23.78C54,22.247,52.753,21,51.22,21z M52,30.22	'
			+'		C52,30.65,51.65,31,51.22,31h-5.052c-1.624,0-3.019,0.932-3.64,2.432c-0.622,1.5-0.295,3.146,0.854,4.294l3.572,3.571	'
			+'		c0.305,0.305,0.305,0.8,0,1.104l-4.553,4.553c-0.304,0.304-0.799,0.306-1.104,0l-3.571-3.572c-1.149-1.149-2.794-1.474-4.294-0.854	'
			+'		c-1.5,0.621-2.432,2.016-2.432,3.64v5.052C31,51.65,30.65,52,30.22,52H23.78C23.35,52,23,51.65,23,51.22v-5.052	'
			+'		c0-1.624-0.932-3.019-2.432-3.64c-0.503-0.209-1.021-0.311-1.533-0.311c-1.014,0-1.997,0.4-2.761,1.164l-3.571,3.572	'
			+'		c-0.306,0.306-0.801,0.304-1.104,0l-4.553-4.553c-0.305-0.305-0.305-0.8,0-1.104l3.572-3.571c1.148-1.148,1.476-2.794,0.854-4.294	'
			+'		C10.851,31.932,9.456,31,7.832,31H2.78C2.35,31,2,30.65,2,30.22V23.78C2,23.35,2.35,23,2.78,23h5.052	'
			+'		c1.624,0,3.019-0.932,3.64-2.432c0.622-1.5,0.295-3.146-0.854-4.294l-3.572-3.571c-0.305-0.305-0.305-0.8,0-1.104l4.553-4.553	'
			+'		c0.304-0.305,0.799-0.305,1.104,0l3.571,3.571c1.147,1.147,2.792,1.476,4.294,0.854C22.068,10.851,23,9.456,23,7.832V2.78	'
			+'		C23,2.35,23.35,2,23.78,2h6.439C30.65,2,31,2.35,31,2.78v5.052c0,1.624,0.932,3.019,2.432,3.64	'
			+'		c1.502,0.622,3.146,0.294,4.294-0.854l3.571-3.571c0.306-0.305,0.801-0.305,1.104,0l4.553,4.553c0.305,0.305,0.305,0.8,0,1.104 '
			+'		l-3.572,3.571c-1.148,1.148-1.476,2.794-0.854,4.294c0.621,1.5,2.016,2.432,3.64,2.432h5.052C51.65,23,52,23.35,52,23.78V30.22z"/>'
			+'	<path d="M27,18c-4.963,0-9,4.037-9,9s4.037,9,9,9s9-4.037,9-9S31.963,18,27,18z M27,34c-3.859,0-7-3.141-7-7s3.141-7,7-7'
			+'		s7,3.141,7,7S30.859,34,27,34z"/></g>'
			+'</svg></div>'
			+'		<ul class="pubLogViewer-setting-area">'
			+'			<li><span>Filter</span><input type="search" id="'+ctx.prefix+'_filter_val" class="pubLogViewer-filter-field"></li>'
			+'			<li><span>'+ctx.options.i18n['setting.more.view']+'</span><select id="'+ctx.prefix+'_filter_line"></select></li>'
			+'			<li style="text-align: right;"><button type="button" class="pubLogViewer-clear-btn" style="margin-right:5px;">'+ctx.options.i18n['setting.clear']+'</button><button type="button" class="pubLogViewer-apply-btn">'+ctx.options.i18n['setting.apply']+'</button></li>'
			+'		</ul>'
			+'		</div>'
			+'		<div id="'+ctx.prefix+'_headerContainer" class="pubLogViewer-header-container">'
			+'			<div class="search-area"><input type="search" id="'+ctx.prefix+'_srh_val" name="pubLogViewer_srh_val" class="pubLogViewer-search-field">'
			+'			<div class="search-btn-area"><button type="button" class="pubLogViewer-sch-btn" data-sch-mode="next" title="'+ctx.options.i18n['search.button']+' F3"></button> '
			+'			<button type="button" class="pubLogViewer-sch-btn" data-sch-mode="prev" title="'+ctx.options.i18n['search.prev']+' F4"></button>'
			+'			<button type="button" class="pubLogViewer-status-btn">'+ctx.options.i18n['search.stop']+'</button></div></div>'
			+'		</div>'
			+' 		<div id="'+ctx.prefix+'_bodyContainer" tabindex="0" class="pubLogViewer-body-container" style="padding-bottom: '+(hScrollH-1)+'px;padding-right: '+(vScrollW-1)+'px">'
			+'				<div id="'+ctx.prefix+'_bodyMessage" class="pubLogViewer-body-message"></div>'		
			+' 				<div class="pubLogViewer-body">'
			+'					<div id="'+ctx.prefix+'_bodyCont" class="pubLogViewer-body-cont-wrapper">'
			+'					  <div id="'+ctx.prefix+'_pubLogViewerCursor" class="pubLogViewer-body-cont-cursor"></div>'
			+'						<div class="pubLog-body-cont" style="position:absolute;top:-100px;">'
			+'							<div class="pubLog-body-cont-area">'
			+'								<pre id="'+ctx.prefix+'_pubLogViewerMeasure" class="pubLog-body-cont-log hidden-measure-area"></pre>'
			+'							</div>'
			+'						</div>'
			+'					</div>'
			+'				</div>'
			+' 				<div id="'+ctx.prefix+'_vscroll" class="pubLogViewer-vscroll" style="width: '+(vScrollW-1)+'px;padding-bottom: '+hScrollH+'px">'
			+' 				  <div class="pubLogViewer-vscroll-bar-area">'
			+'					<div class="pubLogViewer-vscroll-bar-bg"></div>'
			+' 					<div class="pubLogViewer-vscroll-up pubLogViewer-vscroll-btn" data-pubLogViewer-btn="U"><svg width="'+vArrowWidth+'px" height="8px" viewBox="0 0 110 110" style="enable-background:new 0 0 100 100;"><g><polygon points="50,0 0,100 100,100" fill="#737171"/></g></svg></div>'
			+'					<div class="pubLogViewer-vscroll-bar"><div class="pubLogViewer-vscroll-bar-tip" style="right:'+(vScrollW)+'px"></div></div>'
			+' 					<div class="pubLogViewer-vscroll-down pubLogViewer-vscroll-btn" data-pubLogViewer-btn="D"><svg width="'+vArrowWidth+'px" height="8px" viewBox="0 0 110 110" style="enable-background:new 0 0 100 100;"><g><polygon points="0,0 100,0 50,90" fill="#737171"/></g></svg></div>'
			+' 				  </div>'
			+' 				</div>'
			+' 				<div id="'+ctx.prefix+'_hscroll" class="pubLogViewer-hscroll" style="height: '+(hScrollH-1)+'px;padding-right: '+vScrollW+'px">'
			+' 				  <div class="pubLogViewer-hscroll-bar-area">'
			+'					<div class="pubLogViewer-hscroll-bar-bg"></div>'
			+' 					<div class="pubLogViewer-hscroll-left pubLogViewer-hscroll-btn" data-pubLogViewer-btn="L"><svg width="8px" height="'+hArrowWidth+'px" viewBox="0 0 110 110" style="enable-background:new 0 0 100 100;"><g><polygon points="10,50 100,0 100,100" fill="#737171"/></g></svg></div>'
			+'					<div class="pubLogViewer-hscroll-bar"></div>'
			+' 				  <div class="pubLogViewer-hscroll-right pubLogViewer-hscroll-btn" data-pubLogViewer-btn="R"><svg width="8px" height="'+hArrowWidth+'px" viewBox="0 0 110 110" style="enable-background:new 0 0 100 100;"><g><polygon points="0,0 0,100 90,50" fill="#737171"/></g></svg></div>'
			+'				 </div>'
			+' 				</div>'
			+' 		</div>'
			+'      <div id="'+ctx.prefix+'_statusbar" style="height:'+ctx.options.status.height+'px" class="pubLogViewer-statusbar">'
			+'        <span class="pubLogViewer-status-icon"></span>'
			+'        <div class="pubLogViewer-message-info"></div>'
			+'        <div class="pubLogViewer-count-info"></div></div>'
			+' 	</div>'
			+' </div>'
			+' <div style="top:-9999px;left:-9999px;position:fixed;z-index:999999;">'
			+'	<canvas id="'+ctx.prefix+'_pubLogViewerCanvas"></canvas>'
			+'  <textarea id="'+ctx.prefix+'_pubLogViewerCopyArea"></textarea></div>' // copy 하기위한 textarea 꼭 위치해야함. 
			+' </div>';

	}
	,bodyRowDraw : function (ctx, mode){
    
		var bodyContEle = ctx.element.bodyCont; 
		var strHtm = [];
		for(var i =0 ; i < ctx.config.scroll.maxViewCount; i++){
	
			if(mode != 'init' && bodyContEle.find('[rowinfo="'+i+'"]').length > 0){
				if(i >= ctx.config.scroll.viewCount){
					bodyContEle.find('[rowinfo="'+i+'"]').hide();
				}else{
					bodyContEle.find('[rowinfo="'+i+'"]').show();
				}
			}else{
				strHtm.push('<div class="pubLog-body-cont" rowinfo="'+i+'">');
				strHtm.push('  <div class="pubLog-body-cont-overlay">');
				strHtm.push('	 <div class="match-word"></div>');
				strHtm.push('	 <div class="selected-text"></div>');
				strHtm.push('  </div>');
				strHtm.push('  <div class="pubLog-body-cont-area">');
				strHtm.push('	<div class="pubLog-body-cont-log"></div>');
				strHtm.push('  </div>');
				strHtm.push('</div>');
			}
		}
		
		if(mode=='init'){
			bodyContEle.append(strHtm.join(''));
			return true; 
		}else{
			strHtm = strHtm.join('');
			if(strHtm != ''){
				bodyContEle.append(strHtm);
				return true; 
			}
		}
	
		return false;
	}
}

// background click check
$(document).on('mousedown.pubLogViewer.background', function (e){
	if(e.which !==2){
		var targetEle = $(e.target);
		var pubLogViewerLayterEle = targetEle.closest('.pubLogViewer-layer');
		if(pubLogViewerLayterEle.length < 1 ){
			$('.pubLogViewer-layer.open').removeClass('open');
		}else{
			var layerid = pubLogViewerLayterEle.data('pubLogViewer-layer');
			$('.pubLogViewer-layer.open').each(function (){
				var sEle = $(this);

				if(layerid != sEle.data('pubLogViewer-layer')){
					sEle.removeClass('open');
				}
			})
		}
	}
})

$.pubLogViewer = function (selector, options, args) {
	var _cacheObject = _datastore[selector]; 

	if(isUndefined(options)){
		return _cacheObject; 
	}
	
	if(isUndefined(_cacheObject)){
		if(!selector || $(selector).length < 1){
			return '['+selector + '] selector  not found '; 
		}

		_cacheObject = new Plugin(selector, options);
		_datastore[selector] = _cacheObject;
		
		return _cacheObject;
	}else if(typeof options==='object'){
		_cacheObject.destroy();
		_cacheObject = new Plugin(selector, options);
		_datastore[selector] = _cacheObject;
		return _cacheObject;
	}

	if(typeof options === 'string'){
		var callObj =_cacheObject[options]; 
		if(isUndefined(callObj)){
			return options+' not found';
		}else if(typeof callObj==='function'){
			return _cacheObject[options].apply(_cacheObject,args);
		}else {
			return typeof callObj==='function'; 
		}
	}

	return _cacheObject;	
};

}(jQuery, window, document));