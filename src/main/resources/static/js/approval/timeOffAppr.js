// 문서 양식 변경
document.querySelector("#time-off-ct").addEventListener("change", e => {
    if (e.target.value === "") return;

    fetch (`/crispy/change-time-off-ct?timeOffCtNo=${e.target.value}`)
        .then(response => response.text())
        .then(html => {
            document.querySelector("#time-off-doc").outerHTML = html;
            getEmpInfoFn();
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
})