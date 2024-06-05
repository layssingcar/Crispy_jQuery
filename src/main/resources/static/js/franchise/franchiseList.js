const franchiseList = {
    init: function() {
        this.bindEvents();
        this.loadFrnData();
        this.clearSelectedEmpNo();
        this.searchAction();
    },

    bindEvents: function() {
        const selectAllCheckbox = document.querySelector('th input[type=checkbox]');
        const deleteSelectedButton = document.querySelector('.btn-delete-selected');

        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener("change", this.toggleSelectAll.bind(franchiseList));
        }
        if (deleteSelectedButton) {
            deleteSelectedButton.addEventListener("click", this.confirmDeleteSelectedFrns.bind(this))
        }
    },

    toggleSelectAll: function(event) {
        const isChecked = event.target.checked;
        console.log(isChecked);
        const checkboxes = document.querySelectorAll('input[name=frnCheckbox]');
        checkboxes.forEach((checkbox) => {
            checkbox.checked = isChecked;
            console.log(checkbox.value);
        })
    },

    loadFrnData: function() {
        fetch(`/api/franchise/franchises/v1`)
            .then(response => response.json())
            .then(data => this.renderFrnTable(data))
            .catch(error => console.error('Error loading franchise data:', error));
    },

    renderFrnTable: function(franchiseList) {
        const table = document.querySelector('.table tbody');
        table.innerHTML = '';
        franchiseList.forEach(frn => {
            const tr = document.createElement('tr');
            tr.dataset.frnNo = frn.frnNo;
            this.appendFranchiseRow(tr, frn);
            table.appendChild(tr);
        });
        this.setupClickableRows();
    },

    appendFranchiseRow: function(tr, franchise) {
        const frnJoinDt = formatDate(franchise.frnJoinDt)
        tr.appendChild(this.createCell('checkbox', franchise.frnNo));
        tr.appendChild(this.createCell('text', franchise.frnName));
        tr.appendChild(this.createCell('text', franchise.frnOwner));
        tr.appendChild(this.createCell('text', franchise.frnStreet));
        tr.appendChild(this.createCell('text', franchise.frnTel));
        tr.appendChild(this.createCell('text', frnJoinDt));
        tr.appendChild(this.createActionsCell(franchise.frnNo));
    },

    createCell: function(type, value) {
        const td = document.createElement('td');
        if (type === 'checkbox') {
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.name = 'frnCheckbox';
            checkbox.id = value;
            console.log(checkbox)
            checkbox.addEventListener('change', function() {
                console.log(this.checked ? 'Checked' : 'Unchecked', 'Checkbox ID:', this.id);
            });
            td.appendChild(checkbox);
        } else {
            td.textContent = value;
        }
        return td;
    },

    createActionsCell: function(frnNo) {
        const cell = document.createElement('td');
        const editIcon = document.createElement('i');
        editIcon.className = "fa-solid fa-pen-to-square";
        const editLink = document.createElement('a');
        editLink.href = '#';
        editLink.role = 'button';
        editLink.onclick = (event) => {
            event.stopPropagation();
            this.editFranchise(frnNo);
        };
        editLink.appendChild(editIcon);
        cell.appendChild(editLink);

        const removeIcon = document.createElement('i');
        removeIcon.className = "fa-solid fa-user-slash";
        const deleteButton = document.createElement('a');
        deleteButton.href = '#';
        deleteButton.role = 'button';
        deleteButton.onclick = (event) => {
            event.stopPropagation();
            this.confirmDeleteFrn(frnNo);
        }
        deleteButton.appendChild(removeIcon);
        cell.appendChild(deleteButton);

        return cell;
    },

    confirmDeleteFrn: function (frnNo) {
        const confirmed = confirm("정말로 삭제하시겠습니까?");
        if (confirmed) {
            this.deleteFranchise(frnNo);
        }
    },

    confirmDeleteSelectedFrns: function () {
        const confirmed = confirm("정말로 삭제하시겠습니까?");
        if (confirmed) {
            this.deleteSelectedFrns();
        }
    },

    setupClickableRows: function() {
        document.querySelectorAll('.table tbody tr').forEach(row => {
            row.addEventListener('click', (event) => {
                if (!['input', 'a', 'i'].includes(event.target.tagName.toLowerCase())) {
                    this.editFranchise(row.dataset.frnNo);
                }
            });
        });
    },

    editFranchise: function(frnNo) {
        if (frnNo) {
            console.log('Edit Franchise:', frnNo);
            sessionStorage.setItem('selectedFrnNo', frnNo);
            window.location.href = '/crispy/franchise';
        } else {
            console.error('frnNo is undefined');
        }
    },

    deleteFranchise: function(frnNo) {
        console.log('Delete Franchise:', frnNo);
        fetch(`/api/franchise/${frnNo}/v1`, {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
        }).then(response => {
            if (response.ok) {
                alert("삭제되었습니다.")
                this.loadFrnData();
            } else {
                return response.json().then(data => {
                    throw new Error(data.message);
                })
            }
        }).catch(error => {
            console.log(error);
            alert("삭제 중 오류가 발생했습니다.");
        })
    },
    deleteSelectedFrns: function () {
        const checkboxes = document.querySelectorAll('input[name="frnCheckbox"]:checked');
        console.log(checkboxes.length)
        const frnNos = Array.from(checkboxes)
            .map(checkbox => checkbox.id)
            .filter(frnNo => frnNo.trim() !== "");
        console.log(frnNos);
        if(frnNos.length === 0) {
            alert("삭제할 가맹점을 선택해주세요.");
            return;
        }

        fetch(`/api/franchise/franchises/v1`, {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(frnNos)
        }).then(response => {
            if (response.ok) {
                alert("삭제되었습니다.");
                this.loadFrnData();
            } else {
                return response.json().then(data => {
                    throw new Error(data.message);
                });
            }
        }).catch(error => {
            console.log(error);
            alert("삭제 중 오류가 발생했습니다.");
        })
    },

    clearSelectedEmpNo: function() {
        sessionStorage.removeItem('selectedFrnNo');
    },

    searchAction: function() {
        const searchInput = document.querySelector('.search-input');
        const searchIcon = document.querySelector('.search-icon');
        searchInput?.addEventListener('input', function() {
            searchIcon.style.display = searchInput.value.trim() !== '' ? 'none' : 'inline';
        });
    }
};

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit"
    });
}

document.addEventListener("DOMContentLoaded", function() {
    franchiseList.init();
});
