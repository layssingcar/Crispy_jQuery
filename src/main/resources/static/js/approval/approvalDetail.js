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
        inputValidator: (value) => {
            if (!value) return "결재 승인 여부가 선택되지 않았습니다.";

            // 승인
            if (value == 1) {
                Swal.fire({
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
            }

            // 반려
            if (value == 2) {
                Swal.fire({
                    text: "반려 사유를 입력하세요.",
                    input: "textarea",
                    showCancelButton: true,
                    confirmButtonText: "결재하기",
                    cancelButtonText: "취소",
                    width: "400px"
                });
            }
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