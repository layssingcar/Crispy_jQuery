const owner = {
    init: function() {
        this.bindEvents();
        this.loadEmployeeData();
        this.clearSelectedEmpNo();
        this.searchAction();
    },

    bindEvents: function() {
        const myProfileElement = document.getElementById('my-profile');
        if (myProfileElement) {
            myProfileElement.addEventListener('click', this.clearSelectedEmpNo.bind(this));
        }
        const resetPasswordButton = document.getElementById('btn-reset-password');
        if (resetPasswordButton) {
            resetPasswordButton.addEventListener('click', () => this.resetPassword());
        }
    },

    resetPassword: function () {
        const email = document.getElementById("emp-profile-empEmail").value;
        fetch("/api/v1/employee/resetPassword", {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({empEmail : email})
        }).then(response => response.ok)
            .catch(error => console.error("Error:", error))
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
            this.appendEmployeeRow(tr, employee);
            table.appendChild(tr);
        });
    },

    appendEmployeeRow: function(tr, employee) {
        const checkboxCell = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.name = 'employeeCheckbox'; // 선택적으로 이름도 할당 가능
        checkbox.id = `${employee.empNo}`;
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                console.log('Checked Checkbox ID:', this.id);
            } else {
                console.log('Unchecked Checkbox ID:', this.id);
            }
        });
        checkboxCell.appendChild(checkbox);
        console.log(checkbox)
        tr.appendChild(checkboxCell);

        tr.appendChild(this.createCell(employee.empNo));
        tr.appendChild(this.createClickableCell(employee.empName, () => this.editEmployee(employee.empNo)));
        tr.appendChild(this.createCell(employee.empId));
        tr.appendChild(this.createCell(employee.frnName));
        tr.appendChild(this.createCell(employee.posName));
        tr.appendChild(this.createCell(employee.empPhone || 'N/A'));
        tr.appendChild(this.createCell(employee.empStat));
        tr.appendChild(this.createActionsCell(employee.empNo));
    },

    createCell: function(text) {
        const cell = document.createElement('td');
        cell.textContent = text;
        return cell;
    },

    createClickableCell: function(text, onClickFunction) {
        const cell = this.createCell(text);
        cell.classList.add('clickable');
        cell.onclick = onClickFunction;
        return cell;
    },

    createActionsCell: function(empNo) {
        const editIcon = document.createElement('i');
        editIcon.className = "fa-solid fa-pen-to-square"

        const cell = document.createElement('td');
        const editLink = document.createElement('a');
        editLink.href = '#';
        editLink.role = 'button';
        editLink.onclick = () => this.editEmployee(empNo);
        editLink.appendChild(editIcon);
        cell.appendChild(editLink);

        const removeIcon = document.createElement('i');
        removeIcon.className = "fa-solid fa-user-slash"
        const deleteButton = document.createElement('a');
        deleteButton.href = '#';
        deleteButton.role = 'button';
        deleteButton.onclick = () => this.deleteEmployee(empNo);
        deleteButton.appendChild(removeIcon);
        cell.appendChild(deleteButton);

        return cell;
    },

    editEmployee: function(empNo) {
        console.log('Edit:', empNo);
        sessionStorage.setItem('selectedEmpNo', empNo);
        window.location.href = '/crispy/owner/employee';
    },

    deleteEmployee: function(empNo) {
        console.log('Delete:', empNo);
        // Implement deletion logic here
    },

    clearSelectedEmpNo: function() {
        sessionStorage.removeItem('selectedEmpNo');
    },
    searchAction: function () {
        const searchInput = document.querySelector('.search-input');
        const searchIcon = document.querySelector('.search-icon');

        // input 이벤트 핸들러 설정
        searchInput.addEventListener('input', function() {
            // 입력 필드가 비어있지 않으면 아이콘 숨기기
            if (searchInput.value.trim() !== '') {
                searchIcon.style.display = 'none';
            } else {
                // 입력 필드가 비어있으면 아이콘 다시 보여주기
                searchIcon.style.display = 'inline';
            }
        });
    }

};

document.addEventListener("DOMContentLoaded", function() {
    owner.init();
});