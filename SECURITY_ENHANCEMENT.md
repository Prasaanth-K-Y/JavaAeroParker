# Security Enhancement: Dual Token Blacklisting

## Problem Statement

The initial logout implementation had a **critical security vulnerability**:
- Only the access token was blacklisted on logout
- The refresh token remained valid
- Users could generate new access tokens using the refresh token
- **Result**: Logout was ineffective - users could immediately "log back in"

## The Attack Scenario

```bash
# 1. User logs in
POST /api/auth/login
Response: { "accessToken": "token1", "refreshToken": "refresh1" }

# 2. User logs out (OLD IMPLEMENTATION - INSECURE)
POST /api/auth/logout
Body: { "accessToken": "token1" }
# Only token1 is blacklisted

# 3. Attacker uses refresh token (EXPLOIT!)
POST /api/auth/refresh
Body: { "refreshToken": "refresh1" }
Response: { "accessToken": "token2" }  ← NEW TOKEN GENERATED!

# 4. Attacker accesses protected resources
GET /api/items
Header: Authorization: Bearer token2  ← SUCCESS! User is "logged in" again
```

## The Fix

### Changes Made

1. **New DTO: LogoutRequest**
   - Accepts both `accessToken` (required) and `refreshToken` (optional)
   - Located at: [LogoutRequest.java](src/main/java/com/demo/ecommerce/dto/LogoutRequest.java)

2. **Updated Logout Endpoint**
   - Changed from header-based to request body
   - Now accepts both tokens
   - Located at: [AuthController.java](src/main/java/com/demo/ecommerce/controller/AuthController.java:43-47)

3. **Enhanced Logout Logic**
   - Blacklists access token (mandatory)
   - Blacklists refresh token if provided (optional)
   - Gracefully handles invalid refresh tokens
   - Located at: [AuthService.java](src/main/java/com/demo/ecommerce/service/AuthService.java:84-115)

4. **Refresh Token Validation**
   - Added blacklist check before generating new access tokens
   - Prevents blacklisted refresh tokens from being used
   - Located at: [AuthService.java](src/main/java/com/demo/ecommerce/service/AuthService.java:67-69)

## New Logout Flow

```bash
# 1. User logs in
POST /api/auth/login
Response: { "accessToken": "token1", "refreshToken": "refresh1" }

# 2. User logs out (NEW IMPLEMENTATION - SECURE)
POST /api/auth/logout
Body: {
  "accessToken": "token1",
  "refreshToken": "refresh1"
}
# BOTH tokens are blacklisted

# 3. Attacker tries to use refresh token (BLOCKED!)
POST /api/auth/refresh
Body: { "refreshToken": "refresh1" }
Response: ERROR 400 - "Refresh token has been invalidated"  ← REJECTED!

# 4. Attacker tries to use old access token (BLOCKED!)
GET /api/items
Header: Authorization: Bearer token1
Response: HTTP 401 - "Token has been invalidated"  ← REJECTED!

# 5. User is truly logged out
No way to generate new tokens or access protected resources
```

## Security Benefits

1. **Complete Logout**: Users cannot bypass logout by using refresh tokens
2. **Token Revocation**: Both short-lived and long-lived tokens are invalidated
3. **Attack Prevention**: Prevents token reuse after logout
4. **Optional Refresh Token**: Backward compatible - refresh token is optional
5. **Graceful Degradation**: Invalid refresh tokens don't break logout flow

## API Changes

### Before (Insecure):
```bash
POST /api/auth/logout
Header: Authorization: Bearer <access-token>
```

### After (Secure):
```bash
POST /api/auth/logout
Content-Type: application/json
Body: {
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc..."  // optional but recommended
}
```

## Migration Guide

### For Frontend/Mobile Clients:

**Old Code:**
```javascript
// Insecure - only blacklists access token
async function logout() {
  await fetch('/api/auth/logout', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  // Clear tokens from storage
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}
```

**New Code:**
```javascript
// Secure - blacklists both tokens
async function logout() {
  const accessToken = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');

  await fetch('/api/auth/logout', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      accessToken: accessToken,
      refreshToken: refreshToken
    })
  });

  // Clear tokens from storage
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}
```

## Testing the Fix

### Test 1: Verify Access Token Blacklisting
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "test"}'

# Save the tokens, then logout
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"accessToken": "...", "refreshToken": "..."}'

# Try using access token - should fail
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer ..."
# Expected: 401 Unauthorized - "Token has been invalidated"
```

### Test 2: Verify Refresh Token Blacklisting
```bash
# After logout, try using refresh token - should fail
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "..."}'
# Expected: 400 Bad Request - "Refresh token has been invalidated"
```

## Conclusion

This security enhancement closes a critical vulnerability in the logout implementation. Users can now be confident that when they log out:
- Their access token is immediately invalid
- Their refresh token cannot generate new access tokens
- Their session is truly terminated
- No one can use their tokens to access the system

The implementation is backward compatible (refresh token is optional) while providing maximum security when both tokens are provided.
