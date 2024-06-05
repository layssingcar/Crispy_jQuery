const changePw = {
    init: function (){
        this.changePassword();
    },
    changePassword: function () {
        const passwordForm = document.getElementById("passwordForm");
        if (passwordForm) {
            passwordForm.addEventListener("submit", function (e) {
                e.preventDefault();

                const data = {
                    empId: document.querySelector(".empId").value,
                    currentPassword: document.getElementById("current-pw").value,
                    newPassword: document.getElementById("new-pw").value,
                    confirmPassword: document.getElementById("confirm-pw").value
                };

                // 서버로 데이터 전송
                fetch("/api/employee/empPw/v1", {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(data => Promise.reject(data));
                        }

                        // 모든 에러 필드를 숨김
                        const errorFields = document.querySelectorAll('.error-message');
                        errorFields.forEach(errorField => {
                            errorField.style.display = 'none';
                            errorField.textContent = '';
                        });

                        alert("비밀번호가 성공적으로 변경되었습니다.");
                        location.href = "/crispy/login";
                    })
                    .catch(error => {
                        // 검증 실패 시 에러 메시지 표시
                        const errorFields = document.querySelectorAll('.error-message');
                        errorFields.forEach(errorField => {
                            errorField.style.display = 'none';
                            errorField.textContent = '';
                        });

                        Object.entries(error).forEach(([key, value]) => {
                            const errorContainer = document.getElementById(`${key}-error`);
                            if (errorContainer) {
                                errorContainer.textContent = value;
                                errorContainer.style.display = "block";
                            }
                        });
                    });
            });
        }
    },
}

document.addEventListener("DOMContentLoaded", function() {
    changePw.init();
})