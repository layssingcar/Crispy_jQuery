const stockRows = document.querySelectorAll(".stock-row");           // 재고 리스트
const checkList = document.querySelectorAll(".stock-row input");     // 체크박스 리스트
const stockName = document.querySelector(".modal-title");                       // 재고명
const stockPrice = document.querySelector(".stock-price > span:first-child");   // 재고단가
const stockUnit = document.querySelector(".stock-price > span:last-child");     // 재고단위
const stockImg = document.querySelector(".stock-img");                          // 재고이미지
const stockDetail = document.querySelector(".stock-detail");                    // 재고설명

// 재고 상세 조회
stockRows.forEach(stockRow => {
    stockRow.addEventListener("click", e => {
        const stockNo = stockRow.dataset.stockNo;

        fetch ("/crispy/stock-detail?stockNo=" + stockNo)
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

checkList.forEach(checkbox => {
    checkbox.addEventListener("click", e => {
        // 이벤트 버블링 중단
        e.stopPropagation();
    })
})