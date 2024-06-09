const boardList = {
    optionObj: {
        "pageNo": 1,            // 페이지번호
        "sortKey": "boardHit", // 정렬기준
        "sortOrder": "ASC",     // 정렬순서
        "stockNameSearch": ""   // 재고명검색
    },

    init: function() {
            this.addPageLinkEventFn(1);
            this.addSortEventFn();
            this.bindAddBoardEvent();
    },

    getBoardItemsFn: async function(optionObj) {
        const params = new URLSearchParams(); // URL 쿼리 문자열 객체

        for (let key in optionObj) {
            params.append(key, optionObj[key]);
        }

        const response = await fetch(`/crispy/board-items?${params.toString()}`);
        const html = await response.text();
        document.querySelector(".board-list-container").outerHTML = html;

        // 이벤트 재추가
        this.addPageLinkEventFn(optionObj.pageNo === undefined ? 1 : optionObj.pageNo);
        this.addSortEventFn();
    },

    addPageLinkEventFn: function(pageNo) {
        const pageLinks = document.querySelectorAll(".page-link"); // 페이지 버튼

        pageLinks.forEach(pageLink => {
            pageLink.addEventListener("click", e => {
                e.preventDefault(); // a 태그 기본 동작 방지

                this.optionObj["pageNo"] = pageLink.dataset.pageNo;
                this.getBoardItemsFn(this.optionObj);
            });

            // active 클래스 추가
            if (Number(pageLink.innerText) === Number(pageNo)) {
                pageLink.parentElement.classList.add("active");
            }
        });
    },

    addSortEventFn: function() {
        // 정렬 이벤트 추가 코드 작성
    },

    bindAddBoardEvent: function() {
        document.getElementById('addBtn').addEventListener('click', function() {
            window.location.href = '/crispy/board/save';
        });
    }
};

document.addEventListener("DOMContentLoaded", function () {
    boardList.init();
})

