document.getElementById('add-approval')?.addEventListener('click', function() {
    document.getElementById('signature-modal').style.display = 'flex';
    resizeCanvas(); // Ensure canvas is resized appropriately when modal is shown
});

document.querySelector('.close')?.addEventListener('click', function() {
    document.getElementById('signature-modal').style.display = 'none';
});

// 시그니쳐 패드 초기화
var canvas = document.getElementById('signature-pad');
var signaturePad = new SignaturePad(canvas);

function resizeCanvas() {
    var ratio = Math.max(window.devicePixelRatio || 1, 1);
    canvas.width = canvas.offsetWidth * ratio;
    canvas.height = canvas.offsetHeight * ratio;
    canvas.getContext("2d").scale(ratio, ratio);
    signaturePad.clear();  // clear any drawings and reset
}

window.addEventListener('resize', resizeCanvas);

// 저장 버튼
document.getElementById('save-signature').addEventListener('click', function() {
    var signature = signaturePad.toDataURL('image/png');
    console.log(signature); // 브라우저 콘솔에 이미지 데이터 URL 출력
    // 여기서 서버로 전송하거나 다른 처리를 할 수 있습니다.
});

// 지우기 버튼
document.getElementById('clear-signature').addEventListener('click', function() {
    signaturePad.clear();
});