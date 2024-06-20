import {stateManager} from "./stateManager.js";

const emailVerify = {
    init: function () {
        const _this = this;
        document.querySelectorAll(".email-validate-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const empEmail = document.querySelector(".emp-email").value;
                const action = btn.getAttribute('data-action');
                _this.handleVerificationCode(empEmail, action);
            })
        });
        document.querySelector('.check-verify-code')?.addEventListener('click', () => {
            _this.checkVerifyCode();
        });
    },
    handleVerificationCode: function (empEmail, action) {
        let apiUrl = "/api/email/verificationCode/verify/v1";
        let requestBody = {empEmail: empEmail, action: action};
        console.log(empEmail);

        if (action === "verify-user") {
            apiUrl = "/api/employee/verify/email/v1";
            requestBody = {
                empEmail: empEmail,
                empName: document.querySelector(".emp-name").value,
                empId: document.querySelector(".emp-id")?.value,
            }
        }
        fetch(apiUrl, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(requestBody)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || "Unknown error occurred");
                });
            }
            return response.json();
        })
            .then(data => {
                console.log(data)
                if (data.message === "인증 코드가 발송되었습니다.") {
                    document.getElementById('verify-code-div').style.display = 'block';
                    Swal.fire({
                        icon: "success",
                        title: data.message,
                        showConfirmButton: false,
                        timer: 1500
                    })
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: data.error || "Unknown error occurred"
                    });
                }
            }).catch(error => {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "Unknown error occurred"
            });
        });
    },

    checkVerifyCode: function () {
        const data = {
            verifyEmail: document.querySelector(".emp-email").value,
            verifyCode: document.querySelector(".verify-code").value,
        }
        fetch("/api/email/verificationCode/verify/v1", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                if (data.message === "인증 성공") {
                    stateManager.setIsVerified(true);
                    Swal.fire({
                        icon: "success",
                        title: data.message,
                        showConfirmButton: false,
                        timer: 1500
                    })
                    document.querySelector(".check-verify-code").disabled = true;
                } else {
                    stateManager.setIsVerified(false);
                    console.log(data.message);
                }
            })
            .catch(error => {
            })
    },
}

document.addEventListener("DOMContentLoaded", function () {
    emailVerify.init();
})
