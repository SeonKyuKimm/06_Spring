// signup.html 과 연동
// /templates/common/member
// console.log("ㅎㅇ");
// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 객체
// - true  == 해당 항목은 유효한 형식으로 작성됨
// - false == 해당 항목은 유효하지 않은 형식으로 작성됨
const checkObj ={
    "memberEmail" : false,
    "memberPw" : false,
    "memberPwConfirm" : false,
    "memberNickname" : false,
    "memberTel" : false,
    "authKey" : false
};
/* 이메일 유효성 검사 */ 
// 1) 이메일 유효성 검사에 사용되는 요소 얻어오기

const memberEmail = document.querySelector("#memberEmail");
const emailMessage = document.querySelector("#emailMessage");

// 2) 이메일이 입력(input )될 때 마다 유효성 검사 수행
memberEmail.addEventListener("input", e => {

    // 이메일 인증 후 이메일이 변경된 경우
    checkObj.authKey = false;
    document.querySelector("#authKeyMessage").innerText = "";

    // 작성된 이메일 값 얻어오기
    const inputEmail = e.target.value;

    //console.log(inputEmail); . 콘솔창에 입력한 값이 잘 들어오는지 확인하는 콘솔로그

    // 3) 입력된 이메일이 없을 경우
    if(inputEmail.trim().length === 0){
        emailMessage.innerText ="메일을 받을 수 있는 이메일을 입력해주세요";
        
        // 메시지에 색상을 추가하는 클래스 모두 제거 !!!
        emailMessage.classList.remove('confirm', 'error');
        
        // 이메일 유효성 검사 여부를 false로 변경
        checkObj.memberEmail = false;

        // 잘못 입력한 띄어쓰기가 있을 경우 없앰
        memberEmail.value = "";

        return;
    }

    // 4) if문에 걸리지않고 값이 뭔가 작성된 경우
    //    입력된 이메일이 있을 경우 정규식 검사
    //    (알맞은 형태로 작성했는지 검사)
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/; // 이메일 형태로 쓰라는 뜻 ㅎ~

    // 입력 받은 이메일이 정규식과 일치하지 않는 경우
    // 알맞은 이메일 형태가 아닌 경우
    if( !regExp.test(inputEmail) ){
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해주세요.";
        emailMessage.classList.add('error'); // 글자를 빨간색으로 변경해줌 .style-css
        emailMessage.classList.remove('confirm'); // 초록색 글자 제거 .style-css
        checkObj.memberEmail = false; // 유효하지 않은 이메일임을 기록
        return;
    }    

    // 5) 유효한 이메일 형식인 경우중복검사 수행하기
    // 비동기(ajax)이용해서 함

    fetch("/member/checkEmail?memberEmail=" + inputEmail)
    .then( resp => resp.text())
    .then( count => {
         //count : 1이면 중복, 0이면 중복이 아님
        if(count == 1){// 중복이 oo
            emailMessage.innerText="이미 사용중인 이메일 입니다.";
            emailMessage.classList.add('error');
            emailMessage.classList.remove('confirm');
            checkObj.memberEmail = false;

            return;
        }

        // 중복이 xx인 경우
        emailMessage.innerText = "사용 가능한 이메일 입니다.";
        emailMessage.classList.add('confirm');
        emailMessage.classList.remove('error');
        checkObj.memberEmail = true;
    })
    .catch(error => {

        console.log(error);
    });

});


 // -------------------------------------------------------------------------
    /* 이메일 인증*/ 
    const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");

    // 인증번호 입력할 input
    const authKey = document.querySelector("#authKey");

    // 인증번호 입력 후 확인 버튼
    const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");

    // 인증번호 관련 메세지 출력 span태그
    const authKeyMessage = document.querySelector("#authKeyMessage");

    let authTimer; // <타이머 역할을 할 setInterval 함수> 를 저장할 변수

    const initMin = 4;  // 타이머 초기값 (분)
    const initSec = 59; // 타이머 초기값 (초)
    const initTime = "05:00";

    // 실제 줄어드는 시간을 저장할 변수
    let min = initMin;
    let sec = initSec;

    // 인증번호 받기 버튼 클릭 시,
    sendAuthKeyBtn.addEventListener("click", () => {

        checkObj.authKey = false;
        authKeyMessage.innerText = "";

        // 중복되지 않은 유효한 이메일을 입력한 경우가 아니면 
        if(!checkObj.memberEmail){
            alert("유효한 이메일 작성 후 클릭해 주세요");
            return;
        }

        // 클릭 시 타이머 숫자 초기화
        min = initMin;
        sec = initSec;

        // 이전 동작중인 인터벌 클리어
        clearInterval(authTimer);


        /*********************************************/
        
        /* 비동기로 서버에서 메일 보내기 */
        fetch("/email/signup",{
            method : "POST",
            headers : {"content-Type" : "application/json"},
            body : memberEmail.value
        })
        
        /********************************************/
        
        /* 인증번호 타이머 생성 */
        // 메일은 비동기로 서버에서 보내라고 하고,
        // 우리는 그 와중에 화면에서 타이머 시작해야됨 ~~~!!!!!!
        authKeyMessage.innerText = initTime; // 05:00 세팅
        authKeyMessage.classList.remove('confirm', 'error');// 검정색 글씨로 나오게 해줘~~

        alert("인증번호가 발송되었습니다.");

        // setInterval(함수, 지연시간(ms단위로~));
        // 지연시간만큼 시간이 지날 때 마다 함수 수행

        // clearInterval(interval이 저장된 변수. authTimer)
        // - 매개변수로 전달받은 interval을 멈추는 애

        // 인증 시간 출력 (1초마다 동작)

        authTimer = setInterval( ()=> {

            authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;

            // 0분 0초인 경우 ("00:00" 출력 후)
            if( min == 0 && sec == 0){
                checkObj.authKey = false; // 인증 못함
                clearInterval(authTimer); // interval 멈춤
                authKeyMessage.classList.add('error');
                authKeyMessage.classList.remove('confirm');
                
                return;
            }

            // 0초인 경우 (0초를 출력한 후)
            if(sec == 0){
                sec = 60;
                min--;
            }

            sec--;



        }, 1000 ); // 1초 지연시간

    });


    // 전달 받은 숫자가 10 미만인 경우 (한자리) 앞에 0을 붙여서 반환
    function addZero(number){
        if( number < 10) return "0" +number;
        else             return number;
        
    };