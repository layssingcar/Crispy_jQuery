const empRegister = {
    init: function () {
        const _this = this;
        document.getElementById("btn-emp-register")?.addEventListener("click", function () {
            _this.registerEmployee();
        });
    },
    registerEmployee: function () {
        const employeeData = {
            empId: document.getElementById("emp-register-empId").value,
            empName: document.getElementById("emp-register-empName").value,
            empPhone: document.getElementById("emp-register-empPhone").value,
            empEmail: document.getElementById("emp-register-empEmail").value,
            empInDt: document.getElementById("emp-register-empInDt").value,
            posNo: parseInt(document.querySelector("input[name='posNo']:checked")?.value, 10)
        };

        fetch("/api/v1/owner/employee/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(employeeData)
        }).then(response => response.json())
            .then(data => {
                console.log(data);
                alert("직원 등록이 완료되었습니다.");
                window.location.href = "/crispy/owner/employees"; // 성공 페이지로 이동
            })
            .catch(error => {
                console.error("Error:", error);
                alert("등록에 실패했습니다.");
            });
    },
}

document.addEventListener("DOMContentLoaded", function () {
    empRegister.init();
})