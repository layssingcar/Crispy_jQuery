let empObj; // 선택된 결재선 객체
const empNoSet = new Set();     // 선택된 결재선 목록
const selectedFile = new Set(); // 선택된 파일 목록

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

// 파일 선택
const selectFileFn = () => {
    document.querySelector("#formFileMultiple").addEventListener("change", e => {
        const fileList = document.querySelector(".file-list");  // 파일 리스트
        const files = e.target.files;   // 선택된 파일 리스트

        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const fileName = file.name;

            if (!selectedFile.has(fileName)) {
                selectedFile.add(fileName);

                const div = document.createElement("div");
                div.classList.add("file-item");

                const a = document.createElement("a");
                a.href = URL.createObjectURL(file);
                a.download = fileName;
                a.innerHTML = fileName;

                const icon = document.createElement('i');
                icon.classList.add('fa-regular', 'fa-circle-xmark');

                // 파일 항목 삭제
                icon.addEventListener("click", () => {
                    selectedFile.delete(fileName);
                    fileList.removeChild(div);
                })

                div.append(a);
                div.append(icon);
                fileList.append(div);
            }
        }
    })
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

    if (message === "") return true;

    Swal.fire({
        text: message,
        width: "365px"
    })

    // 확인 완료
    return false;
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
        text: "문서 종류를 선택하세요.",
        input: "select",
        inputOptions: {
            0: "휴가신청서",
            1: "휴직신청서"
        },
        inputPlaceholder: "문서 선택",
        showCancelButton: true,
        confirmButtonText: "선택 완료",
        cancelButtonText: "취소",
        width: "400px",
        inputValidator: async (value) => {
            if (!value) return "문서 종류가 선택되지 않았습니다.";
            
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
                    getCurrentDateFn();
                    changeDateFn();
                    selectFileFn();
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
            getCurrentDateFn();
            changeDateFn();
            selectFileFn();
            changeUIFn();
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

// 오늘 날짜
const getCurrentDateFn = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const date = String(today.getDate()).padStart(2, "0");
    const formattedDate = `${year}년 ${month}월 ${date}일`;
    document.querySelector("#appr-dt").innerHTML = formattedDate;
}

// 결재선 불러오기
const getApprLineFn = () => {
    const tempList = [];        // 직책 기록을 위한 임시 리스트
    const parentList =  [];     // 직책명 리스트
    const childList = []        // 직원명 리스트
    let managerOrder = 1;     // 매니저 id 번호 부여를 위한 변수

    apprLineDtoList.forEach((item) => {
        // 직책명
        if (!tempList.includes(item.posNo)) {
            const parentObj = {
                "id": item.posNo === 1 ? "manager" : "owner",
                "parent": "#",
                "text": item.posName,
                "icon": "glyphicon glyphicon-home",
                "state": {"opened" : true}
            };

            parentList.push(parentObj);
            tempList.push(item.posNo);
        }

        // 직원명
        const childObj = {
            "id": item.posNo === 1 ? "m" + managerOrder++ : "o1",
            "parent": item.posNo === 1 ? "manager" : "owner",
            "text": item.empName,
            "icon": "glyphicon glyphicon-picture",
            "a_attr": {"empNo" : item.empNo}
        }

        childList.push(childObj);
    })

    return [...parentList, ...childList];
}

// 결재선 목록
$('#tree').on('changed.jstree', function (e, data) {
    const selectTarget = data.instance.get_node(data.selected[0]);

    // 결재선 객체에 선택된 노드 정보 저장
    empObj = {
        "empNo" : selectTarget.a_attr.empNo,
        "empName" : selectTarget.text,
        "position" : selectTarget.parent === "owner" ? "점주" : "매니저"}

}).jstree({
    'core' : {
        'data' : getApprLineFn()
    }
})

// 결재선 추가
document.querySelector("#add-emp").addEventListener("click", () => {
    if (empObj === undefined) return;
    if (empNoSet.has(empObj.empNo)) return;
    empNoSet.add(empObj.empNo);

    const div = document.createElement("div");

    const span = document.createElement("span");
    span.innerText = `${empObj.empName} (${empObj.position})`;

    const input = document.createElement("input");
    const idx = document.querySelectorAll("#select-tree > div").length; // 선택된 결재선 개수
    input.type = "hidden";
    input.value = empObj.empNo;
    input.name = `apprLineDtoList[${idx}].empNo`;

    div.append(span, input);
    document.querySelector("#select-tree").append(div);
})

// 화면 전환
const changeUIFn = () => {
    const timeOffAppr = document.querySelector(".time-off-appr");   // 결재 신청 화면
    const apprLine = document.querySelector(".appr-line");          // 결재선 선택 화면

    // 결재 신청 -> 결재선 선택
    document.querySelector("#next-btn").addEventListener("click", () => {
        if (checkInputFn()) {
            timeOffAppr.classList.add("d-none");
            apprLine.classList.remove("d-none");
        }
    })

    // 결재선 선택 -> 결재 신청
    document.querySelector("#rollback").addEventListener("click", () => {
        timeOffAppr.classList.remove("d-none");
        apprLine.classList.add("d-none");
    })
}

// 초기화
document.addEventListener("DOMContentLoaded", function () {
    getEmpInfoFn();
    getCurrentDateFn();
    changeDateFn();
    selectFileFn();
    changeUIFn();
    getApprLineFn();
})