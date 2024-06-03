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
        document.querySelector(".find-btn").addEventListener("click", () => {
            _this.submitForm();
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
                throw response
            }
            return response.json();
        })
            .then(data => {
                console.log(data)
                if (data.message === "인증 코드가 발송되었습니다.") {
                    document.getElementById('verify-code-div').style.display = 'block';
                    alert(data.message); // 사용자에게 인증 코드 발송 메시지를 표시
                } else {
                    alert(data.error); // 일치하는 회원 정보가 없다는 메시지를 경고 창으로 표시
                }
            }).catch((error) => {
            console.error('Error:', error);
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
                    console.log(data.message);
                    this.showMessage(data.message, 'success')
                    document.querySelector(".check-verify-code").disabled = true;
                } else {
                    this.showMessage(data.error, 'error');
                    stateManager.setIsVerified(false);
                    console.log(data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                this.showMessage("인증번호 검증에 실패했습니다.", 'error'); // 에러 메시지 표시
            })
    },
    submitForm: function () {
        const form = document.getElementById("findForm");
        form.submit();
    }
}

document.addEventListener("DOMContentLoaded", function () {
    emailVerify.init();
})
