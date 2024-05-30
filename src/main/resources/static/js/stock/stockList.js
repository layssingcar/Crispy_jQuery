const modal = document.querySelector(".modal");         // 재고 상세 모달
const btnClose = document.querySelector(".btn-close");  // 닫기 버튼

// 팝업 레이어 닫기
btnClose.addEventListener("click", () =>
    modal.style.display = "none");

// 재고 상세 조회
const fetchStockDetail = async (stockNo) => {
    const response = await fetch(`/crispy/stock-detail?stockNo=${stockNo}`);
    const result = await response.json();

    document.querySelector(".modal-title").innerHTML = result.stockName;                        // 재고명
    document.querySelector(".stock-price > span:first-child").innerHTML = result.stockPrice;    // 재고단가
    document.querySelector(".stock-price > span:last-child").innerHTML = result.stockUnit;      // 재고단위
    document.querySelector(".stock-detail").innerHTML = result.stockDetail;                     // 재고이미지
    document.querySelector(".stock-img").src = result.stockImg;                                 // 재고설명
};

// 재고 항목
const addStockRowEventFn = () => {
    const stockRows = document.querySelectorAll(".stock-row");           // 재고 항목
    const checkList = document.querySelectorAll(".stock-row input");     // 체크박스 리스트

    // 재고 상세 조회
    stockRows.forEach(stockRow => {
        stockRow.addEventListener("click", async (e) => {
            modal.style.display = "block"; // 팝업 레이어 열기
            const stockNo = stockRow.dataset.stockNo;
            await fetchStockDetail(stockNo);
        })
    })

    // 체크박스
    checkList.forEach(checkbox => {
        checkbox.addEventListener("click", e => {
            e.stopPropagation(); // 이벤트 버블링 중단
        })
    })
}

// 페이지 이동
const addPageLinkEventFn = pageNo => {
    const pageLinks = document.querySelectorAll(".page-link"); // 페이지 버튼

    pageLinks.forEach((pageLink) => {
        pageLink.addEventListener("click", e => {
            e.preventDefault(); // a 태그 기본 동작 방지
            getStockItemsFn(pageLink.dataset.pageNo);
        })

        // active 클래스 추가
        if (Number(pageLink.innerText) === Number(pageNo))
            pageLink.parentElement.classList.add("active");
    })
}

// 재고 항목 리스트
const getStockItemsFn = async (pageNo) => {
    const response = await fetch(`/crispy/stock-items?page=${pageNo}`);
    const html = await response.text();
    document.querySelector(".stock-list-container").outerHTML = html;
    addStockRowEventFn();
    addPageLinkEventFn(pageNo);
}

document.addEventListener("DOMContentLoaded", function () {
    addStockRowEventFn();
    addPageLinkEventFn(1);
})