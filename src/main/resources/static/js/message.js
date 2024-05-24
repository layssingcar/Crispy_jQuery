let stompClient = null;
let currentChatRoomNo = null;

document.addEventListener("DOMContentLoaded", function () {
    connect();
    setupEventListeners();
    loadChatRooms();
});

function setupEventListeners() {
    document.getElementById("sendButton").addEventListener("click", sendMessage);

    document.getElementById("invite-form").addEventListener('submit', function (event) {
        event.preventDefault();
        const recipient = document.getElementById("recipient").value;
        if (currentChatRoomNo) {
            inviteUser(currentChatRoomNo, recipient);
            console.log(currentChatRoomNo);
        } else {
            alert("채팅방을 선택하세요.");
        }
    });

    const toggleButton = document.getElementById("toggleSidebar");
    const rightSidebar = document.getElementById("rightSidebar");
    const midChat = document.querySelector(".mid-chat");
    const notifyOff = document.querySelector(".notify-off");
    const notifyOn = document.querySelector(".notify-on");

    toggleButton.addEventListener("click", function () {
        if (rightSidebar.style.display === "none" || rightSidebar.style.display === "") {
            rightSidebar.style.display = "flex";
            midChat.style.flexGrow = "0";
        } else {
            rightSidebar.style.display = "none";
            midChat.style.flexGrow = "1";
        }
    });

    notifyOff.addEventListener("click", function () {
        this.style.display = "none";
        notifyOn.style.display = "block";
    });

    notifyOn.addEventListener("click", function () {
        this.style.display = "none";
        notifyOff.style.display = "block";
    });
}

function connect() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });
    });
}

function loadChatRooms() {
    fetch('/api/chat/rooms/v1')
        .then(response => response.json())
        .then(chatRooms => {
            console.log(chatRooms)
            const chatRoomList = document.getElementById('chatRoomList');
            chatRoomList.innerHTML = '';
            chatRooms.forEach(chatRoom => {
                const chatRoomElement = createChatRoomElement(chatRoom);
                chatRoomList.appendChild(chatRoomElement);
            });
        })
        .catch(error => console.error('Error loading chat rooms:', error));
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
            <div>${chatRoom.empName}</div>
            <div>최근 메시지 내용</div>
        </div>
        <div class="notify"></div>
    `;
    chatRoomElement.addEventListener('click', function () {
        loadMessages(chatRoom.chatRoomNo);
        currentChatRoomNo = chatRoom.chatRoomNo; // 현재 채팅방 번호를 업데이트
    });
    return chatRoomElement;
}

function loadChatDetails(chatRoomNo) {
    const chatName = document.getElementById("chatName")
    fetch(`/api/chat/rooms/${chatRoomNo}/v1`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            chatName.textContent = data.participants[0].empName;
        })
}
function updateChatRoomTitle(title) {
    console.log("updateChatRoomTitle" + title)
    document.getElementById('chatRoomTitle').textContent = title;
}


function loadMessages(chatRoomNo) {
    fetch(`/api/chat/rooms/${chatRoomNo}/messages/v1`)
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
        })
        .catch(error => console.error('Error loading messages:', error));
}

function createMessageElement(msg) {
    const messageElement = document.createElement('div');
    messageElement.className = `output ${msg.empNo === currentEmpNo ? 'sent' : 'receive'}`;
    messageElement.innerHTML = `
        ${msg.empNo === currentEmpNo ? `
            <div class="chat-datetime">
                ${new Date(msg.msgDt).toLocaleTimeString()}
            </div>
            <div class="chat">
                ${msg.msgContent}
            </div>
        ` : `
            <div class="chat">
                ${msg.msgContent}
            </div>
            <div class="chat-datetime">
                ${new Date(msg.msgDt).toLocaleTimeString()}
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
    } else {
        alert("채팅방을 선택하고 메시지를 입력하세요.");
    }
}

function showMessage(message) {
    const chatWindow = document.getElementById("chatMessages");
    const messageElement = createMessageElement(message);
    chatWindow.appendChild(messageElement);
}

function inviteUser(chatRoomNo, empNo) {
    const participant = {
        chatRoomNo: chatRoomNo,
        empNo: parseInt(empNo),
        entryStat: 0 // 기본 입장 상태
    };

    fetch(`/api/chat/rooms/${chatRoomNo}/invite/v1`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(participant),
    })
        .then(response => {
            if (!response.ok) {
                console.log(response)
                throw new Error('Failed to invite user');
            }
            return response.json();
        })
        .then(data => {
            console.log('User invited successfully:', data);
            // 추가적인 작업이 필요하다면 여기에 추가
        })
        .catch(error => console.error('Error inviting user:', error));
}
