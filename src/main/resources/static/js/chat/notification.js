const notification = {
    stompClient: null,
    currentChatRoomNo: null,

    init: function () {
        this.connect()
    },

    connect: function () {
        const socket = new SockJS('/chat');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, (frame) => {
            const username = frame.headers['user-name'];
            this.stompClient.subscribe('/user/' + username + '/queue/unreadCount', (unreadCount) => {
                    this.updateUnreadCount(JSON.parse(unreadCount.body));
                });

        });
    },

    updateUnreadCount: function (unreadCount) {
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
    },


}

document.addEventListener("DOMContentLoaded", function () {
    notification.init()
})