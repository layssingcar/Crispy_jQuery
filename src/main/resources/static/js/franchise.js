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

        const empNameButton = document.getElementById("btn-change-empName");
        empNameButton?.addEventListener("click", this.changeEmpName);

        const frnTelButton = document.getElementById("btn-change-frnTel");
        frnTelButton?.addEventListener("click", this.changeFrnTel);

        const posNoButton = document.getElementById("btn-change-posNo");
        posNoButton?.addEventListener("click", this.changePosNo);

        this.setupEditableField("btn-edit-frnName", "frn-frnName", "btn-change-frnName");
        this.setupEditableField("btn-edit-frnOwner", "frn-frnOwner", "btn-change-frnOwner");
        this.setupEditableField("btn-edit-frnTel", "frn-frnTel", "btn-change-frnTel");

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
}
document.addEventListener("DOMContentLoaded", function () {
    employee.init();
})