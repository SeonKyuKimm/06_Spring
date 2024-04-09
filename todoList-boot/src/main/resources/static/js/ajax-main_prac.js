
/*
changeComplete.addEventListener("click", ()=>{

    // todo 의 todoNo 가져오기
    const todoNo = popupTodoNo.innerText;
    const complete = (popupComplete.innerText === 'Y') ? 'N' : 'Y';

    const param = {
        "todoNo" : todoNo,
        "complete" : complete
    } // java <-> js 

    fetch("/ajax/changeComplete", {// java내 컨트롤러와 연결해주는 경로
        method : "POST",
        headers : {"Content-Type" : "application/json"}, // 요청 데이터의 형식을 JSON으로 지정해서 보낼거다~
        body : JSON.stringify(param)
        // 전달 정보 ?
    })
    .then( resp => resp.text())
    .then(result => {

        console.log("complete : " + complete);

        popupLayer.classList.add("popup-hidden");

        getTotalCount();
        getCompleteCount();
        selectTodoList();
      
    });

    alert("변경되었습니다ㅎ");
  
});*/