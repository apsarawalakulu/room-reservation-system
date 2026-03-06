document.addEventListener('DOMContentLoaded', async () => {
    const loginForm = document.getElementById('loginForm');
    const errorDiv = document.getElementById('loginError');
    const loginBtn = document.getElementById('loginBtn');

    if (!loginForm) return;

    try {
        const authStatus = await Api.checkAuth();
        if (authStatus) {
            window.location.href = 'dashboard.html';
        }
    } catch (e) { }

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorDiv.textContent = '';

        const username = loginForm.username.value;
        const password = loginForm.password.value;

        if (!username || !password) {
            errorDiv.textContent = 'Please enter both username and password.';
            return;
        }

        loginBtn.textContent = 'Signing in...';
        loginBtn.disabled = true;

        try {
            await Api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ username, password })
            });
            window.location.href = 'dashboard.html';
        } catch (error) {
            loginBtn.textContent = 'Sign In securely';
            loginBtn.disabled = false;
            if (error.data && error.data.error) {
                errorDiv.textContent = error.data.error;
            } else {
                errorDiv.textContent = 'Invalid credentials or server error.';
            }
        }
    });
});
