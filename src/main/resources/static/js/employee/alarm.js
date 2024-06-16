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

    function addNotificationMessage(message) {
        const li = document.createElement("li");
        li.className = 'my-menu-item';

        const a = document.createElement("a");
        a.href = "/crispy/approval-list/sign";
        a.innerHTML = message.replace(' ', '<br>');

        li.appendChild(a)
        notificationListElement.prepend(li); // 새 알림을 목록 상단에 추가
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
                addNotificationMessage(notification.notifyContent);
            });
        })
        .catch(error => {});


    eventSource.addEventListener('notification', function(event) {
        const data = event.data
        console.log(data);
        alert(`새로운 알림: ${data}`);
        let currentCount = parseInt(notificationCountElement.textContent, 10);
        updateNotificationCount(currentCount + 1);
        addNotificationMessage(data);
    });

    eventSource.onerror = function(event) {
        eventSource.close();
    };
});