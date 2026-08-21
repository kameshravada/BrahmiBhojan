# Edge Cases and Risks

## Active Risks

- Area: Auth session hydration
- Risk/Edge case: Server/client auth mismatch can cause hydration warnings and icon flicker.
- Impact: Header UI instability and inconsistent login/profile rendering.
- Mitigation: Persist session in zustand and avoid client-only branching during SSR render paths.
- Owner: Frontend

- Area: OTP UX
- Risk/Edge case: Resend and verify race conditions on slow networks.
- Impact: Invalid challenge IDs and confusing user feedback.
- Mitigation: Disable action buttons during mutation pending states and clear stale errors on transitions.
- Owner: Frontend

- Area: Setup / local environment
- Risk/Edge case: Windows process locks on node modules during install/update.
- Impact: Corrupt installs and unstable dev startup.
- Mitigation: Stop running node processes before reinstall, then clean `node_modules` + lockfile if needed.
- Owner: Frontend

## Tomorrow Checks

- Re-verify login/logout flow after fresh dev start.
- Validate profile icon behavior for guest and logged-in users in desktop and mobile headers.
- Confirm no hydration warnings in browser console before adding catalog dynamic data.
