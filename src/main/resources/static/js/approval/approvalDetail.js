document.querySelector("#approval").addEventListener("click", ()=> {
    Swal.fire({
        text: "결재 승인 여부를 선택하세요.",
        input: "radio",
        inputOptions: {
            1: "승인",
            2: "반려"
        },
        showCancelButton: true,
        confirmButtonText: "선택 완료",
        cancelButtonText: "취소",
        width: "400px",
        inputValidator: async (value) => {
            if (!value) return "결재 승인 여부가 선택되지 않았습니다.";

            const apprNo = location.pathname.split("/")[4];     // 문서번호

            // 결재선 객체
            const apprLineobj = {
                "apprNo": apprNo,
                "apprLineStat": value
            };

            // 승인: 결재서명 입력
            if (value == 1) {
                await Swal.fire({
                    text: "결재 서명을 입력하세요.",
                    html: "<div style='margin-bottom: 20px;'>결재 서명을 입력하세요.</div>" +
                          "<canvas id='signature-pad' width='250' height='250' style='border: 1px solid #ccc;'></canvas>",
                    didOpen: () => {
                        var canvas = document.getElementById('signature-pad');
                        var signaturePad = new SignaturePad(canvas);
                        resizeCanvas(canvas, signaturePad);
                    },
                    showCancelButton: true,
                    confirmButtonText: "결재하기",
                    cancelButtonText: "취소",
                    width: "400px"
                })
                    .then(result => {
                        if (result.isDismissed) return;
                        const canvas = document.getElementById('signature-pad');
                        const apprSign = canvas.toDataURL('image/png');
                        apprLineobj.data = apprSign;
                    })
            }

            // 반려: 반려사유 입력
            if (value == 2) {
                await Swal.fire({
                    text: "반려 사유를 입력하세요.",
                    input: "textarea",
                    showCancelButton: true,
                    confirmButtonText: "결재하기",
                    cancelButtonText: "취소",
                    width: "400px"
                })
                    .then(result => {
                        if (result.isDismissed) return;
                        apprLineobj.data = result.value;
                    })
            }

            // 취소
            if(!apprLineobj.data) return;

            await fetch("/crispy/change-appr-line-stat", {
                method : "PUT",
                headers : {"Content-Type" : "application/json"},
                body : JSON.stringify(apprLineobj)
            })
                .then(response => response.text())
                .then(() => {
                    let resultMsg;
                    if (value === "1") resultMsg = "승인 처리가 완료되었습니다.";
                    else resultMsg = "반려 처리가 완료되었습니다.";

                    Swal.fire({
                        icon: "success",
                        title: resultMsg,
                        showConfirmButton: false,
                        timer: 1500
                    })
                        .then(() => {
                            location.reload();
                        })

                })
                .catch(e => console.log(e));
        }
    })
})

// 시그니처 패드
function resizeCanvas(canvas, signaturePad) {
    var ratio = Math.max(window.devicePixelRatio || 1, 1);
    canvas.width = canvas.offsetWidth * ratio;
    canvas.height = canvas.offsetHeight * ratio;
    canvas.getContext("2d").scale(ratio, ratio);
    signaturePad.clear();
}