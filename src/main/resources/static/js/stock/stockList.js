const modal = document.querySelector(".modal");         // 재고 상세 모달
const btnClose = document.querySelector(".btn-close");  // 닫기 버튼
const stockNoSet = new Set();   // 재고번호 Set

// 페이지네이션 옵션 객체
const optionObj = {
    "pageNo": 1,            // 페이지번호
    "stockCtNo": 0,         // 카테고리번호
    "sortKey": "stockName", // 정렬기준
    "sortOrder": "ASC",     // 정렬순서
    "stockNameSearch": ""   // 재고명검색
};

// 재고 항목
const addStockRowEventFn = () => {
    const stockRows = document.querySelectorAll(".stock-row");      // 재고 항목
    const selectItem = document.querySelectorAll(".select-item");   // 체크박스 아이템

    // 재고 상세 조회
    stockRows.forEach(stockRow => {
        stockRow.addEventListener("click", async (e) => {
            modal.style.display = "block"; // 팝업 레이어 열기
            const stockNo = stockRow.dataset.stockNo;
            await fetchStockDetailFn(stockNo);
        })
    })

    // 체크박스
    selectItem.forEach(item => {
        item.addEventListener("click", e => {
            e.stopPropagation(); // 이벤트 버블링 중단
        })

        item.addEventListener("change", e => {
            const stockNo = e.target.closest("tr").dataset.stockNo;
            if (item.checked) stockNoSet.add(stockNo);
            else stockNoSet.delete(stockNo);
        })

        if (stockNoSet.has(item.closest("tr").dataset.stockNo))
            item.checked = true;
    })

    // 체크박스 전체 선택
    document.querySelector("#select-all").addEventListener("change", e => {
        selectItem.forEach(item => {
            item.checked = e.target.checked;

            const stockNo = item.closest("tr").dataset.stockNo;
            if (item.checked) stockNoSet.add(stockNo);
            else stockNoSet.delete(stockNo);
        });
    })
}

// 팝업 레이어 닫기
btnClose.addEventListener("click", () =>
    modal.style.display = "none");

// 페이지 이동
const addPageLinkEventFn = pageNo => {
    const pageLinks = document.querySelectorAll(".page-link"); // 페이지 버튼

    pageLinks.forEach(pageLink => {
        pageLink.addEventListener("click", e => {
            e.preventDefault(); // a 태그 기본 동작 방지

            const pageNo = pageLink.dataset.pageNo;
            optionObj["pageNo"] = pageNo;
            getStockItemsFn(optionObj);
        })

        // active 클래스 추가
        if (Number(pageLink.innerText) === Number(pageNo))
            pageLink.parentElement.classList.add("active");
    })
}

// 재고명 검색
document.querySelector("#search").addEventListener("input", e => {
    optionObj["stockNameSearch"] = e.target.value;
    optionObj["pageNo"] = 1;
    getStockItemsFn(optionObj);
})

// 카테고리 구분 조회
document.querySelector("#stock-ct").addEventListener("change", e => {
    optionObj["stockCtNo"] = e.target.value;
    optionObj["pageNo"] = 1;
    optionObj["sortKey"] = "stockName";
    optionObj["sortOrder"] = "ASC";
    getStockItemsFn(optionObj);
})

// 정렬 (재고명, 수량)
const addSortEventFn = () => {
    // 재고명 정렬
    document.querySelector("#stock-name-sort").addEventListener("click", e => {
        if (optionObj["sortKey"] === "stockName")
            optionObj["sortOrder"] = (optionObj["sortOrder"] === "ASC") ? "DESC" : "ASC";
        else {
            optionObj["sortKey"] = "stockName";
            optionObj["sortOrder"] = "ASC";
        }
        getStockItemsFn(optionObj);
    })

    // 수량 정렬
    document.querySelector("#is-count-sort").addEventListener("click", e => {
        if (optionObj["sortKey"] === "isCount")
            optionObj["sortOrder"] = (optionObj["sortOrder"] === "ASC") ? "DESC" : "ASC";
        else {
            optionObj["sortKey"] = "isCount";
            optionObj["sortOrder"] = "ASC";
        }
        getStockItemsFn(optionObj);
    })
}

// 재고 상세 조회
const fetchStockDetailFn = async (stockNo) => {
    const response = await fetch(`/crispy/stock-detail?stockNo=${stockNo}`);
    const result = await response.json();

    document.querySelector(".modal-title").innerHTML = result.stockName;                        // 재고명
    document.querySelector(".stock-price > span:first-child").innerHTML = result.stockPrice;    // 재고단가
    document.querySelector(".stock-price > span:last-child").innerHTML = result.stockUnit;      // 재고단위
    document.querySelector(".stock-detail").innerHTML = result.stockDetail;                     // 재고이미지
    document.querySelector(".stock-img").src = result.stockImg;                                 // 재고설명
};

// 재고 항목 리스트
const getStockItemsFn = async (optionObj) => {
    const params = new URLSearchParams(); // URL 쿼리 문자열 객체

    for (let key in optionObj)
        params.append(key, optionObj[key])

    const response = await fetch(`/crispy/stock-items?${params.toString()}`);
    const html = await response.text();
    document.querySelector(".stock-list-container").outerHTML = html;

    // 이벤트 재추가
    addStockRowEventFn();
    addPageLinkEventFn(optionObj.pageNo === undefined ? 1 : optionObj.pageNo);
    addSortEventFn();
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    addStockRowEventFn();
    addPageLinkEventFn(1);
    addSortEventFn();
})

// 발주 신청
document.querySelector("#order").addEventListener("click", () => {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/crispy/stock-order";

    stockNoSet.forEach(stockNo => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "stockNo";
        input.value = stockNo;
        form.append(input);
    })

    document.querySelector("body").append(form);
    form.submit();
})