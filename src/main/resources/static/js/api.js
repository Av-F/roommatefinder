// API Service
class ApiService {
    static async request(endpoint, options = {}) {
        const url = `${CONFIG.API_BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        };

        // Add authentication token if available
        const token = getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers,
        };

        try {
            const controller = new AbortController();
            const timeout = setTimeout(() => controller.abort(), CONFIG.TIMEOUT);

            const response = await fetch(url, {
                ...config,
                signal: controller.signal,
            });

            clearTimeout(timeout);

            if (response.status === 401) {
                // Unauthorized - clear token and redirect to login
                clearToken();
                window.location.hash = '#/';
                throw new Error('Unauthorized. Please login again.');
            }

            // Handle empty response body (like from 204 No Content or empty 200 OK)
            const contentType = response.headers.get('content-type');
            let data = null;
            
            if (contentType && contentType.includes('application/json')) {
                const text = await response.text();
                if (text && text.length > 0) {
                    data = JSON.parse(text);
                }
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                const errorMessage = data?.message || data?.error || data || `HTTP ${response.status}`;
                throw new Error(errorMessage);
            }

            return data;
        } catch (error) {
            if (error.name === 'AbortError') {
                throw new Error('Request timeout. Please try again.');
            }
            throw error;
        }
    }

    // Auth endpoints
    static async register(email, password, username) {
        return this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ email, password, username }),
        });
    }

    static async login(email, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password }),
        });
    }

    // User endpoints
    static async getCurrentUser() {
        return this.request('/users/me', { method: 'GET' });
    }

    static async getUserProfile(userId) {
        return this.request(`/users/${userId}`, { method: 'GET' });
    }

    static async updateUser(userId, userData) {
        return this.request(`/users/${userId}`, {
            method: 'PUT',
            body: JSON.stringify(userData),
        });
    }

    static async deleteUser(userId) {
        return this.request(`/users/${userId}`, { method: 'DELETE' });
    }

    // Profile endpoints
    static async createProfile(profileData) {
        return this.request('/profiles/create', {
            method: 'POST',
            body: JSON.stringify(profileData),
        });
    }

    static async getMyProfile() {
        return this.request('/profiles/me', { method: 'GET' });
    }

    static async getUserProfileDetail(userId) {
        return this.request(`/profiles/${userId}`, { method: 'GET' });
    }

    static async getAllProfiles() {
        return this.request('/profiles/retrieve', { method: 'GET' });
    }

    static async updateProfile(profileData) {
        return this.request('/profiles/update', {
            method: 'PATCH',
            body: JSON.stringify(profileData),
        });
    }

    // Swipe endpoints
    static async getSwipeableProfiles() {
        return this.request('/swipes/discover', { method: 'GET' });
    }

    static async swipeProfile(profileId, liked) {
        return this.request(`/swipes/${profileId}`, {
            method: 'POST',
            body: JSON.stringify({ liked }),
        });
    }

    // Match endpoints
    static async getMatches() {
        return this.request('/matches', { method: 'GET' });
    }

    static async getMatchDetail(userId) {
        return this.request(`/matches/${userId}`, { method: 'GET' });
    }

    // Chat endpoints
    static async sendMessage(receiverId, message) {
        return this.request(`/chat/send?receiverId=${receiverId}&message=${encodeURIComponent(message)}`, {
            method: 'POST',
        });
    }

    static async getConversation(otherUserId) {
        return this.request(`/chat/${otherUserId}`, { method: 'GET' });
    }
}
