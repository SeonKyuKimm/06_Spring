/* 회원 정보 수정 페이지*/
const updateInfo = document.querySelector("#updateInfo"); // 닉 전번 주소를 감싼 폼 태그 자체

// #updateInfo 요소가 존재 할 때만 수행. 모든 html에 저 ID 태그값이 존재하지 않을거니까
// 이 페이지 안에서만 돌아가라는 뜻 
if(updateInfo != null ){

    //form 태그 제출(클릭)시
    updateInfo.addEventListener("submit", e =>{

        const memberNickname = document.querySelector("#memberNickname");
        const memberTel = document.querySelector("#memberTel");
        const memberAddress = document.querySelectorAll("[name='memberAddress']");

        // 닉네임 유효성 검사 (수정할 때에도 유효성 검사를 해줍니당)
        if(memberNickname.value.trim().length === 0 ){
            alert("닉네임을 입력해주세요");
            e.preventDefault(); // 제출 금지. 막아버리기!
            return;
        }

        // 비어있진 않고, 값이 들어왔다면! 닉네임 정규식으로 체크!
        let regExp = /^[가-힣\w\d]{2,10}$/; // 닉네임 정규표현식

        if( !regExp.test(memberNickname.value) ){ //정규식과 맞지 않으면! 이 먼저
            alert("닉네임이 유효하지 않습니다");
            e.preventDefault(); // 제출 금지
            return;
        }

        // ********************* 닉네임 중복검사는 개별적으로 하ㅓ십쇼
        // 테스트 시 닉네임 중복 안되게 조심하기!
        // *********************


        
        // 전화번호 유효성 검사
        if(memberTel.value.trim().length ===0){
            alert("전화번호를 입력해주세요");
            e.preventDefault(); // 제출막기
            return;
        }

        // 전화번호 정규식에 맞지 않으면
        regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
        if( !regExp.test(memberTel.value) ){
            alert("전화번호가 유효하지 않습니다");
            e.preventDefault();
            return;
        }


        // 주소 유효성 검사
        // 입력 안하면 전부 안해야하고
        // 입력을 하면 세 군데 input창 전부 다 해야함
        const addr0 = memberAddress[0].value.trim().length == 0; // true 나 false값이
        const addr1 = memberAddress[1].value.trim().length == 0; // true 나 false값이
        const addr2 = memberAddress[2].value.trim().length == 0;

        // 셋 모두가 true ( 인풋창에 다 작성이 되어있는)
        const result1 = addr0 && addr1 && addr2; 
        // true && true && true일 경우 result1이  true 가 된다. // 셋다 트루면 아무것도 입력 ㄴㄴ

        //모두 false 인 경우 true 저장
        const result2 = !(addr0 || addr1 || addr2); // 인풋 창 모두 다 입력한 경우 

        // 모두 입력 또는 모두 미입력이 아니면
        if(!(result1 || result2) ){
            alert("주소를 모두 작성 / 미작성 해주세요");
            e.preventDefault();
            return;
        }


    });

}

//--------------------------------------------------
/* 비밀번호 수정*/

//비밀번호 변경 form 태그

const changePw = document.querySelector("#changePw");

if(changePw != null){
    //제출이 이루어졌을 때!
    changePw.addEventListener("submit", e=>{

        const currentPw = document.querySelector("#currentPw");
        const newPw = document.querySelector("#newPw");
        const newPwConfirm = document.querySelector("#newPwConfirm");

        // - 값을 모두 입력했는가를 먼저 알아보자
        let str; // 상황별로 문자열을 다르게 넣어서 출력할 예정. undefined 상태임
        if(currentPw.value.trim().length == 0) str = "현재 사용중인 비밀번호를 입력해주세요";
        else if( newPw.value.trim().length == 0 ) str = "새 비밀번호를 입력해주세요";    
        else if( newPwConfirm.value.trim().length == 0 ) str = "새 비밀번호 확인을 입력해주세요";

        // 저 세가지의 경우에 모두 걸리지 않았다면
        //str은 여전히 undefined상태일거임
        if ( str != undefined) { // str에 값이 대입됨 == if 중 하나가 실행됨
            alert(str);
            e.preventDefault();
            return;
        }

        // 새 비밀번호 정규식   
        const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

        if( !regExp.test(newPw.value)){
            alert("새 비밀번호가 유효하지 않습니다");
            e.preventDefault();
            return;
        }

        // 새 비밀번호 == 새 비밀번호 확인
        if(newPw.value != newPwConfirm.value){
            alert("새 비밀번호가 일치하지 않습니다.");
            e.preventDefault();
            return;
        }


    });

};

// -------------------------------------
/* 탈퇴 유효성 검사*/

// 탈퇴 form 태그 ID 얻어오기
const secession = document.querySelector("#secession");

if(secession != null){

    secession.addEventListener("submit", e => {

        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree");

        // - 비밀번호 입력 되었는지 확인해보자!
        if(memberPw.value.trim().length == 0){
            alert("비밀번호를 입력해주세요 !");
            e.preventDefault();
            return;
        }

        // 약관 동의 체크 확인
        // checkbox 또는 radoi type인 경우 checked 속성
        // - checked -> 체크 했을 시 True, 안됐을 시 False를 반환함

        if(!agree.checked){ // 동의란에 체크가 안됐을 때!
            alert("악관에 동의해주세요");
            e.preventDefault();
            return;
        }

        // 정말 탈퇴할것인지 물어보기 
        if( !confirm("정말 탈퇴하시겠습니까 ?") ){
            alert("취소되었습니다.");
            e.preventDefault();
            return;
        }


    });
}