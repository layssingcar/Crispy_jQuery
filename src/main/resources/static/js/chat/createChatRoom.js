const createChatRoom = {
    selectedUserIds: new Set(),
    selectedUserDetails: {},

    init: function() {
        this.setupEventListeners();
        this.loadUserList();
    },

    setupEventListeners: function() {
        const inviteUserInput = document.getElementById("create-inviteUser");
        let debounceTimeout;

        inviteUserInput.addEventListener("input", (event) => {
            const searchTerm = event.target.value.trim();
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                if (searchTerm.length > 0) {
                    this.searchAndDisplayUsers(searchTerm);
                } else {
                    this.loadUserList();
                }
            }, 300);
        });

        document.getElementById("createChatRoomForm").addEventListener("submit", (event) => this.handleCreateChatRoom(event));
    },

    loadUserList: function() {
        fetch(`/api/chat/others/v1`)
            .then(response => response.json())
            .then(data => this.displayUserList(data))
            .catch(error => console.error('Error fetching user list:', error));
    },

    searchAndDisplayUsers: function(searchTerm) {
        const data = {
            empName: searchTerm,
            posName: searchTerm,
            frnName: searchTerm,
        };

        fetch('/api/chat/employees/search/v1', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(users => this.displayUserList(users))
            .catch(error => {
                console.error('Error searching users:', error);
                document.getElementById('userList').textContent = '검색 중 오류가 발생했습니다.';
            });
    },

    displayUserList: function(users) {
        const userListDiv = document.getElementById('userList');
        userListDiv.innerHTML = '';

        const branchGroups = {};

        // 사용자 목록을 지점별로 그룹화
        users.forEach(user => {
            if (!branchGroups[user.frnName]) {
                branchGroups[user.frnName] = [];
            }
            branchGroups[user.frnName].push(user);
        });

        // 그룹화된 사용자 목록을 지점별로 표시
        for (const [branchName, branchUsers] of Object.entries(branchGroups)) {
            const branchDiv = document.createElement('div');
            branchDiv.className = "branch-group";

            const branchHeader = document.createElement('h3');
            branchHeader.className = "branch-header";
            branchHeader.textContent = branchName;
            branchDiv.appendChild(branchHeader);

            const list = document.createElement('ul');
            list.classList.add('list-group');
            branchDiv.appendChild(list);

            branchUsers.forEach(user => {

                const listDiv = document.createElement('div');
                listDiv.className = "invite-info";

                const listImg = document.createElement("img");
                listImg.className = 'modal-profile-image';
                listImg.src = user.empProfile || '/img/anonymous.png';

                const userDiv = document.createElement("div");
                userDiv.className = 'modal-user-div';

                const userNameSpan = document.createElement('span');
                userNameSpan.className = 'ms-3';
                userNameSpan.textContent = `${user.empName} (${user.posName}) - ${user.frnName}`;
                userNameSpan.dataset.userId = user.empNo;

                const listCheckbox = document.createElement("input");
                listCheckbox.type = "checkbox";
                listCheckbox.value = user.empNo;
                listCheckbox.checked = this.selectedUserIds.has(user.empNo.toString());
                listCheckbox.addEventListener('change', () => this.handleCheckboxChange(user, listCheckbox));

                userDiv.appendChild(userNameSpan);
                userDiv.appendChild(listCheckbox);

                listDiv.appendChild(listImg);
                listDiv.appendChild(userDiv);
                list.appendChild(listDiv);
            });

            userListDiv.appendChild(branchDiv);
        }

        if (users.length === 0) {
            userListDiv.textContent = '검색 결과가 없습니다.';
        }

        this.updateSelectedUserList();
    },


    handleCheckboxChange: function(user, checkbox) {
        if (checkbox.checked) {
            this.selectedUserIds.add(user.empNo.toString());
            this.selectedUserDetails[user.empNo] = user;
        } else {
            this.selectedUserIds.delete(user.empNo.toString());
            delete this.selectedUserDetails[user.empNo];
        }
        this.updateSelectedUserList();
    },

    updateSelectedUserList: function() {
        const selectedUserListDiv = document.getElementById('selectedUserList');
        selectedUserListDiv.innerHTML = '';
        Object.values(this.selectedUserDetails).forEach(user => {
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

    handleCreateChatRoom: function(event) {
        event.preventDefault();
        const chatRoomTitle = document.getElementById("chatRoomTitle").value;
        const creatorEmpNo = currentEmpNo;
        const selectedUsers = Array.from(this.selectedUserIds);

        const chatRoom = {
            chatRoomTitle,
            creator: creatorEmpNo,
            participants: selectedUsers.map(empNo => ({ empNo }))
        };

        fetch(`/api/chat/rooms/${creatorEmpNo}/v1`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(chatRoom)
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                location.reload();
                const createChatRoomModal = bootstrap.Modal.getInstance(document.getElementById('createChatRoomModal'));
                createChatRoomModal.hide();
                this.loadUserList();
            })
            .catch(error => {
                console.error('Error creating chat room:', error);
                alert('Failed to create chat room.');
            });
    }
};

document.addEventListener("DOMContentLoaded", function() {
    createChatRoom.init();
});