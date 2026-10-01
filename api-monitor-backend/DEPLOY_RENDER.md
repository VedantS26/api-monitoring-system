# Deploy the backend on Render

This is the recommended deployment for a resume demo. Render's free web
services sleep after 15 minutes without inbound traffic, so the health-check
scheduler will pause while the service is asleep and the first request after
that can take about a minute.

## 1. Put the project on GitHub

Create a GitHub repository, commit the project (including the root
`render.yaml` file), and push it. Do not commit `.env` files or credentials.

## 2. Create a free MySQL-compatible database

Create a free TiDB Cloud Starter instance. It is MySQL-compatible and supplies
the host, port, username, password, and database name needed below.

In TiDB Cloud, create a database named `api_monitoring` and a SQL user with a
password. Copy its public connection details.

## 3. Create the Render service

1. Open the [Render Dashboard](https://dashboard.render.com/) and select
   **New > Blueprint**.
2. Connect your GitHub account and select this repository.
3. Render detects `render.yaml`. Enter the requested secret values, then click
   **Apply**.

Use these environment-variable values:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://<TIDB_HOST>:4000/api_monitoring?sslMode=VERIFY_IDENTITY
SPRING_DATASOURCE_USERNAME=<TIDB_USERNAME>
SPRING_DATASOURCE_PASSWORD=<TIDB_PASSWORD>
APP_CORS_ALLOWED_ORIGINS=https://<YOUR_FRONTEND>.vercel.app
RESEND_API_KEY=<optional Resend key>
```

Leave `JWT_SECRET` alone: Render creates a secure value automatically. If you
do not use email alerts, leave `RESEND_API_KEY` blank.

After deployment, copy the generated URL, for example:

```text
https://api-monitor-backend.onrender.com
```

## 4. Point the frontend to Render

In the Vercel project, add this production environment variable and redeploy:

```text
REACT_APP_API_BASE_URL=https://api-monitor-backend.onrender.com
```

Replace the URL with the exact Render URL shown in your service dashboard.

## Verify

Open the deployed frontend and register a new user. In Render, inspect
**Logs** if the backend fails to start. A successful startup includes a line
showing that the embedded web server started on Render's assigned port.
