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
            addressButton?.addEventListener('click', this.updateAddress.bind(this));
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
        this.setupEditableField(".btn-edit-address", ".zipcode, .street-address, .detail-address", ".btn-change-address", true);
    },
    setupEditableField: function(editButtonId, inputId, changeButtonId, isAddress = false) {
        const editButton = document.querySelector(editButtonId);
        const inputElements = document.querySelectorAll(inputId);
        const changeButton = document.querySelector(changeButtonId);
        const searchAddressButton = document.querySelector('.search-address');

        editButton?.addEventListener("click", () => {
            inputElements.forEach(inputElement => {
                inputElement.readOnly = false;
                inputElement.focus();
            });
            changeButton.style.display = 'inline';
            editButton.style.display = 'none';
            changeButton.disabled = true;

            if (isAddress) {
                searchAddressButton.disabled = false;
            }
        });

        inputElements.forEach(inputElement => {
            inputElement.addEventListener("input", () => {
                const originalValues = Array.from(inputElements).map(input => input.defaultValue.trim());
                const currentValues = Array.from(inputElements).map(input => input.value.trim());
                changeButton.disabled = JSON.stringify(originalValues) === JSON.stringify(currentValues);
            });
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

        Auth.authenticatedFetch('/api/employee/address/v1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(addressData)
        }).then(response => {
            if (response.status === 400) {
                return response.json().then(errors => {
                    this.displayValidationErrors(errors);
                    throw new Error("Validation errors");
                });
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            location.reload();
        }).catch(error => {
            if (error.message === "Validation errors") {
                console.error('Validation errors:', error);
            }
        });
    },

    displayValidationErrors: function (errors) {
        Object.keys(errors).forEach(field => {
            console.log(errors[field])
            const errorContainer = document.querySelector(`.${field}-error`);
            if (errorContainer) {
                console.log(errorContainer);
                errorContainer.textContent = errors[field];
                errorContainer.style.display = 'block';
            }
        });
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
    changeEmpPhone: function () {
        const empPhone = document.querySelector(".empPhone").value;
        const data = {
            empPhone: empPhone,
            empNo: parseInt(document.querySelector(".empNo").value),
        }
        Auth.authenticatedFetch("/api/employee/empPhone/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        const errorMessages = Object.values(err).join("\n");
                        throw new Error(errorMessages);
                    });
                } else {
                    return response.json()
                }
            }).then(data => {
            alert(data.message);
            location.reload();
        }).catch(error => {
            const errorElement = document.querySelector(".empPhone-error");
            errorElement.style.display = 'block';
            errorElement.textContent = error.message;
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
        Auth.authenticatedFetch('/api/employee/empSign/v1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                throw new Error('전자 서명 업데이트에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            alert(error);
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

        const formData = new FormData(profileForm);
        formData.append('empNo', empNo);

        const file = document.querySelector(".file-input").files[0];
        if (file) {
            formData.append('file', file);  // 서버에서 요구하는 파라미터 이름('file')에 맞게 설정
        }

        Auth.authenticatedFetch("/api/employee/profileImg/v1", {
            method: "POST",
            body: formData
        }).then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to update profile image');
        }).then(data => {
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
        console.log(data)
        Auth.authenticatedFetch("/api/employee/empName/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                throw new Error('성함 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            alert('성함 변경에 실패하였습니다.');
        });
    },
    changePosNo: function () {
        const selectedCheck = document.querySelector("input[name='position']:checked").value;
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            posNo: selectedCheck,
        }
        Auth.authenticatedFetch("/api/employee/posNo/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
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
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            empStat: selectedCheck,
        }
        Auth.authenticatedFetch("/api/employee/empStat/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
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
document.querySelector('.btn-edit-form')?.addEventListener('click', function() {
    toggleEditMode(true);
});

document.querySelector('.btn-save-form')?.addEventListener('click', function() {
    saveForm();
});

function toggleEditMode(editMode) {
    const inputs = document.querySelectorAll('.form-control');
    const editButtons = document.querySelectorAll('.emp-modify-btn button, .btn-edit-address');
    const changeAddressButton = document.querySelector('.btn-change-address');
    const searchAddressButton = document.querySelector('.search-address');
    const btnEditForm = document.querySelector('.btn-edit-form');
    const btnSaveForm = document.querySelector('.btn-save-form');

    inputs.forEach(input => {
        input.readOnly = !editMode;
    });

    editButtons.forEach(button => {
        button.style.display = editMode ? 'none' : 'inline-block';
    });

    changeAddressButton.style.display = editMode ? 'none' : 'none';
    searchAddressButton.disabled = !editMode;
    searchAddressButton.style.display = editMode ? 'inline-block' : 'none';

    btnEditForm.style.display = editMode ? 'none' : 'inline-block';
    btnSaveForm.style.display = editMode ? 'inline-block' : 'none';
}

function saveForm() {
    const empNo = document.querySelector('.empNo').value;
    const empName = document.querySelector('.empName')?.value;
    const empEmail = document.querySelector('.empEmail').value;
    const empPhone = document.querySelector('.empPhone').value;
    const empZip = document.querySelector('.zipcode').value;
    const empStreet = document.querySelector('.street-address').value;
    const empDetail = document.querySelector('.detail-address').value;
    const posNo = document.querySelector("input[name='position']:checked")?.value;
    const empStat = document.querySelector("input[name='empStat']:checked")?.value;

    const data = {
        empNo: empNo,
        empName: empName,
        empEmail: empEmail,
        empPhone: empPhone,
        empZip: empZip,
        empStreet: empStreet,
        empDetail: empDetail,
        posNo: posNo,
        empStat: empStat
    };

    const token = getCookie('accessToken');

    Auth.authenticatedFetch(`/api/employee/form/v1`, {
        method: "PUT",
        headers: {
            "Content-Type" : "application/json",
            "Authorization": `Bearer ${token}` // ensure the token is included
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.status === 401) {
                // 401 응답 처리
                alert('인증되지 않은 사용자입니다. 다시 로그인해 주세요.');
                window.location.href = '/login'; // 로그인 페이지로 리디렉션
            } else {
                return response.json();
            }
        })
        .then(data => {
            if (data.message) {
                alert(data.message)
                toggleEditMode(false);
                location.reload()
            } else {
                alert('저장 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('저장 중 오류가 발생했습니다.');
        });
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}
document.addEventListener("DOMContentLoaded", function () {
    employee.init();
})