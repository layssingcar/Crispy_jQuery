const board = {
    init: function() {
        this.bindEvents();
    },

    bindEvents: function() {
        document.getElementById('modifyBtn')?.addEventListener('click', () => {
            window.location.href = '/board/board-modify';
        });

        const addBoard = document.getElementById("addBtn");
        addBoard?.addEventListener("click", this.addBoard.bind(this));


        // 게시판 수정
        const modifyBoard = document.querySelector(".btn-modify");
        modifyBoard?.addEventListener("click", this.modifyBoard.bind(this));


        const deleteFile = document.querySelectorAll('.delete-file');
        deleteFile.forEach(button => {
            button.addEventListener("click", function() {
                const li = this.closest('li');
                if (li) {
                    li.style.display = 'none'; // 요소를 숨김
                }
            });
        });

        document.querySelector('.board-delete')?.addEventListener("click", this.deleteBoard.bind(this));
        document.getElementById("comment-add")?.addEventListener("click", this.createComment.bind(this));
        document.getElementById("like-button")?.addEventListener("click", this.toggleLike.bind(this));

        this.bindCommentEvents();
        this.bindFileDownloadEvents();
    },

    addBoard: function() {
        const form = document.getElementById('frm-board-add');
        const formData = new FormData(form);
        const boardDto = {
            boardCtNo: form.boardCtNo.value,
            boardTitle: form.boardTitle.value,
            boardContent: $('<div>').html($('#summernote').summernote('code')).text()
        };

        formData.append('boardDto', new Blob([JSON.stringify(boardDto)], { type: 'application/json' }));

        fetch('/api/board/v1', {
            method: "POST",
            body: formData
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
            const boardNo = data.boardNo
            location.href =`/crispy/board-detail?boardNo=${boardNo}`
        }).catch(error => {
            console.error('Error:', error);
        });
    },

    displayValidationErrors: function (errors) {
        Object.keys(errors).forEach(field => {
            const errorContainer = document.querySelector(`.${field}-error`);
            if (errorContainer) {
                errorContainer.textContent = errors[field];
                errorContainer.style.display = 'block';
            }
        });
    },


    modifyBoard: function() {
        const form = document.getElementById('frm-board-modify');
        const formData = new FormData(form);

        const data = {
            boardNo: parseInt(document.querySelector(".board-no").value),
            boardCtNo: parseInt(document.querySelector(".board-ct-no").value),
            boardTitle: document.querySelector(".board-title").value,
            boardContent: $('<div>').html($('#summernote').summernote('code')).text()
        }

        formData.append('boardDto', new Blob([JSON.stringify(data)], { type: 'application/json' }));

        const deleteFileNos = [];
        document.querySelectorAll('#existing-files .delete-file').forEach(button => {
            if (button.closest('li').style.display === 'none') {
                deleteFileNos.push(parseInt(button.getAttribute('data-file-id')));
            }
        });
        formData.append('deletedFileNo', new Blob([JSON.stringify(deleteFileNos)], { type: 'application/json' }));

        fetch('/api/board/v1', {
            method: "PUT",
            body: formData
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
            const boardNo = data.boardNo
            location.href =`/crispy/board-detail?boardNo=${boardNo}`
        }).catch(error => {
            console.error('Error:', error);
        });
    },

    deleteBoard: function() {
        const boardNo = document.querySelector(".board-no").value;
        const empNo = document.querySelector(".emp-no")?.value;

        fetch(`/api/board/${boardNo}/v1`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ boardNo: boardNo, empNo: empNo })
        }).then(response => response.json())
            .then(data => {
                alert(data.message);
                location.href = "/crispy/board-list";
            }).catch(error => {
            alert("삭제에 실패했습니다.");
        });
    },

    toggleLike: function() {
        const boardNo = document.querySelector(".board-no").value;

        fetch(`/api/board/${boardNo}/like/v1`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ boardNo: parseInt(boardNo) })
        }).then(response => response.json())
            .then(data => {
                location.reload();
            }).catch(error => {
            alert("좋아요 상태 변경에 실패했습니다.");
        });
    },

    createComment: function() {
        const boardNo = document.querySelector(".board-no").value;
        const cmtContent = document.getElementById("cmt-comment").value;
        const parentCmtNo = null;
        const data = {
            boardNo: parseInt(boardNo),
            cmtContent: cmtContent,
            parentCmtNo: parentCmtNo
        }

        fetch("/api/comments/v1", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
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
            location.reload()
        }).catch(error => {
            console.error('Error:', error);
        });
    },

    bindCommentEvents: function() {
        const cmtModify = document.querySelectorAll('.comment-modify');
        cmtModify.forEach(button => {
            button.addEventListener("click", function() {
                console.log(button)
                const cmtNo = this.getAttribute("data-comment-no");
                const commentDiv = this.closest(".comment-level-0, .comment-level-1"); // 댓글 블록 선택
                console.log(commentDiv);
                const originalContent = commentDiv.querySelector("p").innerText;
                const textarea = document.createElement("textarea");
                textarea.classList.add("form-control");
                textarea.value = originalContent;

                const saveButton = document.createElement("button");
                saveButton.type = "button";
                saveButton.classList.add("btn", "btn-primary");
                saveButton.innerText = "저장";
                saveButton.addEventListener("click", function() {
                    const updatedContent = textarea.value;
                    const data = {
                        boardNo: parseInt(document.querySelector(".board-no").value),
                        cmtNo: parseInt(cmtNo),
                        cmtContent: updatedContent
                    };
                    fetch(`/api/comments/v1`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
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
                        location.reload()
                        }).catch(error => {
                        alert("댓글 수정에 실패했습니다.");
                    });
                });

                commentDiv.innerHTML = '';
                commentDiv.appendChild(textarea);
                commentDiv.appendChild(saveButton);
            });
        });
        const cmtDelete = document.querySelectorAll('.comment-delete');
        cmtDelete.forEach(button => {
            button.addEventListener("click", function() {
                const cmtNo = this.getAttribute("data-comment-no");
                fetch(`/api/comments/${cmtNo}/v1`, {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" }
                }).then(response => response.json())
                    .then(data => {
                        alert(data.message);
                        location.reload();
                    }).catch(error => {
                    alert("댓글 삭제에 실패했습니다.");
                });
            });
        });
        const replyButton = document.querySelectorAll('#reply-button');
        replyButton.forEach(button => {
            button.addEventListener("click", function() {
                const replyContainer = this.closest('.reply-container');
                if (!replyContainer.querySelector('.reply-input')) {
                    const input = document.createElement('textarea');
                    input.classList.add('form-control', 'reply-input');
                    input.placeholder = '답글을 입력하세요';
                    input.rows = 2;

                    const submitButton = document.createElement('button');
                    submitButton.classList.add('btn', 'btn-primary', 'reply-submit');
                    submitButton.textContent = '등록';
                    submitButton.addEventListener("click", function() {
                        const parentCmtNo = replyContainer.querySelector('.cmt-no').value;
                        const cmtContent = input.value;
                        const data = {
                            boardNo: parseInt(document.querySelector(".board-no").value),
                            cmtContent: cmtContent,
                            parentCmtNo: parseInt(parentCmtNo)
                        }
                        fetch("/api/comments/v1", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(data)
                        }).then(response => {
                            if (!response.ok) {
                                return response.json().then(err => {
                                    const errorMessage = err.cmtContent || "답글 등록 실패";
                                    Swal.fire({
                                        icon: 'error',
                                        title: 'Error',
                                        text: errorMessage
                                    });
                                });
                            } else {
                                return response.json();
                            }
                        }).then(data => {
                            alert(data.message);
                            location.reload();
                        }).catch(error => {
                        });
                    });

                    replyContainer.appendChild(input);
                    replyContainer.appendChild(submitButton);
                }
            });
        });
    },

    bindFileDownloadEvents: function() {
        // 단일 파일 다운로드
        const fileDownload = document.querySelectorAll(".file-download");
        fileDownload.forEach(function(element) {
            element.addEventListener("click", function() {
                const boardFileNo = this.dataset.fileNo;
                window.location.href = `/api/board/file/download?boardFileNo=${boardFileNo}`;
            });
        });

        // 전체 파일 다운로드
        const downloadAll = document.getElementById("downloadAllBtn");
        downloadAll.addEventListener("click", function() {
            const boardNo = document.querySelector(".board-no").value;
            window.location.href = `/api/board/file/downloadAll?boardNo=${boardNo}`;
        });
    }
};

document.addEventListener("DOMContentLoaded", function() {
    board.init();
});
