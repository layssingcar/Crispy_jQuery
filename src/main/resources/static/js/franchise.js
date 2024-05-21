const employee = {
    init: function () {
        this.bindEvents();
        this.setupProfileImageUpload();
        document.getElementById("btn-update-frn-img")?.addEventListener("click", () => {
            this.changeProfileImage();
        })
    },
    bindEvents: function() {
        const addressButton = document.querySelector('.btn-change-address');
        if (addressButton) {
            console.log(addressButton);
            addressButton?.addEventListener('click', this.updateFrnAddress);
        }

        const frnOwnerButton = document.getElementById("btn-change-frnOwner");
        frnOwnerButton?.addEventListener("click", this.changeFrnOwner);

        const frnTelButton = document.getElementById("btn-change-frnTel");
        frnTelButton?.addEventListener("click", this.changeFrnTel);

        const posNoButton = document.getElementById("btn-change-posNo");
        posNoButton?.addEventListener("click", this.changePosNo);

        const operatingTimeButton = document.getElementById("btn-edit-operating-time");
        operatingTimeButton?.addEventListener("click", this.setupOperatingTimeFields.bind(this));

        const changeOperatingTimeButton = document.getElementById("btn-change-operating-time");
        changeOperatingTimeButton.addEventListener("click", this.changeOperatingTime.bind(this));


        this.setupEditableField("btn-edit-frnName", "frn-frnName", "btn-change-frnName");
        this.setupEditableField("btn-edit-frnOwner", "frn-frnOwner", "btn-change-frnOwner");
        this.setupEditableField("btn-edit-frnTel", "frn-frnTel", "btn-change-frnTel");
        this.setupEditableField("btn-edit-operating-time", "frnStartTime", "btn-change-operating-time");

    },

    setupOperatingTimeFields: function () {
        this.replaceInputWithSelect('.frnStartTime', true);
        this.replaceInputWithSelect('.frnEndTime', false);
        document.getElementById('btn-edit-operating-time').style.display = 'none';  // 수정 버튼 숨기기
        document.getElementById('btn-change-operating-time').style.display = 'inline'; // 변경 버튼 보이기
    },

    replaceInputWithSelect: function (inputId, focusAfterReplace) {
    const inputElement = document.querySelector(inputId);
    const currentValue = inputElement.value;
    const select = document.createElement("select");
    select.className = inputElement.className;
    select.id = inputElement.id;
    select.name = inputElement.name;

    for (let hour = 0; hour < 24; hour++) {
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

        if (focusAfterReplace) {select.focus();}
},

    setupEditableField: function(editButtonId, inputId, changeButtonId) {
        const editButton = document.getElementById(editButtonId);
        const inputElement = document.getElementById(inputId);
        const changeButton = document.getElementById(changeButtonId);

        let originalValue = inputElement.value;

        editButton.addEventListener("click", () => {
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

        fetch('/api/v1/franchise/updateFrnAddress', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(addressData)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('주소 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
        }).catch(error => {
            console.error('주소 변경에 실패하였습니다 :', error);
            alert('주소 변경에 실패하였습니다.');
        });
    },
    changeFrnTel: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            frnTel: document.querySelector(".frnTel").value,
        }
        fetch("/api/v1/franchise/updateFrnTel", {
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
    setupProfileImageUpload: function () {
        const profileImage = document.querySelector(".frn-img");
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
        const frnNo = document.querySelector(".frnNo").value;
        const frnImgForm = document.getElementById("frn-form")

        const formData = new FormData(frnImgForm);
        console.log("formData: " + formData);
        formData.append('frnNo', frnNo);

        const file = document.querySelector(".file-input").files[0];
        if (file) {
            formData.append('file', file);  // 서버에서 요구하는 파라미터 이름('file')에 맞게 설정
        }

        fetch("/api/v1/franchise/updateFrnImg", {
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
    changeFrnOwner: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            empNo: parseInt(document.querySelector(".empNo").value),
            frnOwner: document.querySelector(".frnOwner").value,
        }
        fetch("/api/v1/franchise/updateFrnOwner", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error('대표자명 변경에 실패했습니다.');
            }
            return response.json();
        }).then(data => {
            alert(data.message);
            return location.reload();
        }).catch(error => {
            console.error('Error updating address:', error);
            alert('대표자명 변경에 실패하였습니다.');
        });
    },
    changeOperatingTime: function () {
        const data = {
            frnNo: parseInt(document.querySelector(".frnNo").value),
            empNo: parseInt(document.querySelector(".empNo").value),
            frnStartTime: document.querySelector(".frnStartTime").value,
            frnEndTime: document.querySelector(".frnEndTime").value
        }

        fetch("/api/v1/franchise/update/operating-time", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data)
        }).then(response => {
            console.log(response);
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
    }
}
document.addEventListener("DOMContentLoaded", function () {
    employee.init();
})