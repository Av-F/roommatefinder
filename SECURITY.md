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
