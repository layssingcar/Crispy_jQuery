const franchiseList = {
    currentPage: 1,
    "frnName": "",

    init: function() {
        this.loadCurrentPage()
        this.bindEvents();
        this.loadFrnData(this.currentPage, this.frnName);
        this.clearCurrentPage();
        this.clearSelectedEmpNo();
        this.searchAction();
    },

    loadCurrentPage: function () {
        const savedPage = sessionStorage.getItem("currentPage");
        if (savedPage) {
            this.currentPage = savedPage;
        }
    },

    saveCurrentPage: function () {
        sessionStorage.setItem("currentPage", this.currentPage);
    },

    bindEvents: function() {
        const selectAllCheckbox = document.querySelector('th input[type=checkbox]');
        const deleteSelectedButton = document.querySelector('.btn-delete-selected');

        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener("change", franchiseList.toggleSelectAll.bind(this));
        }
        if (deleteSelectedButton) {
            deleteSelectedButton.addEventListener("click", this.confirmDeleteSelectedFrns.bind(this))
        }

        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.addEventListener('input', this.handleSearchInput.bind(this));
        }

        // 페이지네이션 이벤트 바인딩
        const paginationLinks = document.querySelectorAll('.pagination .page-link');
        paginationLinks.forEach(link => {
            link.addEventListener('click', this.handlePageChange.bind(this));
        });
    },
    toggleSelectAll: function(event) {
        const isChecked = event.target.checked;
        const checkboxes = document.querySelectorAll('input[name=frnCheckbox]');
        checkboxes.forEach((checkbox) => {
            checkbox.checked = isChecked;
        })
    },

    handlePageChange: function(event) {
        event.preventDefault();
        const page = event.target.dataset.page;

        if (page === "prev") {
            this.currentPage = Math.max(0, this.currentPage - 1);
        } else if (page === "next") {
            this.currentPage += 1;
        } else {
            this.currentPage = parseInt(page);
        }

        this.saveCurrentPage();
        this.loadFrnData(this.currentPage, this.frnName);
    },

    handleSearchInput: function(event) {
        this.frnName = event.target.value.trim();
        this.currentPage = 1;
        this.loadFrnData(this.currentPage, this.frnName);
    },


    loadFrnData: function(page, frnName) {
        const url = new URL('/api/franchise/franchises/v1', window.location.origin);
        url.searchParams.append('page', page);
        if (frnName) {
            url.searchParams.append('search', frnName);
        }

        fetch(url.toString())
            .then(response => response.json())
            .then(data => {
                this.renderFrnTable(data.items);
                this.updatePagination(data);
            })
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

    formatTelNumber(telNumber) {
        if (telNumber.length === 8) { // 1234-5678
            return telNumber.replace(/(\d{4})(\d{4})/, '$1-$2');
        } else if (telNumber.length === 9) { // 02-123-5678
            return telNumber.replace(/(\d{2})(\d{3})(\d{4})/, '$1-$2-$3');
        } else if (telNumber.length === 11) { // 010-1234-5678
            return telNumber.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        } else {
            return telNumber; // 형식에 맞지 않으면 원본 반환
        }
    },

    appendFranchiseRow: function(tr, franchise) {
        const frnJoinDt = formatDate(franchise.frnJoinDt);
        const formattedTel = this.formatTelNumber(franchise.frnTel);
        console.log(formattedTel);
        tr.appendChild(this.createCell('checkbox', franchise.frnNo));
        tr.appendChild(this.createCell('text', franchise.frnName));
        tr.appendChild(this.createCell('text', franchise.frnOwner));
        tr.appendChild(this.createCell('text', franchise.frnStreet));
        tr.appendChild(this.createCell('text', formattedTel));
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
            sessionStorage.setItem('selectedFrnNo', frnNo);
            window.location.href = '/crispy/franchise';
        } else {
            console.error('frnNo is undefined');
        }
    },

    deleteFranchise: function(frnNo) {
        fetch(`/api/franchise/${frnNo}/v1`, {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
        }).then(response => {
            if (response.ok) {
                alert("삭제되었습니다.");
                this.loadFrnData(this.currentPage, this.pageSize);
            } else {
                return response.json().then(data => {
                    throw new Error(data.message);
                });
            }
        }).catch(error => {
            alert("삭제 중 오류가 발생했습니다.");
        });
    },

    deleteSelectedFrns: function () {
        const checkboxes = document.querySelectorAll('input[name="frnCheckbox"]:checked');
        const frnNos = Array.from(checkboxes)
            .map(checkbox => checkbox.id)
            .filter(frnNo => frnNo.trim() !== "");
        if (frnNos.length === 0) {
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
                this.loadFrnData(this.currentPage);
            } else {
                return response.json().then(data => {
                    throw new Error(data.message);
                });
            }
        }).catch(error => {
            alert("삭제 중 오류가 발생했습니다.");
        });
    },

    clearCurrentPage: function () {
        sessionStorage.removeItem('currentPage');
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
    },

    updatePagination: function(data) {
        const paginationContainer = document.querySelector('.pagination');
        paginationContainer.innerHTML = ''; // Clear existing pagination

        // 이전 페이지
        const prevPageItem = document.createElement('li');
        prevPageItem.classList.add('page-item');
        if (data.prevPage > 0) {
            const prevPageLink = document.createElement('a');
            prevPageLink.classList.add('page-link');
            prevPageLink.href = '#';
            prevPageLink.dataset.page = 'prev';
            prevPageLink.textContent = '<<';
            prevPageLink.addEventListener('click', this.handlePageChange.bind(this));
            prevPageItem.appendChild(prevPageLink);
        } else {
            prevPageItem.classList.add('disabled');
            const prevPageSpan = document.createElement('span');
            prevPageSpan.classList.add('page-link');
            prevPageSpan.textContent = '<<';
            prevPageItem.appendChild(prevPageSpan);
        }
        paginationContainer.appendChild(prevPageItem);

        for (let i = data.startPage; i <= data.endPage; i++) {

            const pageItem = document.createElement('li');
            pageItem.classList.add('page-item');
            if (i === data.currentPage) {
                pageItem.classList.add('active');
            }
            const pageLink = document.createElement('a');
            pageLink.classList.add('page-link');
            pageLink.href = '#';
            pageLink.dataset.page = i;
            pageLink.textContent = i;
            pageLink.addEventListener('click', this.handlePageChange.bind(this));
            pageItem.appendChild(pageLink);
            paginationContainer.appendChild(pageItem);
        }

        // 다음 페이지
        const nextPageItem = document.createElement('li');
        nextPageItem.classList.add('page-item');
        if (data.currentPage < data.endPage) {
            const nextPageLink = document.createElement('a');
            nextPageLink.classList.add('page-link');
            nextPageLink.href = '#';
            nextPageLink.dataset.page = 'next';
            nextPageLink.textContent = '>>';
            nextPageLink.addEventListener('click', this.handlePageChange.bind(this));
            nextPageItem.appendChild(nextPageLink);
        } else {
            nextPageItem.classList.add('disabled');
            const nextPageSpan = document.createElement('span');
            nextPageSpan.classList.add('page-link');
            nextPageSpan.textContent = '>>';
            nextPageItem.appendChild(nextPageSpan);
        }
        paginationContainer.appendChild(nextPageItem);
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
