# 🔐 Quick Reference: Authentication Guide

## 1. Get Login Token

```bash
# Register or Login (choose one)
TOKEN=$(curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"u@test.com","password":"pass123"}' \
  -s | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

## 2. Use Token in Any Request

```bash
# Set your token
TOKEN="your_token_here"

# GET request
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users/me

# POST request
curl -X POST http://localhost:8080/api/profiles/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"bio":"Hello","preferences":"Clean"}'
```

## 3. Headers Required

```
Authorization: Bearer <TOKEN>
Content-Type: application/json
```

## 4. Common Endpoints

| Action | Endpoint | Token? |
|--------|----------|--------|
| Register | `POST /api/auth/register` | ❌ |
| Login | `POST /api/auth/login` | ❌ |
| Get Me | `GET /api/users/me` | ✅ |
| My Profile | `GET /api/profiles/me` | ✅ |
| Browse Profiles | `GET /api/profiles/retrieve` | ✅ |
| Like Profile | `POST /api/swipes/like/5` | ✅ |
| My Matches | `GET /api/matches` | ✅ |
| Send Chat | `POST /api/chat/send?receiverId=5&message=Hi` | ✅ |

## 5. Error Codes

| Code | Meaning | Fix |
|------|---------|-----|
| 200 | Success | ✓ |
| 201 | Created | ✓ |
| 400 | Bad Request | Check input |
| 401 | Unauthorized | Add token |
| 403 | Forbidden | Not allowed |
| 404 | Not Found | Wrong URL |
| 500 | Server Error | Check logs |

## 6. Common Issues

**"Missing token"**
- Add `Authorization: Bearer <token>` header

**"Token expired"**
- Login again to get new token
- Token lasts 24 hours

**"Access denied"**
- You're trying to access another user's data
- Only view/edit your own resources

**"Invalid credentials"**
- Wrong email/password combo
- Check registration was successful

## 7. JavaScript Example

```javascript
const token = "eyJhbGciOiJIUzI1NiJ9...";

// Login
async function login() {
  const res = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email: 'user@example.com',
      password: 'password'
    })
  });
  const data = await res.json();
  return data.token;
}

// Use token
async function getProfile() {
  const res = await fetch('http://localhost:8080/api/users/me', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return res.json();
}
```

## 8. Postman Setup

1. **Get Token:**
   - POST `http://localhost:8080/api/auth/login`
   - Body: `{"email":"u@test.com","password":"pass"}`
   - Copy `token` value

2. **Use Token:**
   - In Headers tab: `Authorization: Bearer <token>`
   - Make request

## 9. Database Check

H2 Console: `http://localhost:8080/h2-console`
- URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (empty)

## 10. Stop/Start Application

```bash
# Start
mvn spring-boot:run

# Stop (Ctrl+C in terminal)

# Or build JAR
mvn clean package
java -jar target/roommatefinder-0.0.1-SNAPSHOT.jar
```

---

**Remember:** Every protected endpoint needs the Authorization header with your token! 🔑
