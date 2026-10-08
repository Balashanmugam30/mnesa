# Model Context Protocol (MCP) Inventory & Configuration

**Timestamp:** 2026-10-08T19:54:00+05:30  
**Project:** MNESA

---

## 1. Verified & Configured MCP Servers

| Server Name | Scope | Underlying Package / Command | Required Permissions | Auth Requirement | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **postgres** | Workspace-Local (`.agents/mcp_config.json`) | `@modelcontextprotocol/server-postgres` | Localhost loopback TCP socket (`127.0.0.1:5432`) | Local development credentials (`mnesa_dev_user`) | Configured (Read/Query capability for local database) |
| **github** | Global & Workspace-Local | `@modelcontextprotocol/server-github` | Network access to `api.github.com` | GitHub PAT (Authenticated via keyring / token) | Active & Connected |
| **chrome-devtools** | Global & Workspace-Local | `chrome-devtools-mcp@latest` | Local browser automation / CDP socket | None (Local DevTools protocol) | Active & Connected |
| **firebase** | Global & Workspace-Local | `firebase-tools mcp` | Google Cloud / Firebase API | Firebase CLI login (`firebase login`) | Installed & Configured |
| **vercel** | Global & Workspace-Local | `mcp-remote https://mcp.vercel.com` | Network access to Vercel APIs | Vercel Token / Account Sign-in | Active & Connected |
| **postman** | Workspace-Local (`.agents/mcp_config.json`) | `@postman/mcp-server` | API test execution & collection inspection | Postman API Key (Optional for local mocks) | Configured |

---

## 2. Prohibited MCP Servers (Strictly Excluded)

The following servers are intentionally **excluded** to maintain architectural discipline:
- `supabase` / `supabase-server`: Excluded. MNESA uses PostgreSQL directly; Supabase is forbidden as the backend layer.
- `dart` / `flutter`: Excluded. MNESA uses native Java Android and Next.js.
- Google Workspace (Docs/Sheets/Slides): Excluded.
- Unnecessary cloud or enterprise security vendors: Excluded.
- Kubernetes / GKE: Excluded until production scale justifies it.
