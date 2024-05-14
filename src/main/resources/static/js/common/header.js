document.addEventListener("DOMContentLoaded", () => {
    const navImg = document.querySelector(".nav-img");
    const navUsername = document.querySelector(".nav-username");
    const menuContent = document.querySelector(".my-menu-content");
    const menuItem = document.querySelector(".my-menu-item");


    navImg.addEventListener("click", (e) => {
        menuContent.style.display = menuContent.style.display === 'block' ? 'none' : "block";
        e.stopPropagation();
    });

    navUsername.addEventListener("click", (e) => {
        menuContent.style.display = menuContent.style.display === 'block' ? 'none' : "block";
        e.stopPropagation();
    });


    document.addEventListener("click", (e) => {
        if(e.target !== navImg && e.target !== menuContent) {
            menuContent.style.display = 'none';
        };
    });

    menuContent.addEventListener("click", e => e.stopPropagation());

    menuItem.addEventListener("click", () => {
        location.href = "/";
    });
})