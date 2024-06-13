const message = {
    stompClient: null,
    currentChatRoomNo: null,
    isSubscribed: false,
    lastTimestamp: null,
    isLoading: false,

    init: function () {
        this.connect(() => {
            this.setupEventListeners();
            message.fetchChatRooms();
            this.fetchUnreadCount();
        });
    },

    setupEventListeners: function () {
        const sendButton = document.getElementById("sendButton");
        const chatInput = document.getElementById("chatInput");

        if (sendButton) {
            sendButton.removeEventListener("click", this.sendMessageHandler);
            this.sendMessageHandler = this.sendMessage.bind(this);
            sendButton.addEventListener("click", this.sendMessageHandler);
        }

        if (chatInput) {
            chatInput.removeEventListener("keydown", this.sendMessageEnterHandler);
            this.sendMessageEnterHandler = (event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                    // IME 입력 중인지 확인
                    if (event.isComposing) {
                        return; // IME 입력 중이면 전송하지 않음
                    }

                    event.preventDefault(); // 기본 엔터 동작(새 줄 삽입)을 방지

                    // 메시지 전송 함수 호출
                    this.sendMessage();
                    chatInput.value = ""; // 입력창 비우기
                }
            };
            chatInput.addEventListener("keydown", this.sendMessageEnterHandler);
        }

        const chatWindow = document.getElementById("chatMessages");
        if (chatWindow) {
            chatWindow.addEventListener("scroll", this.handleScroll.bind(this));
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

    handleScroll: function () {
        const chatWindow = document.getElementById("chatMessages");
        if (chatWindow.scrollTop === 0 && !this.isLoading) {
            this.loadMoreMessages();
        }
    },

    loadMoreMessages: function () {
        if (!this.currentChatRoomNo || this.isLoading) return;

        const beforeTimestamp = this.lastTimestamp
            ? this.getFormattedTimestamp(this.lastTimestamp)
            : this.getFormattedCurrentTimestamp();

        this.isLoading = true;
        fetch(`/api/chat/rooms/${this.currentChatRoomNo}/messages/v2?beforeTimestamp=${encodeURIComponent(beforeTimestamp)}`)
            .then(response => response.json())
            .then(messages => {
                if (messages.length > 0) {
                    this.lastTimestamp = messages[0].msgDt;
                    this.prependMessages(messages);
                }
                this.isLoading = false;
            })
            .catch(error => {
                console.error('Error loading more messages:', error);
                this.isLoading = false;
            });
    },

    prependMessages: function (messages) {
        const chatWindow = document.getElementById("chatMessages");
        const initialScrollHeight = chatWindow.scrollHeight;

        messages.reverse().forEach(msg => {
            const messageElement = this.createMessageElement(msg);
            chatWindow.prepend(messageElement);
        });

        const newScrollHeight = chatWindow.scrollHeight;
        chatWindow.scrollTop = newScrollHeight - initialScrollHeight;
    },

    connect: function (callback) {
        const socket = new SockJS('/chat');
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null;
        this.stompClient.connect({}, (frame) => {
            const username = frame.headers['user-name'];

            if (!this.isSubscribed) {
                this.stompClient.subscribe('/topic/messages', (messageOutput) => {
                    const message = JSON.parse(messageOutput.body);
                    if (message.chatRoomNo === this.currentChatRoomNo) {
                        if (message.msgStat === 'DELETED' || message.msgStat === 1) {
                            this.handleDeletedMessage(message.msgNo);
                            this.fetchChatRooms();
                        } else {
                            this.showMessage(message);
                        }
                    }
                    this.updateChatRoomLatestMessage(message.chatRoomNo, message.msgContent);
                    this.fetchUnreadCount();
                });

                this.stompClient.subscribe('/user/' + username + '/queue/messages', (messageOutput) => {
                    const message = JSON.parse(messageOutput.body);
                    if (message.chatRoomNo === this.currentChatRoomNo) {
                        if (message.msgStat === 'DELETED' || message.msgStat === 1) {
                            this.handleDeletedMessage(message.msgNo);
                            this.fetchChatRooms();
                        } else {
                            this.showMessage(message);
                        }
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


                this.isSubscribed = true;
            }
            if (callback) callback();
        });
    },

    fetchChatRooms: function () {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send("/app/fetchChatRooms", {}, JSON.stringify({}));
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
        }
    },

    updateChatRooms: function (chatRooms) {
        if (!Array.isArray(chatRooms)) {
            chatRooms = [];
        }

        const chatRoomList = document.getElementById('chatRoomList');
        if (chatRoomList) {
            chatRoomList.innerHTML = '';
            chatRooms.forEach(chatRoom => {
                const chatRoomElement = this.createChatRoomElement(chatRoom);
                chatRoomList.appendChild(chatRoomElement);
                this.loadLatestMessage(chatRoom.chatRoomNo, chatRoomElement);
            });

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

        const profileImgContainer = document.createElement('div');
        profileImgContainer.className = "profile-image";
        const profileImg = document.createElement("div");
        const profileImgTag = document.createElement("img");
        profileImgTag.src = "/img/anonymous.png";
        profileImg.appendChild(profileImgTag)
        profileImgContainer.appendChild(profileImg);

        const sideChatRoom =  document.createElement("div");
        sideChatRoom.className = "side-chat-room";
        const titleDiv =document.createElement("div");
        titleDiv.textContent = chatRoom.chatRoomTitle;
        const latestMessageDiv = document.createElement("div");
        latestMessageDiv.className = "latest-message";
        latestMessageDiv.innerHTML = chatRoom.msgContent ? chatRoom.msgContent.replace(/\n/g, "<br>") : '첫 메시지를 보내보세요!';
        sideChatRoom.appendChild(titleDiv);
        sideChatRoom.appendChild(latestMessageDiv);

        const unreadCountBadge = document.createElement('div');
        unreadCountBadge.className = 'unread-count-badge';

        chatRoomElement.appendChild(profileImgContainer)
        chatRoomElement.appendChild(sideChatRoom)
        chatRoomElement.appendChild(unreadCountBadge)

        chatRoomElement.addEventListener('click', () => {
            const chatRooms = document.querySelectorAll('.msg-room');
            chatRooms.forEach(room => room.classList.remove('active'));

            chatRoomElement.classList.add('active');
            this.currentChatRoomNo = chatRoom.chatRoomNo;
            this.loadMessages(chatRoom.chatRoomNo);
            this.subscribeToParticipants(chatRoom.chatRoomNo);
            this.updateUnreadMessageCounts();
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
            .then(message => {
                if (message) {
                    const latestMessageDiv = chatRoomElement.querySelector('.latest-message');
                    if(message.msgStat === 1 || message.msgStat === "DELETED") {
                        latestMessageDiv.innerHTML = "삭제된 메시지입니다.";
                    } else {
                        latestMessageDiv.innerHTML = message.msgContent.replace(/\n/g, "<br>");
                    }
                }
                this.updateUnreadMessageCounts();
            })
            .catch(error => {});
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
                    const profileImgContainer = document.createElement('div');
                    profileImgContainer.className = 'profile-img';
                    const profileImg = document.createElement("div");
                    const profileImgTag = document.createElement("img");
                    profileImgTag.src = participant.empProfile;
                    profileImgTag.alt = participant.empName
                    profileImg.appendChild(profileImgTag)
                    profileImgContainer.appendChild(profileImg)
                    const empNameSpan = document.createElement("span");
                    empNameSpan.textContent = participant.empName;
                    participantElement.appendChild(profileImgContainer)
                    participantElement.appendChild(empNameSpan)
                    if (sideBarImgWrap) {
                        sideBarImgWrap.appendChild(participantElement);
                    }
                });
            })
            .catch(error => console.error('Error loading chat details:', error));
    },

    getFormattedTimestamp: function (dataString) {
        const now = new Date(dataString);
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');
        const milliseconds = String(now.getMilliseconds()).padStart(3, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
    },


    getFormattedCurrentTimestamp: function () {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');
        const milliseconds = String(now.getMilliseconds()).padStart(3, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
    },

    loadMessages: function (chatRoomNo) {
        const beforeTimestamp = this.lastTimestamp
            ? this.getFormattedTimestamp(this.lastTimestamp)
            : this.getFormattedCurrentTimestamp();

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
                    return response.json();
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

                if (messages.length > 0) {
                    // 배열의 마지막 요소에서 타임스탬프를 저장
                    this.lastTimestamp = messages[0].msgDt;
                } else {
                    this.lastTimestamp = null; // 메시지가 없을 경우
                }

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
        messageElement.dataset.msgNo = msg.msgNo; // 메시지 번호를 데이터 속성에 저장

        const profileImageContainer = document.createElement('div');
        profileImageContainer.className = 'profile-image';
        const profileImgTag = document.createElement('img');
        profileImgTag.src = msg.empProfile || '/img/anonymous.png';
        profileImgTag.alt = 'Profile Image';
        profileImageContainer.appendChild(profileImgTag);

        const empNameContainer = document.createElement('div');
        empNameContainer.textContent = msg.empName;

        const chatDatetime = document.createElement('div');
        chatDatetime.className = 'chat-datetime';
        chatDatetime.textContent = new Date(msg.msgDt).toLocaleTimeString('ko-KR', {
            hour: 'numeric',
            minute: 'numeric',
            hour12: true
        });

        const chat = document.createElement('div');
        chat.className = 'chat';

        if (msg.msgStat === 1 || msg.msgStat === 'DELETED') {
            // 메시지가 삭제된 상태라면 "삭제된 메시지입니다" 표시
            chat.className = 'deleted-chat';
            chat.textContent = '삭제된 메시지입니다';
        } else {
            chat.innerHTML = msg.msgContent.replace(/\n/g, "<br>");

            if (msg.empNo === currentEmpNo) {
                const ellipsisIcon = document.createElement('i');
                const profileMenu = document.createElement("div");
                profileMenu.className = "chat-dropdown";
                profileMenu.id = "chat-dropdown"
                profileMenu.innerHTML = `
                <li class="my-menu-item">
                    <a class="chat-delete" id="chat-delete" data-msg-no="${msg.msgNo}">
                        <i class="fa-solid fa-trash-can" style="color: var(--main-color)"></i>
                        <span style="color: var(--main-color)">삭제</span>
                    </a>
                </li>
            `;

                ellipsisIcon.className = 'fa-solid fa-ellipsis-vertical me-3';
                ellipsisIcon.addEventListener('mouseover', () => {
                    ellipsisIcon.style.display = 'block';
                });

                ellipsisIcon.addEventListener('mouseout', () => {
                    ellipsisIcon.style.display = 'none';
                });
                ellipsisIcon.addEventListener("click", e => {
                    // 모든 chat-dropdown 요소를 선택하고 숨김
                    document.querySelectorAll('.chat-dropdown').forEach(menu => {
                        if (menu !== profileMenu) {
                            menu.style.display = 'none';
                        }
                    });
                    // 현재 클릭된 요소만 보이도록 설정
                    profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
                    e.stopPropagation();
                });

                profileMenu.querySelector('.chat-delete').addEventListener('click', () => {
                    const confirmed = confirm("정말 삭제 하시겠습니까?");
                    if(confirmed) {
                        this.deleteMessage(msg.msgNo, msg.chatRoomNo);
                    }
                });

                messageElement.appendChild(profileMenu);
                messageElement.appendChild(ellipsisIcon);

                messageElement.addEventListener('mouseover', () => {
                    ellipsisIcon.style.display = 'block';
                });

                messageElement.addEventListener('mouseout', () => {
                    ellipsisIcon.style.display = 'none';
                });
            }
        }

        if (msg.empNo === currentEmpNo) {
            messageElement.appendChild(chatDatetime);
            messageElement.appendChild(chat);
        } else {
            messageElement.appendChild(profileImageContainer);
            empNameContainer.appendChild(chat);
            messageElement.appendChild(empNameContainer);
            messageElement.appendChild(chatDatetime);
        }

        return messageElement;
    },
    deleteMessage: function (msgNo, chatRoomNo) {

        const msgStat = 1;
        const data = {
            msgNo: msgNo,
            chatRoomNo: chatRoomNo,
            msgStat: msgStat,
        }

        fetch('/api/chat/v1', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                const messageElement = document.querySelector(`.output[data-msg-no="${msgNo}"] .chat`);
                const chatDropdown = document.querySelector(`.output[data-msg-no="${msgNo}"] .chat-dropdown`);
                if (messageElement) {
                    messageElement.classList.remove('chat');
                    messageElement.classList.add('deleted-chat');
                    messageElement.textContent = '삭제된 메시지입니다';
                }
                chatDropdown.style.display = 'none';

                if (this.stompClient && this.stompClient.connected) {
                    this.stompClient.send("/app/chat/delete", {}, JSON.stringify({ chatRoomNo: chatRoomNo, msgNo: msgNo, msgStat: 'DELETED' }));
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    },

    handleDeletedMessage: function (msgNo) {
        const messageElement = document.querySelector(`.output[data-msg-no="${msgNo}"] .chat`);
        const chatDropdown = document.querySelector(`.output[data-msg-no="${msgNo}"] .chat-dropdown`);
        if (messageElement) {
            messageElement.classList.remove('chat');
            messageElement.classList.add('deleted-chat');
            messageElement.textContent = '삭제된 메시지입니다';
        }
        if (chatDropdown) {
            chatDropdown.style.display = 'none';
        }
    },

    sendMessage: function () {
        const chatInput = document.getElementById("chatInput");
        const messageContent = chatInput.value;

        if (messageContent && this.currentChatRoomNo) {
            const chatMessage = {
                msgContent: messageContent,
                chatRoomNo: this.currentChatRoomNo,
                empNo: currentEmpNo
            };
            // 금지어 검사
            fetch('/api/chat/checkBadWords', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ msgContent: messageContent })
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => { throw new Error(err.error); });
                    }
                    return response.text();
                })
                .then(() => {
                    // 금지어가 없는 경우에만 메시지를 보냄
                    this.stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
                    chatInput.value = '';

                    const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${this.currentChatRoomNo}"]`);
                    if (chatRoomElement) {
                        this.updateChatRoomLatestMessage(this.currentChatRoomNo, messageContent);
                    }
                })
                .catch(error => {
                    // 금지어가 포함된 경우 alert 띄움
                    Swal.fire({
                        icon: "warning",
                        text: error.message,
                        width: "365px"
                    })
                });
        } else {
            alert("채팅방을 선택하고 메시지를 입력하세요.");
        }
    },

    showMessage: function (message) {
        const chatWindow = document.getElementById("chatMessages");
        const messageElement = this.createMessageElement(message);
        chatWindow.appendChild(messageElement);
        chatWindow.scrollTop = chatWindow.scrollHeight;
    },

    updateChatRoomLatestMessage: function (chatRoomNo, latestMessageContent) {
        const chatRoomElement = document.querySelector(`.msg-room[data-chat-id="${chatRoomNo}"] .latest-message`);
        if (chatRoomElement) {
            chatRoomElement.innerHTML = latestMessageContent.replace(/\n/g, "<br>");
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    message.init();
    message.sendRoomUpdate();
});
