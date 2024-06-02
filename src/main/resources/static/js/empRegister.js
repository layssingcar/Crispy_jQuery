const empRegister = {
    init: function () {
        const _this = this;
        document.getElementById("btn-emp-register")?.addEventListener("click", function () {
            _this.registerEmployee();
        });
    },

    registerEmployee: function () {

        const employeeData = {
            empId: document.querySelector(".empId").value,
            empName: document.querySelector(".empName").value,
            empPhone: document.querySelector(".empPhone").value,
            empEmail: document.querySelector(".empEmail").value,
            empInDt: document.querySelector(".empInDt")?.value,
            posNo: parseInt(document.querySelector("input[name='posNo']:checked")?.value, 10)
        };

        fetch("/api/owner/employee/register/v1", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(employeeData)
        }).then(response => {
            if (response.status === 400) {
                return response.json().then(errors => {
                    this.displayValidationErrors(errors);
                    throw new Error("Validation errors");
                });
            }
            return response.json();
        }).then(data => {
            if (data.message) {
                alert("직원 등록이 완료되었습니다.");
                window.location.href = "/crispy/owner/employees"; // 성공 페이지로 이동
            }
        }).catch(error => {
            if (error.message !== "Validation errors") {
                alert("등록에 실패했습니다.");
            }
        });
    },

    displayValidationErrors: function (errors) {
        Object.keys(errors).forEach(field => {
            const errorContainer = document.querySelector(`.${field}-error`);
            if (errorContainer) {
                errorContainer.textContent = errors[field];
                errorContainer.style.display = 'block';
            }
        });
    },
}

document.addEventListener("DOMContentLoaded", function () {
    empRegister.init();
});
