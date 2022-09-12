/*
**
*ytkim
*vartool common js
 */
if (typeof window != "undefined") {
    if (typeof window.VARTOOL == "undefined") {
        window.VARTOOL = {};
    }
}else{
	if(!VARTOOL){
		VARTOOL = {};
	}
}

(function( window ) {

// 전역 변수
var _g_options = {
	logItem :{}
	,allCmpInfo : {}
};


var ALL_COMPONENT_INFO ={
	appLog :{
        type: 'component',
        id : 'AppLog',
        componentName: 'appLogComponent',
        componentState:{},
        title: 'app log',
        loadSelector : '[component-viewerid="#id#"]'
    }
    ,deployLog : {
		type: 'component',
        id : 'deployLog',
        height :100,
        componentName: 'deployLogComponent',
        componentState:{},
        title:'Deploy log',
        loadSelector : '[component-viewerid="#id#"]'
	}
	,commandLog : {
		type: 'component',
        id : 'commandLog',
        height :100,
        componentName: 'commandLogComponent',
        componentState:{},
        title:'Server log',
        loadSelector : '[component-viewerid="#id#"]'
	}
	,layoutContents :{
		LOG : function (){
			return [{
				type:'row'
				,content : [
				]
			}];
		}
	}
}

// layout default config
var DEFAULT_LAYOUT_CONFIG = {
  settings: {
    hasHeaders: true,
    constrainDragToContainer: true,
    reorderEnabled: true,
    selectionEnabled: false,
    popoutWholeStack: false,
    blockedPopoutsThrowError: true,
    closePopoutsOnUnload: true,
    showPopoutIcon: false,
    showMaximiseIcon: true,
    showCloseIcon: true
  },
  dimensions: {
    borderWidth: 5,
    minItemHeight: 10,
    minItemWidth: 10,
    headerHeight: 20,
    dragProxyWidth: 300,
    dragProxyHeight: 200
  },
  labels: {
    close: 'close',
    maximise: 'maximise',
    minimise: 'minimise',
  },
  content: []
};

function logSplit(log){
	log = log.replace(/\n$/,'');
	return log.split('\n'); 
}

function endsWith(str, searchString, position) {
    var subjectString = str.toString();
    if (position === undefined || position > subjectString.length) {
      position = subjectString.length;
    }
    position -= searchString.length;
    var lastIndex = subjectString.indexOf(searchString, position);
    return lastIndex !== -1 && lastIndex === position;
}


VARTOOL.ui = VARTOOL.ui||{};
VARTOOL.ui.create = function (_opts){
	_g_options = VARTOOL.util.objectMerge(_g_options, _opts);
	
	var allCmpInfo = {};
	for(var cmpKey in _opts.userCmpMap){
		var cmpItems = _opts.userCmpMap[cmpKey];
		
		for(var i =0; i< cmpItems.length; i++){
			var item = cmpItems[i]; 
			allCmpInfo[item.cmpId] = item; 
		}
	}
	
	_g_options.allCmpInfo = allCmpInfo;
	
	VARTOOL.theme(_g_options.logItem.groupId, _g_options.mainSettingInfo.theme);
	_ui.layout.init();
}

VARTOOL.ui.addComponent = function (type, opt){
	_ui.layout.addComponent(type, opt)
}

VARTOOL.ui.isActiveTab = function (id){
	return _ui.layout.isActiveTab(id);
}

VARTOOL.ui.getLoadSelector = function (type){
	return _ui.getLoadSelector(type);
}

VARTOOL.ui.getLayoutID = function (uid){
	return 'vartoolLayout-'+uid;
}

VARTOOL.ui.layoutResize = function (){
	_ui.layout.layoutResize();
}

// set theme
VARTOOL.ui.setTheme = function (theme){
	VARTOOL.theme(_g_options.logItem.groupId, theme);
	VARTOOL.ui.savePrefInfo('theme',theme);
}

// save 설정 정보
VARTOOL.ui.savePrefInfo = function(key, prefVal){

	_g_options.mainSettingInfo[key] = prefVal;

	VARTOOL.req.ajax({
		url: {type:VARTOOL.uri.ignore, url:'/pref/save'}
		,data : {
			groupId : _g_options.logItem.groupId
			,prefKey : 'main_layout' 
			,prefVal : JSON.stringify(_g_options.mainSettingInfo) 
		}
		,success: function(resData) {
			
		}
	})
}

VARTOOL.ui.currentLayout = function(){
	return JSON.stringify(_g_options.mainSettingInfo); 
}

var _ui = {component : {}, layout: {}};

_ui.getLoadSelector = function (type, id){
	return PubEP.util.replaceParamUrl(ALL_COMPONENT_INFO[type].loadSelector,{id : id});
}

_ui.utils = {
	copy : function( target, source ) {
		for( var key in source ) {
			target[ key ] = source[ key ];
		}
		return target;
	}
}
// register component
_ui.registerComponent = function (regItem){
	_ui.utils.copy(_ui.component, regItem);
}

var resizeDelay = 200;

function fnCreateComponent(componentName){

	return function( container, componentState ){
			
		var componentObj = _ui.component[componentName];
		var templateSelector  = componentObj.template || ('#'+componentName);
		
		var templateHtml = VARTOOLTemplate.render.html($(templateSelector).html(),componentState);
		
	    container.getElement().html(templateHtml);
	    
	    var initResize = true; 
	    var resizeTimer;
		var startTime= '';
		
		container.on('resize',function() {
			if(initResize === true){
				initResize = false; 
				return ; 
			}
			
			var componentState = container._config.componentState;

			if(componentState.initFlag !== true){
		    	return ;
	    	}
	    	
	    	if(startTime==''){
				startTime = new Date().getTime(); 
			}

			if(new Date().getTime()-resizeDelay <= startTime){
				clearTimeout(resizeTimer);
			}

			resizeTimer = setTimeout(function() {
				startTime='';
				componentObj.resize({width : container.width-2,height : container.height}, componentState);
			}, resizeDelay);
		});
	}; 
}

_ui.saveArticle = function (url, boardParam){
	if(VARTOOL.isBlank(boardParam.contents)){
		VARTOOLUI.toast.open({text : '선택해주세요.'});
		return ;
	}
	VARTOOL.req.ajax({
		url: {type : 'ignore',url :  url}
		,data : boardParam
		,success: function(resData) {
			if($('#mainArticleFrame').length > 0){
				try{
					$('#mainArticleFrame').get(0).contentWindow.pageReload();
				}catch(e){};
			}
		}
	})
}

_ui.layout ={
	layoutObj:{}
	,layoutResize : function (){
		this.layoutObj.updateSize();
	}
	,init : function (){
		var _this = this; 
		
		var logItem = _g_options.logItem;
		
		var configInfo = _g_options.mainSettingInfo.layoutConfig;
		
		var vartoolLayout = null;
		try{
			if(VARTOOL.isObject(configInfo)) {
				vartoolLayout = new GoldenLayout(configInfo ,$('#mainContainer') );
			}
		}catch(e){
			console.log(e);
		}
		
		if(vartoolLayout==null){
			DEFAULT_LAYOUT_CONFIG.content = ALL_COMPONENT_INFO.layoutContents.LOG();
			vartoolLayout = new GoldenLayout( DEFAULT_LAYOUT_CONFIG ,$('#mainContainer'));
		}
		
		var componentArr = [];
		for(var key in ALL_COMPONENT_INFO){
			var cmpInfo = ALL_COMPONENT_INFO[key];
			if(cmpInfo.componentName){
				componentArr.push(cmpInfo.componentName);
			}
		}
		
		for(var i =0; i<componentArr.length; i++){
			var componentName = componentArr[i];
			vartoolLayout.registerComponent( componentName, fnCreateComponent(componentName));
		}
		
		vartoolLayout.on('stackCreated', function( stack ){

			var items = stack.contentItems;

			for(var i =0 ;i < items.length; i++){
				var item = items[i];
				
				item.config.componentState = VARTOOL.util.objectMerge(item.config.componentState, {
					initFlag :false
					,isComponentInit :false
				});
			}  
		});
		
		vartoolLayout.on('activeContentItemChanged', function( contentItem ){

	    	var componentName = contentItem.componentName;
	    	var componentState = contentItem.config.componentState;
	    	
	    	$(contentItem.tab.element[0]).removeClass('blinkcss');
	    	
	    	if(contentItem.isInitialised === true){
	    		if(componentState.initFlag !== true){
		    		_this.initComponent(componentName, componentState);
		    	}else{
		    		_ui.component[componentName].resize({scroll:true}, componentState);
	    		}
	    	}
	    });
	    
	    // title click
		vartoolLayout.on('tabCreated', function( tab ){
			var tabCfg = tab.contentItem.config; 
			var cmpInfo = _g_options.allCmpInfo[tabCfg.id]; 
			
			if(!VARTOOL.isUndefined(cmpInfo)){
				if(tabCfg.title != cmpInfo.name){
					tabCfg.title  = cmpInfo.name;
					tabCfg.componentState = VARTOOL.util.objectMerge(tabCfg.componentState, cmpInfo);
					tab.contentItem.tab.element.find('.lm_title').html(cmpInfo.name);
				}
			}
		
			tab.titleElement.on('dblclick', function (e){
				tab.contentItem.parent.toggleMaximise();
			})
		});
		
		// component create
		vartoolLayout.on('componentCreated', function( component ){
		
			var componentState = component.config.componentState;
			
			if(componentState.isDynamicAdd == true){
				componentState.initFlag = true; 
				delete componentState.isDynamicAdd;
				return ;
			}
			
			if(component.tab.isActive){
				_this.initComponent(component.componentName, componentState);
			}
		});
		
		var layoutSaveTimer;
		var firstFlag = true;
		
		vartoolLayout.on('stateChanged', function(a1){
			
			if(firstFlag){
				firstFlag = false;
				return ;
			}
			
			if(!a1 || vartoolLayout._maximisedItem) return ; 
			
			if(!a1.origin.isMaximised){
			
				clearTimeout(layoutSaveTimer);

				layoutSaveTimer = setTimeout(function() {
					VARTOOL.ui.savePrefInfo('layoutConfig',vartoolLayout.toConfig());
				}, 300);
			}
		});
		
		// item destroy
		vartoolLayout.on('itemDestroyed', function( component ){
			
			if(component.type != 'component'){
				return ; 
			}
			
			var componentState = component.config.componentState;
			var componentObj = _ui.component[componentState.id];
			
			if(componentObj){
				var destroyFn = componentObj.destroy;
	
				if(VARTOOL.isFunction(destroyFn)){
					return destroyFn.call(componentObj, componentState);
				}
			}
			
			componentObj = _ui.component[component.componentName];
			
			if(componentObj){
				var destroyFn = componentObj.destroy;
	
				if(VARTOOL.isFunction(destroyFn)){
					return destroyFn.call(componentObj, componentState);
				}
			}
		});
		
		vartoolLayout.init();
		
		this.layoutObj = vartoolLayout; 
		
		var windowResizeTimer;
		$(window).resize(function() {
			clearTimeout(windowResizeTimer);
			windowResizeTimer = setTimeout(function() {
				vartoolLayout.updateSize();
			}, 20);
		})
		
		$('#mainContainer').on('click','.delete-component', function (e){
			var cmpId = $(this).closest('[component-viewerid]').attr('component-viewerid');
			
			var cmpItem =vartoolLayout.root.getItemsById(cmpId);
			
			if(cmpItem.length > 0){
				cmpItem[0].remove();
				//console.log(cmpId , cmpItem[0].remove());
			}else{
				throw new Error( 'component not found' );
			}
		})
		
	}
	,initComponent : function (componentName, componentInfo){
		componentInfo.initFlag = true;
		var componentObj = _ui.component[componentName];
		var initFn = componentObj.init;
		
		var cmpId = componentInfo.id; 
		
		if(VARTOOL.isUndefined(_g_options.allCmpInfo[cmpId])){
			$('[component-viewerid="'+cmpId+'"]').html('<table class="wh100"><tr><td style="text-align: center;"><a href="javascript:;" class="delete-component">삭제하기</a><br/>삭제된 컴포넌트 정보 입니다.</td></tr></table>');
			return ; 
		}

		if(VARTOOL.isFunction(initFn)){
			initFn.call(componentObj, componentInfo);
		}
	}
	// add custom component
	,addComponent : function (type, opt){
		var addComponentInfo = ALL_COMPONENT_INFO[type];
		
		var addItemInfo = VARTOOL.util.objectMerge({}, addComponentInfo, opt);
		
		if(this.enableComponent(addItemInfo.id)){
			return ;
		}
		
		var addComponentName = addComponentInfo.componentName; 
		
		var layoutObj =this.layoutObj;
		
		var pluginItem = layoutObj.root._$getItemsByProperty('componentName', addComponentName);

		var pluginLen = pluginItem.length;

		addItemInfo.isDynamicAdd = true;

		if(pluginLen > 0){
			(pluginItem[pluginLen-1].parent).addChild({
				title: addItemInfo.title
			    ,type: 'component'
			    ,id : addItemInfo.id
			    ,componentName: addComponentName
			    ,componentState: addItemInfo
			})
		}else{
			var  addItem ={
				title: addItemInfo.title
			    ,type: 'component'
			    ,id : addItemInfo.id
			    ,componentName: addComponentName
			    ,componentState: addItemInfo
			};
			if(layoutObj.root.contentItems.length > 0){
				layoutObj.root.contentItems[0].addChild(addItem);
			}else{
				if(layoutObj.root.parent){
					layoutObj.root.parent.replaceChild(layoutObj.root, addItem);
				}else{
					layoutObj.root.addChild( addItem);
				}
			}
			
			//layoutObj.root.getItemsById(addItemInfo.id)[0].container.setSize(250);
		}
		
		this.initComponent(addComponentName, addItemInfo);
	}
	,enableComponent : function (cmpId){
		var items = this.layoutObj.root.getItemsById(cmpId);
		
		if(items.length > 0){
			var contentItem= items[0];

			if(!contentItem.tab.isActive){
				contentItem.tab.header.parent.setActiveContentItem(contentItem);
				contentItem.setSize();
			}
			return true;
		}
		return false;
	}
	// tab active
	,isActiveTab : function (tabKey){
		var items = this.layoutObj.root.getItemsById(tabKey);

		if(items.length > 0){
			return items[0].tab.isActive;
		}
		return false;
	}
	// 깜박임 처리.
	,blinkTab : function (cmpId){
		var cmpItem =this.layoutObj.root.getItemsById(cmpId); 
		
		if(cmpItem.length > 0  && !cmpItem[0].tab.isActive){
			$(cmpItem[0].container.tab.element[0]).addClass('blinkcss');
			return true; 
		}
		
		return false; 
	}
}

// server log component
_ui.registerComponent({
	"commandLogComponent" :	{
		logElement : {}
		,init : function (logItem){
			if(VARTOOL.isUndefined(logItem.id)){
				return ; 
			}
			
			var _this =this; 
			this.loadLogViewer(logItem);
		}
		,loadLogViewer : function (logItem){
			var _this = this; 
			
			var logId = logItem.id; 
			
			if(!_this.logElement[logId]){
				_this.logElement[logId] = $.pubLogViewer('[component-viewerid="'+logId+'"]',{
					logFileName : logItem.name
					,itemMaxCount : 10000
					,items : []
					,setting:{
						saveStateKey : logId
					}
					,status : {
						enabled : true
						,height : 17
						,countEnabled : true
					}
					,contextMenu : [
						{key : 'sqlFormatView', name :'Sql Format View', callback: function (){
							getFormatSql(_this.logElement[logId].selectionData());
						}}
						,{key : 'saveArticle', name :'선택영역 로그 저장', callback: function (){
							var selectionData = _this.logElement[logId].selectionData();

							_ui.saveArticle(_g_options.articleSaveUrl,{
								title : ('['+logItem.name+'] - '+selectionData.substring(0,100))
								,contents : selectionData
							});
						}}
					]
				});
				_this.loadLog(logItem);
			}
		}
		,noty: function (logItem){
			var _this = this; 
			
			VARTOOL.socket.connect('user', {
				uid : logItem.id
				,callback : function (resData){
					_this.setLogData(resData);
				}
			});
		}
		,setLogData :function (logInfo, mode){
			var cmpId = logInfo.cmpId;
		
			var activeCmpFlag = true;
			if(mode != 'load'){
				activeCmpFlag = !_ui.layout.blinkTab(cmpId);
			}
			
			var logElement = this.logElement[cmpId]; 
			
			if(logElement){
				if(logInfo.state > 0){
					if(logElement){
						logElement.setInfoMessage('cmd : '+logInfo.item.cmd);
						logElement.setStatusIcon(logInfo.state==100 ?'stop' :'start');
					}
				}
				
				var log = logInfo.log; 
				
				if(log != null && log !=''){
					logElement.setData(logSplit(log), 'addData', {focus: activeCmpFlag, onlyCalc : !activeCmpFlag});
				}
			}
		}
		// server log view;
		,loadLog : function (logInfo){
			var _this = this; 
			VARTOOL.req.ajax({
				url: {type:VARTOOL.uri.cmp, url:'/command/load'}
				,data : {
					cmpId : logInfo.cmpId
				}
				,success: function(resData) {
					_this.setLogData(resData.item ,'load');
					_this.noty(logInfo);
				}
				,error : function (){
					_this.noty(logInfo);
				}
			})
		}
		,resize : function (dimension, componentState){
			var logUid = componentState.id; 
			if(this.logElement[logUid] && this.logElement[logUid].isVisible()){
				this.logElement[logUid].resizeDraw({onlyCalc :true});
				this.logElement[logUid].moveLastRow();
			}
		}
		,destroy: function (componentState){
			var componentId =componentState.id; 
			try{
				if(this.logElement[componentId]){
					this.logElement[componentId].destroy();
				}
			}catch(e){
				console.log(e);
			}
			
			VARTOOL.socket.unSubscribe('user', componentId);
			
			delete this.logElement[componentId];
		}
	}
})

_ui.registerComponent({
	"deployLogComponent": { 
		logElement : {}
		,init : function (logItem){
			if(VARTOOL.isUndefined(logItem.id)){
				return ; 
			}
			
			var _this =this; 
			this.loadLogViewer(logItem);
		}
		,loadLogViewer : function (logItem){
			var _this = this;
			
			var _this = this; 
			
			var logId = logItem.id; 
			
			if(!_this.logElement[logId]){
				_this.logElement[logId] = $.pubLogViewer('[component-viewerid="'+logId+'"]',{
					logFileName : logItem.name
					,itemMaxCount : 10000
					,items : []
					,setting:{
						saveStateKey : logId
					}
					,status : {
						enabled : true
						,height : 17
						,countEnabled : true
					}
					,contextMenu : [
						{key : 'sqlFormatView', name :'Sql Format View', callback: function (){
							getFormatSql(_this.logElement[logId].selectionData());
						}}
						,{key : 'saveArticle', name :'선택영역 로그 저장', callback: function (){
							var selectionData = _this.logElement[logId].selectionData();

							_ui.saveArticle(_g_options.articleSaveUrl,{
								title : ('['+logItem.name+'] - '+selectionData.substring(0,100))
								,contents : selectionData
							});
						}}
					]
				});
				_this.loadLog(logItem);
			}
		}
		,noty: function (logItem){
			var _this = this; 
			
			this.initNoty = true; 
			
			VARTOOL.socket.connect('user', {
				uid : logItem.id
				,callback : function (resData){
					_this.setLogData(resData);
				}
			});
		}
		// server log view;
		,loadLog : function (logInfo){
			var _this = this; 
			
			VARTOOL.req.ajax({
				url : {type:VARTOOL.uri.cmp, url:'/deploy/load'}
				,data : {
					cmpId : logInfo.cmpId
				}
				,loadSelector : _ui.getLoadSelector('deployLog', logInfo.cmpId)
				,success: function(resData) {
					_this.setLogData(resData.item, 'load');
					_this.noty(logInfo);
				}
				,error : function (){
					_this.noty(logInfo);
				}
			})
		}
		,setLogData :function (logInfo, mode){
			var cmpId = logInfo.cmpId;
			
			var activeCmpFlag = true;
			if(mode != 'load'){
				activeCmpFlag = !_ui.layout.blinkTab(cmpId);
			}
			
			var logElement = this.logElement[cmpId]; 
			
			if(logElement){
				if(logInfo.state > 0){
					if(logElement){
						logElement.setStatusIcon(logInfo.state==100 ?'stop' :'start');
					}
				}
				
				var log = logInfo.log; 
				
				if(log != null && log !=''){
					logElement.setData(logSplit(log), 'addData', {focus: activeCmpFlag, onlyCalc : !activeCmpFlag});
				}
				
				if(logInfo.item){
					var userInfo = logInfo.item;
					 
					if(userInfo){
						logElement.setInfoMessage('user id: ['+userInfo.userId+'] deploy ip: ' +userInfo.userIp);
					}	
				}		
			}
		}
		,resize : function (dimension, componentState){
			var logUid = componentState.id; 
			if(this.logElement[logUid] && this.logElement[logUid].isVisible()){
				this.logElement[logUid].resizeDraw({onlyCalc :true});
				this.logElement[logUid].moveLastRow();
			}
		}
		,destroy: function (componentState){
			var componentId = componentState.id;
			try{
				if(this.logElement[componentId]){
					this.logElement[componentId].destroy();
				}
			}catch(e){
				console.log(e);
			}
			
			VARTOOL.socket.unSubscribe('user', componentId);
			
			delete this.logElement[componentId];
		}
	}
})

// app log
_ui.registerComponent({
	"appLogComponent" :	{ 
		logElement : {}
		,init : function (logItem){
			this.loadLogViewer(logItem);
		}
		,loadLogViewer : function (logItem){
			var _this = this; 
			
			var logId = logItem.id; 
			
			if(!_this.logElement[logId]){
				_this.logElement[logId] = $.pubLogViewer('[component-viewerid="'+logId+'"]',{
					logFileName : logItem.name
					,itemMaxCount : 10000
					,items : []
					,setting:{
						saveStateKey : logId
					}
					,contextMenu : [
						{key : 'sqlFormatView', name :'Sql Format View', callback: function (){
							getFormatSql(_this.logElement[logId].selectionData());
						}}
						,{key : 'saveArticle', name :'선택영역 로그 저장', callback: function (){
							var selectionData = _this.logElement[logId].selectionData();

							_ui.saveArticle(_g_options.articleSaveUrl,{
								title : ('['+logItem.name+'] - '+selectionData.substring(0,100))
								,contents : selectionData
							});
						}}
					]
				});
				_this.loadAppLog(logItem);
			}
		}
		,noty: function (logItem){
			var _this = this;
			
			VARTOOL.socket.connect('user', {
				uid : logItem.id
				,callback : function (resData){
					_this.setLogData(resData);
				}
			});
		}
		// server log view;
		,loadAppLog : function (logItem){
			var _this = this; 
			
			VARTOOL.req.ajax({
				url : {type:VARTOOL.uri.cmp, url:'/log/load'} 
				,data : logItem
				,loadSelector : _ui.getLoadSelector('appLog', logItem.id)
				,success: function(resData) {
					_this.setLogData(resData.item,'load');
					_this.noty(logItem);
				}
				,error : function (){
					_this.noty(logItem);
				}
			})
		}
		,setLogData :function (logInfo, mode){
			var cmpId = logInfo.cmpId;
			
			var activeCmpFlag = true;
			if(mode != 'load'){
				activeCmpFlag = !_ui.layout.blinkTab(cmpId);
			}
			
			var logElement = this.logElement[cmpId]; 
			if(logElement){
				var log = logInfo.log;
				 
				logElement.setData(logSplit(log), 'addData' ,{focus: activeCmpFlag, onlyCalc : !activeCmpFlag});			
			}
		}
		,resize : function (dimension, componentState){
			var logUid = componentState.id; 
			if(this.logElement[logUid] && this.logElement[logUid].isVisible()){
				this.logElement[logUid].resizeDraw({onlyCalc :true});
				this.logElement[logUid].moveLastRow();
			}
		}
		,destroy: function (componentState){
			var componentId = componentState.id;
			try{
				if(this.logElement[componentId]){
					this.logElement[componentId].destroy();
				}
			}catch(e){
				console.log(e);
			}
			
			VARTOOL.socket.unSubscribe('user', componentId);
			
			delete this.logElement[componentId];
		}
	}
})


function getFormatSql(sql){
	try{
		sql = sqlFormatter.format(sql, {
	        language: 'sql',
	        linesBetweenQueries : 2
		});
	}catch(e){
		console.log(e);
	}
	
	VARTOOLUI.dialog.open('#sqlFormatDialog',{width:600,height:300,scrolling : 'no'})
	$('#sqlFormatViewArea').val(sql);
}

})( window );