const invite = {
    currentChatRoomNo: null,
    searchInput: document.getElementById("recipient"),
    resultsContainer: document.querySelector(".search-results"),
    inviteForm: document.getElementById("invite-form"),
    selectedUserIds: new Set(),
    selectedUserDetails: {},

    init: function () {
        this.setupEventListeners();
        this.loadUserList(); // 모달 열림과 동시에 사용자 목록을 불러옵니다.
    },

    setupEventListeners: function() {
        const inviteUserInput = document.getElementById("recipient");
        let debounceTimeout;

        inviteUserInput.addEventListener("input", (event) => {
            const searchTerm = event.target.value.trim();
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                if (searchTerm.length > 0) {
                    this.fetchUserList(searchTerm);
                } else {
                    this.loadUserList();
                }
            }, 300);
        });

        this.inviteForm.addEventListener('submit', (event) => {
            event.preventDefault();
            if (!this.currentChatRoomNo) {
                alert("채팅방을 선택하세요.");
                return;
            }

            if (this.selectedUserIds.size === 0) {
                alert("초대할 사용자를 선택하세요.");
                return;
            }

            // 선택된 모든 사용자를 초대
            this.selectedUserIds.forEach(empNo => {
                this.inviteUser(this.currentChatRoomNo, empNo);
            });
        });
    },

    loadUserList: function() {
        fetch(`/api/chat/inviteEmployee/${this.currentChatRoomNo}/v1`)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                this.displayUserList(data)
            })
            .catch(error => console.error('Error fetching user list:', error));
    },

    fetchUserList: function (searchTerm) {
        const data = {
            empName: searchTerm,
            posName: searchTerm,
            frnName: searchTerm,
        };
        fetch(`/api/chat/employees/search/v1`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(users => this.displayUserList(users))
            .catch(error => {
                console.error('Error searching users:', error);
                this.resultsContainer.textContent = '검색 중 오류가 발생했습니다.';
            });
    },

    displayUserList: function (users) {
        this.resultsContainer.innerHTML = '';

        let currentBranch = '';
        let branchDiv;

        users.forEach(user => {
            if (user.frnName !== currentBranch) {
                currentBranch = user.frnName;
                branchDiv = document.createElement('div');
                branchDiv.className = "branch-group";

                const branchHeader = document.createElement('h3');
                branchHeader.className = "branch-header";
                branchHeader.textContent = currentBranch;
                branchDiv.appendChild(branchHeader);

                this.resultsContainer.appendChild(branchDiv);
            }

            const listDiv = document.createElement('div');
            listDiv.className = "invite-info";

            const listImg = document.createElement("img");
            listImg.className = 'modal-profile-image';
            listImg.src = user.empProfile;

            const userDiv = document.createElement("div");
            userDiv.className = 'modal-user-div';

            const listItem = document.createElement('span');
            listItem.className = 'ms-3';
            listItem.textContent = `${user.empName} (${user.posName}) - ${user.frnName}`;
            listItem.dataset.userId = user.empNo;

            const listCheckbox = document.createElement("input");
            listCheckbox.type = "checkbox";
            listCheckbox.value = user.empNo;
            listCheckbox.checked = this.selectedUserIds.has(user.empNo.toString());
            listCheckbox.addEventListener('change', () => this.handleUserSelection(user, listCheckbox));

            userDiv.appendChild(listItem);
            userDiv.appendChild(listCheckbox);

            listDiv.appendChild(listImg);
            listDiv.appendChild(userDiv);
            branchDiv.appendChild(listDiv);
        });
    },

    handleUserSelection: function(user, checkbox) {
        console.log("Selected User:", user); // 선택된 user 객체 확인
        if (checkbox.checked) {
            this.selectedUserIds.add(user.empNo.toString());
            this.selectedUserDetails[user.empNo] = user;
        } else {
            console.log(user);
            this.selectedUserIds.delete(user.empNo.toString());
            delete this.selectedUserDetails[user.empNo];
        }
        this.updateSelectedUserList();
    },

    inviteUser: function (chatRoomNo, empNo) {
        const participant = {
            chatRoomNo: chatRoomNo,
            empNo: parseInt(empNo),
            entryStat: 0 // 기본 입장 상태
        };
        fetch(`/api/chat/rooms/${chatRoomNo}/invite/v1`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(participant)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to invite user');
                } else {
                    this.handleEntry(chatRoomNo, empNo);
                }
                return response.json();
            })
            .then(data => {
                alert(data.message);
                const inviteModal = bootstrap.Modal.getInstance(document.getElementById('invite-modal'));
                inviteModal.hide();
            })
            .catch(error => console.error('사용자 초대 중 오류 발생:', error));
    },

    handleEntry: function (chatRoomNo, empNo) {
        fetch(`/api/chat/rooms/${chatRoomNo}/entry/${empNo}/v2`, {
            method: "POST",
            body: JSON.stringify({ empNo: empNo })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to update exit record');
                }
            })
            .catch(error => console.error('Error updating exit record:', error));
    },
    updateSelectedUserList: function() {
        const selectedUserListDiv = document.getElementById('selectedUserLists');
        selectedUserListDiv.innerHTML = '';

        Object.values(this.selectedUserDetails).forEach(user => {
            console.log(user);
            const userDiv = document.createElement('div');
            userDiv.className = 'selected-user';

            const userNameSpan = document.createElement('span');
            userNameSpan.textContent = `${user.empName} (${user.posName})`;

            const removeCheckbox = document.createElement('input');
            removeCheckbox.type = 'checkbox';
            removeCheckbox.checked = true;
            removeCheckbox.addEventListener('change', () => {
                if (!removeCheckbox.checked) {
                    this.selectedUserIds.delete(user.empNo.toString());
                    delete this.selectedUserDetails[user.empNo];
                    this.synchronizeCheckboxes(user.empNo, false);
                    this.updateSelectedUserList();
                }
            });

            userDiv.appendChild(userNameSpan);
            userDiv.appendChild(removeCheckbox);
            selectedUserListDiv.appendChild(userDiv);
        });
    },
    synchronizeCheckboxes: function(empNo, isChecked) {
        const checkboxes = document.querySelectorAll(`input[type="checkbox"][value="${empNo}"]`);
        checkboxes.forEach(checkbox => checkbox.checked = isChecked);
    },
};

document.addEventListener("DOMContentLoaded", function () {
    const inviteModalElement = document.getElementById('invite-modal');
    inviteModalElement.addEventListener('show.bs.modal', function (event) {
        invite.currentChatRoomNo = message.currentChatRoomNo;
        invite.init();
    });
});
