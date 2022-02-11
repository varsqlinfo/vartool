/**
 * author ytkim
 */
(function(ytTree) {

ytTree.createTree = function(treeInfo) {
	
var _treeCntl = {},
_treeConfig={
	treeName		: ''
	,treeItem		: new Object()
	,selected		: null
	,selectedNode	: null
	,selectedFound	: false
	,completed		: false
	,idArr			: new Array()
	,rootArr		: new Array()
	,topMenuView	:'inline'
	,toggle : function(obj_id){
		var tNode=this.treeItem[obj_id];

		this.selectedNode = tNode;
		var icon = _treeOption.icon;
		var tmpTopDiv = _treeOption.treeName; 
		var nodeDivId = tmpTopDiv+'_'+obj_id;
		var imgSel = document.getElementById('c_'+nodeDivId);

		if(imgSel){
			if(imgSel.childNodes.length >0 || tNode.childCnt > 0){
				var viewYn= imgSel.style.display=='none'?'inline':'none';
				if(tNode.depth !=0 ){
					
					document.getElementById(nodeDivId+'_icon').src = tNode.icon =='' || tNode.icon ==undefined?(viewYn !='none'?icon.folderOpen:icon.folder):(viewYn =='none'?(tNode.iconOpen==''||tNode.iconOpen==undefined?tNode.icon:tNode.iconOpen):tNode.icon);
					document.getElementById(nodeDivId+'_join').src = this.treeItem[tNode.pid].childDataCnt != tNode.sortOrder?((viewYn !='none')?icon.minus:icon.plus):((viewYn !='none')?icon.minusBottom:icon.plusBottom);
				}
				imgSel.style.display =viewYn;
				
				if(viewYn=='none') return ; 
			}

			if(tNode.childCnt > 0 && imgSel.childNodes.length < 1 ) {
				_treeOption.waitStart();
				_treeOption.subTreeCallBack(tNode);
			}
		}
	}
	,doClick : function(obj_id){

		var tNode =	this.treeItem[obj_id];

		this.selectedNode = tNode;

		_treeOption.onSelectNode(tNode);
	}
},
_treeOption={
	icon : {
		root				: global_page_context_path+'/webstatic/imgs/base.gif'
		,folder			: global_page_context_path+'/webstatic/imgs/folder.gif'
		,folderOpen	: global_page_context_path+'/webstatic/imgs/folderopen.gif'
		,node				: global_page_context_path+'/webstatic/imgs/page.gif'
		,empty				: global_page_context_path+'/webstatic/imgs/empty.gif'
		,line				: global_page_context_path+'/webstatic/imgs/line.gif'
		,join				: global_page_context_path+'/webstatic/imgs/join.gif'
		,joinBottom	: global_page_context_path+'/webstatic/imgs/joinbottom.gif'
		,plus				: global_page_context_path+'/webstatic/imgs/plus.gif'
		,plusBottom	: global_page_context_path+'/webstatic/imgs/plusbottom.gif'
		,minus				: global_page_context_path+'/webstatic/imgs/minus.gif'
		,minusBottom	: global_page_context_path+'/webstatic/imgs/minusbottom.gif'
		,nlPlus			: global_page_context_path+'/webstatic/imgs/nolines_plus.gif'
		,nlMinus			: global_page_context_path+'/webstatic/imgs/nolines_minus.gif'
	}
	,waitObj : 'tree_wait_content_cover'
	,openDepth		:'1'
	,subTreeCallBack :  function(sNode) {
		if(typeof subMenuTreeCall!=='undefined'){
			subMenuTreeCall(tNode);
		}else{
			alert('sub tree node not function');
		}
	}
	,onSelectNode : function(tNode){
	
		_treeConfig.selectedNode = tNode;

		if(tNode.url != ''){
			location.href ='javascript:'+tNode.url;
		}
	}
	,waitStart:function (){
		var treeObj = document.getElementById(_treeOption.treeName);
		
		var firstDiv = '<div id="'+_treeOption.waitObj+'" style="width:292px; height:540px;filter:alpha(opacity=20);opacity:0.2;display:inline-block;z-index:1000px;top:0px;left:0px;background-color:white;position:absolute;"></div>';

		if (treeObj.insertAdjacentHTML != null) {
			treeObj.insertAdjacentHTML('BeforeBegin', firstDiv);
		}else{
			var df;	
			var r = treeObj.ownerDocument.createRange();
			r.selectNodeContents(oElement);
			r.collapse(false);
			df = r.createContextualFragment(firstDiv);
			treeObj.appendChild(df);
		}
	}
	,waitStop:function (){
		var rc = document.getElementById(_treeOption.waitObj);
		if(rc) rc.parentNode.removeChild(rc);		
	}
};

_setTreeInfo = function (){
	if(typeof treeInfo==='string'){
		_treeOption.treeName = treeInfo; 
	}else{
		for (var key in treeInfo) {
			if (treeInfo[key] !== undefined){
				if(key == 'icon'){
					_treeOption[key]=setObjectInfo(treeInfo[key], _treeOption[key]);
				}else{
					_treeOption[key] = treeInfo[key];
				}
			}
		}
	}
};_setTreeInfo();

_treeCntl.getTreeObj = function (){
	return _treeOption.treeName; 
}

_treeCntl.setOpenDepth = function(depth){	
	_treeOption.openDepth=depth;
}

_treeCntl.setImg = function(tNode, display){

	var treeItem =  _treeConfig.treeItem;
	var depth = treeItem[tNode.id].depth;
	var str= new Array();	
	var icon = _treeOption.icon;
	
	var imgYnArr= tNode.imgYn.split(',');
	
	for(var i = 0 ; i <depth ; i ++){
		str.push(((imgYnArr[i]=='false' || imgYnArr[i]=='' )?'<img src="'+icon.empty+'">':'<img src="'+icon.line+'">'));
	}
	str.push( '<img id="'+_treeOption.treeName+'_'+tNode.id+'_join" src="');

	var flag = display=='block'?true:false;
	var ti = (treeItem[tNode.pid].childDataCnt != tNode.sortOrder?(tNode.childDataCnt==0 && tNode.childCnt==0 ? icon.join :(tNode.childDataCnt == 0 && tNode.childCnt > 0 ? icon.plus:(flag?icon.minus:icon.plus))):(tNode.childDataCnt==0 && tNode.childCnt==0 ? icon.joinBottom :(tNode.childDataCnt == 0 && tNode.childCnt > 0 ? icon.plusBottom:(flag?icon.minusBottom:icon.plusBottom))));
	str.push(ti );

	if(ti == icon.joinBottom || ti==icon.join) str.push('">');
	else str.push('" class="click-toggle" ytreeId="'+tNode.id+'" style = "cursor:pointer;">');

	return str.join('');
}

_treeCntl.setFolder = function (tNode){
	var treeItem = _treeConfig.treeItem;

	var icon = _treeOption.icon;
	
	sNode = treeItem[tNode.id];

	_treeConfig.idArr.length==0?(_treeConfig.treeItem[sNode.id].childDataCnt=sNode.childDataCnt=0):sNode.childDataCnt;
	
	if(sNode.depth > 0 && sNode != null){
		var tmpTopDiv = _treeOption.treeName; 
		var nodeDivId = tmpTopDiv+'_'+sNode.id ;
		if(sNode.depth >0){
			if(sNode.childDataCnt>0){
				document.getElementById(nodeDivId +'_join').src = (treeItem[sNode.pid].childDataCnt != sNode.sortOrder?(sNode.childDataCnt ==0?icon.join:icon.minus):(sNode.childDataCnt ==0?icon.joinBottom:icon.minusBottom));	
				document.getElementById(nodeDivId +'_icon').src = icon.folderOpen;	
			}else{
				document.getElementById(nodeDivId +'_join').src = (treeItem[sNode.pid].childDataCnt != sNode.sortOrder?(sNode.childDataCnt ==0?icon.join:icon.minus):(sNode.childDataCnt ==0?icon.joinBottom:icon.minusBottom));	
				document.getElementById(nodeDivId +'_icon').src = icon.node;	
			}
		}
	}
}

// 다중 트리 추가 . 
_treeCntl.add = function(o){
	var pid=o.pid;
	var id=o.id;
	
	var tNode = this.createNode(pid);

	for(var key in o){
		if(o[key]!==undefined) tNode[key]= o[key];
	}

	if(_treeConfig.treeItem[pid]){
		_treeConfig.treeItem[pid].childNodes.push(id);
	}else{
		_treeConfig.idArr.push(id);
		_treeConfig.rootArr.push(id);
	}
	
	_treeConfig.treeItem[pid]?(tNode.sortOrder=_treeConfig.treeItem[pid].childDataCnt =(_treeConfig.treeItem[pid].childDataCnt+1)):0;
	
	_treeConfig.treeItem[id] = tNode;
}

_treeCntl.createNode= function (pid){
	var tNode = {
		id :''
		,pid :''
		,name :''
		,url :''
		,title :''
		,target :''
		,icon :''
		,iconOpen :''
		,open :''
		,childDataCnt:0
		,childCnt:0
		,sortOrder:0
		,imgYn:''
		,depth:_treeConfig.treeItem[pid]?((_treeConfig.treeItem[pid].depth)+1):0
		,childNodes: new Array()
	};

	return tNode;
}

// 단일 트리 추가 . 
_treeCntl.addNode = function(o){
	if(document.getElementById(pid)){
		
		var pid=o.pid;
		var id=o.id;

		var tNode = this.createNode(pid);

		for(var key in o){
			if(o[key]!==undefined) tNode[key]= o[key];
		}
			
		if(_treeConfig.treeItem[pid]){
			_treeConfig.treeItem[pid].childNodes.push(id);
		}else{
			_treeConfig.idArr.push(id);
		}
		
		_treeConfig.treeItem[pid]?(tNode.sortOrder=_treeConfig.treeItem[pid].childDataCnt =(_treeConfig.treeItem[pid].childDataCnt+1)):0;
		
		_treeConfig.treeItem[id] = tNode;

		this.addNodeBeforeEnd(tNode);
	}
}

_treeCntl.addNodeBeforeEnd = function (tNode) {
	var oElement = document.getElementById('c_'+_treeOption.treeName+tNode.pid);
	var sHTML  = this.toString();
	
	this.setFolder(_treeConfig.treeItem[tNode.pid]); // 폴더 셋팅. 
	
	if (oElement.insertAdjacentHTML != null) {
		oElement.insertAdjacentHTML('BeforeEnd', sHTML);
	}else{
		var df;	
		var r = oElement.ownerDocument.createRange();
		r.selectNodeContents(oElement);
		r.collapse(false);
		df = r.createContextualFragment(sHTML);
		oElement.appendChild(df);
	}
	_treeConfig.idArr =new Array();
}

// 트리 하나 삭제 . 
_treeCntl.delNode = function(id){
	var tNode = _treeConfig.treeItem[id];
	if(tNode){
		_treeConfig.treeItem[tNode.pid]?(_treeConfig.treeItem[tNode.pid].childDataCnt =(_treeConfig.treeItem[tNode.pid].childDataCnt-1)):0;
		_treeConfig.treeItem[id] = '';
		this.remove(tNode);
	}
}

_treeCntl.remove = function(tNode){
	var tmpTopDiv = _treeOption.treeName; 
	var nodeDivId = tmpTopDiv+'_'+tNode.id ;

	var el = document.getElementById(nodeDivId);
	var el1 = document.getElementById('c_'+nodeDivId);

	el.parentNode.removeChild(el);
	el1.parentNode.removeChild(el1);
	if(_treeConfig.treeItem[tNode.pid]){
		this.setFolder(_treeConfig.treeItem[tNode.pid]);
	}
	_treeConfig.selectedNode='';
}

_treeCntl.isChild = function (obj){
	return obj.length > 0?true:false;
}

_treeCntl.toString = function(obj){
	var treeHtml = new Array();
	var id_arr = 	obj?obj:_treeConfig.idArr;
	var idArrlLen = id_arr.length;
	var tree_item = _treeConfig.treeItem;
	var icon =_treeOption.icon;
	var childNodeArr;
	var firstNodeFlag = _treeConfig.selectedNode==null && _treeOption.treeName?true:false;
	
	if(firstNodeFlag && obj===undefined)	treeHtml.push('<div id="'+_treeOption.treeName+'Top" class="ytTree" ondrag="return false">')
	
	for(var i = 0 ; i < idArrlLen ; i++){
		
		var tNode = tree_item[id_arr[i]];

		childNodeArr = tNode.childNodes;

		_treeConfig.treeItem[id_arr[i]].imgYn = tree_item[tNode.pid]?(tree_item[tNode.pid].imgYn+','+(tree_item[tNode.pid].childDataCnt==tNode.sortOrder?false:true)):'';
		
		tNode=tree_item[id_arr[i]];

		var display = _treeOption.openDepth != 'all' ? (_treeOption.openDepth > tNode.depth?'block':'none') :'block';
		var flag = display=='block'?true:false;
		var iconImg = (tNode.icon =='' || tNode.icon ==undefined) ?(tNode.childDataCnt==0 && tNode.childCnt==0 ? icon.node :(tNode.childDataCnt == 0 && tNode.childCnt > 0 ? icon.folder:(flag?icon.folderOpen:icon.folder))):tNode.icon;
		var tmpTopDiv = _treeOption.treeName; 
		var nodeDivId = tmpTopDiv+'_'+tNode.id;
		var nodeId = tNode.id; 
		if(tNode.depth ==0){
			treeHtml.push('<div id="'+nodeDivId+'" style="display:'+_treeConfig.topMenuView+'"><img id="'+nodeDivId+'_icon" src="'+icon.root+'" class="click-toggle" ytreeId="'+nodeId+'"> <a href ="javascript:" id="'+nodeDivId+'_a" ytreeId="'+nodeId+'" class="dbclick-toggle onclick-click" target="'+(tNode.target?tNode.target:'')+'">'+tNode.name+'</a> </div><span id="c_'+nodeDivId+'" style="display:inline">'+this.toString(childNodeArr) +'</span>');
		}else{
			if(tNode.childDataCnt ==0){
				treeHtml.push('<div id="'+nodeDivId+'" style="display:block">'+this.setImg(tNode, display)+'<img id="'+nodeDivId+'_icon" src="'+iconImg+'"> <a href ="javascript:" id="'+nodeDivId+'_a" ytreeId="'+nodeId+'" class="dbclick-toggle onclick-click"  target="'+(tNode.target?tNode.target:'')+'">'+tNode.name+'</a> </div><span id="c_'+nodeDivId+'" style="display:none"></span>');
				
			}else{
				treeHtml.push('<div id="'+nodeDivId+'" style="display:block">'+this.setImg(tNode, display)+'<img id="'+nodeDivId+'_icon" src="'+iconImg+'"> <a href="javascript:" id="'+nodeDivId+'_a" ytreeId="'+nodeId+'" class="dbclick-toggle onclick-click" target="'+(tNode.target?tNode.target:'')+'">'+tNode.name+'</a> </div><span id ="c_'+nodeDivId+'" style="display:'+display+'">' +this.toString(childNodeArr) +'</span>');
			}
		}
	}
	
	if(firstNodeFlag && obj===undefined)	treeHtml.push('</div>');
	
	return treeHtml.join('');
}
_treeCntl.getSelect= function(){
	var sNode = _treeConfig.selectedNode;
	if(sNode !=null && sNode != ''){
		document.getElementById(_treeOption.treeName+'_'+sNode.id+'_a').className = 'style2';
		return sNode;
	}
}

_treeCntl.open= function(){
	var icon = _treeOption.icon;
	var sNode = _treeConfig.selectedNode;
	var treeItem = _treeConfig.treeItem;
	_treeOption.waitStop();

	var nodeDivId = _treeOption.treeName; 

	if(sNode==null && nodeDivId){
		document.getElementById(nodeDivId).innerHTML = this.toString();
	}else{
		if(sNode.id){
			_treeConfig.idArr = treeItem[sNode.id].childNodes;
			nodeDivId = 'c_'+nodeDivId+'_'+sNode.id;
			if(_treeConfig.idArr.length >0) this.setFolder(sNode);
			document.getElementById(nodeDivId).innerHTML = this.toString();
		}
	}

	this.getSelect();

	$('#'+nodeDivId+' .click-toggle').on('click',function (){
		_treeConfig.toggle($(this).attr('ytreeId'))
	})

	$('#'+nodeDivId+' .dbclick-toggle').on('dblclick',function (){
		_treeConfig.toggle($(this).attr('ytreeId'))
	});

	$('#'+nodeDivId+' .onclick-click').on('click',function (){
		_treeConfig.doClick($(this).attr('ytreeId'))
	});

}

_treeCntl.allOpen= function(){
	var rootArr =  _treeConfig.rootArr;
	var treeItem = _treeConfig.treeItem;
	var rootArrLen =rootArr.length;
	
	for(var i =0 ; i <rootArrLen ;  i ++){
		var tNode =treeItem[rootArr[i]];

		this.setFolderOpenImg(tNode);
		this.folderOpen(tNode);
	}
}

_treeCntl.folderOpen = function (tNode){
	var cNode = tNode.childNodes;
	var cNodeLen = cNode.length;
	var treeItem = _treeConfig.treeItem;

	if( cNodeLen > 0 ){
		for(var i=0; i < cNodeLen; i ++){
			var tmpNode = treeItem[cNode[i]];
			this.setFolderOpenImg(tmpNode)
			
			if(tmpNode.childNodes.length >0) this.folderOpen(tmpNode);
		}
	}
}

_treeCntl.setFolderOpenImg=function (tNode){
	var icon = _treeOption.icon;
	var treeItem = _treeConfig.treeItem;

	var tmpTopDiv = _treeOption.treeName; 
	var nodeDivId = tmpTopDiv+'_'+tNode.id ;

	var c_obj = document.getElementById('c_'+nodeDivId);
	if(c_obj != '' && c_obj != undefined){
		
		if(tNode.depth > 0  && tNode.childNodes.length > 0){
			c_obj.style.display='inline';

			document.getElementById(nodeDivId+'_icon').src = tNode.icon =='' || tNode.icon ==undefined ? (icon.folderOpen) : (tNode.iconOpen==''||tNode.iconOpen==undefined?tNode.icon:tNode.iconOpen);
			document.getElementById(nodeDivId+'_join').src = treeItem[tNode.pid].childDataCnt != tNode.sortOrder?icon.minus:icon.minusBottom;
		}
	}
}

_treeCntl.allClose= function(){
	var rootArr =  _treeConfig.rootArr;
	var treeItem = _treeConfig.treeItem;
	var rootArrLen =rootArr.length;
	
	for(var i =0 ; i <rootArrLen ;  i ++){
		var tNode =treeItem[rootArr[i]];

		this.setFolderCloseImg(tNode);
		this.folderClose(tNode);
	}
}

_treeCntl.folderClose = function (tNode){
	var cNode = tNode.childNodes;
	var cNodeLen = cNode.length;
	var treeItem = _treeConfig.treeItem;

	if( cNodeLen > 0 ){
		for(var i=0; i < cNodeLen; i ++){
			var tmpNode = treeItem[cNode[i]];
			this.setFolderCloseImg(tmpNode)
			
			if(tmpNode.childNodes.length >0) this.folderClose(tmpNode);
		}
	}
}

_treeCntl.setFolderCloseImg=function (tNode){
	var icon = _treeOption.icon;
	var treeItem = _treeConfig.treeItem;

	var tmpTopDiv = _treeOption.treeName; 
	var nodeDivId = tmpTopDiv+'_'+tNode.id;

	var c_obj = document.getElementById('c_'+nodeDivId);
	if(c_obj != '' && c_obj != undefined){
		if(tNode.depth > 0  && tNode.childNodes.length > 0){
			c_obj.style.display='none';		
			document.getElementById(nodeDivId+'_icon').src = tNode.icon =='' || tNode.icon ==undefined?icon.folder:tNode.icon;
			document.getElementById(nodeDivId+'_join').src = treeItem[tNode.pid].childDataCnt != tNode.sortOrder?icon.plus:icon.plusBottom;
		}
	}
}

_treeCntl.clearTree=function (){
	_treeConfig.treeItem=new Object();
	_treeConfig.selectedNode = null;
	_treeConfig.rootArr=new Array();
	document.getElementById(_treeOption.treeName).innerHTML ='';
}

_treeCntl.getSelectItem=function (){
	return _treeConfig.selectedNode; 
}

function setObjectInfo(source, target){
	for (var key in source) {
		if (source[key] !== undefined) target[key] = source[key];
	}
	return target;
};

function generateUUID() {
	var d = new Date().getTime();
	var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = (d + Math.random()*16)%16 | 0;
		d = Math.floor(d/16);
		return (c=='x' ? r : (r&0x7|0x8)).toString(16);
	});
	return uuid;
};

    return _treeCntl;
};

window.ytTree = ytTree;
})(this);