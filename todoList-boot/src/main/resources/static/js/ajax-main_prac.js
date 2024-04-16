/* 요소 얻어와서 변수에 저장하기 */

const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");

const reloadBtn = document.querySelector("#reloadBtn");

// 1 ) 전체 Todo 갯수 조회 및 출력하는 함수 정의

function getTotalCount(){
	
	fetch("ajax/totalCount")
	.then( resp => {
		
	})
	.then(result => {
		
	});
	
}