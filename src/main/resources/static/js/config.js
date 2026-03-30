// Configuration
const CONFIG = {
    // Auto-detect API URL based on environment
    API_BASE_URL: getDynamicApiUrl(),
    TOKEN_KEY: 'roommatefinder_token',
    USER_KEY: 'roommatefinder_user',
    TIMEOUT: 10000,
};

// Detect API URL based on current location
function getDynamicApiUrl() {
    // Local development
    if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        return 'http://localhost:8080/api';
    }
    
    // Production - Update this with your Render backend URL
    return 'https://roommatefinder-t06b.onrender.com';
}

// Utility function to check if user is authenticated
function isAuthenticated() {
    return localStorage.getItem(CONFIG.TOKEN_KEY) !== null;
}

// Utility function to get token
function getToken() {
    return localStorage.getItem(CONFIG.TOKEN_KEY);
}

// Utility function to set token
function setToken(token) {
    localStorage.setItem(CONFIG.TOKEN_KEY, token);
}

// Utility function to clear token
function clearToken() {
    localStorage.removeItem(CONFIG.TOKEN_KEY);
    localStorage.removeItem(CONFIG.USER_KEY);
}

// Utility function to get stored user
function getStoredUser() {
    const user = localStorage.getItem(CONFIG.USER_KEY);
    return user ? JSON.parse(user) : null;
}

// Utility function to set stored user
function setStoredUser(user) {
    localStorage.setItem(CONFIG.USER_KEY, JSON.stringify(user));
}
