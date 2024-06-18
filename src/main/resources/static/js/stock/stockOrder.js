// 금액 계산
const sumCostFn = () => {
    const orderList = document.querySelectorAll(".order-list"); // 발주 재고 항목

    orderList.forEach(orderItem => {
        const inpCount = orderItem.querySelector(".inp-count");  // 수량 입력

        inpCount.addEventListener("input", e => {
            // 숫자만 입력 가능
            const inpValue = inpCount.value;
            const regExp = /^[0-9]+$/;
            if (!regExp.test(inpValue)) inpCount.value = inpValue.slice(0, -1);

            const price = orderItem.querySelector(".stock-price > span:first-child").innerText.replace(/,/g, "");
            const count = inpCount.value;
            const cost = price * count;

            orderItem.querySelector(".total-cost > .strong").innerText = cost.toLocaleString();
            inpCount.nextElementSibling.value = cost;
            sumOrderCostFn();
        })
    })
}

// 총 합계금액 계산
const sumOrderCostFn = () => {
    const stockOrderCost = document.querySelectorAll(".stock-order-cost");  // 금액 리스트
    let orderCost = 0;  // 합계금액

    if (stockOrderCost.length === 0) {
        document.querySelector("#order-cost").innerText = 0;
        document.querySelector("#order-cost").nextElementSibling.value = 0;
        return;
    }

    stockOrderCost.forEach(cost => {
        orderCost += parseInt(cost.value === "" ? 0 : parseInt(cost.value));
        document.querySelector("#order-cost").innerText = orderCost.toLocaleString();
        document.querySelector("#order-cost").nextElementSibling.value = orderCost;
    })
}

// 재고 항목 삭제
const deleteItemFn = (btn) => {
    btn.addEventListener("click", () => {
        btn.parentElement.nextElementSibling.remove();
        btn.parentElement.remove();

        document.querySelectorAll(".inp-count").forEach((item, index) => {
            item.name = `stockOrderList[${index}].stockOrderCount`;
        })

        document.querySelectorAll(".stock-order-cost").forEach((item, index) => {
            item.name = `stockOrderList[${index}].stockOrderCost`;
        })

        document.querySelectorAll(".total-cost > input").forEach((item, index) => {
            item.name = `stockOrderList[${index}].stockNo`;
        })

        sumOrderCostFn();
    })
}

// 발주 재고 임시저장
const stockOrderTempFn = () => {
    const formData = new FormData(document.querySelector("#form-container"));

    fetch ("/crispy/stock-order-temp", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result > 0) {
                Swal.fire({
                    icon: "success",
                    title: "임시저장이 완료되었습니다.",
                    showConfirmButton: false,
                    timer: 1500
                })

            } else {
                Swal.fire({
                    icon: "error",
                    title: "임시저장에 실패했습니다.",
                    showConfirmButton: false,
                    timer: 1500
                })
            }
        })
}

// 임시저장 값 존재 여부 확인
const checkstockOrderTempFn = async () => {
    const response = await fetch ("/crispy/check-order-temp");
    const result = await response.text();

    if (result > 0) {
        Swal.fire({
            icon: "warning",
            title: "임시저장된 내용이 이미 존재합니다.",
            text: "기존의 내용을 지우고 새로 저장할까요?",
            showCancelButton: true,
            confirmButtonText: "네, 다시 저장할게요.",
            cancelButtonText: "아니요, 취소할게요.",
            width: "525px",
        })
            .then((result) => {
                if (result.isConfirmed) stockOrderTempFn();
            })

    } else stockOrderTempFn();
}

// 임시저장 버튼
document.querySelector("#temp").addEventListener("click", checkstockOrderTempFn)

// 임시저장 내용 불러오기
const getOrderTempFn = async () => {
    let response = await fetch("/crispy/check-order-temp");
    const result = await response.text();

    if (result == 0) {
        Swal.fire({
            text: "임시저장된 내용이 존재하지 않습니다.",
            width: "365px"
        })
        return;
    }

    response = await fetch ("/crispy/get-order-temp");
    const html = await response.text();
    document.querySelector("#stock-temp-container").outerHTML = html;
}

// 임시저장 내용 불러오기 버튼
document.querySelector("#temp-content").addEventListener("click", async () => {
    let flag = false;   // 취소 버튼

    if (document.querySelectorAll("#stock-temp-container > div").length > 0) {
        await Swal.fire({
            icon: "warning",
            title: "선택된 재고 목록이 존재합니다.",
            text: "임시저장 내용을 불러오면 작성 중인 내용이 초기화됩니다. 계속할까요?",
            showCancelButton: true,
            confirmButtonText: "네, 계속할게요.",
            cancelButtonText: "아니요, 취소할게요.",
            width: "600px",
        })
            .then(result => {
                if (result.isDismissed) flag = true;
            })
    }

    if (flag) return;

    await getOrderTempFn();
    sumCostFn();
    sumOrderCostFn();

    document.querySelectorAll(".delete-item").forEach(btn => {
        deleteItemFn(btn);
    })
})

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    sumCostFn();
    sumOrderCostFn();

    document.querySelectorAll(".delete-item").forEach(btn => {
        deleteItemFn(btn);
    })
})

// 모달내에 api 호출 되는 부분
const stockListContainer = document.getElementById('stock-list');

// 카테고리 버튼 부분
const categoryButtonsContainer = document.getElementById('category-buttons');

const stockNoSet = new Set();   // 재고번호 Set

// 페이지네이션 옵션 객체
const optionObj = {
    "pageNo": 1,            // 페이지번호
    "stockCtNo": 1,         // 카테고리번호
    "sortKey": "stockName", // 정렬기준
    "sortOrder": "ASC",     // 정렬순서
    "stockNameSearch": ""   // 재고명검색
};

// 카테고리 목록 조회
function fetchCategories() {
    fetch('/api/stock/categories/v1')
        .then(response => response.json())
        .then(categories => {
            categoryButtonsContainer.innerHTML = ''; // 기존 버튼 지우기
            categories.forEach((category, index) => {
                const button = document.createElement('button');
                button.classList.add('btn', 'btn-primary', 'm-2');

                button.textContent = category.stockCtName;
                // button.addEventListener('click', () => fetchStockItemsByCategory(category.stockCtNo));

                button.addEventListener('click', () => {
                    // 선택된 카테고리 버튼에 메인 컬러 적용
                    document.querySelector(".stock-order #category-buttons > .selected")?.classList.remove("selected");
                    button.classList.add("selected");

                    optionObj.stockCtNo = category.stockCtNo;
                    getStockItemsFn(optionObj)
                });

                categoryButtonsContainer.appendChild(button);

                // 모달 열릴 때 첫 번째 카테고리 선택
                if (index === 0) button.click();

            });
        })
        .catch(error => console.error('Error fetching categories:', error));
}

// 재고 항목
const addStockRowEventFn = () => {
    const selectItem = document.querySelectorAll(".select-item");   // 체크박스 아이템

    // 체크박스
    selectItem.forEach(item => {
        item.addEventListener("click", e => {
            e.stopPropagation(); // 이벤트 버블링 중단
        })

        item.addEventListener("change", e => {
            const stockNo = item.dataset.stockNo;
            if (item.checked) stockNoSet.add(stockNo);
            else stockNoSet.delete(stockNo);
        })

        if (stockNoSet.has(item.dataset.stockNo))
            item.checked = true;
    })
}

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

// 재고 항목 리스트
const getStockItemsFn = async (optionObj) => {
    const params = new URLSearchParams(); // URL 쿼리 문자열 객체

    for (let key in optionObj)
        params.append(key, optionObj[key])

    fetch(`/api/stock/stockList/v1?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            stockListContainer.innerHTML = ''; // 기존 항목 지우기
            // console.log(data.items)

            data.items.forEach(stock => {
                const row = document.createElement('tr');

                // 체크박스 생성
                const checkboxCell = document.createElement('td');
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.classList.add('select-item');
                checkbox.dataset.stockNo = stock.stockNo;
                checkboxCell.appendChild(checkbox);

                // 재고 이미지 생성
                const imageCell = document.createElement("td");
                const img = document.createElement("img");
                img.src = stock.stockImg;
                img.alt = stock.stockName;
                img.classList.add('stock-img')
                imageCell.appendChild(img);

                // 재고 이름 생성
                const nameCell = document.createElement("td");
                nameCell.textContent = stock.stockName;

                // 재고 수량 생성
                const countCell = document.createElement("td");
                const strong = document.createElement("span");
                const span = document.createElement("span");

                strong.innerHTML = stock.isCount;
                strong.className = "strong";
                span.innerHTML = "개";

                countCell.append(strong);
                countCell.append(span);

                row.appendChild(checkboxCell);
                row.appendChild(imageCell);
                row.appendChild(nameCell);
                row.appendChild(countCell);

                stockListContainer.appendChild(row);
            });

            // 이벤트 재추가
            addStockRowEventFn();
        })
        .catch(error => console.error('Error fetching stock items:', error));
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    // 모달 띄우는 버튼
    const addList = document.getElementById("add-list");
    // 발주 목록에 추가하는 버튼
    const addSelectedItemsButton = document.getElementById('add-selected-items');
    // 모달내에 api 호출 되는 부분
    const stockListContainer = document.getElementById('stock-list');
    // 카테고리 버튼
    const categoryButtonsContainer = document.getElementById('category-buttons');

    // '추가하기' 버튼 클릭 시 모달 표시 및 재고 조회
    addList.addEventListener("click", () => {
        fetchCategories();
        const stockModal = new bootstrap.Modal(document.getElementById('stockModal'));
        stockModal.show();
    });

    // 체크박스 전체 선택
    document.querySelector("#select-all").addEventListener("change", e => {

        const selectItem = document.querySelectorAll(".select-item");   // 체크박스 아이템
        console.log(selectItem)

        selectItem.forEach(item => {
            item.checked = e.target.checked;

            const stockNo = item.dataset.stockNo;
            if (item.checked) stockNoSet.add(stockNo);
            else stockNoSet.delete(stockNo);
        });
    })

    // 재고명 검색
    document.querySelector("#search").addEventListener("input", e => {
        optionObj["stockNameSearch"] = e.target.value;
        optionObj["pageNo"] = 1;
        optionObj["stockCtNo"] = 0;
        getStockItemsFn(optionObj);
    })

    getStockItemsFn(optionObj);
    addSortEventFn();

    // 선택된 항목을 발주 신청 목록에 추가
    addSelectedItemsButton.addEventListener('click', () => {
    	// const selectedItems = document.querySelectorAll('.select-item:checked');

        const params = new URLSearchParams(); // URL 쿼리 문자열 객체

        // 선택한 재고 번호를 쿼리스트링 형태로 변환
        stockNoSet.forEach(stockNo => {
            params.append("stockNo", stockNo)
        })

        fetch(`/api/stock/get-stock?${params.toString()}`)
            .then(resp => resp.json())
            .then(stockList => {
                console.log(stockList)

                // input type="hidden" 요소의 name 속성에 들어갈 index 번호
                let idx = document.querySelectorAll(".order-list").length;

                stockList.forEach(stock => {

                    // 선택한 상품(재고)이 이미 발주 신청 화면에 존재하는 경우(중복 선택 X)
                    for(let item of document.querySelectorAll(".total-cost input")){
                        if(item.value == stock.stockNo){
                            return;
                        }
                    }

                    const orderList = document.createElement("div");
                    orderList.className = "order-list";

                    // 카테고리
                    const ctDiv = document.createElement("div");

                    const ctDivChild1 = document.createElement("div")
                    ctDivChild1.innerText = "카테고리";
                    ctDivChild1.classList.add("m-right", "stock-ct");

                    const ctDivChild2 = document.createElement("div")
                    ctDivChild2.innerText = stock.stockCtName;

                    ctDiv.append(ctDivChild1, ctDivChild2);

                    // 재고명
                    const nameDiv = document.createElement("div");

                    const nameDivChild1 = document.createElement("div")
                    nameDivChild1.innerText = "재고명";
                    nameDivChild1.classList.add("m-right", "stock-name");

                    const nameDivChild2 = document.createElement("div")
                    nameDivChild2.innerText = stock.stockName;

                    nameDiv.append(nameDivChild1, nameDivChild2);

                    // 단가
                    const priceDiv = document.createElement("div");

                    const priceDivChild1 = document.createElement("div")
                    priceDivChild1.innerText = "단가";
                    priceDivChild1.className = "m-right";

                    const priceDivChild2 = document.createElement("div")
                    priceDivChild2.className = "stock-price";

                    const span1 = document.createElement("span");
                    span1.innerText = stock.stockPrice.toLocaleString()

                    const span2 = document.createElement("span");
                    span2.innerText = "원";

                    const span3 = document.createElement("span");
                    span3.innerHTML = "&nbsp;/&nbsp;";

                    const span4 = document.createElement("span");
                    span4.innerText = stock.stockUnit;

                    priceDivChild2.append(span1, span2, span3, span4);
                    priceDiv.append(priceDivChild1, priceDivChild2);

                    // 수량
                    const countDiv = document.createElement("div");

                    const countDivChild1 = document.createElement("div");
                    countDivChild1.className = "m-right";
                    countDivChild1.innerText = "수량";

                    const countInput = document.createElement("input");
                    countInput.type = "text";
                    countInput.classList.add("form-control", "inp-count");
                    countInput.name = `stockOrderList[${idx}].stockOrderCount`;

                    const costInput = document.createElement("input");
                    costInput.type = "hidden";
                    costInput.classList.add("stock-order-cost");
                    costInput.name = `stockOrderList[${idx}].stockOrderCost`;

                    const countDivChild2 = document.createElement("div");
                    countDivChild2.innerText = "개";

                    countDiv.append(countDivChild1, countInput, costInput, countDivChild2);

                    // 합계 금액
                    const totalCostDiv = document.createElement("div");
                    totalCostDiv.className = "total-cost";

                    const totalSpan1 = document.createElement("span");
                    totalSpan1.className = "strong";
                    totalSpan1.innerText = 0;

                    const totalSpan2 = document.createElement("span");
                    totalSpan2.innerText = "원";

                    const stockNoInput = document.createElement("input");
                    stockNoInput.type = "hidden";
                    stockNoInput.name =  `stockOrderList[${idx}].stockNo`;
                    stockNoInput.value = stock.stockNo;

                    totalCostDiv.append(totalSpan1, totalSpan2, stockNoInput);

                    // 삭제
                    const deleteDiv = document.createElement("div");
                    deleteDiv.className = "delete-item";

                    const deleteBtn = document.createElement("i");
                    deleteBtn.classList.add("fa-regular", "fa-circle-xmark");

                    const deleteSpan = document.createElement("span")
                    deleteSpan.innerText = "삭제";

                    deleteDiv.append(deleteBtn, deleteSpan);

                    deleteItemFn(deleteDiv); // 삭제 이벤트 추가

                    // 경계선
                    const hr = document.createElement("hr");

                    // 전체 조립
                    orderList.append(ctDiv, nameDiv, priceDiv, countDiv, totalCostDiv, deleteDiv);

                    document.querySelector("#stock-temp-container").append(orderList, hr);

                    // 인덱스 증가
                    idx++;

                    // 수량 입력 시 동작
                    countInput.addEventListener("input", e => {
                        // 숫자만 입력 가능
                        const inpValue = countInput.value;
                        const regExp = /^[0-9]+$/;
                        if (!regExp.test(inpValue)) countInput.value = inpValue.slice(0, -1);

                        const price = stock.stockPrice;
                        const count = countInput.value;
                        const cost = price * count;

                        totalSpan1.innerText =  cost.toLocaleString();
                        costInput.value = cost;
                        sumOrderCostFn();
                    })
                })
            })

    	// 모달 닫기
    	const stockModal = bootstrap.Modal.getInstance(document.getElementById('stockModal'));
    	stockModal.hide();
    });
})
