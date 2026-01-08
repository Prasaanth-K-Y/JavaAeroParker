# Logout Feature Implementation

## Overview
The logout feature has been successfully implemented using a **Token Blacklist** approach. When a user logs out, their JWT access token is added to a database blacklist, making it immediately invalid for all future requests.

## What Was Added

### 1. New Entity: TokenBlacklist
**File**: [TokenBlacklist.java](src/main/java/com/demo/ecommerce/model/TokenBlacklist.java)

Stores blacklisted tokens with:
- `token` - The JWT access token string
- `blacklistedAt` - Timestamp when token was blacklisted
- `expiresAt` - Token expiration time (for cleanup)

### 2. New Repository: TokenBlacklistRepository
**File**: [TokenBlacklistRepository.java](src/main/java/com/demo/ecommerce/repository/TokenBlacklistRepository.java)

Provides:
- `existsByToken()` - Check if token is blacklisted
- `deleteExpiredTokens()` - Clean up expired tokens

### 3. Updated JwtService
**File**: [JwtService.java](src/main/java/com/demo/ecommerce/service/JwtService.java)

Added methods:
- `isTokenBlacklisted(String token)` - Check if token is in blacklist
- `blacklistToken(String token)` - Add token to blacklist
- `getTokenExpiration(String token)` - Get token expiration date

### 4. Updated JwtAuthFilter
**File**: [JwtAuthFilter.java](src/main/java/com/demo/ecommerce/filter/JwtAuthFilter.java:62-67)

Added blacklist check that rejects requests with blacklisted tokens:
```java
// Check if token is blacklisted
if (jwtService.isTokenBlacklisted(jwt)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("Token has been invalidated");
    return;
}
```

### 5. Updated AuthService
**File**: [AuthService.java](src/main/java/com/demo/ecommerce/service/AuthService.java:79-93)

Added `logout(String token)` method that:
- Validates the token
- Checks if already blacklisted
- Adds token to blacklist

### 6. New Logout Endpoint
**File**: [AuthController.java](src/main/java/com/demo/ecommerce/controller/AuthController.java:43-47)

New endpoint: `POST /api/auth/logout`
- Accepts JSON body with `accessToken` (required) and `refreshToken` (optional)
- Blacklists both tokens in the database
- Returns success message on logout

Request format:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc..."  // optional
}
```

## How to Use

### 1. Login First
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Use the Access Token
Make authenticated requests with the token:
```bash
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. Logout (Secure - Blacklists Both Tokens)
Invalidate both access and refresh tokens:
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

**Note**: The `refreshToken` field is optional. If not provided, only the access token will be blacklisted.

Response:
```
Logged out successfully
```

### 4. Try Using the Access Token Again
After logout, any request with the blacklisted access token will fail:
```bash
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Response (HTTP 401):
```
Token has been invalidated
```

### 5. Try Using the Refresh Token
After logout, the refresh token is also blacklisted and cannot generate new access tokens:
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

Response (HTTP 400):
```json
{
  "error": "Refresh token has been invalidated"
}
```

## Database Setup

The `token_blacklist` table will be created automatically by JPA. If you need to create it manually:

```sql
CREATE TABLE token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(1000) NOT NULL UNIQUE,
    blacklisted_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL
);

CREATE INDEX idx_token ON token_blacklist(token);
CREATE INDEX idx_expires_at ON token_blacklist(expires_at);
```

## Security Features

1. **Dual Token Blacklisting**: Both access and refresh tokens are invalidated on logout
2. **Immediate Invalidation**: Tokens are rejected immediately after logout
3. **No Token Reuse**: Once logged out, tokens cannot be used again
4. **Refresh Token Protection**: Blacklisted refresh tokens cannot generate new access tokens
5. **Automatic Cleanup**: Expired tokens can be cleaned from blacklist periodically
6. **Stateless JWT**: Still maintains JWT stateless benefits for non-blacklisted tokens
7. **Defense in Depth**: Multiple layers of token validation ensure security

## Implementation Details

- **Token Lifetime**: Access tokens expire in 15 minutes (900,000 ms)
- **Blacklist Storage**: Database table `token_blacklist`
- **Filter Order**: Blacklist check happens after JWT parsing but before authentication
- **Thread Safety**: Repository operations are thread-safe through Spring Data JPA

## Testing Checklist

- [x] Compile project successfully
- [ ] Start the application
- [ ] Register a new user
- [ ] Login and get access token
- [ ] Make authenticated request (should succeed)
- [ ] Logout with the token
- [ ] Try authenticated request again (should fail with 401)
- [ ] Login again and get new token (should work)

## Future Enhancements

Consider these improvements:

1. **Scheduled Cleanup Task**: Add `@Scheduled` task to delete expired tokens
2. **Redis Cache**: Move blacklist to Redis for better performance
3. **Refresh Token Blacklist**: Also blacklist refresh tokens on logout
4. **Logout All Devices**: Add endpoint to blacklist all user tokens
5. **Admin Endpoint**: Allow admins to revoke any user's tokens

## Security Configuration

The logout endpoint is publicly accessible under `/api/auth/**` but requires a valid JWT token in the Authorization header to work. This is handled in:
- [SecurityConfig.java](src/main/java/com/demo/ecommerce/config/SecurityConfig.java:50)
