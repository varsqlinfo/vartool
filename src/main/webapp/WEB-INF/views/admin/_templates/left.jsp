<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>

<ul class="nav left-menu">
	<li><a href="${pageContextPath}/admin/managerMgmt"><i class="fa fa-fw fa-user"></i>매니저 관리</a></li>
	<li>
		<a href="${pageContextPath}/admin/vartoolConfig">
			<i class="fa fa-info"></i>VarTOOL
		</a>
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