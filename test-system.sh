#!/bin/bash

echo "🚀 Construction CRM System - API Test Suite"
echo "=============================================="

BASE_URL="http://localhost:8080/api"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -n "Testing $description... "
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "%{http_code}" -X $method "$BASE_URL$endpoint" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -w "%{http_code}" -X $method "$BASE_URL$endpoint")
    fi
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ]; then
        echo -e "${GREEN}✓ SUCCESS (HTTP $http_code)${NC}"
        if [ -n "$body" ]; then
            echo "$body" | jq . 2>/dev/null || echo "$body"
        fi
    else
        echo -e "${RED}✗ FAILED (HTTP $http_code)${NC}"
        if [ -n "$body" ]; then
            echo "$body" | jq . 2>/dev/null || echo "$body"
        fi
    fi
    echo
}

echo "1. Testing Health Check..."
test_endpoint "GET" "/actuator/health" "" "Health Check"

echo "2. Testing Test Endpoint..."
test_endpoint "GET" "/test/hello" "" "Test Hello Endpoint"

echo "3. Testing Authentication..."
test_endpoint "POST" "/auth/login" '{"username":"admin","password":"admin123"}' "Admin Login"

echo "4. Testing User Registration..."
test_endpoint "POST" "/auth/register" '{
    "username":"testuser",
    "email":"test@example.com",
    "password":"test123",
    "firstName":"Test",
    "lastName":"User",
    "phone":"+1234567890",
    "position":"Worker",
    "department":"Construction"
}' "User Registration"

echo "5. Testing H2 Console Access..."
echo -n "Testing H2 Console... "
h2_response=$(curl -s -w "%{http_code}" http://localhost:8080/api/h2-console)
h2_code="${h2_response: -3}"
if [ "$h2_code" -eq 200 ]; then
    echo -e "${GREEN}✓ SUCCESS (HTTP $h2_code)${NC}"
else
    echo -e "${YELLOW}⚠ H2 Console not accessible (HTTP $h2_code)${NC}"
fi
echo

echo "6. Testing Actuator Endpoints..."
test_endpoint "GET" "/actuator/info" "" "Application Info"
test_endpoint "GET" "/actuator/metrics" "" "Application Metrics"

echo "7. Testing Protected Endpoints (should fail without auth)..."
echo -n "Testing Protected Users Endpoint... "
protected_response=$(curl -s -w "%{http_code}" "$BASE_URL/users")
protected_code="${protected_response: -3}"
if [ "$protected_code" -eq 401 ]; then
    echo -e "${GREEN}✓ CORRECTLY PROTECTED (HTTP 401)${NC}"
else
    echo -e "${RED}✗ NOT PROTECTED (HTTP $protected_code)${NC}"
fi
echo

echo "8. System Information..."
echo "Application URL: http://localhost:8080"
echo "API Base URL: $BASE_URL"
echo "H2 Console: http://localhost:8080/api/h2-console"
echo "Test Users:"
echo "  - admin/admin123 (Admin)"
echo "  - manager/manager123 (Manager)"
echo "  - engineer/engineer123 (Engineer)"
echo "  - worker/worker123 (Worker)"
echo "  - accountant/accountant123 (Accountant)"

echo
echo "🎉 Test Suite Completed!"
echo "If you see mostly green checkmarks, your system is working correctly!"