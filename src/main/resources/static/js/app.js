// Main Application
class RoommateFinder {
    constructor() {
        this.root = document.getElementById('root');
        this.currentPage = null;
        this.currentUser = null;
        this.init();
    }

    init() {
        this.checkAuthState();
        window.addEventListener('hashchange', () => this.route());
        this.route();
    }

    checkAuthState() {
        if (isAuthenticated()) {
            this.currentUser = getStoredUser();
        }
    }

    route() {
        const hash = window.location.hash || '#/';
        const path = hash.replace('#', '') || '/';

        // Always refresh currentUser from storage
        this.checkAuthState();

        if (!isAuthenticated() && path !== '/' && path !== '/login' && path !== '/register') {
            window.location.hash = '#/';
            return;
        }

        if (isAuthenticated() && (path === '/' || path === '/login' || path === '/register')) {
            window.location.hash = '#/dashboard';
            return;
        }

        // Check for chat route with user ID
        if (path.startsWith('/chat/')) {
            const userId = path.replace('/chat/', '');
            this.showChatView(parseInt(userId));
            return;
        }

        switch (path) {
            case '/':
            case '/login':
                this.showLoginPage();
                break;
            case '/register':
                this.showRegisterPage();
                break;
            case '/dashboard':
                this.showDashboard();
                break;
            case '/discover':
                this.showDiscovery();
                break;
            case '/matches':
                this.showMatches();
                break;
            case '/profile':
                this.showProfile();
                break;
            case '/settings':
                this.showSettings();
                break;
            case '/chat':
                this.showChatList();
                break;
            default:
                this.root.innerHTML = '<div class="container" style="padding: 2rem;">Page not found</div>';
        }
    }

    showLoginPage() {
        this.root.innerHTML = `
            <div class="auth-container">
                <div class="auth-card">
                    <h1>Welcome Back</h1>
                    <p>Find your perfect roommate today</p>
                    <div id="alerts"></div>
                    <form id="loginForm">
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" required placeholder="you@example.com">
                        </div>
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" required placeholder="••••••••">
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%" id="loginBtn" data-original-text="Login">
                            Login
                        </button>
                    </form>
                    <p style="margin-top: 1.5rem; text-align: center;">
                        Don't have an account? <a href="#/register">Sign up here</a>
                    </p>
                </div>
            </div>
        `;

        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            setLoading('loginBtn', true);

            const result = await AuthService.login(email, password);
            setLoading('loginBtn', false);

            if (result.success) {
                showAlert('Login successful! Redirecting...', 'success');
                setTimeout(() => {
                    window.location.hash = '#/dashboard';
                }, 1000);
            } else {
                showAlert(result.error, 'error');
            }
        });
    }

    showRegisterPage() {
        this.root.innerHTML = `
            <div class="auth-container">
                <div class="auth-card">
                    <h1>Join Us</h1>
                    <p>Create your account and start finding roommates</p>
                    <div id="alerts"></div>
                    <form id="registerForm">
                        <div class="form-group">
                            <label for="username">Username</label>
                            <input type="text" id="username" required placeholder="Your username">
                        </div>
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" required placeholder="you@example.com">
                        </div>
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" required placeholder="••••••••">
                        </div>
                        <button type="submit" class="btn btn-primary" style="width: 100%" id="registerBtn" data-original-text="Create Account">
                            Create Account
                        </button>
                    </form>
                    <p style="margin-top: 1.5rem; text-align: center;">
                        Already have an account? <a href="#/login">Login here</a>
                    </p>
                </div>
            </div>
        `;

        document.getElementById('registerForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            setLoading('registerBtn', true);

            const result = await AuthService.register(email, password, username);
            setLoading('registerBtn', false);

            if (result.success) {
                showAlert('Account created! Redirecting...', 'success');
                setTimeout(() => {
                    window.location.hash = '#/dashboard';
                }, 1000);
            } else {
                showAlert(result.error, 'error');
            }
        });
    }

    showDashboard() {
        // Ensure currentUser is fresh
        this.checkAuthState();

        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>Welcome, ${this.currentUser?.username || 'User'}!</h1>
                    <p>Find your perfect living companion</p>
                </div>

                <div id="alertsContainer"></div>

                <div class="dashboard-grid">
                    <div class="card">
                        <h2>My Matches</h2>
                        <div class="card-stat" id="matchCount">0</div>
                        <div class="card-stat-label">Compatible roommates found</div>
                        <a href="#/matches" class="btn btn-primary" style="margin-top: 1rem;">View Matches</a>
                    </div>

                    <div class="card">
                        <h2>Discover</h2>
                        <div class="card-stat-label">Explore profiles and find your match</div>
                        <a href="#/discover" class="btn btn-primary" style="margin-top: 1rem;">Start Swiping</a>
                    </div>

                    <div class="card">
                        <h2>My Profile</h2>
                        <div class="card-stat-label">View and update your profile</div>
                        <a href="#/profile" class="btn btn-primary" style="margin-top: 1rem;">Edit Profile</a>
                    </div>
                </div>

                <div class="profile-section">
                    <div class="profile-header">
                        <h2>Quick Stats</h2>
                    </div>
                    <div class="profile-info" id="statsContainer">
                        <div class="profile-field">
                            <div class="profile-field-label">Email</div>
                            <div class="profile-field-value">${this.currentUser?.email || 'N/A'}</div>
                        </div>
                        <div class="profile-field">
                            <div class="profile-field-label">Username</div>
                            <div class="profile-field-value">${this.currentUser?.username || 'N/A'}</div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.loadMatchCount();
    }

    async loadMatchCount() {
        try {
            const matches = await ApiService.getMatches();
            document.getElementById('matchCount').textContent = matches.length || 0;
        } catch (error) {
            console.error('Failed to load matches:', error);
        }
    }

    showProfile() {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>My Profile</h1>
                    <p>Create or update your roommate profile</p>
                </div>

                <div id="alertsContainer"></div>

                <div class="profile-section">
                    <div class="profile-header">
                        <h2>Profile Information</h2>
                    </div>

                    <form id="profileForm">
                        <div class="form-group">
                            <label for="profileName">Full Name</label>
                            <input type="text" id="profileName" required placeholder="Your name">
                        </div>

                        <div class="form-group">
                            <label for="profileAge">Age</label>
                            <input type="number" id="profileAge" min="18" max="100" required placeholder="Your age">
                        </div>

                        <div class="form-group">
                            <label for="profileCity">City</label>
                            <input type="text" id="profileCity" required placeholder="City you're looking in">
                        </div>

                        <div class="form-group">
                            <label for="profileLooking">What are you looking for?</label>
                            <select id="profileLooking" required>
                                <option value="">Select an option</option>
                                <option value="Roommate">Roommate</option>
                                <option value="Looking for lease">Looking for lease</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="profileBio">Bio</label>
                            <textarea id="profileBio" required placeholder="Tell others about yourself..."></textarea>
                        </div>

                        <button type="submit" class="btn btn-primary btn-lg" id="saveProfileBtn" data-original-text="Save Profile">
                            Save Profile
                        </button>
                    </form>
                </div>
            </div>
        `;

        this.loadProfileData();

        document.getElementById('profileForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.saveProfile();
        });
    }

    async loadProfileData() {
        try {
            const profile = await ApiService.getMyProfile();
            if (profile) {
                document.getElementById('profileName').value = profile.name || '';
                document.getElementById('profileAge').value = profile.age || '';
                document.getElementById('profileCity').value = profile.city || '';
                document.getElementById('profileLooking').value = profile.lookingForOption || '';
                document.getElementById('profileBio').value = profile.bio || '';
            }
        } catch (error) {
            console.log('No existing profile found or error loading. Creating new one.', error);
            // This is expected if profile doesn't exist yet
        }
    }

    async saveProfile() {
        const name = document.getElementById('profileName').value.trim();
        const age = parseInt(document.getElementById('profileAge').value);
        let city = document.getElementById('profileCity').value.trim();
        const lookingForOption = document.getElementById('profileLooking').value.trim();
        const bio = document.getElementById('profileBio').value.trim();

        // Normalize city: capitalize first letter, lowercase rest
        city = city.charAt(0).toUpperCase() + city.slice(1).toLowerCase();

        // Validate inputs
        if (!name || !city || !lookingForOption || !bio) {
            showAlert('All fields are required', 'error', 'alertsContainer');
            return;
        }

        if (isNaN(age) || age < 18 || age > 100) {
            showAlert('Please enter a valid age (18-100)', 'error', 'alertsContainer');
            return;
        }

        setLoading('saveProfileBtn', true);

        try {
            const profileData = {
                name,
                age,
                city,
                lookingForOption,
                bio,
            };

            console.log('Saving profile:', profileData);

            let response;
            let isUpdate = false;

            // Try to update first (if profile exists)
            try {
                console.log('Attempting to update profile...');
                response = await ApiService.updateProfile(profileData);
                console.log('Update response:', response);
                isUpdate = true;
                showAlert('Profile updated successfully!', 'success', 'alertsContainer');
            } catch (updateError) {
                console.log('Update failed:', updateError.message);
                console.log('Attempting to create new profile instead...');
                
                // If update fails, try to create a new profile
                try {
                    profileData.userId = this.currentUser.id;
                    response = await ApiService.createProfile(profileData);
                    console.log('Create response:', response);
                    showAlert('Profile created successfully!', 'success', 'alertsContainer');
                } catch (createError) {
                    console.error('Create also failed:', createError);
                    throw new Error(`Failed to save profile: ${createError.message}`);
                }
            }

            setTimeout(() => {
                window.location.hash = '#/dashboard';
            }, 1500);
        } catch (error) {
            console.error('Save profile error:', error);
            showAlert('Error: ' + error.message, 'error', 'alertsContainer');
        } finally {
            setLoading('saveProfileBtn', false);
        }
    }

    showDiscovery() {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>Discover Roommates</h1>
                    <p>Swipe to find your perfect match</p>
                </div>

                <div id="alertsContainer"></div>

                <div class="discovery-container">
                    <div id="swipeContainer"></div>
                </div>
            </div>
        `;

        this.loadSwipeableProfiles();
    }

    async loadSwipeableProfiles() {
        try {
            const profiles = await ApiService.getSwipeableProfiles();

            if (!profiles || profiles.length === 0) {
                document.getElementById('swipeContainer').innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">😢</div>
                        <h3>No profiles available</h3>
                        <p>No one else in your city yet, or you've swiped on everyone!</p>
                        <a href="#/dashboard" class="btn btn-primary" style="margin-top: 1rem;">Back to Dashboard</a>
                    </div>
                `;
                return;
            }

            this.renderSwipeCard(profiles[0], profiles);
        } catch (error) {
            console.error('Failed to load profiles:', error);
            document.getElementById('swipeContainer').innerHTML = `
                <div class="alert alert-error" style="margin-top: 1rem;">
                    Failed to load profiles: ${error.message}. Make sure your profile has a valid city.
                    <a href="#/profile" style="display: block; margin-top: 1rem;">Edit Your Profile</a>
                </div>
            `;
        }
    }

    renderSwipeCard(profile, profiles) {
        const index = profiles.indexOf(profile);
        const cardHTML = `
            <div class="swipe-card">
                <div class="profile-card">
                    <div class="profile-card-header">
                        <div style="text-align: center;">
                            <div style="font-size: 3rem; margin-bottom: 0.5rem;">👤</div>
                            <div>${profile.name} (${profile.age})</div>
                        </div>
                    </div>
                    <div class="profile-card-body">
                        <p class="profile-card-info">
                            <span class="profile-card-info-label">Location:</span> ${profile.city}
                        </p>
                        <p class="profile-card-info">
                            <span class="profile-card-info-label">Looking for:</span> ${profile.lookingForOption}
                        </p>
                        <div class="profile-card-bio">${profile.bio}</div>
                    </div>
                </div>
                <div class="swipe-actions">
                    <button class="btn btn-pass btn-lg" onclick="app.handleSwipe(false, ${profile.userId}, ${index})">
                        Pass
                    </button>
                    <button class="btn btn-like btn-lg" onclick="app.handleSwipe(true, ${profile.userId}, ${index})">
                        ❤️ Like
                    </button>
                </div>
            </div>
        `;

        document.getElementById('swipeContainer').innerHTML = cardHTML;
    }

    async handleSwipe(liked, profileId, index) {
        const btn = document.querySelector(`button[onclick="app.handleSwipe(${liked}, ${profileId}, ${index})"]`);
        if (btn) btn.disabled = true;

        try {
            // Send swipe to backend
            if (liked) {
                await ApiService.request(`/swipes/like/${profileId}`, { method: 'POST' });
            } else {
                await ApiService.request(`/swipes/pass/${profileId}`, { method: 'POST' });
            }

            // Get fresh list of swipeable profiles
            const profiles = await ApiService.getSwipeableProfiles();
            
            if (liked) {
                showAlert('❤️ Liked! Keep swiping to find more matches.', 'success', 'alertsContainer');
            } else {
                showAlert('Passed. Moving to next profile...', 'info', 'alertsContainer');
            }

            if (profiles && profiles.length > 0) {
                this.renderSwipeCard(profiles[0], profiles);
            } else {
                document.getElementById('swipeContainer').innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon">🎉</div>
                        <h3>You've reviewed all profiles!</h3>
                        <p>Come back tomorrow for more matches.</p>
                        <a href="#/dashboard" class="btn btn-primary" style="margin-top: 1rem;">Back to Dashboard</a>
                    </div>
                `;
            }
        } catch (error) {
            console.error('Swipe failed:', error);
            showAlert('Failed to record your swipe: ' + error.message, 'error', 'alertsContainer');
            if (btn) btn.disabled = false;
        }
    }

    showMatches() {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>Your Matches</h1>
                    <p>People who also liked you</p>
                </div>

                <div id="matchesContainer" class="matches-grid">
                    <div style="grid-column: 1 / -1; text-align: center;">
                        <div class="loading-lg"></div>
                    </div>
                </div>
            </div>
        `;

        this.loadMatches();
    }

    async loadMatches() {
        try {
            const matches = await ApiService.getMatches();

            if (!matches || matches.length === 0) {
                document.getElementById('matchesContainer').innerHTML = `
                    <div class="empty-state" style="grid-column: 1 / -1;">
                        <div class="empty-state-icon">💔</div>
                        <h3>No matches yet</h3>
                        <p>Keep swiping to find your perfect roommate!</p>
                        <a href="#/discover" class="btn btn-primary" style="margin-top: 1rem;">Start Swiping</a>
                    </div>
                `;
                return;
            }

            let html = '';
            matches.forEach(match => {
                html += `
                    <div class="match-item profile-card">
                        <div class="profile-card-header">
                            <div style="text-align: center;">
                                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">👤</div>
                                <div>${match.name}</div>
                            </div>
                        </div>
                        <div class="profile-card-body">
                            <div class="profile-card-age">${match.age} years old</div>
                            <div class="profile-card-info">
                                <span class="profile-card-info-label">Location:</span> ${match.city}
                            </div>
                            <div class="profile-card-bio">${match.bio}</div>
                            <div class="match-badge">✓ Match</div>
                        </div>
                    </div>
                `;
            });

            document.getElementById('matchesContainer').innerHTML = html;
        } catch (error) {
            document.getElementById('matchesContainer').innerHTML = `
                <div class="alert alert-error" style="grid-column: 1 / -1;">
                    Failed to load matches: ${error.message}
                </div>
            `;
        }
    }

    showSettings() {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>Settings</h1>
                </div>

                <div class="profile-section">
                    <h2>Account Settings</h2>
                    <div style="margin: 2rem 0;">
                        <button class="btn btn-danger btn-lg" onclick="app.logout()">
                            Logout
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    showChatList() {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <h1>Messages</h1>
                    <p>Chat with your matches</p>
                </div>

                <div id="alertsContainer"></div>
                <div id="chatListContainer" class="matches-grid">
                    <div style="grid-column: 1 / -1; text-align: center;">
                        <div class="loading-lg"></div>
                    </div>
                </div>
            </div>
        `;

        this.loadChatList();
    }

    async loadChatList() {
        try {
            const matches = await ApiService.getMatches();

            if (!matches || matches.length === 0) {
                document.getElementById('chatListContainer').innerHTML = `
                    <div class="empty-state" style="grid-column: 1 / -1;">
                        <div class="empty-state-icon">💬</div>
                        <h3>No conversations yet</h3>
                        <p>Start by matching with someone!</p>
                        <a href="#/discover" class="btn btn-primary" style="margin-top: 1rem;">Find Matches</a>
                    </div>
                `;
                return;
            }

            let html = '';
            matches.forEach(match => {
                html += `
                    <div class="match-item profile-card" onclick="app.openChat(${match.userId}, '${match.name.replace(/'/g, "\\'")}')">
                        <div class="profile-card-header">
                            <div style="text-align: center;">
                                <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">👤</div>
                                <div>${match.name}</div>
                            </div>
                        </div>
                        <div class="profile-card-body" style="cursor: pointer;">
                            <div class="profile-card-age">${match.age} years old</div>
                            <div class="profile-card-info">
                                <span class="profile-card-info-label">Location:</span> ${match.city}
                            </div>
                            <div style="margin-top: 1rem; color: var(--primary-color); font-weight: 600;">
                                Click to message →
                            </div>
                        </div>
                    </div>
                `;
            });

            document.getElementById('chatListContainer').innerHTML = html;
        } catch (error) {
            document.getElementById('chatListContainer').innerHTML = `
                <div class="alert alert-error" style="grid-column: 1 / -1;">
                    Failed to load chats: ${error.message}
                </div>
            `;
        }
    }

    openChat(userId, userName) {
        window.location.hash = `#/chat/${userId}`;
    }

    async showChatView(otherUserId) {
        this.root.innerHTML = `
            ${this.getNavbar()}
            <div class="container dashboard">
                <div class="dashboard-header">
                    <a href="#/chat" style="color: var(--primary-color); font-weight: 600;">← Back to Messages</a>
                    <h1 id="chatHeader">Messages</h1>
                </div>

                <div id="alertsContainer"></div>

                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: var(--radius-lg); overflow: hidden; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); display: flex; flex-direction: column; height: 500px;">
                    <div id="messagesContainer" style="flex: 1; overflow-y: auto; padding: 1.5rem; display: flex; flex-direction: column; gap: 1rem;">
                        <div style="text-align: center;">
                            <div class="loading-lg"></div>
                        </div>
                    </div>
                    
                    <div style="padding: 1.5rem; border-top: 1px solid var(--gray-200); display: flex; gap: 0.5rem;">
                        <textarea id="messageInput" placeholder="Type a message..." style="flex: 1; resize: none; height: 50px; font-size: 0.9rem;"></textarea>
                        <button class="btn btn-primary" onclick="app.sendMessage(${otherUserId})" style="height: 50px; width: 100px;">
                            Send
                        </button>
                    </div>
                </div>
            </div>
        `;

        // Allow Enter key to send
        document.getElementById('messageInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage(otherUserId);
            }
        });

        // Load conversation
        this.loadConversation(otherUserId);
    }

    async loadConversation(otherUserId) {
        try {
            const messages = await ApiService.getConversation(otherUserId);
            const messagesContainer = document.getElementById('messagesContainer');
            
            if (!messages || messages.length === 0) {
                messagesContainer.innerHTML = `
                    <div style="text-align: center; color: var(--gray-500); margin: auto;">
                        <div style="font-size: 2rem; margin-bottom: 1rem;">💬</div>
                        <p>No messages yet. Start the conversation!</p>
                    </div>
                `;
                return;
            }

            let html = '';
            messages.forEach(msg => {
                const isOwn = msg.senderId === this.currentUser?.id;
                html += `
                    <div style="display: flex; ${isOwn ? 'justify-content: flex-end;' : 'justify-content: flex-start;'}">
                        <div style="max-width: 70%; background: ${isOwn ? 'var(--primary-color)' : 'var(--gray-200)'}; color: ${isOwn ? 'white' : 'var(--gray-900)'}; padding: 0.75rem 1rem; border-radius: var(--radius-md); word-wrap: break-word;">
                            ${msg.message}
                            <div style="font-size: 0.75rem; opacity: 0.7; margin-top: 0.25rem;">
                                ${new Date(msg.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                            </div>
                        </div>
                    </div>
                `;
            });

            messagesContainer.innerHTML = html;
            // Scroll to bottom
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        } catch (error) {
            document.getElementById('messagesContainer').innerHTML = `
                <div class="alert alert-error">Failed to load messages: ${error.message}</div>
            `;
        }
    }

    async sendMessage(otherUserId) {
        const input = document.getElementById('messageInput');
        const message = input.value.trim();

        if (!message) return;

        try {
            // Send message
            await ApiService.sendMessage(otherUserId, message);
            
            // Clear input
            input.value = '';

            // Reload conversation
            await this.loadConversation(otherUserId);
        } catch (error) {
            showAlert('Failed to send message: ' + error.message, 'error', 'alertsContainer');
        }
    }

    logout() {
        if (confirm('Are you sure you want to logout?')) {
            AuthService.logout();
        }
    }

    getNavbar() {
        return `
            <nav class="navbar">
                <div class="container">
                    <div class="navbar-content">
                        <a href="#/dashboard" class="navbar-brand">
                            <img src="images/mates.jpg" alt="Mates Logo" class="navbar-logo">
                        </a>
                        <ul class="navbar-nav">
                            <li><a href="#/dashboard">Dashboard</a></li>
                            <li><a href="#/discover">Discover</a></li>
                            <li><a href="#/matches">Matches</a></li>
                            <li><a href="#/chat">Messages</a></li>
                            <li><a href="#/profile">Profile</a></li>
                        </ul>
                        <div class="navbar-user">
                            <div class="user-avatar">${this.currentUser?.username?.charAt(0).toUpperCase() || 'U'}</div>
                            <button class="btn btn-secondary btn-sm" onclick="window.location.hash='#/settings'">Settings</button>
                        </div>
                    </div>
                </div>
            </nav>
        `;
    }
}

// Initialize app when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.app = new RoommateFinder();
});
