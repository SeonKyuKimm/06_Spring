/* 좋아요 버튼 (하트) 클릭 시 비동기로 좋아요 INSERT / DELETE */

/* 
	Thymeleaf 코드 해석 순서 정리해보자
	
	1 . th: 코드(java) + Spring El
	2 . html 코드 (+ css, js)
	
	*/




// 좋아요를 누가 눌렀는지 알아야 하니까
// 1 ) 로그인한 회원 번호 준비하기
//	   -- > session에서 얻어오기 ( session은 서버에서 관리하기 때문에 JS에서 
//								바로 얻어올 방법이 없다 ㅠ.ㅠ)
// 어느 게시글에서 발생했는지를 알아야 하니까
// 2 ) 현재 게시글 번호를 준비

// 좋아요가 눌렸는지 안눌렸는지 알아야 하니까
// 3 ) 좋아요 여부 준비	


// 1 . #boardLike 가 클릭 되었을 때
document.querySelector("#boardLike").addEventListener("click", e => {
	
	// 2 . 로그인 상태가 아닌 경우 동작 안하게 해주기
	if(loginMemberNo == null){
		alert("로그인 후 이용해 주세요");
		return;
	}
	
	// 3 . 로그인이 된 상태니까 준비된 3개의 변수를 객체로 저장합시다 !
	
	const obj = {
		
		"memberNo" : loginMemberNo,
		"boardNo" : boardNo,
		"likeCheck" : likeCheck
	};
	
	// 4 . 좋아요 INSERT or DELETE 하는 비동기요청
	fetch("/board/like", {
		method: "POST",
		headers : {"Content-type" : "application/json"},
		body : JSON.stringify(obj)
		})
		.then(resp => resp.text() ) // 반환 결과 text로 변환
		.then(count => {
			
			if (count == -1){
				console.log ("좋아요 처리 실패!");
				return;
			}
			
			// 5 .만약 성공이라면, likeCheck  값 0 <-> 1
			// 클릭될 떄 마다 INSERT / DELETE 동작을 번갈아가면서 하게끔..
			likeCheck = likeCheck == 0 ? 1 : 0;
			
			// 6 . 하트를 채웠다 비웠다 바꿔야함 
			//( likeCheck 가 1 , 0 값을 왔다갔다할때마다)
			// toggle() : 있으면 없애고, 없으면 추가해주는 애임
			e.target.classList.toggle("fa-regular");
			e.target.classList.toggle("fa-solid");
			
			// 7 . 게시글 좋아요 수 수정
			// 우리가 구해온 count 수로 변경해줄거다
			e.target.nextElementSibling.innerText = count;
		
	})
	
	
});



//------------------------------- 게시글 수정 버튼 -------------------------------------
const updateBtn = document.querySelector("#updateBtn");
if(updateBtn != null){ // 수정 버튼 존재 시 (로그인한 사용자인 경우)
	
	updateBtn.addEventListener('click', () => {
		
		// GET 방식 요청 처리
		// 현재 : /board/1/2001?cp=1
		// 목표 : /editBoard/1/2001/update?cp=1
		location.href = location.pathname.replace('board','editBoard') // board 라고 써 있는 부분을 찾아서 editBoard라고 바꿀것이다
						+ "/update"
						+ location.search; // 쿼리스트링 시작점을 찾아서 (물음표부터시작해서) 내가 만들고자 하는 경로 뒤에 붙여준다


	});
}










