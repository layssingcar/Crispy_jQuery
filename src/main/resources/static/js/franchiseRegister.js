const franchiseRegister = {
    init: function () {
        const _this = this;
        document.getElementById("next-button")?.addEventListener("click", function () {
            _this.saveFranchiseData();
        });
        document.getElementById("btn-frn-register")?.addEventListener("click", function () {
            _this.registerFranchiseAndOwner();
        });
        const currentPage = window.location.pathname;
        const step = currentPage.includes('/crispy/franchiseRegister/register') ? 1 : 2;
        _this.updateStepIndicator(step);
    },

    // 가맹점 정보를 로컬 스토리지에 저장하고 점주 등록 페이지로 이동
    saveFranchiseData: function () {
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
            franchiseDto: franchiseData,
            ownerRegisterDto: ownerData
        };

        fetch('/api/v1/franchise/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        }).then(response => response.json())
            .then(data => {
                console.log(data);
                alert('가맹점 및 점주 등록이 완료되었습니다.');
                // window.location.href = '/successPage'; // 성공 페이지로 이동
            })
            .catch(error => {
                console.error('Error:', error);
                alert('등록에 실패했습니다.');
            });
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
