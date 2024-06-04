// 금액 계산
const selectStockList = document.querySelectorAll(".select-stock");
selectStockList.forEach(stock => {
    const stockCount = stock.querySelector(".test-count");  // 수량 입력

    stockCount.addEventListener("input", e => {
        // 숫자만 입력 가능
        const inpValue = stockCount.value;
        const regExp = /^[0-9]+$/;
        if (!regExp.test(inpValue)) stockCount.value = inpValue.slice(0, -1);

        const price = stock.querySelector(".test-price").innerText.replace(/,/g, '');
        const count = stockCount.value;
        const cost = price * count;

        stock.querySelector(".test-cost").innerText = cost.toLocaleString();
        stockCount.nextElementSibling.value = cost;
    })
})

// 발주 재고 임시저장
document.querySelector("#temp").addEventListener("click", () => {
    document.querySelector("#form-container").action = "/crispy/stock-order-temp";
    document.querySelector("#form-container").submit();
})