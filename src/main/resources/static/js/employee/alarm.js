document.addEventListener("DOMContentLoaded", function() {
    const eventSource = new EventSource(`/notifications/${currentEmpNo}`);

    const notificationCountElement = document.getElementById("notification-count");
    const notificationListElement = document.getElementById("notification-list");

    function updateNotificationCount(count) {
        notificationCountElement.textContent = count;
        if (count === 0 ) {
            notificationCountElement.style.display = 'none';
        } else {
            notificationCountElement.style.display = 'inline';
        }
    }

    function addNotificationMessage(notification) {
        const li = document.createElement("li");
        li.className = 'my-menu-item';

        const a = document.createElement("a");
        if (isAdmin) {
            a.href = `/crispy/order-list/admin?notifyNo=${notification.notifyNo}`;
        } else if (notification.documentType === 'time-off') {
            if (notification.status === 'sign') {
                a.href = `/crispy/approval-list/sign?notifyNo=${notification.notifyNo}`;
            } else if (notification.status === 'final') {
                a.href = `/crispy/approval-list/draft?notifyNo=${notification.notifyNo}`;
            }
        } else if (notification.documentType === 'stock-order') {
            a.href = `/crispy/order-list/franchise?notifyNo=${notification.notifyNo}`;
        }

        a.innerHTML = notification.notifyContent.replace(' ', '<br>');
        a.addEventListener('click', function() {
            markAsRead(notification.notifyNo);
        });

        li.appendChild(a);
        notificationListElement.prepend(li); // 새 알림을 목록 상단에 추가

    }

    function markAsRead(notifyNo) {
        fetch(`/api/notifications/read/${notifyNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                // 알림 읽음 처리 후 할 동작 추가
                console.log(`알림 ${notifyNo} 읽음 처리 완료`);
            }
        }).catch(error => {
        });
    }

    fetch(`/api/notifications/unreadCount/${currentEmpNo}`)
        .then(response => response.json())
        .then(data => {
            updateNotificationCount(data.count);
        })
        .catch(error => {})

    fetch(`/api/notifications/unread/${currentEmpNo}`)
        .then(response => response.json())
        .then(data => {
            data.forEach(notification => {
                addNotificationMessage(notification);
            });
        })
        .catch(error => {});

    eventSource.addEventListener('notification', function(event) {
        const data = JSON.parse(event.data);
        alert(`새로운 알림: ${data.notifyContent}`);
        let currentCount = parseInt(notificationCountElement.textContent, 10);
        updateNotificationCount(currentCount + 1);
        addNotificationMessage(data);

    });

    eventSource.onerror = function(event) {
        eventSource.close();
    };
});