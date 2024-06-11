document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll('.menu');

    menuItems.forEach(menu => {
        const targetId = menu.dataset.bsTarget;
        const collapseElement = document.querySelector(targetId);

        if (!collapseElement) {
            console.error(`Element with selector ${targetId} not found.`);
            return;
        }

        // 아이콘 업데이트 함수
        const updateIcon = (collapseElement, isOpen) => {
            const icon = menu.querySelector('i');
            if (isOpen) {
                icon.classList.remove('fa-chevron-down');
                icon.classList.add('fa-chevron-up');
            } else {
                icon.classList.remove('fa-chevron-up');
                icon.classList.add('fa-chevron-down');
            }
        };

        // Collapse 이벤트 리스너 추가
        collapseElement.addEventListener('show.bs.collapse', () => {
            // 다른 열린 메뉴 닫기
            menuItems.forEach(otherMenu => {
                if (otherMenu !== menu) {
                    const otherTargetId = otherMenu.dataset.bsTarget;
                    const otherCollapseElement = document.querySelector(otherTargetId);
                    if (otherCollapseElement && otherCollapseElement.classList.contains('show')) {
                        const bsCollapse = new bootstrap.Collapse(otherCollapseElement, {
                            toggle: false
                        });
                        bsCollapse.hide();
                    }
                }
            });
        });

        collapseElement.addEventListener('shown.bs.collapse', () => {
            updateIcon(collapseElement, true);
        });

        collapseElement.addEventListener('hide.bs.collapse', () => {
            updateIcon(collapseElement, false);
        });

        // 초기 상태 설정
        if (collapseElement.classList.contains('show')) {
            updateIcon(collapseElement, true);
        } else {
            updateIcon(collapseElement, false);
        }
    });
});
