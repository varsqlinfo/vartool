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
	}
	this.element;
	this.selectorElement = $(this.selector);
	
	this.init();

	return this; 
}

Plugin.prototype ={
	init :function(){
		this.calcSplitterInfo();
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
	,calcSplitterInfo : function (){
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
			
			var confPercent = String(sEle.data('percent'));
			
			if(confPercent == 'false'){
				confPercent = false;
			}else if( confPercent == 'true'){
				confPercent = true;
			}else{
				confPercent = _this.options.percent[orientationInfo];
			}

			splitterConf[prefixIdx] = {
				prev: prevMinSize
				,next: nextMinSize
				,percent: confPercent
				,orientation: orientationInfo
			};

			_this._setInitPanelSize(sEle, confPercent, orientationInfo, splitterConf[prefixIdx]);			
		})

		_this.config.splitterConf = splitterConf;
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

		this.options.start();

		if(splitterConf.orientation == 'vertical'){
			this._setVerticalLimitSize(ele, prevSize, nextSize, splitterConf);
		}else{
			this._setHorizontalSplitterSize(ele, prevSize, nextSize, splitterConf);
		}

		this.options.stop();
	}
	,setLimitSize : function (sEle, splitterConf, mode){
		this._setSizeInfo(sEle, splitterConf);
		var sizeInfo = this.config.sizeInfo; 

		var eleTotW = sizeInfo.prevWidth + sizeInfo.nextWidth + (splitterConf.percent ? this.options.handleSize : 0);  

		var prevSize=0, nextSize=0; 

		if(mode == 'prev'){
			prevSize = sizeInfo.prevMinSize;
			nextSize = eleTotW - prevSize;
		}else{
			nextSize = sizeInfo.nextMinSize;
			prevSize = eleTotW - nextSize;
		}
		
		_$util.setVerticalWidth(this, sizeInfo, splitterConf.percent, eleTotW, prevSize, nextSize);		
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
			
			if(parentWidth < eleTotW){
				eleTotW = parentWidth;

				prevW = percentage(percent(prevW, eleTotW), eleTotW);
				nextW = percentage(percent(nextW, eleTotW), eleTotW);
			}

			console.log('_setInitPanelSize', prevW , prevW, nextW, eleTotW)

			_$util.setVerticalWidth(this, sizeInfo, confPercent, eleTotW, prevW, nextW);
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
				sizeInfo.prevMinSize = Math.max(percentage(splitterConf.prev, sizeInfo.parentWidth), sizeInfo.prevOverW);
				sizeInfo.nextMinSize = Math.max(percentage(splitterConf.next, sizeInfo.parentWidth), sizeInfo.nextOverW);
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
				sizeInfo.prevMinSize = Math.max(percentage(splitterConf.prev, sizeInfo.parentHeight), sizeInfo.prevOverH);
				sizeInfo.nextMinSize = Math.max(percentage(splitterConf.next, sizeInfo.parentHeight), sizeInfo.nextOverH);
			}
		}
		
		if(splitterConf.percent !== true){
			sizeInfo.prevMinSize = Math.max(splitterConf.prev, sizeInfo.prevOverW);
			sizeInfo.nextMinSize = Math.max(splitterConf.next, sizeInfo.nextOverW);
		}

		sizeInfo.prevElement = prevElement;
		sizeInfo.nextElement = nextElement;

		this.config.sizeInfo=sizeInfo;
	}
	/**
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
		
		this.options.start(data);
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
			
			_this.options.stop(_this.config.sizeInfo);
			$(document).off('touchmove.pubvsplitter mousemove.pubvsplitter').off('touchend.pubvsplitter mouseup.pubvsplitter mouseleave.pubvsplitter');
		});
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

		if(splitterConf.percent){
			eleTotW += this.options.handleSize;
			if(prevW > (sizeInfo.prevOverW)){
				prevW+=this.options.handleSize;	
			}
		}	
		
		_$util.setVerticalWidth(this, sizeInfo, splitterConf.percent, eleTotW, prevW, nextW);
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
		
		var prevMinSize = splitterConf.prev, nextMinSize = splitterConf.next;

		if(splitterConf.percent){
			prevMinSize = percentage(prevMinSize, parentHeight);
			nextMinSize = percentage(nextMinSize, parentHeight);
		}	
		
		this.options.start(data);
		
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
			
			_this.options.stop(_this.config.sizeInfo);
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
};

var _$util = {
	setVerticalWidth : function (gridCtx, sizeInfo, percentFlag, parentW, prevSize, nextSize){
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

		console.log(parentW, '333333`````````',prevSize, nextSize,prevMinSize, nextMinSize)	
		
		if(percentFlag){
			
			if(resizePrevFlag){
				prevElement.css('width', getCssCalc(percent(prevSize, sizeInfo.parentWidth), gridCtx.options.handleSize));
				nextElement.css('width', percent(nextSize, parentW)+'%');
			}else{
				prevElement.css('width', percent(prevSize, parentW)+'%');
				nextElement.css('width', getCssCalc(percent(nextSize, sizeInfo.parentWidth), gridCtx.options.handleSize));
			}
		}else{
			if(resizePrevFlag){
				prevElement.css('width', prevSize+'px');
				nextElement.css('width', nextSize+'px');
			}else{
				prevElement.css('width', prevSize+'px');
				nextElement.css('width', nextSize+'px');
			}
		}
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
