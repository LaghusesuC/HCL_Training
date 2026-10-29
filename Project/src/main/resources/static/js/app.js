/**
 * Enterprise Payroll Management System - SPA Client Logic
 */

const API_BASE = '/api/v1';

let state = {
  token: localStorage.getItem('payroll_jwt') || null,
  user: JSON.parse(localStorage.getItem('payroll_user') || 'null'),
  currentTab: 'dashboard',
  selectedRunId: null,
  selectedRunStatus: null,
  allEmployees: [],
  allDepartments: [],
  allStructures: [],
  allComponents: []
};

// ============================================================================
// INITIALIZATION & AUTHENTICATION
// ============================================================================
document.addEventListener('DOMContentLoaded', async () => {
  initTheme();
  
  if (!state.token || !state.user) {
    // Auto-login as demo Admin by default
    await quickLogin('admin', 'admin123');
  } else {
    updateUserUI();
    switchTab(state.currentTab);
  }
});

function initTheme() {
  const savedTheme = localStorage.getItem('payroll_theme') || 'dark';
  document.documentElement.setAttribute('data-theme', savedTheme);
  updateThemeButton(savedTheme);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme');
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('payroll_theme', next);
  updateThemeButton(next);
}

function updateThemeButton(theme) {
  const btn = document.getElementById('theme-btn');
  if (btn) btn.textContent = theme === 'dark' ? '🌙' : '☀️';
}

async function quickLogin(username, password) {
  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
      throw new Error('Authentication failed');
    }

    const data = await res.json();
    state.token = data.token;
    state.user = data;
    localStorage.setItem('payroll_jwt', data.token);
    localStorage.setItem('payroll_user', JSON.stringify(data));

    updateUserUI();
    showToast(`Signed in as ${data.fullName} (${data.role})`, 'success');

    // Switch view appropriately
    if (data.role === 'ROLE_EMPLOYEE') {
      switchTab('payslips');
    } else {
      switchTab('dashboard');
    }
  } catch (err) {
    showToast('Failed to sign in: ' + err.message, 'error');
  }
}

function logout() {
  localStorage.removeItem('payroll_jwt');
  localStorage.removeItem('payroll_user');
  state.token = null;
  state.user = null;
  showToast('Logged out successfully', 'info');
  quickLogin('admin', 'admin123');
}

function updateUserUI() {
  if (!state.user) return;

  const displayNameEl = document.getElementById('user-display-name');
  const roleLabelEl = document.getElementById('user-role-label');
  const avatarEl = document.getElementById('user-avatar');

  if (displayNameEl) displayNameEl.textContent = state.user.fullName || state.user.username;
  if (roleLabelEl) roleLabelEl.textContent = state.user.role;
  if (avatarEl) avatarEl.textContent = (state.user.fullName || state.user.username).charAt(0).toUpperCase();

  // Update quick picker pill styling
  document.querySelectorAll('.role-chip').forEach(c => c.classList.remove('active'));
  if (state.user.username === 'admin') document.getElementById('chip-admin')?.classList.add('active');
  else if (state.user.username === 'officer') document.getElementById('chip-officer')?.classList.add('active');
  else if (state.user.username === 'emp001') document.getElementById('chip-emp1')?.classList.add('active');
  else if (state.user.username === 'emp002') document.getElementById('chip-emp2')?.classList.add('active');
}

// ============================================================================
// API UTILITY
// ============================================================================
async function api(endpoint, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(state.token ? { 'Authorization': `Bearer ${state.token}` } : {}),
    ...(options.headers || {})
  };

  const res = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers
  });

  if (res.status === 401) {
    showToast('Session expired, logging in as Admin', 'error');
    await quickLogin('admin', 'admin123');
    return null;
  }

  if (!res.ok) {
    const errorData = await res.json().catch(() => ({}));
    const msg = errorData.message || errorData.error || `HTTP error ${res.status}`;
    throw new Error(msg);
  }

  if (options.isBlob) {
    return res.blob();
  }

  const contentType = res.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return res.json();
  }
  return null;
}

// ============================================================================
// TAB NAVIGATION
// ============================================================================
function switchTab(tabId) {
  state.currentTab = tabId;

  document.querySelectorAll('.nav-item').forEach(item => {
    item.classList.toggle('active', item.getAttribute('data-tab') === tabId);
  });

  document.querySelectorAll('.content-view').forEach(view => {
    view.style.display = 'none';
  });

  const activeView = document.getElementById(`view-${tabId}`);
  if (activeView) {
    activeView.style.display = 'block';
  }

  // Load relevant data
  switch (tabId) {
    case 'dashboard': loadDashboard(); break;
    case 'employees': loadEmployees(); break;
    case 'structures': loadStructuresAndComponents(); break;
    case 'attendance': loadAttendance(); break;
    case 'payroll': loadPayrollRuns(); break;
    case 'payslips': loadPayslips(); break;
    case 'reports': loadReports(); break;
  }
}

// ============================================================================
// DASHBOARD
// ============================================================================
async function loadDashboard() {
  try {
    const stats = await api('/reports/dashboard-stats');
    if (!stats) return;

    document.getElementById('kpi-headcount').textContent = stats.activeEmployees;
    document.getElementById('kpi-net-pay').textContent = formatCurrency(stats.lastMonthNet);
    document.getElementById('kpi-latest-month').textContent = `Month: ${stats.latestMonth || 'N/A'}`;
    document.getElementById('kpi-ytd-cost').textContent = formatCurrency(stats.ytdPayrollCost);
    document.getElementById('kpi-runs-count').textContent = stats.totalPayrollRuns;
    document.getElementById('kpi-locked-status').textContent = `${stats.lockedRuns} Locked / ${stats.totalPayrollRuns} Total`;

    const tbody = document.getElementById('dashboard-dept-tbody');
    tbody.innerHTML = '';

    if (!stats.departmentBreakdown || stats.departmentBreakdown.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 20px;">No payroll data recorded yet. Run a payroll calculation to see department metrics!</td></tr>';
      return;
    }

    stats.departmentBreakdown.forEach(dept => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 600;">${escapeHtml(dept.departmentName)}</td>
        <td><span class="badge badge-info">${escapeHtml(dept.departmentCode)}</span></td>
        <td>${dept.employeeCount}</td>
        <td>${formatCurrency(dept.totalGross)}</td>
        <td style="color: var(--danger);">${formatCurrency(dept.totalLossOfPay)}</td>
        <td>${formatCurrency(dept.totalDeductions)}</td>
        <td style="font-weight: 700; color: var(--success);">${formatCurrency(dept.totalNet)}</td>
        <td>${formatCurrency(dept.averageNetSalary)}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load dashboard: ' + err.message, 'error');
  }
}

// ============================================================================
// EMPLOYEES
// ============================================================================
async function loadEmployees() {
  try {
    const employees = await api('/employees');
    state.allEmployees = employees || [];
    renderEmployees(state.allEmployees);
  } catch (err) {
    showToast('Failed to load employees: ' + err.message, 'error');
  }
}

function renderEmployees(list) {
  const tbody = document.getElementById('employees-tbody');
  tbody.innerHTML = '';

  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 20px;">No employees found</td></tr>';
    return;
  }

  list.forEach(emp => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td style="font-weight: 700;">${escapeHtml(emp.employeeNumber)}</td>
      <td>
        <div style="font-weight: 600;">${escapeHtml(emp.fullName)}</div>
        <div style="font-size: 11px; color: var(--text-muted);">${escapeHtml(emp.email)}</div>
      </td>
      <td>${escapeHtml(emp.departmentName || 'N/A')}</td>
      <td>${escapeHtml(emp.designation || 'Staff')}</td>
      <td><span class="badge ${emp.salaryStructureName ? 'badge-info' : 'badge-draft'}">${escapeHtml(emp.salaryStructureName || 'Unassigned')}</span></td>
      <td style="font-family: monospace;">${escapeHtml(emp.panNumber || 'N/A')}</td>
      <td>
        <span class="badge ${emp.active ? 'badge-locked' : 'badge-draft'}">
          ${emp.active ? 'Active' : 'Inactive'}
        </span>
      </td>
      <td>
        <button class="btn btn-secondary btn-sm" onclick="openAssignStructureModal(${emp.employeeId}, '${escapeHtml(emp.fullName)}')">
          Assign Structure
        </button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function filterEmployees() {
  const query = document.getElementById('employee-search-input').value.toLowerCase().trim();
  if (!query) {
    renderEmployees(state.allEmployees);
    return;
  }
  const filtered = state.allEmployees.filter(e =>
    e.fullName.toLowerCase().includes(query) ||
    e.employeeNumber.toLowerCase().includes(query) ||
    (e.email && e.email.toLowerCase().includes(query)) ||
    (e.departmentName && e.departmentName.toLowerCase().includes(query))
  );
  renderEmployees(filtered);
}

async function openAddEmployeeModal() {
  // Populate departments & structures dropdown
  const [departments, structures] = await Promise.all([
    api('/departments'),
    api('/salary-structures')
  ]);

  state.allDepartments = departments || [];
  state.allStructures = structures || [];

  const deptSelect = document.getElementById('add-emp-dept');
  deptSelect.innerHTML = '<option value="">Select Department</option>';
  state.allDepartments.forEach(d => {
    deptSelect.innerHTML += `<option value="${d.departmentId}">${escapeHtml(d.departmentName)}</option>`;
  });

  const structSelect = document.getElementById('add-emp-struct');
  structSelect.innerHTML = '<option value="">Select Salary Structure (Optional)</option>';
  state.allStructures.forEach(s => {
    structSelect.innerHTML += `<option value="${s.structureId}">${escapeHtml(s.structureName)}</option>`;
  });

  document.getElementById('form-add-employee').reset();
  openModal('modal-add-employee');
}

async function submitAddEmployee(event) {
  event.preventDefault();
  try {
    const payload = {
      employeeNumber: document.getElementById('add-emp-num').value.trim(),
      fullName: document.getElementById('add-emp-name').value.trim(),
      email: document.getElementById('add-emp-email').value.trim(),
      designation: document.getElementById('add-emp-desig').value.trim(),
      departmentId: Number(document.getElementById('add-emp-dept').value),
      salaryStructureId: document.getElementById('add-emp-struct').value ? Number(document.getElementById('add-emp-struct').value) : null,
      panNumber: document.getElementById('add-emp-pan').value.trim(),
      bankAccountNumber: document.getElementById('add-emp-bank').value.trim(),
      active: true
    };

    await api('/employees', {
      method: 'POST',
      body: JSON.stringify(payload)
    });

    closeModal('modal-add-employee');
    showToast('Employee created successfully!', 'success');
    loadEmployees();
  } catch (err) {
    showToast('Failed to create employee: ' + err.message, 'error');
  }
}

async function openAssignStructureModal(empId, empName) {
  const structures = await api('/salary-structures');
  state.allStructures = structures || [];

  document.getElementById('assign-emp-id').value = empId;
  document.getElementById('assign-emp-name').textContent = `Assign compensation structure to: ${empName}`;

  const select = document.getElementById('assign-struct-select');
  select.innerHTML = '<option value="">Select Structure</option>';
  state.allStructures.forEach(s => {
    select.innerHTML += `<option value="${s.structureId}">${escapeHtml(s.structureName)}</option>`;
  });

  openModal('modal-assign-struct');
}

async function submitAssignStructure(event) {
  event.preventDefault();
  try {
    const empId = document.getElementById('assign-emp-id').value;
    const structureId = Number(document.getElementById('assign-struct-select').value);

    await api(`/employees/${empId}/salary-structure`, {
      method: 'PUT',
      body: JSON.stringify({ structureId })
    });

    closeModal('modal-assign-struct');
    showToast('Salary structure assigned successfully! (FR2)', 'success');
    loadEmployees();
  } catch (err) {
    showToast('Failed to assign structure: ' + err.message, 'error');
  }
}

// ============================================================================
// SALARY STRUCTURES & COMPONENTS
// ============================================================================
async function loadStructuresAndComponents() {
  await Promise.all([loadComponents(), loadStructures()]);
}

async function loadComponents() {
  try {
    const comps = await api('/salary-components');
    state.allComponents = comps || [];
    const tbody = document.getElementById('components-tbody');
    tbody.innerHTML = '';

    if (state.allComponents.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 15px;">No components defined</td></tr>';
      return;
    }

    state.allComponents.forEach(c => {
      const isEarning = c.componentType === 'EARNING';
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700; font-family: monospace;">${escapeHtml(c.componentCode)}</td>
        <td>${escapeHtml(c.componentName)}</td>
        <td><span class="badge ${isEarning ? 'badge-earning' : 'badge-deduction'}">${c.componentType}</span></td>
        <td>${escapeHtml(c.calculationMode)}</td>
        <td style="font-weight: 600;">${c.calculationMode === 'PERCENTAGE_OF_BASIC' ? c.defaultValue + '%' : formatCurrency(c.defaultValue)}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load components: ' + err.message, 'error');
  }
}

async function loadStructures() {
  try {
    const structs = await api('/salary-structures');
    state.allStructures = structs || [];
    const tbody = document.getElementById('structures-tbody');
    tbody.innerHTML = '';

    if (state.allStructures.length === 0) {
      tbody.innerHTML = '<tr><td colspan="3" style="text-align:center; padding: 15px;">No structures created</td></tr>';
      return;
    }

    state.allStructures.forEach(s => {
      const compBadges = (s.components || []).map(sc =>
        `<span class="badge badge-info" style="margin: 2px;">${escapeHtml(sc.componentCode)}: ${sc.calculationMode === 'PERCENTAGE_OF_BASIC' ? sc.amountOrRate + '%' : '₹' + sc.amountOrRate}</span>`
      ).join('');

      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${escapeHtml(s.structureName)}</td>
        <td>${compBadges}</td>
        <td>
          <span style="font-size: 12px; color: var(--text-muted);">${s.components ? s.components.length : 0} items</span>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load structures: ' + err.message, 'error');
  }
}

function openAddComponentModal() {
  document.getElementById('form-add-comp').reset();
  openModal('modal-add-comp');
}

async function submitAddComponent(event) {
  event.preventDefault();
  try {
    const payload = {
      componentCode: document.getElementById('comp-code').value.trim().toUpperCase(),
      componentName: document.getElementById('comp-name').value.trim(),
      componentType: document.getElementById('comp-type').value,
      calculationMode: document.getElementById('comp-mode').value,
      defaultValue: parseFloat(document.getElementById('comp-val').value)
    };

    await api('/salary-components', {
      method: 'POST',
      body: JSON.stringify(payload)
    });

    closeModal('modal-add-comp');
    showToast('Salary component defined successfully! (FR1)', 'success');
    loadComponents();
  } catch (err) {
    showToast('Failed to save component: ' + err.message, 'error');
  }
}

function openAddStructureModal() {
  showToast('Standard bands already seeded. You can configure more structures via Swagger or API!', 'info');
}

// ============================================================================
// ATTENDANCE & LOSS OF PAY
// ============================================================================
async function loadAttendance() {
  const month = document.getElementById('attendance-month-input').value || '2026-10';
  document.getElementById('attendance-month-label').textContent = month;

  try {
    const records = await api(`/attendance?month=${month}`);
    const tbody = document.getElementById('attendance-tbody');
    tbody.innerHTML = '';

    if (!records || records.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 20px;">No attendance logged for this month. Click "Auto-Generate 30 Days" to seed attendance!</td></tr>';
      return;
    }

    records.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${escapeHtml(r.employeeNumber)}</td>
        <td style="font-weight: 600;">${escapeHtml(r.employeeName)}</td>
        <td>${escapeHtml(r.departmentName)}</td>
        <td>${r.totalWorkingDays}</td>
        <td><strong style="color: var(--success);">${r.payableDays}</strong></td>
        <td><strong style="color: ${r.lossOfPayDays > 0 ? 'var(--danger)' : 'var(--text-muted)'};">${r.lossOfPayDays}</strong></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="openEditAttendance(${r.employeeId}, '${escapeHtml(r.employeeName)}', ${r.totalWorkingDays}, ${r.payableDays}, ${r.lossOfPayDays})">
            Edit Days
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load attendance: ' + err.message, 'error');
  }
}

async function generateDefaultAttendance() {
  const month = document.getElementById('attendance-month-input').value || '2026-10';
  try {
    const res = await api(`/attendance/generate-default?month=${month}`, { method: 'POST' });
    showToast(`Default attendance recorded (${res.recordsGenerated} employees)!`, 'success');
    loadAttendance();
  } catch (err) {
    showToast('Failed to generate attendance: ' + err.message, 'error');
  }
}

function openEditAttendance(empId, empName, total, payable, lop) {
  document.getElementById('att-emp-id').value = empId;
  document.getElementById('att-emp-name-label').textContent = `Attendance for: ${empName}`;
  document.getElementById('att-total-days').value = total;
  document.getElementById('att-payable-days').value = payable;
  document.getElementById('att-lop-days').value = lop;
  openModal('modal-edit-att');
}

async function submitEditAttendance(event) {
  event.preventDefault();
  const month = document.getElementById('attendance-month-input').value || '2026-10';
  try {
    const payload = {
      employeeId: Number(document.getElementById('att-emp-id').value),
      payrollMonth: month,
      totalWorkingDays: Number(document.getElementById('att-total-days').value),
      payableDays: Number(document.getElementById('att-payable-days').value),
      lossOfPayDays: Number(document.getElementById('att-lop-days').value)
    };

    await api('/attendance', {
      method: 'POST',
      body: JSON.stringify(payload)
    });

    closeModal('modal-edit-att');
    showToast('Attendance updated successfully! LOP will apply to payroll run.', 'success');
    loadAttendance();
  } catch (err) {
    showToast('Failed to save attendance: ' + err.message, 'error');
  }
}

// ============================================================================
// PAYROLL RUNS WIZARD
// ============================================================================
async function loadPayrollRuns() {
  try {
    const runs = await api('/payroll-runs');
    const tbody = document.getElementById('payroll-runs-tbody');
    tbody.innerHTML = '';

    if (!runs || runs.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 20px;">No payroll runs executed yet. Pick a month and click "Calculate Payroll Run" above!</td></tr>';
      return;
    }

    runs.forEach(r => {
      const isLocked = r.status === 'LOCKED';
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${r.payrollMonth}</td>
        <td>
          <span class="badge ${isLocked ? 'badge-locked' : 'badge-draft'}">
            ${isLocked ? '🔒 LOCKED' : '📝 DRAFT'}
          </span>
        </td>
        <td>${r.employeeCount} staff</td>
        <td style="font-weight: 600;">${formatCurrency(r.totalGross)}</td>
        <td style="color: var(--danger);">${formatCurrency(r.totalDeductions)}</td>
        <td style="font-weight: 700; color: var(--success);">${formatCurrency(r.totalNet)}</td>
        <td>
          <div style="font-size: 11.5px; color: var(--text-muted);">
            ${isLocked ? `By ${escapeHtml(r.lockedBy || 'Admin')}` : 'Pending approval'}
          </div>
        </td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="viewRunEntries(${r.runId}, '${r.payrollMonth}', '${r.status}')">
            View Line Items
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    // Auto-view the first run if available
    if (runs.length > 0 && !state.selectedRunId) {
      viewRunEntries(runs[0].runId, runs[0].payrollMonth, runs[0].status);
    }
  } catch (err) {
    showToast('Failed to load payroll runs: ' + err.message, 'error');
  }
}

async function triggerPayrollRun() {
  const month = document.getElementById('payroll-month-input').value;
  if (!month) {
    showToast('Please select a valid payroll month', 'error');
    return;
  }

  try {
    showToast(`Calculating payroll for ${month}...`, 'info');
    const res = await api('/payroll-runs', {
      method: 'POST',
      body: JSON.stringify({ payrollMonth: month })
    });

    showToast(`Payroll calculated successfully for ${month}! Total Net: ₹${res.totalNet} (FR3, FR4)`, 'success');
    await loadPayrollRuns();
    viewRunEntries(res.runId, res.payrollMonth, res.status);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function viewRunEntries(runId, month, status) {
  state.selectedRunId = runId;
  state.selectedRunStatus = status;

  const card = document.getElementById('run-entries-card');
  card.style.display = 'block';

  document.getElementById('entries-month-title').textContent = month;
  const statusBadge = document.getElementById('entries-status-badge');
  const lockBtn = document.getElementById('btn-lock-run');

  const isLocked = status === 'LOCKED';
  statusBadge.className = `badge ${isLocked ? 'badge-locked' : 'badge-draft'}`;
  statusBadge.textContent = isLocked ? '🔒 LOCKED' : '📝 DRAFT';

  lockBtn.disabled = isLocked;
  lockBtn.style.opacity = isLocked ? '0.5' : '1';
  lockBtn.title = isLocked ? 'Payroll run is already locked' : 'Approve and prevent further edits (FR5)';

  try {
    const entries = await api(`/payroll-runs/${runId}/entries`);
    const tbody = document.getElementById('entries-tbody');
    tbody.innerHTML = '';

    if (!entries || entries.length === 0) {
      tbody.innerHTML = '<tr><td colspan="10" style="text-align:center; padding: 20px;">No entries calculated for this run.</td></tr>';
      return;
    }

    entries.forEach(e => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${escapeHtml(e.employeeNumber)}</td>
        <td style="font-weight: 600;">${escapeHtml(e.employeeName)}</td>
        <td>${escapeHtml(e.departmentName)}</td>
        <td>${e.payableDays}</td>
        <td><strong style="color: ${e.lossOfPayDays > 0 ? 'var(--danger)' : 'var(--text-muted)'};">${e.lossOfPayDays}</strong></td>
        <td>${formatCurrency(e.grossSalary)}</td>
        <td style="color: var(--danger);">${formatCurrency(e.lossOfPayAmount)}</td>
        <td>${formatCurrency(e.totalDeductions)}</td>
        <td style="font-weight: 700; color: var(--success);">${formatCurrency(e.netSalary)}</td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick='previewPayslip(${JSON.stringify(e)})'>
            <span>📄</span> Payslip
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    card.scrollIntoView({ behavior: 'smooth' });
  } catch (err) {
    showToast('Failed to load line items: ' + err.message, 'error');
  }
}

async function lockCurrentRun() {
  if (!state.selectedRunId) return;

  if (!confirm(`Are you sure you want to approve and LOCK payroll run for ${document.getElementById('entries-month-title').textContent}? Once locked, no further changes or recalculations can be made (FR5, FR7).`)) {
    return;
  }

  try {
    const res = await api(`/payroll-runs/${state.selectedRunId}/lock`, { method: 'POST' });
    showToast(`Payroll run for ${res.payrollMonth} successfully LOCKED! (FR5, FR7)`, 'success');
    await loadPayrollRuns();
    viewRunEntries(res.runId, res.payrollMonth, res.status);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ============================================================================
// PAYSLIP PORTAL & PDF DOWNLOAD
// ============================================================================
async function loadPayslips() {
  const tbody = document.getElementById('payslips-tbody');
  tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 20px;">Fetching payslips archive...</td></tr>';

  try {
    let payslips = [];
    if (state.user && state.user.role === 'ROLE_EMPLOYEE') {
      payslips = await api('/payslips/me');
    } else {
      // If Admin or Officer, display payslips from the latest run
      const runs = await api('/payroll-runs');
      if (runs && runs.length > 0) {
        payslips = await api(`/payroll-runs/${runs[0].runId}/entries`);
      }
    }

    tbody.innerHTML = '';
    if (!payslips || payslips.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 20px;">No payslips available for your profile.</td></tr>';
      return;
    }

    payslips.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${escapeHtml(p.payrollMonth || '2026-10')}</td>
        <td style="font-family: monospace; font-size: 11.5px;">${escapeHtml(p.payslipDocRef || 'PAY-REF')}</td>
        <td style="font-weight: 600;">${escapeHtml(p.employeeName)}</td>
        <td>${escapeHtml(p.departmentName)}</td>
        <td>${formatCurrency(p.grossSalary)}</td>
        <td style="font-weight: 700; color: var(--success);">${formatCurrency(p.netSalary)}</td>
        <td>
          <div style="display: flex; gap: 6px;">
            <button class="btn btn-secondary btn-sm" onclick='previewPayslip(${JSON.stringify(p)})'>
              Preview
            </button>
            <button class="btn btn-primary btn-sm" onclick="downloadPayslipPdf(${p.payslipId}, '${p.payslipDocRef}')">
              <span>📥</span> Download PDF
            </button>
          </div>
        </td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load payslips: ' + err.message, 'error');
  }
}

function previewPayslip(p) {
  document.getElementById('slip-month').textContent = p.payrollMonth || '2026-10';
  document.getElementById('slip-emp-name').textContent = p.employeeName;
  document.getElementById('slip-emp-id').textContent = p.employeeNumber;
  document.getElementById('slip-dept').textContent = p.departmentName;
  document.getElementById('slip-pan').textContent = `PAN: ${p.panNumber || 'N/A'}`;

  document.getElementById('slip-payable').textContent = p.payableDays;
  document.getElementById('slip-lop').textContent = p.lossOfPayDays;
  document.getElementById('slip-doc-ref').textContent = p.payslipDocRef || 'PAY-DOCUMENT';

  document.getElementById('slip-gross').textContent = formatCurrency(p.grossSalary);
  document.getElementById('slip-lop-amt').textContent = `- ${formatCurrency(p.lossOfPayAmount)}`;
  document.getElementById('slip-net').textContent = formatCurrency(p.netSalary);

  const dedTbody = document.getElementById('slip-deductions-tbody');
  dedTbody.innerHTML = '';
  if (p.deductionsBreakdown) {
    for (const [key, val] of Object.entries(p.deductionsBreakdown)) {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(key)}</td>
        <td style="text-align: right; color: var(--danger);">- ${formatCurrency(val)}</td>
      `;
      dedTbody.appendChild(tr);
    }
  }

  const downloadBtn = document.getElementById('btn-download-pdf-modal');
  downloadBtn.onclick = () => downloadPayslipPdf(p.payslipId, p.payslipDocRef);

  openModal('modal-payslip-preview');
}

async function downloadPayslipPdf(payslipId, docRef) {
  if (!payslipId) {
    showToast('No PDF generated for this entry yet.', 'error');
    return;
  }

  try {
    showToast('Generating official OpenPDF payslip document...', 'info');
    const blob = await api(`/payslips/${payslipId}/pdf`, { isBlob: true });
    if (!blob) return;

    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${docRef || 'payslip'}.pdf`;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();

    showToast('PDF Payslip downloaded successfully!', 'success');
  } catch (err) {
    showToast('Failed to download PDF: ' + err.message, 'error');
  }
}

// ============================================================================
// REPORTS & STATUTORY ANALYTICS
// ============================================================================
async function loadReports() {
  try {
    const list = await api('/reports/department-costs');
    const tbody = document.getElementById('reports-dept-tbody');
    tbody.innerHTML = '';

    if (!list || list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding: 20px;">No cost data available. Run payroll to generate report figures!</td></tr>';
      return;
    }

    list.forEach(r => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td style="font-weight: 700;">${escapeHtml(r.departmentName)}</td>
        <td><span class="badge badge-info">${escapeHtml(r.departmentCode)}</span></td>
        <td>${r.employeeCount} staff</td>
        <td style="font-weight: 600;">${formatCurrency(r.totalGross)}</td>
        <td style="color: var(--danger);">${formatCurrency(r.totalLossOfPay)}</td>
        <td>${formatCurrency(r.totalDeductions)}</td>
        <td style="font-weight: 700; color: var(--success);">${formatCurrency(r.totalNet)}</td>
        <td style="font-weight: 600;">${formatCurrency(r.averageNetSalary)}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Failed to load reports: ' + err.message, 'error');
  }
}

function openRunPayrollModal() {
  switchTab('payroll');
}

// ============================================================================
// UI HELPERS & TOAST
// ============================================================================
function openModal(id) {
  document.getElementById(id)?.classList.add('open');
}

function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}

function formatCurrency(amount) {
  if (amount == null) return '₹ 0.00';
  const num = typeof amount === 'number' ? amount : parseFloat(amount);
  return '₹ ' + num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span>
    <div>${escapeHtml(message)}</div>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}
