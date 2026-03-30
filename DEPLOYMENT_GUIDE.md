# Deployment Guide

## Frontend Deployment (GitHub Pages)

### Prerequisites
- GitHub repository created
- GitHub Actions enabled in repository settings

### Setup Steps

1. **Push to GitHub**
   ```bash
   git add .
   git commit -m "Setup GitHub Pages deployment"
   git push origin main
   ```

2. **Enable GitHub Pages**
   - Go to your repository → Settings → Pages
   - Source: Deploy from a branch
   - Branch: gh-pages (will be auto-created by GitHub Actions)
   - Save

3. **Wait for Deployment**
   - Go to Actions tab and wait for the workflow to complete
   - Your frontend will be live at: `https://YOUR_GITHUB_USERNAME.github.io/roommatefinder/`

4. **Update Backend URL**
   - After your backend is deployed to Render (see below), update the URL in `src/main/resources/static/js/config.js`
   - Replace `YOUR_RENDER_APP_URL` with your actual Render domain

---

## Backend Deployment (Render.com)

### Prerequisites
- Render.com account (free tier available)
- Your GitHub repository with this project

### Setup Steps

1. **Create a Render Account**
   - Go to https://render.com and sign up
   - Connect your GitHub account

2. **Create a New Web Service**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select the repository

3. **Configure Service**
   - **Name**: roommatefinder (or your preferred name)
   - **Runtime**: Java
   - **Build Command**: `./mvnw clean package`
   - **Start Command**: `java -jar target/roommatefinder-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free (if eligible)

4. **Environment Variables**
   - Add any required environment variables (database credentials, JWT secrets, etc.)
   - Example:
     ```
     DATABASE_URL=your_database_url
     JWT_SECRET=your_jwt_secret
     ```

5. **Deploy**
   - Click "Create Web Service"
   - Render will automatically build and deploy
   - Wait for deployment to complete (~5-10 minutes)
   - Your backend URL will be: `https://roommatefinder.onrender.com` (or your chosen name)

6. **Backend API Base Path**
   - Ensure your Spring Boot API is configured correctly
   - Default endpoints should be at: `https://roommatefinder.onrender.com/api/*`

7. **Update Frontend Config**
   - After backend is deployed, update `config.js`:
     ```javascript
     return 'https://roommatefinder.onrender.com/api';
     ```

### CORS Configuration

Your backend Spring Boot app needs to allow requests from GitHub Pages:

In your `application.properties` or `application.yaml`:
```properties
cors.allowed-origins=https://YOUR_GITHUB_USERNAME.github.io
```

Or add CORS configuration in your Spring Security config to allow your GitHub Pages URL.

---

## HTTPS & Mixed Content Warnings

- GitHub Pages uses HTTPS
- Ensure your Render backend uses HTTPS
- Update `config.js` to use HTTPS URLs only
- Browser will block HTTP requests from HTTPS pages

---

## Testing

1. **Local Testing**
   ```bash
   # Backend
   ./mvnw spring-boot:run
   
   # Frontend - open in browser
   http://localhost:8080
   ```

2. **Production Testing**
   - Visit: `https://YOUR_GITHUB_USERNAME.github.io/roommatefinder/`
   - Check browser console for API connection errors
   - Test authentication and core features

---

## Troubleshooting

### Frontend not deploying
- Check GitHub Actions tab for workflow errors
- Ensure branch is `main` (or change trigger in workflow file)
- Verify `src/main/resources/static/` has files

### Backend not starting
- Check Render logs in dashboard
- Verify build command runs locally: `./mvnw clean package`
- Check that `target/` has the JAR file

### API calls failing
- Check browser Network tab for failed requests
- Verify backend URL in `config.js` is correct
- Check CORS settings on backend
- Verify backend is running (check Render logs)

### Mixed Content Error
- All URLs must use HTTPS in production
- Update `config.js` to use HTTPS backend URL

