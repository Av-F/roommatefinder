# Security Best Practices for Roommate Finder

## Secrets Management

### Never Commit Secrets to Git
- JWT secret keys
- Database credentials
- API keys
- API tokens
- Private keys

### How Secrets Are Handled in This Project

#### Local Development
1. **Option 1: Using application-local.properties**
   - Copy `application-local.properties.example` to `application-local.properties`
   - Add your local secrets
   - `application-local.properties` is in `.gitignore` and won't be committed
   - Activate the profile: `mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=local'`

2. **Option 2: Using Environment Variables**
   - Set environment variables in your shell:
     ```bash
     export JWT_SECRET="your-secret-key-here"
     export JWT_EXPIRATION="3600000"
     ```
   - Then run the application

#### Production
1. **Using Environment Variables (Recommended)**
   - Set via deployment platform (Docker, Kubernetes, AWS, etc.):
     ```bash
     export JWT_SECRET="production-secret-key"
     export DB_USERNAME="prod_user"
     export DB_PASSWORD="prod_password"
     ```

2. **Using External Secrets Manager**
   - AWS Secrets Manager
   - Azure Key Vault
   - HashiCorp Vault
   - Google Cloud Secrets

### Configuration Priority
The application reads configuration in this order (highest priority first):
1. Environment variables
2. `application-{profile}.properties`
3. `application.properties`
4. Defaults in `@Value` annotations

### Files in .gitignore
```
application-prod.properties
application-local.properties
application-dev.properties
.env
.env.local
.env.*.local
```

## JWT Secret Best Practices

- **Minimum length**: 32 characters for HS256
- **Use strong random strings**: `openssl rand -base64 32`
- **Rotate regularly**: Change secrets periodically
- **Never log secrets**: Avoid printing to console or logs
- **Use HTTPS only**: Always transmit tokens over HTTPS in production

## How This Project Implements Security

```java
// JwtService now uses @Value annotation to read from environment
@Value("${jwt.secret:default-dev-key}")
private String secret;

// application.properties uses environment variable placeholders
jwt.secret=${JWT_SECRET:my-super-secret-key-that-is-at-least-32-bytes!}
```

## Verification Checklist

Before pushing to GitHub:
```bash
# Check if any secrets are in git history
git log --source --all -S "JWT_SECRET" 
git log --source --all -S "password"

# Check staged files for secrets
git diff --cached | grep -i "secret\|password\|token"

# Scan entire repo for common secret patterns
grep -r "secret\|password\|private_key" src/ --exclude-dir=target
```

## If You Accidentally Commit Secrets

1. **Immediately rotate the secret** in production
2. **Remove from local git history**:
   ```bash
   git filter-branch --tree-filter 'rm -f src/main/resources/application-prod.properties' HEAD
   git push --force
   ```
3. **Better approach**: Use `git-filter-repo` (modern replacement for git filter-branch)
4. **Consider**: Using services like TruffleHog to scan your history

## Additional Security Considerations

- [ ] Use HTTPS/TLS in production
- [ ] Implement rate limiting on auth endpoints
- [ ] Add CORS configuration if frontend is separate
- [ ] Implement proper error handling (don't expose internal details)
- [ ] Add security headers (Content-Security-Policy, X-Frame-Options, etc.)
- [ ] Regularly update dependencies for security patches
- [ ] Add comprehensive logging and monitoring
- [ ] Implement token refresh mechanism
- [ ] Consider adding 2FA/MFA in the future

## References

- [OWASP Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [Spring Boot Configuration Externalization](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

# Authentication & Authorization Implementation

## Overview

The API uses **JWT (JSON Web Tokens)** for stateless authentication. All protected endpoints require a valid Bearer token in the `Authorization` header.

## Authentication Flow

### 1. Register a New User

**POST** `/api/auth/register`

Request body:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

Response (201 Created):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Login (Get Token)

**POST** `/api/auth/login`

Request body:
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

Response (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Using the Token

Include the token in the `Authorization` header for all subsequent requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Example with cURL

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json"
```

### Example with JavaScript/Fetch

```javascript
const token = "eyJhbGciOiJIUzI1NiJ9...";

fetch('http://localhost:8080/api/users/me', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

### Example with Axios

```javascript
const token = "eyJhbGciOiJIUzI1NiJ9...";

axios.get('http://localhost:8080/api/users/me', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
.then(response => console.log(response.data))
.catch(error => console.error('Error:', error));
```

## Protected Endpoints

### User Endpoints

| Method | Endpoint | Description | Who Can Access |
|--------|----------|-------------|-----------------|
| GET | `/api/users/me` | Get current authenticated user | Any authenticated user |
| GET | `/api/users/{id}` | Get specific user | Only that user |
| PUT | `/api/users/{id}` | Update user profile | Only that user |
| DELETE | `/api/users/{id}` | Delete user account | Only that user |

#### Example: Get Current User
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

Response (200 OK):
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

### Profile Endpoints

| Method | Endpoint | Description | Who Can Access |
|--------|----------|-------------|-----------------|
| POST | `/api/profiles/create` | Create new profile | Any authenticated user |
| GET | `/api/profiles/me` | Get own profile | Any authenticated user |
| GET | `/api/profiles/{userId}` | View specific profile | Any authenticated user |
| GET | `/api/profiles/retrieve` | Browse all profiles | Any authenticated user |
| PATCH | `/api/profiles/update` | Update own profile | Only that user |

#### Example: Create Profile
```bash
curl -X POST http://localhost:8080/api/profiles/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bio": "Looking for roommate",
    "preferences": "Quiet, clean, respectful"
  }'
```

### Swipe Endpoints

| Method | Endpoint | Description | Authorization |
|--------|----------|-------------|-----------------|
| GET | `/api/swipes/discover` | Get profiles to swipe on | Authenticated user |
| POST | `/api/swipes/like/{targetId}` | Like a profile | Authenticated user (self only) |
| POST | `/api/swipes/pass/{targetId}` | Pass on profile | Authenticated user (self only) |
| GET | `/api/swipes/match/{id1}/{id2}` | Check if match | One of the users |

#### Example: Swipe
```bash
# Like a profile
curl -X POST http://localhost:8080/api/swipes/like/5 \
  -H "Authorization: Bearer $TOKEN"

# Pass on a profile
curl -X POST http://localhost:8080/api/swipes/pass/5 \
  -H "Authorization: Bearer $TOKEN"
```

### Match Endpoints

| Method | Endpoint | Description | Authorization |
|--------|----------|-------------|-----------------|
| GET | `/api/matches` | Get my matches | Authenticated user |
| GET | `/api/matches/{userId}` | Get specific user's matches | Only that user |

#### Example: Get Matches
```bash
curl -X GET http://localhost:8080/api/matches \
  -H "Authorization: Bearer $TOKEN"
```

### Chat Endpoints

| Method | Endpoint | Description | Authorization |
|--------|----------|-------------|-----------------|
| POST | `/api/chat/send?receiverId=X&message=Y` | Send message | Authenticated user (as sender) |
| GET | `/api/chat/{userId}` | Get conversation | As participant |

#### Example: Send Message
```bash
curl -X POST "http://localhost:8080/api/chat/send?receiverId=5&message=Hello!" \
  -H "Authorization: Bearer $TOKEN"
```

## Public Endpoints (No Authentication Required)

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/users/test` - Health check
- `GET /test/**` - Test endpoints (if any)

## Error Responses

### 401 Unauthorized
Returned when authentication fails

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Missing or invalid authentication token",
  "path": "/api/users/me"
}
```

Scenarios:
- No Authorization header provided
- Token is missing or malformed
- Token has expired
- Token is invalid/tampered with

### 403 Forbidden
Returned when authorization fails (user lacks permission)

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource",
  "path": "/api/users/5"
}
```

Scenarios:
- User tries to access another user's profile
- User tries to update someone else's information
- User tries to view someone else's private matches

## Security Implementation Details

### JWT Token Structure

**Token Format**: `<header>.<payload>.<signature>`

**Payload contains:**
```json
{
  "sub": "1",              // User ID (subject)
  "iss": "roommatefinder", // Issuer
  "iat": 1234567890,       // Issued at (timestamp)
  "exp": 1234671690        // Expiration (timestamp)
}
```

**Algorithm**: HS256 (HMAC with SHA-256)
**Expiration**: 24 hours (configurable)

### Security Architecture

```
Client Request
    ↓
[Authorization Header with Bearer Token]
    ↓
SecurityConfig (Spring Security)
    ↓
JwtFilter (Extract & Validate Token)
    ↓
JwtUtils (Verify Signature & Extract Claims)
    ↓
SecurityContext (Store Authenticated User)
    ↓
Controller (Access via SecurityUtils.getCurrentUser())
    ↓
Authorization Check (Compare with Path Variable)
    ↓
Response or 403 Forbidden
```

### Key Security Components

#### 1. SecurityConfig.java
- Disables CSRF (not needed for stateless JWT)
- Disables session management (stateless)
- Configures exception handlers for auth failures
- Registers JWT filter

#### 2. JwtFilter.java
- Extracts Bearer token from Authorization header
- Validates token signature
- Extracts user ID from token
- Sets authentication in SecurityContext

#### 3. JwtUtils.java
- Generates JWT tokens (with user ID as subject)
- Validates token signatures
- Extracts user ID from tokens
- Reads configuration from application.properties

#### 4. SecurityUtils.java
- Helper methods to access current user
- `getCurrentUser()` - Returns authenticated User object
- `getCurrentUserId()` - Returns current user's ID
- `isCurrentUser(userId)` - Checks if given ID matches current user

#### 5. CustomAuthenticationEntryPoint.java
- Handles 401 responses with JSON format
- Returns meaningful error messages

#### 6. CustomAccessDeniedHandler.java
- Handles 403 responses with JSON format
- Returns meaningful error messages

### Authorization Pattern in Controllers

All protected endpoints follow this pattern:

```java
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    Long currentUserId = SecurityUtils.getCurrentUserId();
    if (currentUserId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Authorization check
    if (!currentUserId.equals(id)) {
        log.warn("Unauthorized access: User {} tried to access User {}", currentUserId, id);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Proceed with business logic
    return userRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

## JWT Configuration

Configure JWT behavior via `application.properties`:

```properties
# JWT Settings
jwt.secret=${JWT_SECRET:supersecretkeysupersecretkey123456}
jwt.expiration=${JWT_EXPIRATION:86400000}
jwt.issuer=roommatefinder
```

**Environment Variables (Production):**
```bash
export JWT_SECRET="your-production-secret-key-at-least-32-chars"
export JWT_EXPIRATION="86400000"  # 24 hours in milliseconds
```

## Testing Authentication

### 1. Test Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

Save the returned token.

### 2. Test Protected Endpoint
```bash
TOKEN="<paste-token-from-above>"

curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Test Unauthorized Access
```bash
# Without token
curl -X GET http://localhost:8080/api/users/me

# With invalid token
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer invalid_token"
```

## Deployment Checklist

- [ ] Change JWT secret to a strong random value
- [ ] Set appropriate JWT expiration time
- [ ] Enable HTTPS/TLS
- [ ] Set secure CORS origins
- [ ] Enable rate limiting on auth endpoints
- [ ] Set up monitoring/logging for auth failures
- [ ] Configure secrets management (AWS Secrets Manager, etc.)
- [ ] Test all authentication scenarios
- [ ] Document API for clients
- [ ] Prepare token refresh strategy for future

## Future Enhancements

- [ ] Implement refresh token mechanism (long-lived tokens)
- [ ] Add role-based access control (RBAC)
- [ ] Implement API rate limiting
- [ ] Add audit logging for sensitive operations
- [ ] Implement OAuth2 / OpenID Connect
- [ ] Add multi-factor authentication (MFA)
- [ ] Token blacklisting for logout functionality
