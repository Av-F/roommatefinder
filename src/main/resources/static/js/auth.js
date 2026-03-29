// Authentication Service
class AuthService {
    static async login(email, password) {
        try {
            const response = await ApiService.login(email, password);
            if (response.token) {
                setToken(response.token);
                // Extract user ID from JWT token (stored in subject)
                const userId = this.getUserIdFromToken(response.token);
                // Fetch and store user info
                const user = await ApiService.getCurrentUser();
                if (user) {
                    user.id = userId; // Ensure ID is set
                    setStoredUser(user);
                }
                return { success: true, user };
            }
            throw new Error('No token received');
        } catch (error) {
            return { success: false, error: error.message };
        }
    }

    static async register(email, password, username) {
        try {
            const response = await ApiService.register(email, password, username);
            if (response.token) {
                setToken(response.token);
                // Extract user ID from JWT token (stored in subject)
                const userId = this.getUserIdFromToken(response.token);
                // Fetch and store user info
                const user = await ApiService.getCurrentUser();
                if (user) {
                    user.id = userId; // Ensure ID is set
                    setStoredUser(user);
                }
                return { success: true, user };
            }
            throw new Error('No token received');
        } catch (error) {
            return { success: false, error: error.message };
        }
    }

    // Extract user ID from JWT token
    static getUserIdFromToken(token) {
        try {
            // JWT format: header.payload.signature
            const parts = token.split('.');
            if (parts.length !== 3) return null;
            
            // Decode payload (add padding if needed)
            const payload = parts[1];
            const padded = payload + '=='.substring(0, (4 - payload.length % 4) % 4);
            const decoded = JSON.parse(atob(padded));
            
            // Token subject is the user ID
            return parseInt(decoded.sub);
        } catch (error) {
            console.error('Failed to extract user ID from token:', error);
            return null;
        }
    }

    static logout() {
        clearToken();
        window.location.hash = '#/';
    }

    static isAuthenticated() {
        return isAuthenticated();
    }

    static getToken() {
        return getToken();
    }
}

// UI Helpers for auth forms
function showAlert(message, type = 'success', containerId = 'alerts') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertId = `alert-${Date.now()}`;
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${type}">
            <span class="alert-close" onclick="document.getElementById('${alertId}').remove();">&times;</span>
            ${message}
        </div>
    `;

    container.innerHTML += alertHTML;

    // Auto-remove after 5 seconds
    setTimeout(() => {
        const element = document.getElementById(alertId);
        if (element) element.remove();
    }, 5000);
}

function setLoading(buttonId, loading = true) {
    const btn = document.getElementById(buttonId);
    if (!btn) return;

    if (loading) {
        btn.disabled = true;
        btn.innerHTML = `<span class="loading"></span> Processing...`;
    } else {
        btn.disabled = false;
        btn.innerHTML = btn.getAttribute('data-original-text') || 'Submit';
    }
}
