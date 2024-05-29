let stompClient = null;
let currentChatRoomNo = null;

document.addEventListener("DOMContentLoaded", function () {
    if (currentEmpNo === null) {
        alert("로그인이 필요합니다.");
        window.location.href = "/crispy/login";
    } else {
        connect(() => {
            setupEventListeners();
            fetchChatRooms();
        });
    }
});

function setupEventListeners() {
    const sendButton = document.getElementById("sendButton");
    if (sendButton) {
        sendButton.addEventListener("click", sendMessage);
    }

    const leaveRoomButton = document.getElementById("leave-room");
    if (leaveRoomButton) {
        leaveRoomButton.addEventListener("click", function () {
            if (currentChatRoomNo) {
                leaveChatRoom(currentChatRoomNo);
            } else {
                alert("채팅방을 먼저 선택하세요.");
            }
        });
    }
    const toggleButton = document.getElementById("toggleSidebar");
    const rightSidebar = document.getElementById("rightSidebar");
    const midChat = document.querySelector(".mid-chat");
    const notifyOff = document.querySelector(".notify-off");
    const notifyOn = document.querySelector(".notify-on");
    if (toggleButton) {
        toggleButton.addEventListener("click", function () {
            if (rightSidebar.style.display === "none" || rightSidebar.style.display === "") {
                rightSidebar.style.display = "flex";
                midChat.style.flexGrow = "0";
            } else {
                rightSidebar.style.display = "none";
                midChat.style.flexGrow = "1";
            }
        });
    }
    if (notifyOff) {
        notifyOff.addEventListener("click", function () {
            toggleAlarmStatus(currentChatRoomNo);
            this.style.display = "none";
            notifyOn.style.display = "block";
        });
    }
    if (notifyOn) {
        notifyOn.addEventListener("click", function () {
            toggleAlarmStatus(currentChatRoomNo);
            this.style.display = "none";
            notifyOff.style.display = "block";
        });
    }
    document.getElementById("chatInput").addEventListener("keydown", function(event) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    });
    function toggleAlarmStatus(chatRoomNo) {
        fetch(`/api/chat/rooms/${chatRoomNo}/toggleAlarm/v1`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to toggle alarm status');
                }
            })
            .catch(error => console.error('Error toggling alarm status:', error));
    }
}

function connect(callback) {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        const username = frame.headers['user-name'];

        console.log("Connected as: " + username); // 추가된 로그

        stompClient.subscribe('/topic/messages', function (messageOutput) {
            console.log("Received message on /topic/messages", messageOutput); // 추가된 로그
            const message = JSON.parse(messageOutput.body);
            if (message.chatRoomNo === currentChatRoomNo) {
                showMessage(message);
            }
            fetchUnreadCount();
        });

        stompClient.subscribe('/topic/roomUpdate', function (roomUpdate) {
            console.log("Received room update on /topic/roomUpdate", roomUpdate); // 추가된 로그
            updateChatRooms(JSON.parse(roomUpdate.body));
        });

        stompClient.subscribe('/user/' + username + '/queue/messages', function (messageOutput) {
            console.log("Received message on /user/" + username + "/queue/messages", messageOutput); // 추가된 로그
            const message = JSON.parse(messageOutput.body);
            if (message.chatRoomNo === currentChatRoomNo) {
                showMessage(message);
            } else {
                loadMessages(message.chatRoomNo);
                showMessage(message);
            }
            loadUnreadMessageCounts();
        });

        stompClient.subscribe('/user/' + username + '/queue/unreadCount', function (unreadCount) {
            console.log("Received unread count on /user/" + username + "/queue/unreadCount", unreadCount); // 추가된 로그
            updateUnreadCount(JSON.parse(unreadCount.body));
            loadUnreadMessageCounts();
        });

        stompClient.subscribe('/user/' + username + '/queue/chatRooms', function (chatRoomsOutput) {
            console.log("Received chat rooms on /user/" + username + "/queue/chatRooms", chatRoomsOutput); // 추가된 로그
            const chatRooms = JSON.parse(chatRoomsOutput.body);
            updateChatRooms(chatRooms);
        });

        stompClient.subscribe('/user/' + username + '/queue/roomUpdate', function (roomUpdateOutput) {
            const chatRooms = JSON.parse(roomUpdateOutput.body);
            console.log("Received room updates:", chatRooms);
            updateChatRooms(chatRooms);
        });


        fetchUnreadCount();
        if (callback) callback();
    });
}

function fetchChatRooms() {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/fetchChatRooms", {}, JSON.stringify({}));
    } else {
        console.error("WebSocket is not connected.");
    }
}

function updateParticipantsList(participants) {
    console.log(participants);
    const sideBarImgWrap = document.querySelector(".side-bar-img-wrap");
    if (sideBarImgWrap) {
        while (sideBarImgWrap.children.length > 1) {
            sideBarImgWrap.removeChild(sideBarImgWrap.lastChild);
        }
    }
    participants.forEach(participant => {
        const participantElement = document.createElement('div');
        participantElement.className = 'chat-user-list';
        participantElement.innerHTML = `
            <div class="profile-image">
                <div><img src="${participant.empProfile}" alt="${participant.empName}'s profile"></div>
            </div>
            <span>${participant.empName}</span>
        `;
        sideBarImgWrap.appendChild(participantElement);
    });
}

function fetchUnreadCount() {
    console.log("호출 되나요?");
    if (stompClient) {
        console.log("이 안에도 호출 되요?");
        stompClient.send("/app/fetchUnreadCount", {}, JSON.stringify({}));
    }
}

function updateUnreadCount(unreadCount) {
    const unreadCountBadge = document.getElementById('unreadMessageCountBadge');
    const menuItem = unreadCountBadge.closest('.chat-menu');
    if (unreadCount > 0) {
        unreadCountBadge.textContent = unreadCount;
        unreadCountBadge.style.display = 'inline-block'; // 배지를 보이게 설정
        menuItem.classList.add('has-unread');
    } else {
        unreadCountBadge.style.display = 'none'; // 배지를 숨기기
        menuItem.classList.remove('has-unread');
    }
}

function updateChatRooms(chatRooms) {
    const chatRoomList = document.getElementById('chatRoomList');
    if (chatRoomList) {
        chatRoomList.innerHTML = '';  // 기존 목록을 비우고
        chatRooms.forEach(chatRoom => {
            const chatRoomElement = createChatRoomElement(chatRoom);
            chatRoomList.appendChild(chatRoomElement);
            loadLatestMessage(chatRoom.chatRoomNo, chatRoomElement);  // 마지막 메시지 로드 로직 추가
        });
    }
}

function updateUnreadMessageCount(chatRoomNo) {
    fetch(`/api/chat/rooms/unreadCount/v1`)
        .then(response => response.json())
        .then(unreadCounts => {
            unreadCounts.forEach(count => {
                if (count.chatRoomNo === chatRoomNo) {
                    const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${chatRoomNo}"]`);
                    if (chatRoomElement) {
                        const unreadCountBadge = chatRoomElement.querySelector('.unread-count-badge');
                        if (unreadCountBadge) {
                            unreadCountBadge.textContent = count.unreadCount;
                            unreadCountBadge.style.display = count.unreadCount > 0 ? 'block' : 'none';
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error loading unread message counts:', error));
}

function loadUnreadMessageCounts() {
    fetch('/api/chat/rooms/unreadCount/v1')
        .then(response => response.json())
        .then(unreadCounts => {
            unreadCounts.forEach(count => {
                const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${count.chatRoomNo}"]`);
                if (chatRoomElement) {
                    const unreadCountBadge = chatRoomElement.querySelector('.unread-count-badge');
                    if (unreadCountBadge) {
                        unreadCountBadge.textContent = count.unreadCount;
                        if (count.unreadCount > 0) {
                            unreadCountBadge.style.display = 'block';
                        } else {
                            unreadCountBadge.style.display = 'none';
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error loading unread message counts:', error));
}

function createChatRoomElement(chatRoom) {
    const chatRoomElement = document.createElement('div');
    chatRoomElement.className = 'msg-room';
    chatRoomElement.dataset.chatId = chatRoom.chatRoomNo;
    chatRoomElement.innerHTML = `
        <div class="profile-image">
            <div><img src="/img/anonymous.png"></div>
        </div>
        <div class="temp">
            <div>${chatRoom.chatRoomTitle}</div>
            <div class="latest-message">최근 메시지 내용</div>
        </div>
        <div class="unread-count-badge"></div>
    `;
    chatRoomElement.addEventListener('click', function () {
        currentChatRoomNo = chatRoom.chatRoomNo; // 현재 채팅방 번호를 업데이트
        loadMessages(chatRoom.chatRoomNo);
        subscribeToParticipants(chatRoom.chatRoomNo);
        updateUnreadMessageCount(chatRoom.chatRoomNo); // 채팅방 클릭 시 즉시 갱신
    });
    return chatRoomElement;
}

function subscribeToParticipants(chatRoomNo) {
    if (stompClient) {
        const topic = `/topic/roomParticipants/${chatRoomNo}`;
        stompClient.subscribe(topic, function (participantsOutput) {
            console.log("참가자 목록 업데이트 호출됨");
            updateParticipantsList(JSON.parse(participantsOutput.body));
        });
    }
}

function loadLatestMessage(chatRoomNo, chatRoomElement) {
    fetch(`/api/chat/rooms/${chatRoomNo}/messages/v1`)
        .then(response => response.json())
        .then(messages => {
            if (messages.length > 0) {
                const latestMessage = messages[messages.length - 1];
                const latestMessageDiv = chatRoomElement.querySelector('.latest-message');
                latestMessageDiv.textContent = latestMessage.msgContent;
            }
        })
        .catch(error => console.error('Error loading latest message:', error));
}

function loadChatDetails(chatRoomNo) {
    const chatName = document.getElementById("chatName");
    const sideBarImgWrap = document.querySelector(".side-bar-img-wrap");
    fetch(`/api/chat/rooms/${chatRoomNo}/v1`)
        .then(response => response.json())
        .then(data => {
            // 기존 내용을 지우고 새로 추가
            if (chatName) {
                chatName.innerHTML = '';
            }
            // 초기 요소 (새로운 사용자 초대)를 유지하고 이후의 자식 요소들만 제거
            if (sideBarImgWrap) {
                while (sideBarImgWrap.children.length > 1) {
                    sideBarImgWrap.removeChild(sideBarImgWrap.lastChild);
                }
            }
            // Collect participant names
            const participantNames = data.participants.map(participant => participant.empName).join(', ');
            // Create and append a single element with all participant names
            const participantNameDiv = document.createElement('div');
            participantNameDiv.textContent = participantNames;
            if (chatName) {
                chatName.appendChild(participantNameDiv);
            }

            // Append individual participant elements
            data.participants.forEach(participant => {
                const participantElement = document.createElement('div');
                participantElement.className = 'chat-user-list';
                participantElement.innerHTML = `
                    <div class="profile-image">
                        <div><img src="${participant.empProfile}" alt="${participant.empName}'s profile"></div>
                    </div>
                    <span>${participant.empName}</span>
                `;
                if (sideBarImgWrap) {
                    sideBarImgWrap.appendChild(participantElement);
                }
            });
        })
        .catch(error => console.error('Error loading chat details:', error));
}

function loadMessages(chatRoomNo) {
    fetch(`/api/chat/rooms/${chatRoomNo}/access/v1`, {
        method: "POST"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to add access record');
            }
            return updateAccessTime(chatRoomNo); // 추가된 부분
        })
        .then(() => {
            return fetch(`/api/chat/rooms/${chatRoomNo}/messages/v1`);
        })
        .then(response => response.json())
        .then(messages => {
            document.querySelector(".mid-intro").style.display = "none";
            document.querySelector(".mid-chat").style.display = "flex";
            loadChatDetails(chatRoomNo);
            const chatWindow = document.getElementById("chatMessages");
            chatWindow.innerHTML = '';
            messages.forEach(msg => {
                const messageElement = createMessageElement(msg);
                chatWindow.appendChild(messageElement);
            });
            // 읽지 않은 메시지 카운트를 로드하여 업데이트
            updateUnreadMessageCount(chatRoomNo);
        })
        .catch(error => console.error('Error loading messages:', error));
}

function updateAccessTime(chatRoomNo) {
    return fetch(`/api/chat/rooms/${chatRoomNo}/access/v1`, {
        method: "POST"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update access time');
            }
            // 접속 시간이 업데이트되면 읽지 않은 메시지 개수 갱신
            return fetchUnreadCount();
        })
        .catch(error => console.error('Error updating access time:', error));
}

function leaveChatRoom(chatRoomNo, empNo) {
    fetch(`/api/chat/rooms/${chatRoomNo}/leave/v1`, {
        method: "POST"
    })
        .then(response => {
            if (response.ok) {
                handleExit(chatRoomNo, empNo);
            } else {
                alert("채팅방 나가기 실패")
            }
            return response.json();
        })
        .then(data => {
            alert(data.message)
            location.reload();
        })
        .catch(error => console.error("Error : " + error))
}

function handleExit(chatRoomNo, empNo) {
    fetch(`/api/chat/rooms/${chatRoomNo}/exit/v1`, {
        method: "POST",
        body: JSON.stringify({ empNo: empNo })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update exit record');
            }
        })
        .catch(error => console.error('Error updating exit record:', error));
}

function createMessageElement(msg) {
    const messageElement = document.createElement('div');
    messageElement.className = `output ${msg.empNo === currentEmpNo ? 'sent' : 'receive'}`;
    messageElement.innerHTML = `
        ${msg.empNo === currentEmpNo ? `
            <div class="chat-datetime">
                ${new Date(msg.msgDt).toLocaleTimeString('ko-KR', { hour: 'numeric', minute: 'numeric', hour12: true })}
            </div>
            <div class="chat">
                ${msg.msgContent}
            </div>
        ` : `
            <div class="profile-image">
                <img src="${msg.empProfile}" alt="Profile Image">
            </div>
            <div>
            ${msg.empName}
                <div class="chat">
                    ${msg.msgContent}
                </div>
            </div>
            <div class="chat-datetime">
                ${new Date(msg.msgDt).toLocaleTimeString('ko-KR', { hour: 'numeric', minute: 'numeric', hour12: true })}
            </div>
        `}
    `;
    return messageElement;
}

function sendMessage() {
    const chatInput = document.getElementById("chatInput");
    const messageContent = chatInput.value.trim();
    if (messageContent && currentChatRoomNo) {
        const chatMessage = {
            msgContent: messageContent,
            chatRoomNo: currentChatRoomNo,
            empNo: currentEmpNo
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        chatInput.value = '';

        // 최신 메시지 업데이트
        const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${currentChatRoomNo}"]`);
        if (chatRoomElement) {
            loadLatestMessage(currentChatRoomNo, chatRoomElement);
        }
    } else {
        alert("채팅방을 선택하고 메시지를 입력하세요.");
    }
}

function showMessage(message) {
    const chatWindow = document.getElementById("chatMessages");
    const messageElement = createMessageElement(message);
    chatWindow?.appendChild(messageElement);
    chatWindow.scrollTop = chatWindow.scrollHeight;
}
