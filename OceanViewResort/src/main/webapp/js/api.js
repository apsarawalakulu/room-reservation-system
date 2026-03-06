const currentPath = window.location.pathname;
const baseContext = currentPath.substring(0, currentPath.indexOf('/', 1));
const contextPath = baseContext.includes('OceanViewResort') ? '/OceanViewResort' : '';

const Api = {
    baseUrl: contextPath + '/api',
    
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        
        const defaultHeaders = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                if (!window.location.pathname.endsWith('index.html') && window.location.pathname !== contextPath + '/') {
                    window.location.href = contextPath + '/index.html';
                }
            }

            const contentType = response.headers.get("content-type");
            let data = null;
            if (contentType && contentType.includes("application/json") && response.status !== 204) {
                data = await response.json();
            }

            if (!response.ok) {
                throw { status: response.status, data: data };
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    async checkAuth() {
        try {
            const res = await this.request('/auth/status');
            if (res && res.authenticated) {
                return true;
            }
        } catch (e) {
            return false;
        }
        return false;
    },
    
    async logout() {
        try {
            await this.request('/auth/logout', { method: 'POST' });
            window.location.href = contextPath + '/index.html';
        } catch (e) {
            console.error('Logout failed', e);
        }
    }
};

// Global Auth Check for protected pages
(async function init() {
    const isLoginPage = window.location.pathname.endsWith('index.html') || window.location.pathname === contextPath + '/';
    
    if (isLoginPage) return; 

    // Quick structural initialization for shared UI (Sidebar logout hook if it exists)
    document.addEventListener('DOMContentLoaded', () => {
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                Api.logout();
            });
        }
    });

    const isAuthenticated = await Api.checkAuth();
    if (!isAuthenticated) {
        window.location.href = contextPath + '/index.html';
    }
})();
