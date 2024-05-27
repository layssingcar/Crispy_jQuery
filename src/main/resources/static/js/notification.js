let notificationStompClient = null;

document.addEventListener("DOMContentLoaded", function () {
    connectForNotifications();
});

function connectForNotifications() {
    const socket = new SockJS('/chat');
    notificationStompClient = Stomp.over(socket);
    notificationStompClient.connect({}, function (frame) {
        console.log('Connected for notifications: ' + frame);
        const username = frame.headers['user-name'];

        notificationStompClient.subscribe(`/user/${username}/queue/unreadCount`, function (unreadCount) {
            console.log('Received unread message count:', unreadCount);
            updateUnreadCount(JSON.parse(unreadCount.body));
        });

        fetchUnreadCount();
    });
}

function fetchUnreadCount() {
    if (notificationStompClient) {
        notificationStompClient.send("/app/fetchUnreadCount", {}, JSON.stringify({}));
    }
}

function updateUnreadCount(unreadCount) {
    console.log('Received unread message count:', unreadCount);
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

