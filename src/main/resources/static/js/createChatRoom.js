document.addEventListener("DOMContentLoaded", function () {
    const createChatRoomForm = document.getElementById("createChatRoomForm");

    createChatRoomForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const chatRoomTitle = document.getElementById("chatRoomTitle").value;
        const creatorEmpNo = currentEmpNo;
        console.log(chatRoomTitle);

        const chatRoom = {
            chatRoomTitle: chatRoomTitle,
            creator: currentEmpNo
        };

        fetch(`/api/chat/rooms/${creatorEmpNo}/v1`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(chatRoom)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('채팅방 생성에 실패했습니다.');
                }
            })
            .then(data => {
                // 성공적으로 생성된 경우 채팅방 목록을 다시 로드
                loadChatRooms();
                // 모달 닫기
                const createChatRoomModal = bootstrap.Modal.getInstance(document.getElementById('createChatRoomModal'));
                createChatRoomModal.hide();
            })
            .catch(error => console.error('Error:', error));
    });
});
