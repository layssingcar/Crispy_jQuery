// 페이지네이션 옵션 객체
const optionObj = {
    "pageNo": 1,            // 페이지번호
    "timeOffCtNo": -1,      // 문서카테고리번호
    "apprStat": -1,         // 문서상태번호
    "apprDtSort": "DESC",   // 기안일정렬
    "empName": ""           // 기안자검색
}

// 페이지 이동
const addPageLinkEventFn = pageNo => {
    const pageLinks = document.querySelectorAll(".page-link"); // 페이지 버튼

    pageLinks.forEach(pageLink => {
        pageLink.addEventListener("click", e => {
            e.preventDefault(); // a 태그 기본 동작 방지

            const pageNo = pageLink.dataset.pageNo;
            optionObj["pageNo"] = pageNo;
            getApprItemsFn(optionObj);
        })

        // active 클래스 추가
        if (Number(pageLink.innerText) === Number(pageNo))
            pageLink.parentElement.classList.add("active");
    })
}

// 기안자 검색
document.querySelector("#search")?.addEventListener("input", e => {
    optionObj["empName"] = e.target.value;
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
})

// 카테고리 구분 조회
document.querySelector("#time-off-ct-no").addEventListener("change", e => {
    optionObj["timeOffCtNo"] = e.target.value;
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
})

// 문서상태 구분 조회
document.querySelector("#appr-stat").addEventListener("change", e => {
    optionObj["apprStat"] = e.target.value;
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
})

// 기안일 정렬
const addSortEventFn = () => {
    document.querySelector("#appr-dt-sort").addEventListener("click", e => {
        optionObj["apprDtSort"] = (optionObj["apprDtSort"] === "DESC") ? "ASC" : "DESC";
        getApprItemsFn(optionObj);
    })
}

// 결재 문서 항목 리스트
const getApprItemsFn = async (optionObj) => {
    const params = new URLSearchParams(); // URL 쿼리 문자열 객체

    for (let key in optionObj)
        params.append(key, optionObj[key])

    // URL 경로에서 type 변수 값을 얻어와 저장
    const subIdx = location.pathname.lastIndexOf("/");
    const type = location.pathname.substring(subIdx + 1);

    const response = await fetch(`/crispy/approval-items/${type}?${params.toString()}`);
    const html = await response.text();
    document.querySelector(".appr-list-container").outerHTML = html;

    // 이벤트 재추가
    addPageLinkEventFn(optionObj.pageNo === undefined ? 1 : optionObj.pageNo);
    addSortEventFn();
    addApprRowsEventFn();
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    addPageLinkEventFn(1);
    addSortEventFn();
    addApprRowsEventFn();
})

// 결재 문서 항목
const addApprRowsEventFn = () => {
    const apprRows = document.querySelectorAll(".appr-row");

    apprRows.forEach(apprRow => {
        apprRow.addEventListener("click", () => {
            const apprNo = apprRow.dataset.apprNo;
            location.href = `/crispy/approval-detail/time-off/${apprNo}`;
        })
    })
}