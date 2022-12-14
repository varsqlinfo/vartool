/*
**
*ytkim
*vartool base lang js
desc
코드 추가 시  아래 규칙을 따라서 추가해주세요.
공통 -> vartool.
사용자 -> vartool.u.
매니저 -> vartool.m.
관리자 -> vartool.a.
error -> error.
 */
if (typeof window != "undefined") {
    if (typeof window.VARTOOLLANG == "undefined") {
        window.VARTOOL_LANG = {};
    }
}else{
	if(!VARTOOL_LANG){
		VARTOOL_LANG = {};
	}
}

VARTOOL_LANG = {
	'vartool.0001' : '페이지를 나가시겠습니까?'
	,'vartool.0002' : '저장되었습니다.'
	,'vartool.0003' : '추가할 항목을 선택해주세요.'
	,'vartool.0004' : '삭제할 항목을 선택해주세요.'
	,'vartool.0005' : '저장하지않은 파일이 존재 합니다.\n페이지를 나가시겠습니까?'
	,'vartool.0006' : '항목을 선택해주세요.'
	,'vartool.0007' : '보낼 사람을 선택하세요.'
	,'vartool.0008' : '내보내기 하시겠습니까?'
	,'vartool.0009' : '에디터 창이 없습니다.\n새파일을 클릭후에 추가해주세요.'
	,'vartool.0010' : 'sql명을 입력해주세요.'
	,'vartool.0011' : '일치하는 내용이 {count}회 변경되었습니다.'
	,'vartool.0012' : '다음 문자열을 찾을수 없습니다.\n{findText}'
	,'vartool.0013' : '초기화 하시면 기본 레이아웃으로 구성되고 새로고침 됩니다.\n초기화 하시겠습니까?'
	,'vartool.0014' : '메시지를 보내시겠습니까?'
	,'vartool.0015' : '[{itemText}] 삭제하시겠습니까?'
	,'vartool.0016' : '삭제 하시겠습니까?'
	,'vartool.0017' : '삭제 되었습니다'
	,'vartool.0018' : '이미 추가된 항목 입니다'
	,'vartool.0019' : '저장 하시겠습니까?'
	,'vartool.0020' : '적용 하시겠습니까?'
	,'vartool.0021' : '초기 설정으로 복구 하시겠습니까?'
	,'vartool.0022' : '설정 정보가 잘못되었습니다\n초기 설정으로 복구 하시겠습니까?'
	,'vartool.0023' : '비밀번호를 입력해 주세요.'
	,'vartool.0024' : '저장 하시겠습니까?\n저장후 페이지 Reload 후에 반영됩니다.'
	,'vartool.0025' : '설정 정보가 잘못되었습니다\n 설정 정보를 확인해주세요.'
	,'vartool.0026' : '유효 하지 않은 설정입니다.'
	,'vartool.0027' : '복사 되었습니다'
	,'vartool.0028' : '내용을 입력해주세요.'
	,'vartool.0029' : '파일 업로드후 처리 실행해주세요.'

	,'vartool.m.0001' : '변경되었습니다.\n변경된 패스워드는 5초후에 사라집니다.'
	,'vartool.m.0002' : ' 매니저 권한을 삭제 하시겠습니까?'
	,'vartool.m.0003' : ' 차단 하시겠습니까?'
	,'vartool.m.0004' : ' 해제 하시겠습니까?'
	,'vartool.m.0005' : '사용자의 {itemName} 그룹을 제거 하시겠습니까?\n그룹을 제거 하시면 그룹에 속한 모든 db를 접근할수 없습니다.'
	,'vartool.m.0006' : '{groupName} 그룹을 제거 하시면 그룹에 속한 모든 db맵핑 정보와 사용자 정보가 삭제 됩니다.\n그룹을 삭제 하시겠습니까?'
	,'vartool.m.0007' : '비밀번호를 정확하게 입력해 주세요.'
	,'vartool.m.0008' : '비밀번호가 변경되었습니다'
	,'vartool.m.0009' : '{userName}님의 비밀번호를 변경하시겠습니까?\n변경된 패스워드는 5초후에 사라집니다.'

	,'vartool.a.0001' : '모두 닫으시겠습니까?'
	,'vartool.a.0002' : '성공\n다시 연결을 하실경우 풀 초기화를 해주세요.'
	,'vartool.a.0003' : '모두 초기화 하시겠습니까?'
	,'vartool.a.0004' : '비밀번호를 변경하시겠습니까?'

	,'vartool.form.0001' : '필수 입력사항입니다'
	,'vartool.form.0002' : '최소 {len}글자 이상 이여야 합니다'
	,'vartool.form.0003' : '비밀번호가 같아야합니다'
	,'vartool.form.0004' : '크기는  {range} 사이여야 합니다'


	//error message code
	,'error.0001' : '로그아웃 되었습니다.\n로그인창 으로 이동하시겠습니까?'
	,'error.0002' : '유효하지않은 요청입니다. 새로고침하시겠습니까?'
	,'error.0003' : '유효하지않은 database 입니다.\n메인 페이지로 이동하시겠습니까?'
	,'error.0004' : '연결이거부되었습니다.관리자에게 문의하세요.'
	,'error.403' : '권한이 없습니다.'
	,'error.80000' : '연결[80000]이 거부되었습니다.\n관리자에게 문의하세요.'
	,'error.80001' : '연결[80001]이 닫혀 있습니다.\n관리자에게 문의하세요.'
	,'btn.ok' : 'Ok'
	,'btn.cancel' : 'Cancel'
	,'btn.close' : 'Close'
	,'btn.copy' : 'Copy'
	,'btn.save' : 'Save'
	,'copy' : '복사'
	,'success' : '성공'
	,'fail' : '실패'
	,'remove' : '삭제'
	,'data' : '데이터'
	,'dataview' : '데이터 보기'
	,'count' : '카운트'
	,'export' : '내보내기'
	,'refresh' : '새로고침'
	,'data.export' : '데이터 내보내기'
		
	,'step.prev' : '이전'
	,'step.next' : '다음'
	,'step.complete' : '완료'
		
	,'file.add' : '파일 선택'	
	,'file.upload' : '업로드'	
	,'file.remove' : '모두삭제'	
	
	,'menu.file.import' : '가져오기'
	,'menu.file.export' : '내보내기'
	,'menu.file.import_export' : '가져오기 & 내보내기...'

	,'msg.add.manager.confirm' : '"{name}" 님에게  매니저 권한을 추가 하시겠습니까?'
	,'msg.del.manager.confirm' : '"{name}" 님의 매니저 권한을 제거 하시겠습니까?'

	,'msg.close.window' : '창을 닫으시겠습니까?'
	,'msg.loading' : '로딩중 입니다.'
	,'msg.refresh' : '새로고침 하시겠습니까?'
}