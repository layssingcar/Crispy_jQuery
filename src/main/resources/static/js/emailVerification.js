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
        let apiUrl = "/api/v1/email/send-verification-code";
        let requestBody = {empEmail: empEmail, action: action};
        console.log(empEmail);

        if (action === "verify-user") {
            apiUrl = "/api/v1/employee/verify-employee";
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
        fetch("/api/v1/email/verify-code", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                if (data.message === "인증 성공") {
                    stateManager.setIsVerified(true);
                    console.log(data.message);
                    document.querySelector(".check-verify-code").disabled = true;
                } else {
                    alert(data.error);
                    stateManager.setIsVerified(false);
                    console.log(data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("인증번호 검증에 실패했습니다.");
            })
    }
}

document.addEventListener("DOMContentLoaded", function () {
    emailVerify.init();
})