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