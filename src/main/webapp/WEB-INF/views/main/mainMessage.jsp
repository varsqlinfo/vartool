<%@ page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/tagLib.jspf"%>
<div class="main-container" id="mainPage">
	<table class="wh100">
		<tr>
			<td>
				<h2 class="ta-c">
					<div>존재하지않는 정보 입니다.</div>
					<sec:authorize access="hasAnyAuthority('MANAGER','ADMIN')">
						<div style="padding-top:10px;">로그 정보를 설정해주세요. 
							<a href="<c:url value="/mgmt"/>"> <i class="fa fa-cog "></i> 설정 바로가기 </a>
						</div>
					</sec:authorize>
				</h2> 
				
				<c:if test="${fn:length(userGroupList) > 0}">
					<table class="w100 ta-c" style="padding-top:20px;">
			        	<tbody>
			        		<tr>
			        			<td>
			        				<h2>사용자 페이지 정보</h2>
			        			</td>
			        		</tr>
			 			<c:forEach var="item" items="${userGroupList}" varStatus="status">
							<tr>
								<td>
									<a href="<c:url value="/main/${item.groupId}"/>">${item.groupName}</a>
								</td> 
							</tr>
						</c:forEach>
						</tbody>
			        </table>
			     </c:if>
			</td>
		</tr>
	</table>
</div>

