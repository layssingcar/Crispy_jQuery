const franchiseList = {
    init: function() {
        this.loadFrnData();
        this.clearSelectedEmpNo();
        this.searchAction();
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
            checkbox.name = 'franchiseCheckbox';
            checkbox.id = value;
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
        cell.appendChild(this.createActionLink(frnNo, "fa-pen-to-square", this.editFranchise));
        cell.appendChild(this.createActionLink(frnNo, "fa-user-slash", this.deleteFranchise));
        return cell;
    },

    createActionLink: function(frnNo, iconClass, action) {
        const icon = document.createElement('i');
        icon.className = `fa-solid ${iconClass}`;
        const link = document.createElement('a');
        link.href = '#';
        link.role = 'button';
        link.onclick = () => action(frnNo);
        link.appendChild(icon);
        return link;
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
        // Implement deletion logic here
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
