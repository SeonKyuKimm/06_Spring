/* */

// 1 . SockJS 라이브러리 추가
//     -> common.html에 작성해둠

// 2 . SockJS 객체를 생성할 수 있게 된다
 
 const testSock = new SockJS("/testSock");
 // - 객체 생성 시 자동으로
 // ws://localhost( 또는 ip)/testSock으로 연결 요청으ㄹ 보냄
 
 // 3 . 생성된 SockJS 객체를 이용해서 메세지 전달하는걸 만들어보자
 
const sendMessageFn = (name, str) => {
	
	
	// JSON을 이용해서 데이터를 TEXT 형태로 전달
	const obj ={
		
		"name" : name,
		"str" : str 
	};
	
	// 연결된 웹소켓 핸들러로 JSON을 전달
	testSock.send(JSON.stringify(obj));
}

// 4 . 서버로부터 현재 클라이언트에게
//     웹소켓을 이용한 메세지가 전달된 경우
testSock.addEventListener("message", e => {
	
	// 저 이벤트 객체엔 e.data : 서버로부터 전달받은 message가 들어온다
	const msg = JSON.parse(e.data);// json -> JS Object 로 변환
	console.log(`${msg.name} : ${msg.str}`); // 홍길동 : Hi
	
});