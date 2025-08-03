#!/bin/bash

echo "=== Construction CRM System API Test ==="
echo

# Base URL
BASE_URL="http://localhost:8080/api"

echo "1. Testing Health Check..."
curl -s "$BASE_URL/actuator/health" | jq .
echo

echo "2. Testing Authentication..."
echo "Login as admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "Login Response:"
echo "$LOGIN_RESPONSE" | jq .

# Extract token
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.accessToken')

if [ "$TOKEN" != "null" ] && [ "$TOKEN" != "" ]; then
    echo
    echo "3. Testing Users API with token..."
    curl -s -H "Authorization: Bearer $TOKEN" "$BASE_URL/users" | jq .
    echo

    echo "4. Testing Projects API..."
    curl -s -H "Authorization: Bearer $TOKEN" "$BASE_URL/projects" | jq .
    echo

    echo "5. Testing Project Statistics..."
    curl -s -H "Authorization: Bearer $TOKEN" "$BASE_URL/projects/stats/status" | jq .
    echo

    echo "6. Testing User Statistics..."
    curl -s -H "Authorization: Bearer $TOKEN" "$BASE_URL/users/stats/department" | jq .
    echo

else
    echo "Failed to get token from login response"
fi

echo
echo "=== Test Complete ==="