const modal = document.querySelector(".modal");         // 재고 상세 모달
const btnClose = document.querySelector(".btn-close");  // 닫기 버튼

// 팝업 레이어 닫기
btnClose.addEventListener("click", () =>
    modal.style.display = "none");

// 재고 항목
const addStockRowEventFn = () => {
    const stockRows = document.querySelectorAll(".stock-row");           // 재고 항목
    const checkList = document.querySelectorAll(".stock-row input");     // 체크박스 리스트
    const stockName = document.querySelector(".modal-title");                       // 재고명
    const stockPrice = document.querySelector(".stock-price > span:first-child");   // 재고단가
    const stockUnit = document.querySelector(".stock-price > span:last-child");     // 재고단위
    const stockImg = document.querySelector(".stock-img");                          // 재고이미지
    const stockDetail = document.querySelector(".stock-detail");                    // 재고설명

    // 재고 상세 조회
    stockRows.forEach(stockRow => {
        stockRow.addEventListener("click", e => {
            modal.style.display = "block"; // 팝업 레이어 열기

            const stockNo = stockRow.dataset.stockNo;

            fetch("/crispy/stock-detail?stockNo=" + stockNo)
                .then(response => response.json())
                .then(result => {
                    stockName.innerHTML = result.stockName;
                    stockPrice.innerHTML = result.stockPrice;
                    stockUnit.innerHTML = result.stockUnit;
                    stockDetail.innerHTML = result.stockDetail;
                    stockImg.src = result.stockImg;
                })
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
const getStockItemsFn = pageNo => {
    fetch("/crispy/stock-items?page=" + pageNo)
        .then(response => response.text())
        .then(html => {
            document.querySelector(".stock-list-container").outerHTML = html;
            addStockRowEventFn();
            addPageLinkEventFn(pageNo);
        })
}

document.addEventListener("DOMContentLoaded", function () {
    addStockRowEventFn();
    addPageLinkEventFn(1);
})