// 휴가, 휴직 기간 계산
const getPeriodFn = () => {
    const startDt = document.querySelector("#start-dt");                // 시작일
    const endDt = document.querySelector("#end-dt");                    // 종료일
    const timeOffPeriod = document.querySelector("#time-off-period");   // 휴직 기간 출력

    if (startDt.value != "" && endDt.value != "") {
        // 시작일, 종료일 비교
        if (new Date(startDt.value) > new Date(endDt.value)) {
            Swal.fire({
                icon: "warning",
                text: "종료일은 시작일보다 빠를 수 없습니다.",
                width: "365px"
            });
            endDt.value = "";
            timeOffPeriod.innerHTML = "00";
            timeOffPeriod.nextElementSibling.value = 0;
            return;
        }

        // 기간 출력
        const sub = new Date(endDt.value).getTime() - new Date(startDt.value).getTime();
        const period = sub / (1000 * 3600 * 24) + 1; // 종료일 포함
        timeOffPeriod.innerHTML = String(period).padStart(2, "0");
        timeOffPeriod.nextElementSibling.value = period;

    } else timeOffPeriod.innerHTML = "00";
}

// 날짜 선택
const changeDateFn = () => {
    document.querySelector("#start-dt").addEventListener("change", getPeriodFn);
    document.querySelector("#end-dt").addEventListener("change", getPeriodFn);
}

// 값 입력 여부 확인
const checkInputFn = () => {
    const startDt = document.querySelector("#start-dt");    // 시작일
    const endDt = document.querySelector("#end-dt");        // 종료일
    const vctCont = document.querySelector("#vct-cont");    // 문서내용
    const timeOffCtNo = document.querySelector("#time-off-ct-no");  // 카테고리번호
    let message = "";   // alert 메시지

    if (startDt.value === "") message = "시작일을 입력하세요.";
    else if (endDt.value === "") message = "종료일을 입력하세요.";
    else if (vctCont.value === "") {
        if (timeOffCtNo.value === "0") message = "휴가 사유를 입력하세요.";
        else message = "휴직 사유를 입력하세요.";
    }

    Swal.fire({
        text: message,
        width: "365px"
    })

    // 확인 완료
    return message === "";
}

// 휴가, 휴직 임시저장
const timeOffTempFn = () => {
    const formData = new FormData(document.querySelector("#form-container"));

    fetch ("/crispy/time-off-temp", {
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
const checkTimeOffTempFn = async () => {
    const timeOffCtNo = document.querySelector("#time-off-ct-no").value;
    const response = await fetch(`/crispy/check-time-off-temp?timeOffCtNo=${timeOffCtNo}`);
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
            .then ((result) => {
                if (result.isConfirmed) timeOffTempFn();
            })

    } else timeOffTempFn();
}

// 임시저장 버튼
document.querySelector("#temp").addEventListener("click", () => {
    if (checkInputFn()) checkTimeOffTempFn();
})

// 임시저장 내용 불러오기
document.querySelector("#temp-content").addEventListener("click", async () => {
    Swal.fire({
        title: "문서를 선택해 주세요.",
        input: "select",
        inputOptions: {
            0: "휴가신청서",
            1: "휴직신청서"
        },
        inputPlaceholder: "문서 선택",
        showCancelButton: true,
        confirmButtonText: "선택 완료",
        cancelButtonText: "취소",
        width: "450px",
        inputValidator: async (value) => {
            const response = await fetch(`/crispy/check-time-off-temp?timeOffCtNo=${value}`);
            const result = await response.text();

            if (result == 0) {
                Swal.fire({
                    text: "임시저장된 내용이 존재하지 않습니다.",
                    width: "365px"
                })
                return;
            }

            fetch (`/crispy/get-time-off-temp?timeOffCtNo=${value}`)
                .then(response => response.text())
                .then(html => {
                    document.querySelector("#time-off-doc").outerHTML = html;
                    document.querySelector("#time-off-ct-no").value = value;

                    // 이벤트 재추가
                    getEmpInfoFn();
                    changeDateFn();
                })
        }
    })
})

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
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const date = String(today.getDate()).padStart(2, "0");
    const formattedDate = `${year}년 ${month}월 ${date}일`;
    document.querySelector("#appr-dt").innerHTML = formattedDate;

    getEmpInfoFn();
    changeDateFn();
})