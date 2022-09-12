<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<ul class="nav left-menu">
	<li style="background: #f1f1f1;line-height: 30px;"><a href="${pageContextPath}" class="text-ac"><i class="fa fa-home"></i>Main Page</a></li>
	<li><a href="${pageContextPath}/mgmt/userInfoMgmt"><i class="fa fa-fw fa-user"></i>사용자</a></li>
	<li>
		<a href="#" data-toggle="collapse" data-target="#component-sub-menu">
			<i class="fa fa-server"></i>
			<span class="hidden-xs">Component</span>
			<i class="fa fa-fw fa-caret-down"></i>
		</a>
		<ul id="component-sub-menu" class="nav sub-menu collapse ${fn:indexOf(reqUri,'/mgmt/cmp/') > 0 ?'in':''  }" >
			<li><a href="${pageContextPath}/mgmt/cmp/logMgmt">Log</a></li>
			<li><a href="${pageContextPath}/mgmt/cmp/commandMgmt">Command</a></li>
			<li><a href="${pageContextPath}/mgmt/cmp/deployMgmt">Deploy</a></li>
		</ul>
	</li>
	<li>
		<a href="#" data-toggle="collapse" data-target="#component-group-menu">
			<i class="fa fa-object-group"></i>
			<span class="hidden-xs">Component 그룹관리</span>
			<i class="fa fa-fw fa-caret-down"></i>
		</a>
		<ul id="component-group-menu" class="nav sub-menu collapse ">
			<li><a href="${pageContextPath}/mgmt/cmpGroupMgmt">Group</a></li>
			<li><a href="${pageContextPath}/mgmt/cmpGroupMgmt/user">Group & User</a></li>
		</ul>
	</li>
	<li><a href="${pageContextPath}/mgmt/cred"><i class="fa fa-fw fa-user"></i>Credentials Provider</a></li>
	<li>
		<a href="#" data-toggle="collapse" data-target="#log-sub-menu">
			<i class="fa fa-bar-chart-o"></i>
			<span class="hidden-xs">Log Info</span>
			<i class="fa fa-fw fa-caret-down"></i>
		</a>
		<ul id="log-sub-menu" class="nav sub-menu collapse ">
			<li>
               <a href="${pageContextPath}/mgmt/cmpMonitoring">Log Info</a>
            </li>
		</ul>
	</li>
</ul>

<script>
$(function (){
	var pathname = location.pathname; 
	$('.left-menu a').each(function (){
		var href = $(this).attr('href');
		
		if(href.indexOf(pathname) > -1){
			$(this).closest('ul').addClass('in');
		}
	})	
});


</script>