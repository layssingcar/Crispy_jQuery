document.addEventListener("DOMContentLoaded", function() {
    // 하드코딩된 메시지 데이터를 객체 형태로 저장
    const chatData = {
        1: [
            {
                sender: "moz1mozi",
                time: "오후 12:02",
                message: "미안하다 이거 보여주려고 어그로 끌었다."
            },
            {
                sender: "나",
                time: "오후 12:03",
                message: "이거 레알 실화냐? 가슴이 웅장해진다."
            }
        ],
        2: [
            {
                sender: "사황",
                time: "오후 12:04",
                message: "카이도우는 죽었다.. 미안하다.."
            },
            {
                sender: "나",
                time: "오후 12:05",
                message: "진짜냐? 대박이다."
            }
        ],
        3: [
            {
                sender: "원피스",
                time: "오후 12:06",
                message: "루피가 태양신이라고?"
            },
            {
                sender: "나",
                time: "오후 12:07",
                message: "그게 무슨 말이야?"
            }
        ]
    };

    // 메시지 안내 문구 -> 메시지 내용
    document.querySelectorAll(".msg-room").forEach(room => {
        room.addEventListener("click", function() {
            const chatId = this.getAttribute("data-chat-id");
            const messages = chatData[chatId];
            console.log(chatId);

            // Hide the intro section and show the chat section
            document.querySelector(".mid-intro").style.display = "none";
            document.querySelector(".mid-chat").style.display = "flex";

            // Update the chat name in the chat section
            const chatName = this.querySelector(".temp > div:first-child").textContent;
            document.querySelector(".mid-chat .chat-name > div:nth-child(2)").textContent = chatName;

            // Clear previous chat messages
            const chatWindow = document.querySelector(".mid-chat .chat-window > div:first-child");
            chatWindow.innerHTML = "";

            // Add new chat messages
            messages.forEach(msg => {
                const messageElement = `
                    <div class="output ${msg.sender === '나' ? 'sent' : 'receive'}">
                        ${msg.sender === '나' ? `
                            <div class="chat-datetime">
                                ${msg.time}
                            </div>
                            <div class="chat">
                                ${msg.message}
                            </div>
                        ` : `
                            <div class="chat">
                                ${msg.message}
                            </div>
                            <div class="chat-datetime">
                                ${msg.time}
                            </div>
                        `}
                    </div>
                `;
                chatWindow.innerHTML += messageElement;
            });

            // Ensure the right-info section is hidden
            // document.querySelector(".right-info").style.display = "none";
        });
    });

    // 채팅방 상세 정보 -> 메시지 내용
    document.querySelector(".right-info > div:nth-child(1) a").addEventListener("click", function() {
        document.querySelector(".right-info").style.display = "none";
        document.querySelector(".mid-chat").style.display = "flex";
    });
});
