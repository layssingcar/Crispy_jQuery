const franchiseRegister = {
    init: function () {
        const _this = this;
        document.getElementById("next-button")?.addEventListener("click", function () {
            _this.saveFranchiseData();
        });
        document.getElementById("btn-frn-register")?.addEventListener("click", function () {
            _this.registerFranchiseAndOwner();
        });

        // 운영 시간 검증 이벤트 리스너 추가
        document.getElementById("frnStartTime").addEventListener("change", function () {
            _this.validateOperatingHours();
        });
        document.getElementById("frnEndTime").addEventListener("change", function () {
            _this.validateOperatingHours();
        });

        // 기존 입력 필드에 대한 실시간 검증 이벤트 리스너 추가
        document.getElementById("frn-frnName").addEventListener("input", function () {
            _this.validateField("frn-frnName", "frnName-error", "가맹점 이름을 입력해주세요.");
        });
        document.getElementById("frn-frnOwner").addEventListener("input", function () {
            _this.validateField("frn-frnOwner", "frnOwner-error", "대표자 이름을 입력해주세요.");
        });
        document.getElementById("frn-frnTel").addEventListener("input", function () {
            _this.validatePhoneField("frn-frnTel", "frnTel-error", "올바른 전화번호 형식을 입력해주세요.", /^\d{2,3}-\d{3,4}-\d{4}$/);
        });
        document.getElementById("frn-frnZip").addEventListener("input", function () {
            _this.validateField("frn-frnZip", "frnZip-error", "우편번호는 5자리여야 합니다.", /^\d{5}$/);
        });
        document.getElementById("frn-frnStreet").addEventListener("input", function () {
            _this.validateField("frn-frnStreet", "frnStreet-error", "도로명 주소를 입력해주세요.");
        });
        document.getElementById("frn-frnDetail").addEventListener("input", function () {
            _this.validateField("frn-frnDetail", "frnDetail-error", "상세 주소를 입력해주세요.");
        });

        const currentPage = window.location.pathname;
        const step = currentPage.includes('/crispy/franchise/register') ? 1 : 2;
        _this.updateStepIndicator(step);

        // 로컬 스토리지에서 데이터 불러오기
        _this.loadFranchiseData();
    },

    // 로컬 스토리지에서 가맹점 데이터를 불러오는 함수
    loadFranchiseData: function () {
        const franchiseData = JSON.parse(sessionStorage.getItem('franchiseData'));
        if (franchiseData) {
            document.getElementById("frn-frnName").value = franchiseData.frnName || '';
            document.getElementById("frn-frnOwner").value = franchiseData.frnOwner || '';
            document.getElementById("frn-frnTel").value = franchiseData.frnTel || '';
            document.getElementById("frn-frnZip").value = franchiseData.frnZip || '';
            document.getElementById("frn-frnStreet").value = franchiseData.frnStreet || '';
            document.getElementById("frn-frnDetail").value = franchiseData.frnDetail || '';
            document.querySelector(".frnStartTime").value = franchiseData.frnStartTime || '';
            document.querySelector(".frnEndTime").value = franchiseData.frnEndTime || '';

            // 오류 메시지 숨기기
            this.hideErrorMessages();
        }
    },

    // 오류 메시지를 숨기는 함수
    hideErrorMessages: function () {
        const errorMessages = document.querySelectorAll('.error-message');
        errorMessages.forEach(error => {
            error.style.display = 'none';
        });
    },

    // 필드별 실시간 검증 함수
    validateField: function (fieldId, errorId, errorMessage, pattern = /.*/) {
        const fieldValue = document.getElementById(fieldId).value;
        const errorElement = document.getElementById(errorId);

        if (!fieldValue || !pattern.test(fieldValue)) {
            errorElement.textContent = errorMessage;
            errorElement.style.display = "block";
        } else {
            errorElement.style.display = "none";
        }
    },

    // 전화번호 필드 검증 함수
    validatePhoneField: function (fieldId, errorId, errorMessage, pattern) {
        const fieldValue = document.getElementById(fieldId).value;
        const errorElement = document.getElementById(errorId);

        if (fieldValue && !pattern.test(fieldValue)) {
            errorElement.textContent = errorMessage;
            errorElement.style.display = "block";
        } else {
            errorElement.style.display = "none";
        }
    },

    // 가맹점 정보를 로컬 스토리지에 저장하고 점주 등록 페이지로 이동
    saveFranchiseData: function () {
        if (!this.validateFranchiseForm()) {
            return; // 검증 실패 시 반환
        }

        const franchiseData = {
            frnName: document.getElementById("frn-frnName").value,
            frnOwner: document.getElementById("frn-frnOwner").value,
            frnTel: document.getElementById("frn-frnTel").value,
            frnZip: document.getElementById("frn-frnZip").value,
            frnStreet: document.getElementById("frn-frnStreet").value,
            frnDetail: document.getElementById("frn-frnDetail").value,
            frnStartTime: document.querySelector(".frnStartTime").value,
            frnEndTime: document.querySelector(".frnEndTime").value
        };
        sessionStorage.setItem('franchiseData', JSON.stringify(franchiseData));
        window.location.href = '/crispy/franchise/owner/register'; // 점주 등록 페이지로 이동
    },

    // 가맹점 및 점주 등록 정보를 서버로 전송
    registerFranchiseAndOwner: function () {
        const ownerData = {
            empId: document.getElementById("owner-empId").value,
            empPhone: document.getElementById("owner-empPhone").value,
            empEmail: document.getElementById("owner-empEmail").value
        };
        const franchiseData = JSON.parse(sessionStorage.getItem('franchiseData')); // 로컬 스토리지에서 가맹점 정보 불러오기

        const requestData = {
            franchise: franchiseData,
            owner: ownerData
        };

        fetch('/api/franchise/register/v1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        }).then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('가맹점 및 점주 등록이 완료되었습니다.');
                    // window.location.href = '/successPage'; // 성공 페이지로 이동
                } else {
                    this.displayErrorMessages(data);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('등록에 실패했습니다.');
            });
    },

    displayErrorMessages: function (errorResponse) {
        document.querySelectorAll('.error-message').forEach(errorContainer => {
            errorContainer.style.display = 'none';
            errorContainer.textContent = '';
        });

        Object.keys(errorResponse).forEach(field => {
            const errorContainer = document.getElementById(`${field}-error`);
            if (errorContainer) {
                errorContainer.textContent = errorResponse[field];
                errorContainer.style.display = 'block';
            }
        });
    },

    // 전체 폼 검증 로직
    validateFranchiseForm: function () {
        const frnName = document.getElementById("frn-frnName").value;
        const frnOwner = document.getElementById("frn-frnOwner").value;
        const frnTel = document.getElementById("frn-frnTel").value;
        const frnZip = document.getElementById("frn-frnZip").value;
        const frnStreet = document.getElementById("frn-frnStreet").value;
        const frnDetail = document.getElementById("frn-frnDetail").value;

        let isValid = true;

        if (!frnName) {
            document.getElementById("frnName-error").textContent = "가맹점 이름을 입력해주세요.";
            document.getElementById("frnName-error").style.display = "block";
            isValid = false;
        } else {
            document.getElementById("frnName-error").style.display = "none";
        }

        if (!frnOwner) {
            document.getElementById("frnOwner-error").textContent = "대표자 이름을 입력해주세요.";
            document.getElementById("frnOwner-error").style.display = "block";
            isValid = false;
        } else {
            document.getElementById("frnOwner-error").style.display = "none";
        }

        if (frnTel && !/^010\d{4}\d{4}$/.test(frnTel)) {
            console.log(frnTel.value)
            document.getElementById("frnTel-error").textContent = "올바른 전화번호 형식을 입력해주세요.";
            document.getElementById("frnTel-error").style.display = "block";
            isValid = false;
        } else {
            document.getElementById("frnTel-error").style.display = "none";
        }

        // 운영 시간 유효성 검증
        if (!this.validateOperatingHours()) {
            isValid = false;
        }

        return isValid;
    },

    // 운영 시간 유효성 검증 함수
    validateOperatingHours: function () {
        const startTime = document.querySelector(".frnStartTime").value;
        const endTime = document.querySelector(".frnEndTime").value;

        if (startTime && endTime && startTime > endTime) {
            Swal.fire({
                icon: "warning",
                text: "종료 시간은 시작 시간보다 빠를 수 없습니다.",
                width: "365px"
            }).then(() => {
                document.querySelector(".frnEndTime").focus();
            });
            endTime.value = "";
            return;
        }
        return true;
    },

    updateStepIndicator: function (step) {
        const indicatorLine = document.querySelector('.indicator-line');
        if (step === 1) {
            indicatorLine.style.background =
                `linear-gradient(to right, var(--main-color) 50%, #c2c2c2 50%)`;
        } else if (step === 2) {
            indicatorLine.style.background =
                `linear-gradient(to left, var(--main-color) 100%, #c2c2c2 0%)`;
        }
    },
};

document.addEventListener("DOMContentLoaded", function () {
    franchiseRegister.init();
});
