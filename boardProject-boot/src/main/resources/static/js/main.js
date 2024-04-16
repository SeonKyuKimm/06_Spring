//console.log("main.js loaded."); --> 연결 식별 로그

// 쿠키에서 key가 일치하는 value 얻어오기 함수를 생성

// 브라우저에서 저장되서 오는 sessionId값은 "K:V, "K:V, K:V".. 형식임

    // 배열.map(함수) : 배열의 각 요소를 이용해서 '함수'를 수행 후,
                        // 결과값으로 새로운 배열을 만들어서 반환

const getCookie = (key) => {
    
    const cookies = document.cookie; // " K:V; K=V "

    // cookies 문자열을 배열 형태로 변환해줄거다 (안의 데이터를 꺼내 사용하기 쉽게 가공)
    const cookieList = cookies.split("; ") // ["k:V, K:V, K:V"]
                      .map( el => el.split("=") ); // ["k","v"] ...

    // 배열의 각 요소를 어떤 함수를 실행시킴으로서 한번 더 쪼갠다.

    // console.log(cookieList);

    // 현재 배열형태를 -> 객체로 변환( 그래야 다루기가 수월하다.. )

    const obj = {}; // 비어있는 객체를 선언 후,

    for(let i = 0; i < cookieList.length; i++){
        const k = cookieList[i][0]; // key 값
        const v = cookieList[i][1]; // value 값
        obj[k] = v; // 객체에 추가 
    }

    //console.log(obj);

    return obj[key]; // 매개변수로 전달 받은 key 와 
                     // obj 객체에 저장된 키가 일치하는 요소의 value 값 반환

                     //쿠키에 저장되지 않은 애를 불러오면 개발자 도구에 undefiend가 나옴
}


const loginEmail = document.querySelector("#loginForm input[name='memberEmail']");

// 로그인 안된 상태인 경우에 수행한다 (html 상에 thymeleaf 이용해서 분기처리를 해놔서 로그인 창이 보여질 때 수행하는거)
// 
if(loginEmail != null) {// 로그인 창의 이메일 입력부분이 화면에 있을 때
 
    // 쿠키 중 key 값이 "saveId"인 요소의 value값 얻어오기
    const saveId = getCookie("saveId"); // undefined 또는 이메일이 옴

    // saveId 값이 있을 경우
    if(saveId != undefined){
        loginEmail.value = saveId; // 쿠키에서 얻어온 값을 input에 value로 세팅!

        // 아이디 저장 체크박스에 체크해두기
        document.querySelector("input[name ='saveId']").checked = true;
    }
}
//console.log(getCookie("saveId"));
//console.log(getCookie("-"));

// 이메일 , 비밀번호 미작성시 로그인 막기
const loginForm = document.querySelector("#loginForm");

const loginPw = document.querySelector("#loginForm input[name='memberPw']");

// #loginForm이 화면에 존재할 때 (== 로그인 상태 아닐 때)
if( loginForm != null) {

    // 제출 이벤트 발생 시
    loginForm.addEventListener("submit", e => {

        // 이메일 미작성
        if(loginEmail.value.trim().length === 0 ){
            alert("이메일을 작성해주세요~");
            e.preventDefault(); // 기본 이벤트(제출) 막는것. (submit 막기!)
            loginEmail.focus(); // 초점 이동
            return;
        }

        // 비밀번호 미작성
        if(loginPw.value.trim().length === 0 ){
            alert("비밀번호를 작성해주세요~");
            e.preventDefault(); // 기본 이벤트(제출) 막는것. (submit 막기!)
            loginPw.focus(); // 초점 이동
            return;
        }
    });
}

// 빠른 로그인

const quickLoginBtns = document.querySelectorAll(".quick-login");
	
quickLoginBtns.forEach((item, index) => {
	
	// item : 현재 반복 시 꺼내온 객체
	// index : 현재 반복 중인 인덱스
	
	 // quickLoginBtns 요소인 button 태그 하나씩 꺼내서 이벤트 리스너 추가
	item.addEventListener("click", ()=> {
		
		const email = item.innerText; // 버튼에 작성된 이메일 가져오기
		
		location.href ="/member/quickLogin?memberEmail=" + email;
				
		
	})

});

// 회원 목록 조회 (비동기)
// 조회버튼
const selectMemberListBtn = document.querySelector("#selectMemberList");

// tbody 내에 값 들어갈 자리
const memberList = document.querySelector("#memberList"); // tbody 내에 값 들어갈 자리

// 아래는 keyList.forEach( key => tr.append (createTd(member[key]) ) ) 이거 다음에 만들어지는거
// td ( 서버에 갔다오면서 )요소를 만들고 text 추가 후 반환해주기
const createTd = (text) => {
	const td = document.createElement("td");
	td.innerText = text;
	
	return td;
}

// 조회버튼 클릭 시

selectMemberListBtn.addEventListener("click", () => {
	
	// 1)  비동기로 회원 목록 조회
	//		(포함될 회원 정보 : 회원 번호, 이메일, 닉네임 , 탈퇴 여부)
	
	fetch("/member/selectMemberList")// '조회' 이기때문에 보낼 파라미터 값은 없다.
	// 여기서 컨트롤러에 selectMemberList 이름으로 먼저 메서드 만들고 올거임
	// Controller - service - serviceImpl(@Override) - Mapper - mapper-xml 로 갔다옴	
	.then(resp => resp.json() )// 응답객체가 넘어오는 1번쨰. then // JSON.parse(resp) 한 느낌임
	.then(list => {
		
		//만약 첫 then에서 .text() 로 받았다면 
		// const data = JSON.parse(list); 로 써줄 수 있다
		// 하지만 java단에서 List를 받은걸 알기 때문에
		// list 바로 이용 -> JS 객체 배열
		console.log(list);	
		
		// 이전 내용 삭제
		memberList.innerHTML="";
		
		// tbody에 들어갈 요소를 만들고, 값 세팅 후에 tbody(selectMemberList)에 추가해준다
		list.forEach((member,index) => {
			// member : 현재 반복 접근중인 요소
			// index : 현재 접근중인 인덱스

			// tr 만들어서 그 안에 td 만들고, append(붙이는거) 후
			// 그 tr을 tbody에 append
			
			const keyList = ['memberNo', 'memberEmail', 'memberNickname','memberDelFl'];
			
					
			const tr = document.createElement("tr");
			// 이 시점에 <tr></tr> 이 한개 만들어진거임
			
			keyList.forEach( key => tr.append ( createTd(member[key]) ));
			
			// tbody의 자식으로 tr추가
			memberList.append(tr);			
		});
	})
});

// -----------------------------------------------------------------------------------------
/* 특정 회원 비밀번호 초기화*/

const resetMemberNo = document.querySelector("#resetMemberNo");
const resetPw = document.querySelector("#resetPw");

resetPw.addEventListener("click", ()=> {
	
	// 입력 받은 회원 번호를 얻어오기
	const inputNo = resetMemberNo.value;
	
	if(inputNo.trim().lengt == 0 ){
		
		alert("회원 번호 입력해라");
		return;
	}
	
	// 회원 번호가 input창에 들어왔다면 = 자바로 보낼 입력된 데이터가 있어서
	fetch("/member/resetPw",{
		method :"PUT", // PUT : 수정 요청 방식
		headers : {"Content-Type" : "application/JSON"},
		body : inputNo
	})
	.then(resp => resp.text() )
	.then(result => {
		// result == 컨트롤러로부터 반환받아서 Text 형태로 파싱한 값
		// '1' , '0'이 들어와있을거임
		
		if(result >0){
			alert("초기화 성공!");
		}else{
			alert("해당 회원이 존재하지 않습니다");	
		}
	});
});


/*
const fastLogin = document.querySelector("#fastLogin");


fastLogin.addEventListener('click', function(e) {
   
   fetch('/member/fastLogin?memberEmail=' + fastLogin.innerText)
   .then(resp => resp.text())
   .then(result => {
      
      if(result == 1){
         // 멤버를 찾은 경우
         window.location.href = '/';
      }
      else{
         alert('존재하지 않는 아이디입니다.');
      }
   })
   .catch(error => console.log((error)))
})*/
















