const employee = {  
    init: function () {
        this.changePassword();
        this.bindEvents();
        this.setupProfileImageUpload();
        document.getElementById("btn-update-profile")?.addEventListener("click", () => {
            this.changeProfileImage();
        })
    },
    bindEvents: function() {
        const addressButton = document.querySelector('.btn-change-address');
        if (addressButton) {
            console.log(addressButton);
            addressButton?.addEventListener('click', this.updateAddress);
        }
        const empSignButton = document.getElementById("save-signature");
        empSignButton?.addEventListener("click", this.updateEmpSign);

        const empNameButton = document.querySelector(".btn-change-empName");
        empNameButton?.addEventListener("click", this.changeEmpName);

        const empPhoneButton = document.querySelector(".btn-change-empPhone");
        empPhoneButton?.addEventListener("click", this.changeEmpPhone);

        const posNoButton = document.getElementById("btn-change-posNo");
        posNoButton?.addEventListener("click", this.changePosNo);

        const empStatButton = document.getElementById("btn-change-empStat");
        empStatButton?.addEventListener("click", this.changeEmpStat);

        this.setupEditableField(".btn-edit-empName", ".empName", ".btn-change-empName");
        this.setupEditableField(".btn-edit-empPhone", ".empPhone", ".btn-change-empPhone");
        this.setupEditableField(".btn-edit-empEmail", ".empEmail", ".btn-change-empEmail");

    },
    setupEditableField: function(editButtonId, inputId, changeButtonId) {
        const editButton = document.querySelector(editButtonId);
        const inputElement = document.querySelector(inputId);
        const changeButton = document.querySelector(changeButtonId);

        let originalValue = inputElement.value;

        editButton?.addEventListener("click", () => {
            inputElement.readOnly = false;
            inputElement.focus();
            changeButton.style.display = 'inline';
            editButton.style.display = 'none';
            changeButton.disabled = true;

            originalValue = inputElement.value;
        });

        inputElement.addEventListener("input", () => {
            changeButton.disabled = inputElement.value.trim() === originalValue.trim();
        });
    },
    updateAddress: function() {
        const empNo = document.querySelector(".empNo").value;
        const zipCode = document.querySelector('.zipcode').value;
        const street = document.querySelector('.street-address').value;
        const detail = document.querySelector('.detail-address').value;

        const addressData = {
            empNo: empNo,
            empZip: zipCode,
            empStreet: street,
            empDetail: detail
        };

        fetch('/api/v1/employee/address', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(addressData)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('Failed to update the address');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('주소 변경에 실패하였습니다.');
        });
    },
    changePassword: function () {
        const passwordForm = document.getElementById("passwordForm");
        if(passwordForm) {
            passwordForm.addEventListener("submit", function (e) {
                e.preventDefault();

                const data = {
                    empId: document.querySelector(".empId").value,
                    currentPassword: document.getElementById("current-pw").value,
                    newPassword: document.getElementById("new-pw").value,
                    confirmPassword: document.getElementById("confirm-pw").value
                }
                // 서버로 데이터 전송
                fetch("/api/v1/employee/updateEmpPw", {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if(!response.ok) {
                            return response.json().then(data => Promise.reject(data));
                        }
                        alert("비밀번호가 성공적으로 변경 되었습니다.");
                        location.href = "/crispy/login";
                        return response.json();
                    })
                    .catch(error => {
                        console.log("Error:", error);
                        Object.entries(error).forEach(([key, value]) => {
                            const errorContainer = document.getElementById(`${key}-error`);
                            console.log(key)
                            console.log(value)
                            if(errorContainer) {
                                errorContainer.textContent = value;
                                errorContainer.style.display = "block";
                            }
                        })
                    })
            })
        }
    },
    changeEmpPhone: function () {
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            empPhone: document.querySelector(".empPhone").value,
        }
        fetch("/api/v1/employee/updateEmpPhone", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
                console.log(response);
                if (!response.ok) {
                    throw new Error('휴대폰 번호 변경에 실패했습니다.');
                }
                return response.json();
            }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('주소 변경에 실패하였습니다.');
        });
    },
    updateEmpSign: function () {
        const empNo = document.querySelector(".empNo").value;
        const canvas = document.getElementById('signature-pad');
        const empSign = canvas.toDataURL('image/png');

        const data = {
            empNo: empNo,
            empSign: empSign
        }
        fetch('/api/v1/employee/empSign', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('Failed to update the address');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('주소 변경에 실패하였습니다.');
        });
    },
    setupProfileImageUpload: function () {
        const profileImage = document.querySelector(".profile-img");
        const updateText = document.querySelector(".update-img");
        const fileInput = document.querySelector(".file-input");

        profileImage?.addEventListener('click', function() {
            fileInput.click();
        });

        fileInput?.addEventListener("change", function () {
            if(this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = (e) =>
                    profileImage.src = e.target.result;
                reader.readAsDataURL(this.files[0]);
            }
        })
    },
    changeProfileImage: function () {
        const empNo = document.querySelector(".empNo").value;
        const profileForm = document.getElementById("profileImageForm")
        console.log(empNo);

        const formData = new FormData(profileForm);
        console.log("formData: " + formData);
        formData.append('empNo', empNo);

        const file = document.querySelector(".file-input").files[0];
        if (file) {
            formData.append('file', file);  // 서버에서 요구하는 파라미터 이름('file')에 맞게 설정
        }

        fetch("/api/v1/employee/profileImg", {
            method: "POST",
            body: formData
        }).then(response => {
            console.log(response)
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to update profile image');
        }).then(data => {
            console.log(data)
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error:', error);
        });
    },
    changeEmpName: function () {
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            empName: document.querySelector(".empName").value,
        }
        fetch("/api/v1/employee/updateEmpName", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('성함 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('성함 변경에 실패하였습니다.');
        });
    },
    changePosNo: function () {
        const selectedCheck = document.querySelector("input[name='position']:checked").value;
        console.log(selectedCheck)
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            posNo: selectedCheck,
        }
        fetch("/api/v1/employee/updatePosNo", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('직책 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('직책 변경에 실패하였습니다.');
        });
    },
    changeEmpStat: function () {
        const selectedCheck = document.querySelector("input[name='empStat']:checked").value;
        console.log(selectedCheck)
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            empStat: selectedCheck,
        }
        fetch("/api/v1/employee/updateEmpStat", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('재직 상태 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('재직 상태 변경에 실패하였습니다.');
        });
    },

}
document.addEventListener("DOMContentLoaded", function () {
    employee.init();
})