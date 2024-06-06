// 휴가, 휴직 기간 계산
const getPeriodFn = () => {
    const startDt = document.querySelector("#start-dt");                // 시작일
    const endDt = document.querySelector("#end-dt");                    // 종료일
    const timeOffPeriod = document.querySelector("#time-off-period");   // 휴직 기간 출력

    if (startDt.value != "" && endDt.value != "") {
        const sub = new Date(endDt.value).getTime() - new Date(startDt.value).getTime();
        const period = sub / (1000 * 3600 * 24);
        timeOffPeriod.innerHTML = String(period).padStart(2, "0");
        timeOffPeriod.nextElementSibling.value = period;

    } else timeOffPeriod.innerHTML = "00";
}

// 날짜 선택
const changeDateFn = () => {
    document.querySelector("#start-dt").addEventListener("change", getPeriodFn);
    document.querySelector("#end-dt").addEventListener("change", getPeriodFn);
}

// 휴가, 휴직 신청 임시저장
const timeOffTempFn = async () => {
    const timeOffCtNo = document.querySelector("#time-off-ct-no").value;
    const response = await fetch(`/crispy/check-time-off-temp?timeOffCtNo=${timeOffCtNo}`);
    const result = await response.text();

    if (result > 0)
        if (!confirm("임시저장된 내용이 이미 존재합니다. 기존의 내용을 지우고 새로 저장하시겠습니까?")) return;

    const formData = new FormData(document.querySelector("#form-container"));

    fetch ("/crispy/time-off-temp", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(result => {
            if (result > 0) alert("임시저장이 완료되었습니다.");
            else alert("임시저장에 실패했습니다.")
        })
}

// 임시저장 버튼
document.querySelector("#temp").addEventListener("click", timeOffTempFn)

// 문서 양식 변경
document.querySelector("#time-off-ct").addEventListener("change", e => {
    if (e.target.value === "") return;

    fetch (`/crispy/change-time-off-ct?timeOffCtNo=${e.target.value}`)
        .then(response => response.text())
        .then(html => {
            document.querySelector("#time-off-doc").outerHTML = html;
            document.querySelector("#time-off-ct-no").value = e.target.value;

            // 이벤트 재추가
            getEmpInfoFn();
            changeDateFn();
        })
})

// 직원 정보 조회
const getEmpInfoFn = async () => {
    const response = await fetch("/crispy/get-emp-info");
    const result = await response.json();

    const empNameList = document.querySelectorAll(".emp-name");
    empNameList.forEach(empName => {
        empName.innerHTML = result.empName;
    })

    document.querySelector("#pos-name").innerHTML = result.posName;
    document.querySelector("#emp-address").innerHTML = result.empStreet + ", " + result.empDetail;
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    // 기안일 출력
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const date = String(today.getDate()).padStart(2, '0');
    const formattedDate = `${year}년 ${month}월 ${date}일`;
    document.querySelector("#appr-dt").innerHTML = formattedDate;

    getEmpInfoFn();
    changeDateFn();
})