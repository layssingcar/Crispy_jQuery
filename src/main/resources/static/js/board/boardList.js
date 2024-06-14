const boardList = {
    optionObj: {
        "pageNo": 1,            // 페이지번호
        "sortKey": "boardNo", // 정렬기준
        "sortOrder": "ASC",     // 정렬순서
        "boardTitleSearch": ""   // 재고명검색
    },

    init: function() {
            this.addPageLinkEventFn(this.optionObj.pageNo);
            this.addSortEventFn();
            this.searchBoardTitle();
            this.bindAddBoardEvent();
            this.truncateText(20);
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
        this.truncateText(20); // 텍스트 자르기 재적용

    },

    addPageLinkEventFn: function(pageNo) {
        const pageLinks = document.querySelectorAll(".page-link"); // 페이지 버튼

        pageLinks.forEach(pageLink => {
            pageLink.addEventListener("click", e => {
                e.preventDefault(); // a 태그 기본 동작 방지

                this.optionObj.pageNo = pageLink.dataset.pageNo;
                this.getBoardItemsFn(this.optionObj);
            });

            // active 클래스 추가
            if (Number(pageLink.innerText) === Number(pageNo)) {
                pageLink.parentElement.classList.add("active");
            }
        });
    },

    addSortEventFn: function() {
        // 게시물 번호 정렬
        document.querySelector("#board-no-sort").addEventListener("click", e => {
            if (this.optionObj.sortKey === "boardNo")
                this.optionObj.sortOrder = (this.optionObj.sortOrder === "ASC") ? "DESC" : "ASC";
            else {
                this.optionObj.sortKey = "boardNo";
                this.optionObj.sortOrder = "DESC";
            }
            this.getBoardItemsFn(this.optionObj);
        })

        // 조회수 정렬
        document.querySelector("#board-hit-sort").addEventListener("click", e => {
            if (this.optionObj.sortKey === "boardHit") {
                this.optionObj.sortOrder = (this.optionObj.sortOrder === "ASC") ? "DESC" : "ASC";
            } else {
                this.optionObj.sortKey = "boardHit";
                this.optionObj.sortOrder = "DESC";
            }
            this.getBoardItemsFn(this.optionObj);
        });
    },
    searchBoardTitle: function() {
        document.querySelector(".search").addEventListener("input", e => {
            this.optionObj.boardTitleSearch = e.target.value;
            this.optionObj.pageNo = 1;
            this.getBoardItemsFn(this.optionObj);
        })
    },

    bindAddBoardEvent: function() {
        document.getElementById('addBtn').addEventListener('click', function() {
            window.location.href = '/crispy/board/save';
        });
    },

    truncateText: function (maxLength) {
        const boardTitle = document.querySelectorAll(".board-title");
        boardTitle.forEach(e => {
            if (e.textContent.length > maxLength) {
                e.textContent = e.textContent.slice(0, maxLength) + '...';
            }
        })
    }
};

document.addEventListener("DOMContentLoaded", function () {
    boardList.init();
})

