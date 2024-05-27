document.addEventListener("DOMContentLoaded", function () {
    const userListDiv = document.getElementById('userList');
    fetch(`/api/employee/employees/v1`)
        .then(response => response.json())
        .then(data => {
            displayUserList(data);
        })
        .catch(error => console.error('Error fetching user list:', error));

    function displayUserList(users) {
        userListDiv.innerHTML = '';
        if (users.length > 0) {
            const list = document.createElement('ul');
            list.classList.add('list-group');
            users.forEach(user => {
                console.log(user)
                const listDiv = document.createElement('div')
                listDiv.className = "invite-info"

                const listImg = document.createElement("img")
                listImg.className = 'modal-profile-image'
                listImg.src = `${user.empProfile}`
                const listItem = document.createElement('span');
                listItem.className = 'ms-3';
                listItem.textContent = ` ${user.empName} (${user.posName}) - ${user.empPhone}`;
                listItem.dataset.userId = user.empNo;
                const listCheckbox = document.createElement("input");
                listCheckbox.type = "checkbox";
                listCheckbox.value = user.empNo;
                listDiv.appendChild(listImg);
                listDiv.appendChild(listItem);
                listDiv.appendChild(listCheckbox);
                userListDiv.appendChild(listDiv);
            });
        } else {
            userListDiv.textContent = '검색 결과가 없습니다.';
        }
    }

    const createChatRoomForm = document.getElementById("createChatRoomForm");

    createChatRoomForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const chatRoomTitle = document.getElementById("chatRoomTitle").value;
        const creatorEmpNo = currentEmpNo;

        // 체크된 체크박스에서 empNo를 수집
        const selectedUsers = Array.from(userListDiv.querySelectorAll('input[type="checkbox"]:checked'))
            .map(checkbox => ({ empNo: checkbox.value }));

        // 채팅방 생성 데이터
        const chatRoom = {
            chatRoomTitle: chatRoomTitle,
            creator: currentEmpNo,
            participants: selectedUsers
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
                alert(data.message);
                loadChatRooms();
                const createChatRoomModal = bootstrap.Modal.getInstance(document.getElementById('createChatRoomModal'));
                createChatRoomModal.hide();
            })
            .catch(error => console.error('Error:', error));
    });
});
