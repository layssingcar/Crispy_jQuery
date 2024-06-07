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
        const resetPasswordButton = document.querySelector('.btn-reset-password');
        const selectAllCheckbox = document.querySelector('th input[type=checkbox]');
        const deleteSelectedButton = document.getElementById('btn-delete-selected');
        const searchRole = document.getElementById('search-role');
        const searchStatus = document.getElementById('search-status');

        if (myProfileElement) {
            myProfileElement.addEventListener('click', this.clearSelectedEmpNo.bind(this));
        }
        if (resetPasswordButton) {
            resetPasswordButton.addEventListener('click', () => this.resetPassword());
        }
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener("change", this.toggleSelectAll.bind(this));
        }
        if (deleteSelectedButton) {
            deleteSelectedButton.addEventListener("click", this.confirmDeleteSelectedEmployees.bind(this))
        }
        if (searchRole) {
            searchRole.addEventListener("change", this.searchEmployees.bind(this));
        }
        if (searchStatus) {
            searchStatus.addEventListener("change", this.searchEmployees.bind(this));
        }
    },

    resetSelectConditions: function() {
        document.getElementById("search-role").value = "";
        document.getElementById("search-status").value = "";
    },

    searchEmployees: function () {
        const position = document.getElementById("search-role").value;
        const empStat = document.getElementById("search-status").value;
        const frnNo = document.getElementById("employees-frnNo").value;
        console.log(frnNo);

        fetch(`/api/owner/employees/${frnNo}/v1?position=${position}&empStat=${empStat}`)
            .then(response => response.json())
            .then(data => this.renderEmployeeTable(data))
            .catch(error => {});
    },


    toggleSelectAll: function(event) {
        const isChecked = event.target.checked;
        const checkboxes = document.querySelectorAll('input[name=employee-checkbox]');
        checkboxes.forEach((checkbox) => {
            checkbox.checked = isChecked;
        })
    },

    resetPassword: function() {
        const email = document.getElementById("emp-profile-empEmail").value;
        const empName = document.querySelector(".empName").value;
        Auth.authenticatedFetch("/api/employee/password/reset/v1", {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                empName: empName,
                empEmail: email})
        }).then(response => response.json())
            .then(data => {
                alert(data.message)
            })
            .catch(error => console.error("Error:", error));
    },

    loadEmployeeData: function() {
        const frnNo = document.getElementById("employees-frnNo").value;
        fetch(`/api/owner/employees/${frnNo}/v1`)
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
        tr.appendChild(this.createCell('text', employee.empPhone));
        tr.appendChild(this.createCell('text', employee.empStat));
        tr.appendChild(this.createActionsCell(employee.empNo));
    },

    createCell: function(type, value) {
        const td = document.createElement('td');
        if (type === 'checkbox') {
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.name = 'employee-checkbox';
            checkbox.id = value;
            console.log(checkbox);
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
        editLink.onclick = (event) => {
            event.stopPropagation();
            this.editEmployee(empNo);
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
            this.confirmDeleteEmployee(empNo);
        }
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
        window.location.href = '/crispy/owner/employeeDetail';
    },

    deleteEmployee: function(empNo) {
        console.log('Delete Employee:', empNo);
        fetch(`/api/owner/employee/${empNo}/v1`, {
            method: 'DELETE',
        }).then(response => {
            if (response.ok) {
                alert('삭제되었습니다.')
                this.loadEmployeeData();
            } else {
                return response.json().then(data => {
                    throw new Error(data.message);
                })
            }
        }).catch(error => {
            console.log(error);
            alert("삭제 중 오류가 발생했습니다.")
        });
    },

    deleteSelectedEmployees: function () {
      const checkboxes = document.querySelectorAll('input[name="employee-checkbox"]:checked');
      console.log(checkboxes.length);
      const empNos = Array.from(checkboxes)
                                .map(checkbox => checkbox.id)
                                .filter(empNo => empNo.trim() !== "");

      if(empNos.length === 0) {
          alert("삭제할 직원을 선택해주세요.");
          return;
      }

      fetch(`/api/owner/employees/v1`, {
          method: 'DELETE',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify(empNos)
      }).then(response => {
          if (response.ok) {
              alert("삭제되었습니다.");
              this.loadEmployeeData();
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

    confirmDeleteEmployee: function(empNo) {
        const confirmed = confirm('정말로 삭제하시겠습니까?');
        if (confirmed) {
            this.deleteEmployee(empNo);
        }
    },
    confirmDeleteSelectedEmployees: function() {
        const confirmed = confirm('선택한 직원들을 정말로 삭제하시겠습니까?');
        if (confirmed) {
            this.deleteSelectedEmployees();
        }
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
window.addEventListener('pageshow', function() {
    owner.resetSelectConditions();
});