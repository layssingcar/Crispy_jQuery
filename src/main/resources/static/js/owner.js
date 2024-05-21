const owner = {
    init: function() {
        this.bindEvents();
        this.loadEmployeeData();
        this.clearSelectedEmpNo();
        this.searchAction();
        this.setupClickableRows();
    },

    bindEvents: function() {
        const myProfileElement = document.getElementById('my-profile');
        const resetPasswordButton = document.getElementById('btn-reset-password');
        if (myProfileElement) {
            myProfileElement.addEventListener('click', this.clearSelectedEmpNo.bind(this));
        }
        if (resetPasswordButton) {
            resetPasswordButton.addEventListener('click', () => this.resetPassword());
        }
    },

    resetPassword: function() {
        const email = document.getElementById("emp-profile-empEmail").value;
        fetch("/api/v1/employee/resetPassword", {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({empEmail: email})
        }).then(response => response.ok)
            .catch(error => console.error("Error:", error));
    },

    loadEmployeeData: function() {
        const frnNo = document.getElementById("employees-frnNo").value;
        fetch(`/api/v1/owner/employees/${frnNo}`)
            .then(response => response.json())
            .then(data => this.renderEmployeeTable(data))
            .catch(error => console.error('Error loading employee data:', error));
    },

    renderEmployeeTable: function(employees) {
        const table = document.querySelector('.table tbody');
        table.innerHTML = '';
        employees.forEach(employee => {
            const tr = document.createElement('tr');
            tr.dataset.empNo = employee.empNo; // 이용하여 함수 호출 시 사용
            this.appendEmployeeRow(tr, employee);
            table.appendChild(tr);
        });
        this.setupClickableRows();
    },

    appendEmployeeRow: function(tr, employee) {
        tr.appendChild(this.createCell('checkbox', employee.empNo));
        tr.appendChild(this.createCell('text', employee.empNo));
        tr.appendChild(this.createCell('text', employee.empName));
        tr.appendChild(this.createCell('text', employee.empId));
        tr.appendChild(this.createCell('text', employee.frnName));
        tr.appendChild(this.createCell('text', employee.posName));
        tr.appendChild(this.createCell('text', employee.empPhone || 'N/A'));
        tr.appendChild(this.createCell('text', employee.empStat));
        tr.appendChild(this.createActionsCell(employee.empNo));
    },

    createCell: function(type, value) {
        const td = document.createElement('td');
        if (type === 'checkbox') {
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.name = 'employeeCheckbox';
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

    createActionsCell: function(empNo) {
        const cell = document.createElement('td');
        const editIcon = document.createElement('i');
        editIcon.className = "fa-solid fa-pen-to-square";
        const editLink = document.createElement('a');
        editLink.href = '#';
        editLink.role = 'button';
        editLink.onclick = () => this.editEmployee(empNo);
        editLink.appendChild(editIcon);
        cell.appendChild(editLink);

        const removeIcon = document.createElement('i');
        removeIcon.className = "fa-solid fa-user-slash";
        const deleteButton = document.createElement('a');
        deleteButton.href = '#';
        deleteButton.role = 'button';
        deleteButton.onclick = () => this.deleteEmployee(empNo);
        deleteButton.appendChild(removeIcon);
        cell.appendChild(deleteButton);

        return cell;
    },

    setupClickableRows: function() {
        document.querySelectorAll('.table tbody tr').forEach(row => {
            row.addEventListener('click', (event) => {
                if (event.target.tagName.toLowerCase() !== 'input' && event.target.tagName.toLowerCase() !== 'a' && event.target.tagName.toLowerCase() !== 'i') {
                    this.editEmployee(row.dataset.empNo);
                }
            });
        });
    },

    editEmployee: function(empNo) {
        console.log('Edit Employee:', empNo);
        sessionStorage.setItem('selectedEmpNo', empNo);
        window.location.href = '/crispy/owner/employee';
    },

    deleteEmployee: function(empNo) {
        console.log('Delete Employee:', empNo);
        // Implement deletion logic here
    },

    clearSelectedEmpNo: function() {
        sessionStorage.removeItem('selectedEmpNo');
    },

    searchAction: function() {
        const searchInput = document.querySelector('.search-input');
        const searchIcon = document.querySelector('.search-icon');
        searchInput.addEventListener('input', function() {
            searchIcon.style.display = searchInput.value.trim() !== '' ? 'none' : 'inline';
        });
    }
};

document.addEventListener("DOMContentLoaded", function() {
    owner.init();
});
