document.addEventListener("DOMContentLoaded", () => {
    const navImg = document.querySelector(".nav-img");
    const navNotify = document.querySelector(".notification");
    const notificationMenu = document.querySelector(".notification-mc");
    const profileMenu = document.querySelector(".menu-content");

    navNotify.addEventListener("click", (e) => {
        notificationMenu.style.display = notificationMenu.style.display === 'block' ? 'none' : "block";
        profileMenu.style.display = 'none';
        e.stopPropagation(); // 이벤트 버블링 방지
    });

    navImg.addEventListener("click", (e) => {
        profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : "block";
        notificationMenu.style.display = 'none';
        e.stopPropagation();
    });

    document.addEventListener("click", (e) => {
        if (!notificationMenu.contains(e.target) && e.target !== navNotify) {
            notificationMenu.style.display = 'none';

        }
        if (!profileMenu.contains(e.target) && e.target !== navImg) {
            profileMenu.style.display = 'none';

        }
    });

    // 메뉴 클릭 시 버블링 방지
    [notificationMenu, profileMenu].forEach(menu => {
        menu.addEventListener("click", e => e.stopPropagation());
    });
});
