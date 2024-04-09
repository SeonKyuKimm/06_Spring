/*

                            <비동기요청> 

    ** Controller 메서드에 명시하는 @Responsebody 어노테이션에 의해
    Spring에서 return값이 호출한 주소 값이 아니고 변수로 작성되더라도
    호출한 곳으로 (본문에선 java scrpit ) db에서 작성된 값을 가지고 돌아갈 수 있다.ㅎ 

*/

/* 요소 얻어와서 변수에 저장하기 */
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");
const reloadBtn = document.querySelector("#reloadBtn");

// 할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");

// 할 일 상세조회와 관련된 요소 얻어오기
const popupLayer = document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");
const popupClose = document.querySelector("#popupClose");

// 상세 조회 버튼
const deleteBtn = document.querySelector("#deleteBtn");
const updateView = document.querySelector("#updateView");
const changeComplete = document.querySelector("#changeComplete");

// 수정 레이어 버튼
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");




// 3 . 할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");

// 전체 Todo 갯수 조회 및 출력하는 함수 정의
function getTotalCount(){
    
    /* 비동기로 서버에서 전체 Todo 개수를 조회하는
       fetch() API 코드를 작성할것임
      (fetch : 가지고오다 ) */

      fetch("/ajax/totalCount") // 비동기 요청을 수행 -> promise 객체 반환
      .then( response => { 
           //response : 비동기 요청에 대한 응답이 담긴 객체
            console.log("response : ", response);
           // response.text() : 응답 데이터를 문자열 / 숫자 형태로 변환한
           //                   결과를 가지는 Promise객체가 되는거다..
           return response.text();
      })
      /* 두 번째 then의 매개변수 (**result**)
       == 첫 번째 then 에서 반환된 promise 객체의 PromiseResult 값 
       쉽게 생각하면 첫번째 then에들어온 response의 값을 바로 쓸 수 없어서
        response에 들어온 값을 뜯어서 result에서 쓴다.*/
      .then(result => {
        // result 매개변수 == Controller 메서드에서 반환된 값
        console.log("result", result);


        // #totalCount 요소의 내용을 result로 변경해주면 되겠네요~ 
        totalCount.innerText = result;
      });

}

// completeCount값 비동기 통신으로 얻어와서 출력하기

function getCompleteCount(){
    
    // fetch() : 비동기로 요청해서 결과 데이터 가져올 수 있게 만든 api

    // 첫 번째 then 의 response : 
    // - 응답 결과, 요청주소,응답 데이터 등이 담겨있다.

    // response.text() : 응답 데이터를 text 형태로 변환

    // 두 번째 then의 result
    // - 첫 번쨰 then에서 text로 변환된 응답 데이터
    fetch("/ajax/completeCount")
    .then( response => {
        //console.log("response1 : ", response);
        return response.text();
    })
    .then( result => {
        console.log("result1", result);
        // #completeCount 요소에 내용으로 result값 출력
        completeCount.innerText = result;
    });
    
}

// 새로고침 버튼이 클릭되었을 때
reloadBtn.addEventListener("click", () => {
    
    
    getTotalCount(); // 비동기로 전체 할 일 개수 조회
    getCompleteCount(); // 비동기로 완료된 할 일 개수 조회
}) 

// ---------------------------------------------------


// 할 일 추가 버튼 클릭 시 동작한다.
addBtn.addEventListener("click", () => {

    // 비동기요청으로 할 일 추가하는 fetch() 코드 작성
    // - 요청 주소 : "/ajax/add"
    // - 데이터 전달방식 (method) : "POST" 방식으로 ~
    
    // 파라미터를 저장한 JS객체 
    const param = {
        //    KEY   :    Value
        "todoTitle" : todoTitle.value,
        "todoContent" : todoContent.value
        // js -> json변환 -> java
    }
    
fetch("/ajax/add" , {
    // 옵션에 대한 key  :  value 형태로 
    method : "POST", // POST방식 요청
    headers : {"Content-Type" : "application/json"}, // 요청 데이터의 형식을 JSON으로 지정해서 보낼거다~
    body : JSON.stringify(param) // param 객체를 JSON(string)형태로 실제 변환해주는 코드
})
.then( resp => resp.text() )// 반환된 응답 값을 text로 변환하는것
.then( temp => { // 첫번째 then에서 반환된 값 중 변환된 text를 temp에 저장한거

    if(temp > 0) { // success
        alert("추가 성공 ㅎㅎ");

        // 추가성공한 제목, 내용 지우기
        todoTitle.value = "";
        todoContent.value = "";

        // 할 일이 추가되었기 떄문에 전체 Todo개수를 다시 조회한다~
        getTotalCount();

        // 할 일 목록 다시 조회하기
        selectTodoList();
    }else{ // fail
        alert("추가실패 ㅠㅠ");
    }

});


});

// -------------------------------------------------------------------

// 비동기(ajax)로 할 일 상세 조회하는 함수
const selectTodo = (url) => {

            // update Layer 가 혹시라도 열려있으면 숨기기
            //updateLayer.classList.add("pupup-hidden");

    // 매개변수 url =='ajax/detail?todoNo=10'; 이라는 형태의 문자열이 넘어옴
    // response.json() :
    // 응답 데이터가 JSON인 경우
    // 이를 자동으로 Object 형태로 변환해주는 메서드~~
    // == JSON.parse(JSON데이터)를 자동으로 한 효과
    // Todo라는 자바 객체 형태라서 객체가 많아서 ??
    // 제이슨의 형태로 넘어왔어서 ~ 저렇게 써도 됨;
    fetch(url)
    .then(resp => resp.json() )
    .then(todo => {

        // 매개변수 todo : 
        // - 서버 응답(JSON)이 Object로 변환된 값

        // const todo = JSON.parse(temp); //string 형태인 json을 자바스크립트 형태로 변환해주는거

        //console.log(todo);

        // popup Layer에 조회된 값을 출력만 해주면 된다 ㅎ
        popupTodoNo.innerText = todo.todoNo;
        popupTodoTitle.innerText = todo.todoTitle;
        popupComplete.innerText = todo.complete;
        popupRegDate.innerText = todo.regDate;
        popupTodoContent.innerText = todo.todoContent;

        // popup Layer 보이게 하기 
        popupLayer.classList.remove("popup-hidden");

        // update Layer 가 혹시라도 열려있으면 숨기기
        updateLayer.classList.add("popup-hidden");
    })
};

// popup layer 의 x 버튼 (#popupClose) 클릭 시 
popupClose.addEventListener("click" , () => {
    // 숨기는 class를 추가
    popupLayer.classList.add("popup-hidden");
})




// 비동기로 할 일 목록을 조회하는 함수

const selectTodoList = () => {

    fetch("/ajax/selectList")
    .then(resp => resp.text() )// 응답 결과를 text로 변환해준다
    .then(result => {
        console.log(result);
        console.log(typeof result); // 객체가 아닌 문자열 형태

        // 문자열은 가공은 할 수 있지만 너무나 힘들다.
        // JSON 형태를 JS 객체타입으로 변환 ?!
        // JSON.parse("JSON데이터를 넣으세용ㅎ")이용
        
        // JSON.pars(JSON데이터) : string -> object
        // - string 형태의 JSON 데이터를 JS Object 타입으로 변환
        
        // JSON.stringify(JS object) : objec -> string
        // JS Object 타입을 String 형태의 JSON 데이터로 변환
        const todoList = JSON.parse(result);

        console.log(todoList);

        // ------------------------------------------------------------

        // 기존에 출력되어있던 할 일 목록을 먼저 모두 삭제한 후, 새로운 목록에 붙이기
        // 모달창 직전에 작성됐다.
        tbody.innerHTML = "";

        // #tbody에 tr/td 요소를 생성해서 내용 추가하기
        for( let todo of todoList ){ // JS 스타일의 향상된 for of 문

            // tr 태그 생성
            const tr = document.createElement("tr");
            const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];

            for(let key of arr){
                const td = document.createElement("td");

                // 제목인 경우
                if(key === 'todoTitle'){
                    const a = document.createElement("a") // a 태그 생성
                    a.innerText = todo[key]; // 제목을 a태그 내용으로 대입한것
                    a.href = "/ajax/detail?todoNo=" + todo.todoNo;
                    td.append(a);
                    tr.append(td);

                    // a태그 클릭 시 기본 이벤트(페이지 이동) 막기
                    a.addEventListener("click", (e) => {
                        e.preventDefault(); // 기본 이벤트를 막아주는 함수

                        // 할 일 상세 조회를 비동기 요청
                        // e.target.href : 클릭된 a 태그의 href 속성값
                        selectTodo(e.target.href);

                    });

                    continue;
                }
                
                td.innerText = todo[key];
                tr.append(td);
            }
            // tbody의 자식으로 tr( 한 행 ) 추가
            tbody.append(tr);
        }

    })
};

//------------------------------------------------------------------
// 삭제버튼(#deleteBtn) 클릭 시
deleteBtn.addEventListener("click", ()=> {

    // 취소 클릭 시, 아무것도 안하도록 먼저 함
    if( !confirm("정말 삭제하시겠습니까?") ){ return; }

    // 삭제할 할 일의 번호 얻어오기
    const todoNo = popupTodoNo.innerText; // #popupTodoNo 내용 얻어오기

    // 비동기 DELETE방식으로 요청
    fetch("/ajax/delete", {
        method : "DELETE", // DELETE방식 요청 -> @DeleteMapping으로 처리
        
        // 데이터 하나를 전달해도 application/ json 작성을 해야함
        headers : {"content-type" : "application/json"},
        body : todoNo // 여러가지면 JS객체 형태로 바꿔서 보냈을거임
                      // -> @RequestBody로 꺼냄
    })
    .then( resp => resp.text() )
    .then(result => {
        
        if(result > 0) { // 삭제 성공이다
            alert("삭제되었습니다");

            // 삭제됐으니까 일단 상세조회창 닫기
            popupLayer.classList.add("popup-hidden");

            // 전체, 완료된 할 일 개수 다시 조회
            // + 할 일 목록 다시 조회
            getTotalCount();
            getCompleteCount();
            selectTodoList();

        }else{ // 삭제 실패당
            alert("삭제 실패 ㅠ");
        }
    })
});


//------------------------------------------------------------------------------


/* // 모달창 내 완료 여부 변경 버튼 클릭 시 - 
changeComplete.addEventListener("click", () => {
    
    const todoNo = popupTodoNo.innerText;
    const complete = (popupComplete.innerText === 'Y') ? 'N': 'Y';

    const param = {
        "todoNo" : todoNo,
        "complete" : complete
    } 

    fetch("/ajax/changeComplete", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(param)
    })
    .then(resp => resp.text())
    .then( result => {

        popupLayer.classList.add("popup-hidden");

        selectTodoList(); // 최신화
        getCompleteCount(); // 완료된 complete에 관한 함수
        getTotalCount();

    });s
        
    alert("변경되었습니다 ~");
    
}); */


// 모달창 내 완료 여부 변경 버튼 클릭 시- (수업)
changeComplete.addEventListener("click", () => {

    // 변경할 할 일 번호, 완료여부 (Y , N)
    const todoNo = popupTodoNo.innerText;
    const complete = popupComplete.innerText === 'Y' ? 'N' : 'Y' ;

    // SQL수행에 필요한 값을 객체로 묶음
    const obj ={ "todoNo" : todoNo, "complete" : complete }

    // 비동기로 완료 변경 (fetch API 이용)

    fetch("/ajax/changeComplete", {
        method: "PUT",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj) //obj라는 객체를 JSON형태로 변경하는거
    })
    .then( resp => resp.text() ) // 컨트롤러 메서드 작성 후 결과값
    .then( result => {

        if(result > 0){

            // update된 DB의 데이터를 재 조회하여 화면에 출력
            // -> 서버 부하가 좀 커진다

            // selectTodoList();
            // 서버 부하를 줄이기 위해 상세 조회에서 Y / N만 바꿈
            popupComplete.innerText = complete;

            
            //getCompleteCount();
            // 서버 부하를 줄이기 위해 완료된 Todo 개수 + - 1씩 

            const count = Number(completeCount.innerText);
            if(complete === 'Y') completeCount.innerText = count + 1;
            else                 completeCount.innerText = count - 1;

            // 서버 부하 줄이기 가능!! 
            selectTodoList();
        }else{
            alert("완료 여부 변경 실패ㅠㅠ");
        }
    });

});

// -----------------------------------------------------------

// 상세 조회에서 수정 버튼 (#updateView) 클릭 시
updateView.addEventListener("click", () => {
   
    // 기존 팝업 레이어는 숨기고 (상세 조회 모달)
    popupLayer.classList.add("popup-hidden");

    // 수정 레이어 띄워주기
    updateLayer.classList.remove("popup-hidden");

    console.log("popupLayer.classList:" , popupLayer.classList);
    console.log("updateLayer.classList:" , updateLayer.classList);

    // 수정 레이어 보일 때
    // 팝업 레이어에 작성된 제목, 내용을 얻어와서 세팅
    updateTitle.value = popupTodoTitle.innerText;

    updateContent.value = popupTodoContent.innerHTML.replaceAll("<br>", "\n");
    // HTML 화면에서 줄 바꿈이 <br>로 인식되고 있는데
    // textarea 에서는 \n으로 바꿔줘야 줄바꿈으로 인식함.

    // 수정 레이어에 있는 수정 버튼에 data-todo-no속성 추가
    updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);


});

//-----------------------------------------------------------------------------

// 수정 레이어에서 취소 버튼 (#updateCancel)이 클릭되었을 때

updateCancel.addEventListener("click", () => {

    // 수정 레이어 숨기기
    updateLayer.classList.add("popup-hidden");
   
});

//-----------------------------------------------------------------------------

updateBtn.addEventListener("click", e => {

    console.log(e.target);

    // 서버로 전달해야 하는 값을 객체로 묶어두자
    const obj = {
        "todoNo" : e.target.dataset.todoNo,
        "todoTitle" : updateTitle.value,
        "todoContent" : updateContent.value
    }

    fetch("/ajax/update", {
       method : "PUT",
       header : {"Content-Type" : "application/json"},
       body : JSON.stringify(obj)
    })
    .then( res => res.text() )
    .then( result => {

        if( result > 0 ){ // 수정 성공 
            alert("수정이 성공했어욯");

            // 수정 레이어 숨기기
            updateLayer.classList.add("popup-hidden");

            // 팝업 레이어 보이기
            popupLayer.classList.remove("popup-hidden");
            
            //목록 재조회해서 목록 리스트 최신화~
            selectTodoList();

            popupTodoTitle.innerText = updateTitle.value;
            popupTodoContent.innerHTML// <br>코드 해석해야해서 HTML로 읽어옴
                = updateContent.value.replaceAll("\n", "<br>");

            popupLayer.classList.remove("popup-hidden")

            // 수정 레이어에 있는 남은 흔적 제거
            updateTitle.value = "";
            updateContent.value = "";
            updateBtn.removeAttribute("data-todo-no"); // 속성 제거
            
        }else{

            alert("수정이 실패했어요ㅠㅎ");
        }

    });

});

// ----------




selectTodoList();
getCompleteCount();
getTotalCount(); // 함수 호출