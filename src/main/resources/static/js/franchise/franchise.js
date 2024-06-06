const franchise = {
    init: function () {
        this.setupProfileImageUpload();
        this.setupFormButtons();
        document.getElementById("btn-update-frn-img")?.addEventListener("click", () => {
            this.changeProfileImage();
        });
        this.bindEvents();
        const frnNo = sessionStorage.getItem("selectedFrnNo")
        if (frnNo) {
            fetch(`/api/franchise/${frnNo}/v1`)
                .then(response => response.json())
                .then(data => {
                    this.updateFranchiseProfile(data);
                    document.getElementById('frn-Img').style.display = 'block';
                })
                .catch(error => console.error('Error loading franchise profile:', error));
        } else {
            document.getElementById('frn-Img').style.display = 'block';
        }
    },

    bindEvents: function() {
        const addressButton = document.querySelector('.btn-change-address');
        if (addressButton) {
            addressButton.addEventListener('click', this.updateFrnAddress.bind(this));
        }

        const frnOwnerButton = document.getElementById("btn-change-frnOwner");
        if (frnOwnerButton) {
            frnOwnerButton.addEventListener("click", this.changeFrnOwner.bind(this));
        }

        const frnTelButton = document.getElementById("btn-change-frnTel");
        if (frnTelButton) {
            frnTelButton.addEventListener("click", this.changeFrnTel.bind(this));
        }

        const operatingTimeButton = document.getElementById("btn-edit-operating-time");
        if (operatingTimeButton) {
            operatingTimeButton.addEventListener("click", this.setupOperatingTimeFields.bind(this));
        }

        const changeOperatingTimeButton = document.getElementById("btn-change-operating-time");
        if (changeOperatingTimeButton) {
            changeOperatingTimeButton.addEventListener("click", this.changeOperatingTime.bind(this));
        }

        this.setupFrnEditBtn("btn-edit-frnName", "frn-frnName", "btn-change-frnName");
        this.setupFrnEditBtn("btn-edit-frnOwner", "frn-frnOwner", "btn-change-frnOwner");
        this.setupFrnEditBtn("btn-edit-frnTel", "frn-frnTel", "btn-change-frnTel");
        this.setupFrnEditBtn("btn-edit-operating-time", "frnStartTime", "btn-change-operating-time");
    },

    setupFormButtons: function () {
        document.querySelector('.btn-edit-form')?.addEventListener('click', () => {
            this.toggleEditMode(true);
        });
        document.querySelector('.btn-save-form')?.addEventListener('click', () => {
            this.saveForm();
        });
    },

    toggleEditMode: function(editMode) {
        const inputs = document.querySelectorAll('.form-control');
        const buttons = [
            {edit: "#btn-edit-frnName", change: "#btn-change-frnName"},
            {edit: "#btn-edit-frnOwner", change: "#btn-change-frnOwner"},
            {edit: "#btn-edit-frnTel", change: "#btn-change-frnTel"},
            {edit: ".btn-change-address", change: ".btn-change-address"},
            {edit: "#btn-edit-operating-time", change: "#btn-change-operating-time"}
        ];
        const btnEditForm = document.querySelector('.btn-edit-form');
        const btnSaveForm = document.querySelector('.btn-save-form');

        inputs.forEach(input => {
            input.readOnly = !editMode;
        });

        buttons.forEach(pair => {
            const editButton = document.querySelector(pair.edit);
            const changeButton = document.querySelector(pair.change);
            if (editButton) editButton.style.display = editMode ? 'none' : 'inline-block';
            if (changeButton) changeButton.style.display = editMode ? 'none' : 'none';
        });

        if (editMode) {
            this.setupOperatingTimeFields();
        }

        if (btnEditForm) btnEditForm.style.display = editMode ? 'none' : 'inline-block';
        if (btnSaveForm) btnSaveForm.style.display = editMode ? 'inline-block' : 'none';
    },

    saveForm: function() {
        const frnNo = document.querySelector('.frnNo').value;
        const frnName = document.querySelector('.frnName').value;
        const frnOwner = document.querySelector('.frnOwner').value;
        const frnTel = document.querySelector('.frnTel').value;
        const frnZip = document.querySelector('.zipcode').value;
        const frnStreet = document.querySelector('.street-address').value;
        const frnDetail = document.querySelector('.detail-address').value;
        const frnStartTime = document.querySelector('.frnStartTime').value;
        const frnEndTime = document.querySelector('.frnEndTime').value;

        const data = {
            frnNo: frnNo,
            frnName: frnName,
            frnOwner: frnOwner,
            frnTel: frnTel,
            frnZip: frnZip,
            frnStreet: frnStreet,
            frnDetail: frnDetail,
            frnStartTime: frnStartTime,
            frnEndTime: frnEndTime,
        };

        const token = Auth.getCookie('accessToken');

        Auth.authenticatedFetch(`/api/franchise/form/v1`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.status === 401) {
                    alert('인증되지 않은 사용자입니다. 다시 로그인해 주세요.');
                    window.location.href = '/login';
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

    setupOperatingTimeFields: function () {
        this.replaceInputWithSelect('.frnStartTime', true);
        this.replaceInputWithSelect('.frnEndTime', false);
    },

    replaceInputWithSelect: function (inputSelector, focusAfterReplace) {
        const inputElement = document.querySelector(inputSelector);
        const currentValue = inputElement.value;
        const select = document.createElement("select");
        select.className = inputElement.className;
        select.id = inputElement.id;
        select.name = inputElement.name;

        for (let hour = 8; hour < 24; hour++) {
            for (let minute = 0; minute < 60; minute += 30) { // 30분 간격으로 옵션 추가
                const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                const option = document.createElement('option');
                option.value = option.textContent = time;
                if (time === currentValue) {
                    option.selected = true;
                }
                select.appendChild(option);
            }
        }

        // 기존 input을 select로 교체
        inputElement.parentNode.replaceChild(select, inputElement);

        if (focusAfterReplace) { select.focus(); }
    },

    setupFrnEditBtn: function(editButtonId, inputId, changeButtonId) {
        const editButton = document.getElementById(editButtonId);
        const inputElement = document.getElementById(inputId);
        const changeButton = document.getElementById(changeButtonId);

        let originalValue = inputElement?.value;

        editButton?.addEventListener("click", () => {
            inputElement.readOnly = false;
            inputElement.focus();
            changeButton.style.display = 'inline';
            editButton.style.display = 'none';
            changeButton.disabled = true;

            originalValue = inputElement.value;
        });

        inputElement?.addEventListener("input", () => {
            changeButton.disabled = inputElement.value.trim() === originalValue.trim();
        });
    },

    updateFrnAddress: function() {
        const frnNo = document.querySelector(".frnNo").value;
        const zipCode = document.querySelector('.zipcode').value;
        const street = document.querySelector('.street-address').value;
        const detail = document.querySelector('.detail-address').value;

        const addressData = {
            frnNo: frnNo,
            frnZip: zipCode,
            frnStreet: street,
            frnDetail: detail
        };

        fetch('/api/franchise/frnAddress/v1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
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
            const errorContainer = document.querySelector(`.${field}-error`);
            const inputElement = document.querySelector(`.${field}`);
            if (errorContainer) {
                errorContainer.textContent = errors[field];
                errorContainer.style.display = 'block';
                inputElement.focus();
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

    changeFrnTel: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            frnTel: document.querySelector(".frnTel").value,
        }
        fetch("/api/franchise/frnTel/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    this.displayValidationErrors(err);
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

    setupProfileImageUpload: function () {
        const profileImage = document.querySelector(".frn-img");
        const fileInput = document.querySelector(".file-input");

        profileImage?.addEventListener('click', function() {
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
        const frnNo = document.querySelector(".frnNo").value;
        const frnImgForm = document.getElementById("frn-form")

        const formData = new FormData(frnImgForm);
        formData.append('frnNo', frnNo);

        const file = document.querySelector(".file-input").files[0];
        if (file) {
            formData.append('file', file);  // 서버에서 요구하는 파라미터 이름('file')에 맞게 설정
        }

        fetch("/api/franchise/frnImg/v1", {
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

    changeFrnOwner: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            empNo: parseInt(document.querySelector(".empNo").value),
            frnOwner: document.querySelector(".frnOwner").value,
        }
        fetch("/api/franchise/frnOwner/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    this.displayValidationErrors(err);
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

    changeOperatingTime: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            empNo: parseInt(document.querySelector(".empNo").value),
            frnStartTime: document.querySelector(".frnStartTime").value,
            frnEndTime: document.querySelector(".frnEndTime").value
        }

        fetch("/api/franchise/operatingTime/v1", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                throw new Error('운영시간 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('운영시간 변경에 실패하였습니다.');
        });
    },

    updateFranchiseProfile: function(frn) {
    const frnJoinDt = this.formatDate(frn.frnJoinDt);
    document.querySelector('.frn-img').src = frn.frnImg || '';
    document.querySelector('.frnNo').value = frn.frnNo || '';
    document.querySelector(".empNo").value = frn.empNo || '';
    document.querySelector('.frnName').value = frn.frnName || '';
    document.querySelector('.frnOwner').value = frn.frnOwner || '';
    document.querySelector('.frnTel').value = frn.frnTel || '';
    document.querySelector('.zipcode').value = frn.frnZip || '';
    document.querySelector('.frnStartTime').value = frn.frnStartTime || '';
    document.querySelector('.frnEndTime').value = frn.frnEndTime || '';
    document.querySelector(".empInDt").value = frnJoinDt || '';
    document.querySelector('.street-address').value = frn.frnStreet || '';
    document.querySelector('.detail-address').value = frn.frnDetail || '';
},

     formatDate: function(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    },
}
document.addEventListener("DOMContentLoaded", function () {
    // 세션 스토리지 확인 용
    const frnNo = sessionStorage.getItem('selectedFrnNo');
    const isOwner = document.getElementById("isOwner").value;

    if(frnNo && isOwner === 'true') {
        sessionStorage.removeItem('selectedFrnNo');
    }
    franchise.init();
});
