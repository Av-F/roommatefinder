#!/usr/bin/env bash
# Quick test script for authentication endpoints

HOST="http://localhost:8080"
EMAIL="testuser@example.com"
PASSWORD="password123"

echo "=========================================="
echo "Roommate Finder API - Authentication Test"
echo "=========================================="

# Register
echo -e "\n1. Testing Registration..."
RESPONSE=$(curl -s -X POST "$HOST/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "'$EMAIL'",
    "password": "'$PASSWORD'"
  }')

TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "✗ Registration failed"
  echo "Response: $RESPONSE"
  exit 1
fi

echo "✓ Registration successful"
echo "Token: $TOKEN"

# Get current user
echo -e "\n2. Testing Get Current User (/api/users/me)..."
curl -s -X GET "$HOST/api/users/me" \
  -H "Authorization: Bearer $TOKEN" | jq .

# Try without token (should fail)
echo -e "\n3. Testing without token (should fail with 401)..."
curl -s -X GET "$HOST/api/users/me" \
  -w "\nHTTP Status: %{http_code}\n" | head -n 5

# Login
echo -e "\n4. Testing Login..."
LOGIN_RESPONSE=$(curl -s -X POST "$HOST/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "'$EMAIL'",
    "password": "'$PASSWORD'"
  }')

LOGIN_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$LOGIN_TOKEN" ]; then
  echo "✗ Login failed"
  else
  echo "✓ Login successful"
fi

echo -e "\n=========================================="
echo "Authentication tests completed!"
echo "=========================================="
