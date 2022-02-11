/**
 * pubSplitter: v0.0.1
 * ========================================================================
 * Copyright 2021-2021 ytkim
 * Licensed under MIT
 * http://www.opensource.org/licenses/mit-license.php
 * url : https://github.com/ytechinfo/pub
 * demo : http://pub.moaview.com/
*/
;(function ($) {

var pluginName = "pubSplitter"
	,_datastore = {}
	,_splitTargetSeq = 0
	,defaults = {
		fadeSpeed: 100				// 숨김 속도 
		,mode : 'simple'			// wrapper
		,orientation: 'vertical'	// splitter 방향  default vertical
		,border:true				// splitter border 
		,widthFixed :false			// panel width fix
		,useButton : false			// click 으로 이동. 
		,minSize : 50				// default pixel
		,percent: true			// position % 여부 true , false , {vertical : false, horizontal : false}
		,useHelper: true		// 위치 조정시 helper 사용여부.
		,theme: 'light'			// 테마  light , dark
		,handleSize: 6
		,autoResize :true
		,start: function (data){	// start event callback
			
		}
		,move: function (data){	// move event callback
			
		}
		,stop: function (data){	// stop event callback
			
		}
	};

function isUndefined(obj){
	return typeof obj==='undefined';
}

function isEmpty(obj){
	return obj ==null || typeof obj==='undefined';
}

function getEleWidth(ele){
	var eleW = ele.outerWidth(true);

	return eleW; 
	if(ele.outerWidth() != eleW){
		eleW = eleW - (ele.outerWidth() - eleW)
	}
	return eleW; 
}

function getCssCalc(percentVal, pxVal){
	return 'calc('+percentVal+'% - '+pxVal+'px)';
	//return percentVal+'%';
}

function getEleHeight(ele){
	var eleH = ele.height();
	if(ele.outerHeight() != eleH){
		eleH = eleH - (ele.outerHeight() - eleH)
	}
	return eleH; 
}

function getHashCode (str){
    var hash = 0;
    if (str.length == 0) return hash;
    for (var i = 0; i < str.length; i++) {
        var tmpChar = str.charCodeAt(i);
        hash = ((hash<<5)-hash)+tmpChar;
        hash = hash & hash; 
    }
    return (hash+'').replace(/-/gi,'1_');
}

function percentage(val,tot){
	return val*tot/100;
}

function percent(val,tot){
	return val/tot*100;
}
	
function Plugin(selector, options) {
	this.selector = selector;
	this.prefix = getHashCode(selector);
	this.setOptions(options);

	this.config = {
		splitterConf: {}
		,moveInfo :{}
		,allTargetSize : {}
	}
	this.element;
	this.selectorElement = $(this.selector);
	
	this.init();

	this._windowResize();

	return this; 
}

Plugin.prototype ={
	init :function(){
		this.initPanelSize();
		this.initEvt();
		//this.addStyle();
	}
	,setOptions : function (opts){
		var options = $.extend({}, defaults, opts);

		if(options.percent===true){
			options.percent = {vertical: true, horizontal: false};
		}else if(opts.percent===false){
			options.percent = {vertical: false, horizontal: false};
		}else{
			options.percent = $.extend({},  {vertical: false, horizontal: false}, options.percent);
		}

		this.options =options; 
	}
	,initPanelSize : function (){
		var _this = this; 
		
		var splitterConf = {};
		var orientationInfo = '';
		var defaultMinSize = this.options.minSize;
		var helperFlag = this.options.useHelper;
		var useButtonFlag = this.options.useButton;
		

		// html element attribute
		// orientation = vertical , horizontal
		// prev-min-size = pixel 
		// next-min-size = pixel 
		// percent = true, false; 
		var percentParentEle = [];
		var beforeParentEle; 
		this.selectorElement.each(function (i){
			var sEle = $(this); 

			var orientation = sEle.data('orientation') || _this.options.orientation;

			var prefixIdx= _this.prefix+(i); 

			if(orientation == 'horizontal'){
				sEle.css('height', _this.options.handleSize+'px');
				orientationInfo = 'horizontal'
			}else{
				sEle.css('width', _this.options.handleSize+'px');
				orientationInfo = 'vertical';
			}
			
			sEle.addClass('pub-splitter').addClass(orientationInfo +' ' + (_this.options.border==true?'pub-border':'pub-border-none'));
			sEle.attr('data-pubsplitter', prefixIdx);

			if(helperFlag===true){
				sEle.html($('<div class="pub-splitter-helper"/>').addClass(orientationInfo));
			}

			if(useButtonFlag===true){
				var strHtm = '<div class="pub-splitter-button '+orientationInfo+'" >';
				if(orientationInfo == 'vertical'){
					strHtm +='<span class="pub-toggle-btn" data-mode="prev" style="width:100%;height:16px;"><svg viewBox="0 0 6 12"><path d="M0 6 L6 12 L6 0 Z"></path></svg></span>'
					+'<span class="pub-toggle-btn" data-mode="next" style="width:100%;height:16px;"><svg viewBox="0 0 6 12"><g><path d="M0 0 L0 12 L6 6 Z"></path></g></svg></span>';
				}else{
					strHtm 	+='<span class="pub-toggle-btn" data-mode="prev" style="width:16px;height:100%"><svg viewBox="0 0 12 6" style="position: absolute;top: -6px;"><g><path d="M6 0 L0 6 L12 6 Z"></path></g></svg></span>'
					+'<span class="pub-toggle-btn" data-mode="next" style="width:16px;height:100%;"><svg viewBox="0 0 12 6" style="position: absolute;top: -6px;"><g><path d="M0 0 L6 6 L12 0 Z"></path></g></svg></span>'
				}
				strHtm+='</div>';

				sEle.append(strHtm);
			}
						
			var prevMinSize = sEle.data('prev-min-size')
				,nextMinSize = sEle.data('next-min-size');

			prevMinSize= parseInt(isNaN(prevMinSize) ? defaultMinSize : prevMinSize, 10);
			nextMinSize= parseInt(isNaN(nextMinSize) ? defaultMinSize : nextMinSize, 10);

			prevMinSize = prevMinSize < 0 ? 0 : prevMinSize;
			nextMinSize = nextMinSize < 0 ? 0 : nextMinSize;

			var prevEle = sEle.prev()
				,nextEle = sEle.next(); 
			var prevTarget = '', nextTarget='';

			if(!isEmpty(prevEle.data('split-target'))){
				prevTarget = prevEle.data('split-target');
			}else{
				prevTarget = ++_splitTargetSeq;
				_this._setTargetSize(prevTarget, 0);
			}

			nextTarget = ++_splitTargetSeq;
			_this._setTargetSize(nextTarget, 0);

			prevEle.data('split-target', prevTarget);
			nextEle.data('split-target', nextTarget);
			
			var confPercent = String(sEle.data('percent'));
			
			if(confPercent == 'false'){
				confPercent = false;
			}else if( confPercent == 'true'){
				confPercent = true;
			}else{
				confPercent = _this.options.percent[orientationInfo];
			}
			var currentParentEle =sEle.parent()[0]; 
			if(confPercent && beforeParentEle != currentParentEle){
				percentParentEle.push(sEle);
			}

			beforeParentEle = currentParentEle;

			splitterConf[prefixIdx] = {
				prevMinSize: prevMinSize
				,nextMinSize: nextMinSize
				,prevTarget : prevTarget
				,nextTarget : nextTarget
				,percent: confPercent
				,orientation: orientationInfo
			};		

			_this._setInitPanelSize(sEle, confPercent, orientationInfo, splitterConf[prefixIdx]);			
		})

		_this.config.splitterConf = splitterConf;

		for(var i =0; i < percentParentEle.length; i++){
			_this._setPercentSize(percentParentEle[i]);
		}

		
	}
	/**
	 * @method initEvt
	 * @description init splitter event 
	 */
	,initEvt : function (){
		var _this = this; 
		var element =this.selectorElement; 
		
		element.off('touchstart.pubsplitter mousedown.pubsplitter');
		element.on('touchstart.pubsplitter mousedown.pubsplitter',function (e){
			var ele = $(this); 	

			if($(e.target).closest('.pub-toggle-btn').length > 0){
				return ;
			}
			
			if(ele.hasClass('vertical')){
				_this._moveVertical(e, ele);
			}else{
				_this._moveHorizontal(e, ele);
			}
			
			return false; 
		});

		element.on('click.toggle.btn', '.pub-toggle-btn', function (e){
			var ele = $(this);
			var mode = ele.data('mode');
				 				
			var splitterEle = ele.closest('.pub-splitter');

			var splitterConf = _this.config.splitterConf[splitterEle.data('pubsplitter')];

			_this.setLimitSize(splitterEle, splitterConf, mode);
						
			return false; 
		});
	}
	/**
	 * @method setPanelSize
	 * @param prevSize {Integer} - prev size
	 * @param nextSize {Integer} - next size
	 * @param splitter {Integer or Ele} - splitter index or splitter ele
	 * @description move vertial 
	 */
	,setPanelSize : function (prevSize, nextSize, splitter){

		if(isUndefined(prevSize) || isNaN(prevSize)){
			throw new Error('not valid prevSize : '+ prevSize );
		}

		var ele;
		if(isUndefined(splitter)){
			ele = this.selectorElement.get(0);
		}else{
			if(!isNaN(splitter)){
				if(splitter < 0 || splitter > this.selectorElement.length){
					throw new Error( 'arrayIndexOutOfBoundsException : '+ splitter );
				}
	
				ele = this.selectorElement.get(splitter);
			}else{
				ele = splitter;
			}
		}

		ele = $(ele);
		var splitterid =ele.data('pubsplitter'); 
		var splitterConf = this.config.splitterConf[splitterid]; 

		this._setSizeInfo(ele, splitterConf);
		var sizeInfo = this.config.sizeInfo; 

		this._startResize(ele, splitterConf);

		var eleTotW = sizeInfo.prevWidth + sizeInfo.nextWidth;  
		
		_$util.setVerticalWidth(this, sizeInfo, splitterConf, eleTotW, prevSize, nextSize);	

		this.stopResize(ele, splitterConf);
		
	}
	,setLimitSize : function (sEle, splitterConf, mode){
		this._setSizeInfo(sEle, splitterConf);
		var sizeInfo = this.config.sizeInfo; 

		var eleTotW = sizeInfo.prevWidth + sizeInfo.nextWidth;  

		var prevSize=0, nextSize=0; 

		if(mode == 'prev'){
			prevSize = sizeInfo.prevMinSize;
			nextSize = eleTotW - prevSize;
		}else{
			nextSize = sizeInfo.nextMinSize;
			prevSize = eleTotW - nextSize;
		}
		
		_$util.setVerticalWidth(this, sizeInfo, splitterConf, eleTotW, prevSize, nextSize);		
	}
	/**
	 * @method _setInitPanelSize
	 * @param ele {element} - splitter element
	 * @param confPercent {Boolean} - percent 여부
	 * @param orientationInfo {String} - vertical or horizontal
	 * @description set 초기 panel size
	 */
	,_setInitPanelSize : function (sEle, confPercent, orientationInfo, splitterConf){
		this._setSizeInfo (sEle, splitterConf);
		var sizeInfo = this.config.sizeInfo; 
		
		if(orientationInfo=='vertical'){	
			var parentWidth =sizeInfo.parentWidth; 
						
			var prevW = sizeInfo.prevWidth
				,nextW = sizeInfo.nextWidth
				,eleTotW = prevW + nextW;

			
			prevW = percentage(percent(prevW, parentWidth), parentWidth)
			nextW = percentage(percent(nextW, parentWidth), parentWidth)
			
			
			if(parentWidth < eleTotW){
				eleTotW = parentWidth;
			}

			eleTotW-=this.options.handleSize; // prev + next  size에서 prev 에서 handle 사이즈 빼주기.

			console.log('_setInitPanelSize', parentWidth, prevW-this.options.handleSize, nextW, eleTotW , prevW + nextW)

			_$util.setVerticalWidth(this, sizeInfo, splitterConf, eleTotW, prevW-this.options.handleSize, nextW);
		}else{

			var handleSize = this.options.handleSize; 

			if(resizeEle.height() <  handleSize){
				resizeEle = sEle.next();
			}

			var eleH = getEleHeight(resizeEle)

			if(confPercent){
				resizeEle.css('height', getCssCalc(percent(eleH-handleSize, resizeEle.parent().height()), handleSize));
			}else{
				resizeEle.css('height', (eleH - handleSize)+'px');
			}
		}	
	}
	/**
	 * @method _setSizeInfo
	 * @description move info
	 */
	,_setSizeInfo : function (ele, splitterConf){
		var prevElement = ele.prev()
			,nextElement = ele.next();
		var sizeInfo = {};
		if(splitterConf.orientation == 'vertical'){
			sizeInfo={
				 prevWidth : prevElement.outerWidth()
				, nextWidth : nextElement.outerWidth()
	
				, prevOverW : prevElement.outerWidth() - prevElement.width()
				, nextOverW : nextElement.outerWidth() - nextElement.width()

				, parentWidth : ele.parent().width()
			}	

			if(splitterConf.percent){
				sizeInfo.prevMinSize = Math.max(percentage(splitterConf.prevMinSize, sizeInfo.parentWidth), sizeInfo.prevOverW);
				sizeInfo.nextMinSize = Math.max(percentage(splitterConf.nextMinSize, sizeInfo.parentWidth), sizeInfo.nextOverW);
			}
		}else{
			sizeInfo={
				 prevHeight : prevElement.outerHeight()
				, nextHeight : nextElement.outerHeight()

				, prevOverH : prevElement.outerHeight() - prevElement.height()
				, nextOverH : nextElement.outerHeight() - nextElement.height()

				, parentHeight : ele.parent().height()
			}
			
			if(splitterConf.percent){
				sizeInfo.prevMinSize = Math.max(percentage(splitterConf.prevMinSize, sizeInfo.parentHeight), sizeInfo.prevOverH);
				sizeInfo.nextMinSize = Math.max(percentage(splitterConf.nextMinSize, sizeInfo.parentHeight), sizeInfo.nextOverH);
			}
		}
		
		if(splitterConf.percent !== true){
			sizeInfo.prevMinSize = Math.max(splitterConf.prevMinSize, sizeInfo.prevOverW);
			sizeInfo.nextMinSize = Math.max(splitterConf.nextMinSize, sizeInfo.nextOverW);
		}

		sizeInfo.prevElement = prevElement;
		sizeInfo.nextElement = nextElement;

		this.config.sizeInfo=sizeInfo;
	}
	,_setTargetSize : function (targetId , size){
		this.config.allTargetSize[targetId] = size;
	}
	,_getTargetSize : function (targetId){
		return this.config.allTargetSize[targetId];
	}
	,_removeTargetSize : function (targetId){
		return (delete this.config.allTargetSize[targetId]);
	}
	/*
	 * @method _moveVertical
	 * @description move vertial 
	 */
	,_moveVertical : function (e, ele){
		var _this = this; 
		var oe = e.originalEvent.touches;
			
		var data = {
			startX :(oe ? oe[0].pageX : e.pageX)
		};

		var splitterConf = _this.config.splitterConf[ele.data('pubsplitter')];

		_this._setSizeInfo(ele, splitterConf);

		var prevWidth = _this.config.sizeInfo.prevWidth
			,nextWidth = _this.config.sizeInfo.nextWidth;
		
		var prevMinSize = _this.config.sizeInfo.prevMinSize, nextMinSize = _this.config.sizeInfo.nextMinSize;
		
		this._startResize(ele, splitterConf);
		this._showHelper(ele);
		$(document).on('touchmove.pubvsplitter mousemove.pubvsplitter', function (e){
			var movePosition = e.pageX - data.startX;
			var absPos= Math.abs(movePosition);
			
			_this.config.sizeInfo.isLeftPos = movePosition < 0;

			if(_this.config.sizeInfo.isLeftPos){
				if(prevMinSize > prevWidth-absPos){
					absPos = prevWidth- prevMinSize; 
				}
			}else{
				if(nextMinSize> nextWidth-absPos){
					absPos = nextWidth- nextMinSize; 
				}
			}

			_this.config.sizeInfo.absPos = absPos;
			
			if(_this.options.useHelper){
				_this.config.helperEle.css('left', (_this.config.sizeInfo.isLeftPos?'-':'')+absPos+'px');
			}else{
				_this._setPanelWidth(splitterConf);
			}

			_this.options.move({start : data.startX, move : absPos}); // move 
		}).on('touchend.pubvsplitter mouseup.pubvsplitter mouseleave.pubvsplitter', function (e){
			if(_this.options.useHelper){
				_this._hideHelper(ele);
				_this._setPanelWidth(splitterConf);
			}
			_this.stopResize(ele, splitterConf);
			
			$(document).off('touchmove.pubvsplitter mousemove.pubvsplitter').off('touchend.pubvsplitter mouseup.pubvsplitter mouseleave.pubvsplitter');
		});
	}
	,_startResize : function (ele, splitterConf){
		var _this =this; 
		if(splitterConf.percent){
			var beforeNextEle; 
			ele.parent().find('.pub-splitter').each(function (){
				var splitterEle = $(this);

				var splitterConf = _this.config.splitterConf[splitterEle.data('pubsplitter')];

				if(splitterConf.percent==true){
					var prevEle = splitterEle.prev()
						,nextEle = splitterEle.next();

					if(beforeNextEle != prevEle[0]){
						prevEle.css('width', prevEle.outerWidth() +'px');
					}

					nextEle.css('width', nextEle.outerWidth() +'px');
					
					beforeNextEle= nextEle[0];
				}
			})
		}

		this.options.start(ele, splitterConf);
	}
	,stopResize : function (ele, splitterConf){
		
		if(splitterConf.percent){
			this._setPercentSize(ele);
		}

		this.options.stop(ele, splitterConf);
	}
	,_setPercentSize : function (ele){
		
		var _this = this; 
		var beforeNextEle; 
		var panelItemObj = {};
		var parentEle = ele.parent(); 
		parentEle.find('.pub-splitter').each(function (){
			var splitterEle = $(this);

			var splitterid = splitterEle.data('pubsplitter'); 
			var splitterConf = _this.config.splitterConf[splitterid];

			if(splitterConf.percent==true){
				var prevEle = splitterEle.prev()
					,nextEle = splitterEle.next();

				if(beforeNextEle != prevEle[0]){
					panelItemObj[splitterConf.prevTarget] = {
						conf: splitterConf
						,position : 'prev'
						,targetId : splitterConf.prevTarget
						,minSize : splitterConf.prevMinSize
						,ele : prevEle
					};
				}else{
					panelItemObj[splitterConf.prevTarget].position = 'prev';
				}

				panelItemObj[splitterConf.nextTarget] = {
					conf: splitterConf
					,position : 'next'
					,targetId : splitterConf.nextTarget
					,minSize : splitterConf.nextMinSize
					,ele : nextEle
				};
				beforeNextEle= nextEle[0];
			}
		})

		var handleSize = this.options.handleSize;
		var parentW = parentEle.width();

		var modSize=0; 
		for(var key in panelItemObj){
			var panelItem = panelItemObj[key];
			var ele = panelItem.ele;
			var eleW = ele.outerWidth();

			var minSize = Math.max(percentage(panelItem.minSize, parentW), eleW - ele.width());

			if(panelItem.position =='prev'){
				if(eleW >= minSize+handleSize){
					eleW +=handleSize;
					ele.css('width', getCssCalc(percent(eleW, parentW), handleSize));
				}else{
					modSize = (minSize+handleSize) - eleW;
				}
			}else{
				ele.css('width', percent(eleW, parentW)+'%');
				/*
				if(eleW >= minSize){
					
				}else{
					ele.css('width', minSize+'px');
					modSize = minSize;
				}
				*/
			}
			

			console.log(modSize);
		}
	}
	/**
	 * @method _setPanelWidth
	 * @description set panel width
	 */
	,_setPanelWidth : function (splitterConf){
		var sizeInfo = this.config.sizeInfo; 
		var absPos = sizeInfo.absPos;
		var isLeftPos = sizeInfo.isLeftPos; 
		var eleTotW = sizeInfo.prevWidth + sizeInfo.nextWidth;

		var prevW = (sizeInfo.prevWidth + (absPos * (isLeftPos?-1:1)))
			,nextW = (sizeInfo.nextWidth + (absPos * (isLeftPos?1:-1)));
	
		_$util.setVerticalWidth(this, sizeInfo, splitterConf, eleTotW, prevW, nextW);
	}
	/**
	 * @method _moveHorizontal
	 * @description move horizontal 
	 */
	,_moveHorizontal : function (e, ele){
		var _this = this; 
		var oe = e.originalEvent.touches;
			
		var data = {
			startY :(oe ? oe[0].pageY : e.pageY)
		};

		var splitterConf = _this.config.splitterConf[ele.data('pubsplitter')];

		_this._setSizeInfo(ele, splitterConf);

		var prevHeight = _this.config.sizeInfo.prevHeight
			,nextHeight = _this.config.sizeInfo.nextHeight
			,parentHeight = _this.config.sizeInfo.parentHeight; 
		
		var prevMinSize = splitterConf.nextMinSize, nextMinSize = splitterConf.nextMinSize;

		if(splitterConf.percent){
			prevMinSize = percentage(prevMinSize, parentHeight);
			nextMinSize = percentage(nextMinSize, parentHeight);
		}	
		
		this._startResize(ele, splitterConf);
	
		var moveFn = this.options.move; 
		var useHelper = this.options.useHelper; 
			
		this._showHelper(ele);

		$(document).on('touchmove.pubhsplitter mousemove.pubhsplitter', function (e){
			var movePosition = e.pageY - data.startY;
			var absPos= Math.abs(movePosition);
			
			_this.config.sizeInfo.isTopPos = movePosition < 0;

			if(_this.config.sizeInfo.isTopPos){
				if(prevMinSize > prevHeight-absPos){
					absPos = prevHeight- prevMinSize; 
				}
			}else{
				if(nextMinSize> nextHeight-absPos){
					absPos = nextHeight- nextMinSize; 
				}
			}

			_this.config.sizeInfo.absPos = absPos;
			
			if(useHelper){
				_this.config.helperEle.css('top', (_this.config.sizeInfo.isTopPos?'-':'')+absPos+'px');
			}else{
				_this._setPanelHeight(splitterConf);
			}

			moveFn({start : data.startY, move : absPos}); // move 
		}).on('touchend.pubhsplitter mouseup.pubhsplitter mouseleave.pubhsplitter', function (e){
			if(useHelper){
				_this._hideHelper(ele);
				_this._setPanelHeight(splitterConf);
			}
			
			_this.stopResize(ele, splitterConf);

			$(document).off('touchmove.pubhsplitter mousemove.pubhsplitter').off('touchend.pubhsplitter mouseup.pubhsplitter mouseleave.pubhsplitter');
		});
	}
	/**
	 * @method _setPanelHeight
	 * @description set peanel height
	 */
	,_setPanelHeight : function (splitterConf){
		var absPos = this.config.sizeInfo.absPos;
		var isTopPos = this.config.sizeInfo.isTopPos; 

		var prevH = (this.config.sizeInfo.prevHeight + (absPos* (isTopPos?-1:1)))
			,nextH = (this.config.sizeInfo.nextHeight + (absPos* (isTopPos?1:-1)));

		if(splitterConf.percent===true){
			var parentHeight = this.config.sizeInfo.parentHeight; 

			this.config.sizeInfo.prevElement.css('height', percent(prevH, parentHeight)+'%');
			this.config.sizeInfo.nextElement.css('height', percent(nextH, parentHeight)+'%');
		}else{
			this.config.sizeInfo.prevElement.css('height', prevH+'px');
			this.config.sizeInfo.nextElement.css('height', nextH+'px');
		}
	}
	/**
	 * @method _showHelper
	 * @description show helper
	 */
	,_showHelper : function (ele){
		this.config.helperEle = ele.find('.pub-splitter-helper');
		this.config.helperEle.css({
			'left': '0px'
			,'top': '0px'
		});

		if(this.options.useHelper)	ele.addClass('helper');
	}
	/**
	 * @method _hideHelper
	 * @description hide helper
	 */
	,_hideHelper : function (ele){
		ele.removeClass('helper');
	}
	,destroy:function (){
		
		this.selectorElement.find('*').off();

		this.selectorElement.each(function (){
			var sEle = $(this);
			sEle.removeClass('pub-splitter vertical horizontal');
			sEle.empty();
		})
		
		$._removeData(this.selector)
		delete _datastore[this.selector];
	}
	,_windowResize :function (){
		var _this = this;

		if(_this.options.autoResize === false) return false;

		var _evt = $.event,
			_special,
			resizeTimeout,
			eventName =  _this.prefix+"pubsplitterResize";

		_special = _evt.special[eventName] = {
			setup: function() {
				$( this ).on( "resize.pubsplitter", _special.handler );
			},
			teardown: function() {
				$( this ).off( "resize.pubsplitter", _special.handler );
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
			//_this.resize();
		});
	}
	,resize : function (){
		var _this = this; 
		
		var beforeParentEle; 
		
		var eleLen = this.selectorElement.length; 

		var allParentEle = {};
		var resizeIdx= 0; 
		var beforeNextEle;
		for(var i = 0; i < eleLen; i++){
			var ele = $(this.selectorElement[i]);
			//역순으로 사이즈 재조정 할것. 

			var splitterid =ele.data('pubsplitter'); 
			var splitterConf = _this.config.splitterConf[splitterid];

			if(splitterConf.percent==true){
				var currentParentEle = ele.parent()[0];

				if(beforeParentEle != currentParentEle){
					++resizeIdx;
					allParentEle[resizeIdx] = {
						parentEle : currentParentEle
						,splitterLen : 0
						,newParentSize : ( splitterConf.orientation=='vertical' ? $(currentParentEle).width() : $(currentParentEle).height() )
						,beforeParentSize : splitterConf.currentParentSize
						,children :[]
					};
				}
				allParentEle[resizeIdx].splitterLen +=1;

				var prevEle = ele.prev()
					,nextEle = ele.next();

				if(beforeNextEle != prevEle[0]){
					allParentEle[resizeIdx].children.push({
						conf: splitterConf
						,targetId : splitterConf.prevTarget
						,minSize : splitterConf.prevMinSize
						,ele : prevEle
					});	
				}

				allParentEle[resizeIdx].children.push({
					conf: splitterConf
					,targetId : splitterConf.nextTarget
					,minSize : splitterConf.nextMinSize
					,ele : nextEle
				});	

				beforeNextEle= nextEle[0];
											
				beforeParentEle = currentParentEle;
				
			}
		}

		for(var key in allParentEle){
			var item = allParentEle[key];

			var children = item.children; 
			var childLen = children.length;
			var newParentSize = item.newParentSize
				,beforeParentSize = item.beforeParentSize;

			var resizeVal = newParentSize - beforeParentSize;

			var addflag = resizeVal > 0;

			var absResizeVal = Math.abs(resizeVal);

			var addSize = Math.floor(absResizeVal / childLen) * (addflag ? 1 : -1);
			var modSize = (absResizeVal % childLen) * (addflag ? 1 : -1);

			console.log(childLen+'\t'+absResizeVal+'\t'+newParentSize+'\t'+beforeParentSize+'\t'+addSize+'\t'+modSize)

			var totMinSize = 0; 
			
			for(var j = childLen-1; j >= 0 ; j--){
				var childItem = children[j];
				var ele = childItem.ele; 
				var splitterConf = childItem.conf;

				splitterConf.currentParentSize = newParentSize;

				var currentSize = _this._getTargetSize(childItem.targetId)+ addSize + modSize;
				modSize = 0; 

				var minSize = Math.max(percentage(childItem.minSize, newParentSize), ele.outerWidth() - ele.width());
				totMinSize +=minSize;

				if(minSize >= currentSize){	
					console.log('eeeeeee : ', j, minSize, currentSize)
					var tmpModSize = minSize-currentSize;
					modSize = (modSize+tmpModSize > 0 ? tmpModSize : Math.abs(currentSize)-minSize) * (addflag ? 1 : -1);
					currentSize = minSize;
				}

				ele.css('width', (currentSize)+'px');

				_this._setTargetSize(childItem.targetId, currentSize);
			}
			totMinSize = totMinSize + (allParentEle[resizeIdx].splitterLen * this.options.handleSize); 
			
			if(newParentSize <= totMinSize){
				$(item.parentEle).css('min-width',totMinSize+'px');
			}
			console.log(totMinSize)
			
		}
	}
};

var _$util = {
	setVerticalWidth : function (gridCtx, sizeInfo, splitterConf, parentW, prevSize, nextSize){
		var prevElement = sizeInfo.prevElement
			,nextElement = sizeInfo.nextElement;

		var prevMinSize = sizeInfo.prevMinSize;
		var resizePrevFlag = true;

		if(prevSize <= prevMinSize){
			resizePrevFlag = false;
			prevSize = prevMinSize;
			nextSize = parentW - prevMinSize;
		}

		var nextMinSize =sizeInfo.nextMinSize;
		if(nextSize <= nextMinSize){
			nextSize = nextMinSize;
			prevSize = parentW - nextMinSize;
		}

		console.log(parentW, '333333`````````',prevSize, nextSize,prevMinSize, nextMinSize);

		splitterConf.currentParentSize = sizeInfo.parentWidth;

		prevElement.css('width', prevSize+'px');
		nextElement.css('width', nextSize+'px');	

		gridCtx._setTargetSize(prevElement.data('split-target'), prevSize);
		gridCtx._setTargetSize(nextElement.data('split-target'), nextSize);
	}
}


$[ pluginName ] = function (selector,options) {
	var _cacheObject = _datastore[selector]; 

	if(isUndefined(_cacheObject)){
	
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

})(jQuery);
