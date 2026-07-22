let currentAuthMode = 'login'; // login or register
let token = localStorage.getItem('jwt_token') || null;

// DOM Elements
const authView = document.getElementById('auth-view');
const dashboardView = document.getElementById('dashboard-view');
const authSubmitBtn = document.getElementById('auth-submit');
const authError = document.getElementById('auth-error');
const welcomeMsg = document.getElementById('welcome-msg');
const expenseForm = document.getElementById('expense-form');
const expenseList = document.getElementById('expense-list');
const emptyState = document.getElementById('empty-state');
const summaryContent = document.getElementById('ai-summary-content');
const summaryBtn = document.getElementById('summary-btn');

// Initialize App
function init() {
    if (token) {
        showDashboard();
    } else {
        showAuth();
    }
    
    // Set today's date as default
    document.getElementById('date').valueAsDate = new Date();
}

// UI Toggles
function showAuth() {
    authView.classList.remove('hidden');
    dashboardView.classList.add('hidden');
}

function showDashboard() {
    authView.classList.add('hidden');
    dashboardView.classList.remove('hidden');
    // Decode JWT to get username (very basic decode for UI purposes)
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        welcomeMsg.textContent = `Hello, ${payload.sub}`;
    } catch(e) {
        welcomeMsg.textContent = 'Hello, User';
    }
    fetchExpenses();
}

function switchTab(mode) {
    currentAuthMode = mode;
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    authSubmitBtn.textContent = mode === 'login' ? 'Login' : 'Register';
    authError.textContent = '';
}

// API Calls
async function handleAuth(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    authError.textContent = '';
    
    authSubmitBtn.innerHTML = '<span class="loader"></span>';
    
    try {
        const url = currentAuthMode === 'login' ? '/api/auth/login' : '/api/auth/register';
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await res.json();
        
        if (res.ok) {
            if (currentAuthMode === 'register') {
                switchTab('login');
                authError.style.color = 'var(--accent)';
                authError.textContent = 'Registration successful! Please login.';
            } else {
                token = data.token;
                localStorage.setItem('jwt_token', token);
                showDashboard();
            }
        } else {
            authError.style.color = 'var(--danger)';
            authError.textContent = data.message || 'Authentication failed';
        }
    } catch (err) {
        authError.style.color = 'var(--danger)';
        authError.textContent = 'Server connection error';
    } finally {
        authSubmitBtn.textContent = currentAuthMode === 'login' ? 'Login' : 'Register';
    }
}

function logout() {
    token = null;
    localStorage.removeItem('jwt_token');
    showAuth();
}

// Expenses
async function fetchExpenses() {
    try {
        const res = await fetch('/api/expenses', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (res.status === 401 || res.status === 403) {
            logout();
            return;
        }
        
        const data = await res.json();
        renderExpenses(data);
    } catch (err) {
        console.error('Failed to fetch expenses', err);
    }
}

function renderExpenses(expenses) {
    expenseList.innerHTML = '';
    if (expenses.length === 0) {
        emptyState.style.display = 'block';
    } else {
        emptyState.style.display = 'none';
        
        // Sort descending by ID (newest first)
        expenses.sort((a,b) => b.id - a.id).forEach(exp => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${exp.date}</td>
                <td><strong>${exp.description}</strong></td>
                <td>$${exp.amount.toFixed(2)}</td>
                <td><span class="badge">${exp.category}</span></td>
            `;
            expenseList.appendChild(tr);
        });
    }
}

async function handleAddExpense(e) {
    e.preventDefault();
    const submitBtn = document.getElementById('expense-submit');
    const textSpan = submitBtn.querySelector('.btn-text');
    const loader = submitBtn.querySelector('.loader');
    
    const amount = document.getElementById('amount').value;
    const date = document.getElementById('date').value;
    const description = document.getElementById('description').value;
    
    textSpan.classList.add('hidden');
    loader.classList.remove('hidden');
    submitBtn.disabled = true;
    
    try {
        const res = await fetch('/api/expenses', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ amount: parseFloat(amount), date, description })
        });
        
        if (res.ok) {
            document.getElementById('description').value = '';
            document.getElementById('amount').value = '';
            fetchExpenses(); // refresh list
        }
    } catch (err) {
        console.error('Failed to add expense', err);
    } finally {
        textSpan.classList.remove('hidden');
        loader.classList.add('hidden');
        submitBtn.disabled = false;
    }
}

async function generateSummary() {
    summaryBtn.disabled = true;
    summaryBtn.innerHTML = '<span class="loader"></span> Thinking...';
    summaryContent.innerHTML = '<p class="placeholder-text">Analyzing your spending habits...</p>';
    
    try {
        const res = await fetch('/api/expenses/summary', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        const data = await res.text();
        summaryContent.innerHTML = `<p>${data}</p>`;
    } catch (err) {
        summaryContent.innerHTML = '<p style="color:var(--danger)">Failed to generate summary.</p>';
    } finally {
        summaryBtn.disabled = false;
        summaryBtn.innerHTML = '✨ Generate AI Summary ✨';
    }
}

// Run on load
init();
