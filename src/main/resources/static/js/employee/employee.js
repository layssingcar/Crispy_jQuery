const employee = {
    init: function () {
        this.bindEvents();
        this.setupFormButtons();
        this.setupProfileImageUpload();
        this.setupValidationListeners();
        document.getElementById("btn-update-profile")?.addEventListener("click", () => {
            this.changeProfileImage();
        })
    },

    setupFormButtons: function () {
        document.querySelector('.btn-edit-form')?.addEventListener('click', () => {
            this.toggleEditMode(true);
        });

        document.querySelector('.btn-save-form')?.addEventListener('click', () => {
            this.saveForm();
        });
    },

    bindEvents: function () {
        const addressButton = document.querySelector('.btn-change-address');
        if (addressButton) {
            addressButton?.addEventListener('click', this.updateAddress.bind(this));
        }
        const empSignButton = document.getElementById("save-signature");
        empSignButton?.addEventListener("click", this.updateEmpSign);

        const empEmailButton = document.querySelector(".btn-change-empEmail");
        empEmailButton?.addEventListener("click", this.changeEmpEmail)

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
        this.setupEditableField(".btn-edit-address", ".detail-address", ".btn-change-address", true);
    },
    setupEditableField: function (editButtonId, inputId, changeButtonId, isAddress = false) {
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

            if (isAddress) {
                searchAddressButton.disabled = false;
            }
        });
    },

    updateAddress: function () {
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

    hideValidationError: function(inputElement) {
        const fieldName = inputElement.name;
        const errorContainer = document.querySelector(`.${fieldName}-error`);
        if (errorContainer) {
            errorContainer.style.display = 'none';
            errorContainer.textContent = '';
        }
    },

    showValidationError: function(inputElement, message) {
        const fieldName = inputElement.name;
        const errorContainer = document.querySelector(`.${fieldName}-error`);
        if (errorContainer) {
            errorContainer.style.display = 'block';
            errorContainer.textContent = message;
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
                    employee.displayValidationErrors(err);
                });
            } else {
                return response.json()
            }
        }).then(data => {
            alert(data.message);
            location.reload();
        }).catch(error => {
            console.error('Error:', error);
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

        profileImage?.addEventListener('click', function () {
            fileInput.click();
        });

        fileInput?.addEventListener("change", function () {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = (e) =>
                    profileImage.src = e.target.result;
                reader.readAsDataURL(this.files[0]);
            }
        })
    },
    changeProfileImage: function () {
        const empNo = document.querySelector(".empNo").value;
        const profileForm = document.getElementById("profile-image-form")

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
                return response.json().then(err => {
                    employee.displayValidationErrors(err);
                });
            } else {
                return response.json()
            }
        }).then(data => {
            alert(data.message);
            location.reload();
        }).catch(error => {
            console.error('Error:', error);
        });
    },
    changeEmpEmail: function () {
        const data = {
            empNo: parseInt(document.querySelector(".empNo").value),
            empEmail: document.querySelector(".empEmail").value,
        }
        Auth.authenticatedFetch("/api/employee/empEmail/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    employee.displayValidationErrors(err);
                });
            } else {
                return response.json()
            }
        }).then(data => {
            alert(data.message);
            location.reload();
        }).catch(error => {
            console.error('Error:', error);
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

    toggleEditMode: function (editMode) {
        const inputs = document.querySelectorAll('.form-control');
        const editButtons = document.querySelectorAll('.emp-modify-btn button, .btn-edit-address');
        const changeAddressButton = document.querySelector('.btn-change-address');
        const searchAddressButton = document.querySelector('.search-address');
        const btnEditForm = document.querySelector('.btn-edit-form');
        const btnSaveForm = document.querySelector('.btn-save-form');

        inputs.forEach(input => {
            if (input.classList.contains('street-address') || input.classList.contains('zipcode')) {
                input.readOnly = true;
            } else {
                input.readOnly = !editMode;
            }
        });

        editButtons.forEach(button => {
            button.style.display = editMode ? 'none' : 'inline-block';
        });

        changeAddressButton.style.display = editMode ? 'none' : 'none';
        searchAddressButton.disabled = !editMode;
        searchAddressButton.style.display = editMode ? 'inline-block' : 'none';

        btnEditForm.style.display = editMode ? 'none' : 'inline-block';
        btnSaveForm.style.display = editMode ? 'inline-block' : 'none';
    },

    saveForm: function () {
        const empNo = document.querySelector('.empNo').value;
        const empName = document.querySelector('.empName').value;
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
            empStat: empStat,
        };

        const token = Auth.getCookie('accessToken');
        console.log(empName);
        Auth.authenticatedFetch(`/api/employee/form/v1`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}` // ensure the token is included
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.status === 401) {
                    alert('인증되지 않은 사용자입니다. 다시 로그인해 주세요.');
                    window.location.href = '/login';
                } else if (response.status === 400) {
                    return response.json().then(errors => {
                        this.displayValidationErrors(errors);
                        throw new Error("Validation errors");
                    });
                } else {
                    return response.json();
                }
            })
            .then(data => {
                if (data.message) {
                    alert(data.message);
                    this.toggleEditMode(false);
                    location.reload();
                } else {
                    alert('저장 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                if (error.message === "Validation errors") {
                    console.error('Validation errors:', error);
                } else {
                    console.error('Error:', error);
                    alert('저장 중 오류가 발생했습니다.');
                }
            });
    },
    setupValidationListeners: function() {
        const inputFields = document.querySelectorAll('.form-control');
        inputFields.forEach(input => {
            input.addEventListener('change', (e) => {
                this.hideValidationError(e.target);
            });
        });
    },
    updateEmployeeProfile: function(employee) {
        if (employee.empProfile) {
            document.querySelector(".profile-img").src = employee.empProfile;
        } else {
            document.querySelector(".profile-img").src = "/img/anonymous.png";
        }
        document.querySelector(".empId").value = employee.empId;
        document.querySelector('.empNo').value = employee.empNo;
        document.querySelector('.frnName').textContent = employee.frnName;
        document.querySelector('.empName-span').textContent = employee.empName;
        document.querySelector('.empName').value = employee.empName;
        document.querySelector('.posName').textContent = employee.posName;
        document.querySelectorAll('input[name="position"]').forEach(function (radio) {
            radio.checked = radio.value === employee.posNo;
        });
        document.querySelectorAll('input[name="empStat"]').forEach(function (radio) {
            radio.checked = radio.value === employee.empStat
        })
        document.getElementById('emp-profile-empEmail').value = employee.empEmail;
        document.getElementById('emp-profile-empPhone').value = employee.empPhone;
        document.querySelector('.zipcode').value = employee.empZip || '';
        document.querySelector('.street-address').value = employee.empStreet || '';
        document.querySelector('.detail-address').value = employee.empDetail || '';

        document.getElementById('employee-profile').style.display = 'block';
    },
}


document.addEventListener("DOMContentLoaded", function () {
    const empNo = sessionStorage.getItem('selectedEmpNo');
    if (empNo) {
        // 세션에 저장된 empNo가 있다면, 해당 직원 정보를 비동기적으로 불러와서 업데이트
        fetch(`/api/employee/${empNo}/v1`)
            .then(response => response.json())
            .then(data => {
                // 받은 데이터로 페이지 업데이트
                employee.updateEmployeeProfile(data);
                document.getElementById('employee-profile').style.display = 'block';
            })
            .catch(error => console.error('Error loading employee profile:', error));
    } else {
        // 세션 스토리지에 empNo가 없다면 기본적으로 타임리프로 렌더링된 데이터를 그대로 사용
        document.getElementById('employee-profile').style.display = 'block';
    }
    employee.init();
})