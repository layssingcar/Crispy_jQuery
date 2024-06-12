// 페이지네이션 옵션 객체
const optionObj = {
    "pageNo": 1,        // 페이지번호
    "timeOffCtNo": 0,   // 문서카테고리번호
    "apprStat": 0       // 문서상태번호
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
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    addPageLinkEventFn(1);
})