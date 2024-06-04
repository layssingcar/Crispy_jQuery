// 금액 계산
const selectStockList = document.querySelectorAll(".select-stock");
selectStockList.forEach(stock => {
    stock.querySelector(".test-count").addEventListener("keyup", e => {
        const price = stock.querySelector(".test-price").innerText;
        const count = e.target.value;
        const cost = price * count;

        stock.querySelector(".test-cost").innerText = cost.toLocaleString();
        e.target.nextElementSibling.value = cost;
    })
})

// 발주 재고 임시저장
document.querySelector("#temp").addEventListener("click", () => {
    document.querySelector("#form-container").action = "/crispy/stock-order-temp";
    document.querySelector("#form-container").submit();
})