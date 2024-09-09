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
    $(".page-link").each(function() {
        $(this).on("click", e => {
            e.preventDefault(); // a 태그 기본 동작 방지

            const pageNo = $(this).data("pageNo");
            optionObj["pageNo"] = pageNo;
            getApprItemsFn(optionObj);
        });

        // active 클래스 추가
        if (Number($(this).text()) === Number(pageNo))
            $(this).parent().addClass("active");
    });
}

// 기안자 검색
$("#search").on("input", e => {
    optionObj["empName"] = $(e.target).val();
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
});

// 카테고리 구분 조회
$("#time-off-ct-no").on("change", e => {
    optionObj["timeOffCtNo"] = $(e.target).val();
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
});

// 문서상태 구분 조회
$("#appr-stat").on("change", e => {
    optionObj["apprStat"] = $(e.target).val();
    optionObj["pageNo"] = 1;
    getApprItemsFn(optionObj);
});

// 기안일 정렬
const addSortEventFn = () => {
    $("#appr-dt-sort").on("click", () => {
        optionObj["apprDtSort"] = (optionObj["apprDtSort"] === "DESC") ? "ASC" : "DESC";
        getApprItemsFn(optionObj);
    });
}

// 결재 문서 항목 리스트
const getApprItemsFn = async (optionObj) => {
    const params = new URLSearchParams(); // URL 쿼리 문자열 객체

    for (let key in optionObj)
        params.append(key, optionObj[key]);

    // URL 경로에서 type 변수 값을 얻어와 저장
    const subIdx = location.pathname.lastIndexOf("/");
    const type = location.pathname.substring(subIdx + 1);

    const response = await fetch(`/crispy/approval-items/${type}?${params.toString()}`);
    const html = await response.text();
    $(".appr-list-container").html(html);

    // 이벤트 재추가
    addPageLinkEventFn(optionObj.pageNo === undefined ? 1 : optionObj.pageNo);
    addSortEventFn();
    addApprRowsEventFn();
}

// 초기화
$(() => {
    addPageLinkEventFn(1);
    addSortEventFn();
    addApprRowsEventFn();
});

// 결재 문서 항목
const addApprRowsEventFn = () => {
    $(".appr-row").each(function() {
        $(this).on("click", () => {
            const apprNo = $(this).data("apprNo");
            location.href = `/crispy/approval-detail/time-off/${apprNo}`;
        });
    });
}
