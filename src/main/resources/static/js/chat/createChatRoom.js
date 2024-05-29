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
        fetch(`/api/chat/employees/v1`)
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
        const list = document.createElement('ul');
        list.classList.add('list-group');

        users.forEach(user => {
            const listDiv = document.createElement('div');
            listDiv.className = "invite-info";
            const listImg = document.createElement("img");
            listImg.className = 'modal-profile-image';
            listImg.src = user.empProfile;
            const listItem = document.createElement('span');
            listItem.className = 'ms-3';
            listItem.textContent = `${user.empName} (${user.posName}) - ${user.frnName}`;
            listItem.dataset.userId = user.empNo;
            const listCheckbox = document.createElement("input");
            listCheckbox.type = "checkbox";
            listCheckbox.value = user.empNo;
            listCheckbox.checked = this.selectedUserIds.has(user.empNo.toString());
            listCheckbox.addEventListener('change', () => this.handleCheckboxChange(user, listCheckbox));

            listDiv.appendChild(listImg);
            listDiv.appendChild(listItem);
            listDiv.appendChild(listCheckbox);
            list.appendChild(listDiv);
        });

        if (users.length === 0) {
            userListDiv.textContent = '검색 결과가 없습니다.';
        } else {
            userListDiv.appendChild(list);
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