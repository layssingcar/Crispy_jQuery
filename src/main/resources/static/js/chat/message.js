const message = {
    stompClient: null,
    currentChatRoomNo: null,
    isSubscribed: false, // 중복 구독 방지 플래그

    init: function () {
        this.connect(() => {
            this.setupEventListeners();
            message.fetchChatRooms();
        });
    },

    setupEventListeners: function () {
        const sendButton = document.getElementById("sendButton");
        const chatInput = document.getElementById("chatInput");

        if (sendButton) {
            sendButton.removeEventListener("click", this.sendMessageHandler); // 기존 이벤트 핸들러 제거
            this.sendMessageHandler = this.sendMessage.bind(this); // 핸들러 바인딩
            sendButton.addEventListener("click", this.sendMessageHandler); // 클릭 이벤트로 수정
        }

        if (chatInput) {
            chatInput.removeEventListener("keydown", this.sendMessageEnterHandler); // 기존 이벤트 핸들러 제거
            this.sendMessageEnterHandler = (event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                    event.preventDefault();
                    this.sendMessage();
                }
            };
            chatInput.addEventListener("keydown", this.sendMessageEnterHandler);
        }

        const leaveRoomButton = document.getElementById("leave-room");
        if (leaveRoomButton) {
            leaveRoomButton.addEventListener("click", () => {
                if (this.currentChatRoomNo) {
                    this.leaveChatRoom(this.currentChatRoomNo);
                } else {
                    alert("채팅방을 먼저 선택하세요.");
                }
            });
        }

        const toggleButton = document.getElementById("toggleSidebar");
        const rightSidebar = document.getElementById("rightSidebar");
        const midChat = document.querySelector(".mid-chat");

        if (toggleButton) {
            toggleButton.addEventListener("click", () => {
                if (rightSidebar.style.display === "none" || rightSidebar.style.display === "") {
                    rightSidebar.style.display = "flex";
                    midChat.style.flexGrow = "0";
                } else {
                    rightSidebar.style.display = "none";
                    midChat.style.flexGrow = "1";
                }
            });
        }

        const notifyOff = document.querySelector(".notify-off");
        const notifyOn = document.querySelector(".notify-on");
        if (notifyOff) {
            notifyOff.addEventListener("click", () => {
                this.toggleAlarmStatus(this.currentChatRoomNo);
                notifyOff.style.display = "none";
                notifyOn.style.display = "block";
            });
        }
        if (notifyOn) {
            notifyOn.addEventListener("click", () => {
                this.toggleAlarmStatus(this.currentChatRoomNo);
                notifyOn.style.display = "none";
                notifyOff.style.display = "block";
            });
        }
    },

    toggleAlarmStatus: function (chatRoomNo) {
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
    },

    connect: function (callback) {
        const socket = new SockJS('/chat');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, (frame) => {
            const username = frame.headers['user-name'];

            if (!this.isSubscribed) { // 중복 구독 방지
                this.stompClient.subscribe('/topic/messages', (messageOutput) => {
                    const message = JSON.parse(messageOutput.body);
                    if (message.chatRoomNo === this.currentChatRoomNo) {
                        this.showMessage(message);
                    }
                    this.updateChatRoomLatestMessage(message.chatRoomNo, message.msgContent);
                    this.fetchUnreadCount();
                });

                this.stompClient.subscribe('/user/' + username + '/queue/messages', (messageOutput) => {
                    const message = JSON.parse(messageOutput.body);
                    if (message.chatRoomNo === this.currentChatRoomNo) {
                        this.showMessage(message);
                    }
                    this.updateChatRoomLatestMessage(message.chatRoomNo, message.msgContent);
                    this.fetchUnreadCount();
                });

                this.stompClient.subscribe('/user/' + username + '/queue/unreadCount', (unreadCount) => {
                    notification.updateUnreadCount(JSON.parse(unreadCount.body));
                });

                this.stompClient.subscribe('/user/' + username + '/queue/chatRooms', (chatRoomsOutput) => {
                    this.updateChatRooms(JSON.parse(chatRoomsOutput.body));
                });

                this.stompClient.subscribe('/user/' + username + '/queue/roomUpdate', (roomUpdateOutput) => {
                    this.updateChatRooms(JSON.parse(roomUpdateOutput.body));
                });

                this.isSubscribed = true; // 구독 완료
            }
            if (callback) callback();
        });
    },

    fetchChatRooms: function () {
        if (this.stompClient && this.stompClient.connected) {
            console.log("Sending /app/fetchChatRooms message");
            this.stompClient.send("/app/fetchChatRooms", {}, JSON.stringify({}));
        } else {
            console.error("WebSocket is not connected.");
        }
    },

    fetchUnreadCount: function () {
        if (this.stompClient) {
            this.stompClient.send("/app/fetchUnreadCount", {}, JSON.stringify({}));
        }
    },

    updateParticipantsList: function (participants) {
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
    },

    sendRoomUpdate: function () {
        if (message.stompClient && message.stompClient.connected) {
            message.stompClient.send("/app/roomUpdate", {}, JSON.stringify({}));
            console.log("Sent roomUpdate message");
        } else {
            console.error("WebSocket is not connected.");
        }
    },

    updateChatRooms: function (chatRooms) {
        // chatRooms가 배열이 아닌 경우 빈 배열로 초기화
        if (!Array.isArray(chatRooms)) {
            console.log(chatRooms);
            chatRooms = [];
        }

        const chatRoomList = document.getElementById('chatRoomList');
        if (chatRoomList) {
            chatRoomList.innerHTML = '';  // 기존 목록을 비우고
            chatRooms.forEach(chatRoom => {
                console.log("이건 뭐임?", chatRoom);
                const chatRoomElement = this.createChatRoomElement(chatRoom);
                chatRoomList.appendChild(chatRoomElement);
                this.loadLatestMessage(chatRoom.chatRoomNo, chatRoomElement);  // 마지막 메시지 로드 로직 추가
            });

            // 현재 선택된 채팅방의 메시지를 로드
            if (this.currentChatRoomNo) {
                this.loadMessages(this.currentChatRoomNo);
            }
        }
    },

    updateUnreadMessageCounts: function () {
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
    },

    createChatRoomElement: function (chatRoom) {
        const chatRoomElement = document.createElement('div');
        chatRoomElement.className = 'msg-room';
        chatRoomElement.dataset.chatId = chatRoom.chatRoomNo;
        chatRoomElement.innerHTML = `
            <div class="profile-image">
                <div><img src="/img/anonymous.png"></div>
            </div>
            <div class="temp">
                <div>${chatRoom.chatRoomTitle}</div>
                <div class="latest-message">${chatRoom.msgContent}</div>
               
            </div>
            <div class="unread-count-badge"></div>
        `;

        chatRoomElement.addEventListener('click', () => {
            this.currentChatRoomNo = chatRoom.chatRoomNo; // 현재 채팅방 번호를 업데이트
            this.loadMessages(chatRoom.chatRoomNo);
            this.subscribeToParticipants(chatRoom.chatRoomNo);
            this.updateUnreadMessageCounts(); // 채팅방 클릭 시 즉시 갱신
        });
        return chatRoomElement;
    },

    subscribeToParticipants: function (chatRoomNo) {
        if (this.stompClient) {
            const topic = `/topic/roomParticipants/${chatRoomNo}`;
            this.stompClient.subscribe(topic, (participantsOutput) => {
                this.updateParticipantsList(JSON.parse(participantsOutput.body));
            });
        }
    },

    loadLatestMessage: function (chatRoomNo, chatRoomElement) {
        fetch(`/api/chat/rooms/${chatRoomNo}/regentMessage/v1`)
            .then(response => response.json())
            .then(message => {  // Changed from 'messages' to 'message'
                if (message) {  // Check if message exists before accessing properties
                    const latestMessageDiv = chatRoomElement.querySelector('.latest-message');
                    latestMessageDiv.textContent = message.msgContent;
                }
                this.updateUnreadMessageCounts();
            })
            .catch(error => console.error('Error loading latest message:', error));
    },

    loadChatDetails: function (chatRoomNo) {
        const chatName = document.getElementById("chatName");
        const sideBarImgWrap = document.querySelector(".side-bar-img-wrap");
        fetch(`/api/chat/rooms/${chatRoomNo}/v1`)
            .then(response => {
                if (!response.ok) {
                    return response.text()
                } else {
                    return response.json()
                }
            })
            .then(data => {
                if (chatName) {
                    chatName.innerHTML = '';
                }
                if (sideBarImgWrap) {
                    while (sideBarImgWrap.children.length > 1) {
                        sideBarImgWrap.removeChild(sideBarImgWrap.lastChild);
                    }
                }
                const participantNames = data.participants.map(participant => participant.empName).join(', ');
                const participantNameDiv = document.createElement('div');
                participantNameDiv.textContent = participantNames;
                if (chatName) {
                    chatName.appendChild(participantNameDiv);
                }

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
    },

    loadMessages: function (chatRoomNo) {
        fetch(`/api/chat/rooms/${chatRoomNo}/access/v1`, {
            method: "POST"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to add access record');
                }
                return this.updateAccessTime(chatRoomNo);
            })
            .then(() => {
                return fetch(`/api/chat/rooms/${chatRoomNo}/messages/v1`);
            })
            .then(response => {
                if (!response.ok) {
                    return response.text();
                } else {
                    return response.json()
                }

            })
            .then(messages => {
                document.querySelector(".mid-intro").style.display = "none";
                document.querySelector(".mid-chat").style.display = "flex";
                this.loadChatDetails(chatRoomNo);
                const chatWindow = document.getElementById("chatMessages");
                chatWindow.innerHTML = '';

                messages.forEach(msg => {
                    const messageElement = this.createMessageElement(msg);
                    chatWindow.appendChild(messageElement);
                });
                chatWindow.scrollTop = chatWindow.scrollHeight;

                const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${chatRoomNo}"]`);
                if (chatRoomElement) {
                    const unreadCountBadge = chatRoomElement.querySelector('.unread-count-badge');
                    if (unreadCountBadge) {
                        unreadCountBadge.textContent = 0;
                        unreadCountBadge.style.display = 'none';
                    }
                }
                this.updateUnreadMessageCounts();
            })
            .catch(error => console.error('Error loading messages:', error));
    },

    updateAccessTime: function (chatRoomNo) {
        return fetch(`/api/chat/rooms/${chatRoomNo}/access/v1`, {
            method: "POST"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to update access time');
                }
                return this.fetchUnreadCount();
            })
            .catch(error => console.error('Error updating access time:', error));
    },

    leaveChatRoom: function (chatRoomNo) {
        fetch(`/api/chat/rooms/${chatRoomNo}/leave/v1`, {
            method: "POST"
        })
            .then(response => {
                if (response.ok) {
                    this.handleExit(chatRoomNo);
                } else {
                    alert("채팅방 나가기 실패");
                }
                return response.json();
            })
            .then(data => {
                alert(data.message);
                location.reload();
            })
            .catch(error => console.error("Error: " + error));
    },

    handleExit: function (chatRoomNo) {
        fetch(`/api/chat/rooms/${chatRoomNo}/exit/v1`, {
            method: "POST"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to update exit record');
                }
            })
            .catch(error => console.error('Error updating exit record:', error));
    },

    createMessageElement: function (msg) {
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
    },

    sendMessage: function () {
        const chatInput = document.getElementById("chatInput");
        const messageContent = chatInput.value.trim();
        if (messageContent && this.currentChatRoomNo) {
            const chatMessage = {
                msgContent: messageContent,
                chatRoomNo: this.currentChatRoomNo,
                empNo: currentEmpNo
            };
            this.stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
            chatInput.value = '';

            // 최신 메시지 업데이트
            const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${this.currentChatRoomNo}"]`);
            if (chatRoomElement) {
                this.updateChatRoomLatestMessage(this.currentChatRoomNo, messageContent);
            }
        } else {
            alert("채팅방을 선택하고 메시지를 입력하세요.");
        }
    },

    showMessage: function (message) {
        const chatWindow = document.getElementById("chatMessages");
        const messageElement = this.createMessageElement(message);
        chatWindow.appendChild(messageElement);
        console.log()
        chatWindow.scrollTop = chatWindow.scrollHeight;
    },

    updateChatRoomLatestMessage: function (chatRoomNo, latestMessageContent) {
        const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${chatRoomNo}"] .latest-message`);
        if (chatRoomElement) {
            chatRoomElement.textContent = latestMessageContent;
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    message.init();
    this.sendRoomUpdate();
});
