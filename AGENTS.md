# AGENTS.md

Repository rules for AI coding agents working in this project.

## Secret Safety (STRICT — never bypass)

This repo protects against leaking secrets via a Gitleaks pre-commit hook at `.githooks/pre-commit`.

### Mandatory rules

1. **Never use `git commit --no-verify`.** The pre-commit hook is the primary secret gate. Bypassing it is prohibited unless the user explicitly, knowingly approves in that moment.
2. **Never edit, delete, rename, or disable `.githooks/pre-commit`** or the `core.hooksPath` configuration, and never neutralize the scan (e.g., by adding secrets to `.gitleaksignore` or a `gitleaks:allow` comment).
3. **If Gitleaks is not installed** (`gitleaks` command not found), do NOT skip the scan and do NOT bypass the hook. Stop and instruct the user to install it:
   - Windows: `winget install Gitleaks.Gitleaks`
   - macOS: `brew install gitleaks`
   - Linux: `curl -sSfL https://github.com/gitleaks/gitleaks/releases/download/v8.18.4/gitleaks_8.18.4_linux_x64.tar.gz | tar -xz -C /usr/local/bin`
4. **If the hook blocks a commit** ("Potential secret(s) detected"), do not force past it. Help the user locate and remove the secret, then re-commit.
5. **Never write real secrets into committed files.** API keys, tokens, and passwords belong in environment variables, secret managers, or untracked local files (e.g. `.env` — which must be gitignored).
6. **When scaffolding secrets in demo/test files**, always use clearly-fake placeholder values and confirm the file is removed before commit.

### Rationale

The CI pipeline runs Gitleaks too (`.github/workflows/ci.yml`), but CI only runs *after* a push. For a public repository, a leaked secret is already exposed by then. The pre-commit hook is the earliest and most important defense; treat it as non-negotiable.

## Project

- Java 17 Maven project (`pom.xml`).
- Build & test: `mvn -B clean test`.

## CI Configuration Convention (general rule for all projects)

Workflows must never hard-code project-specific settings that can change between forks/orgs.
Instead, read them from **GitHub repository variables** at runtime, with **sensible defaults**
baked into the workflow, so a fork or a different org works with zero setup.

- **Variables** (non-secret): declare a default inline in the workflow, e.g.
  `env: SONAR_ORG: ${{ vars.SONAR_ORG || 'auto-repo' }}`.
  Changing them is a **runtime-only operation** — update with `gh variable set`
  (no commit needed): `gh variable set SONAR_ORG --repo <owner/repo> --body "my-org"`.
- **Secrets**: never provide a default or a placeholder value. Use the
  `${{ secrets.X }}` reference and fail fast with a clear error if it is empty.
- **Feature on/off switches**: model as a boolean variable, e.g.
  `SONAR_ENABLED`, defaulting to enabled (`if: ${{ vars.SONAR_ENABLED != 'false' }}`).
- Do not create a commit just to change a variable value; edit the variable on GitHub instead.

Current variables used by `.github/workflows/ci.yml`:

| Variable          | Kind      | Default                     | Purpose                          |
| ----------------- | --------- | --------------------------- | -------------------------------- |
| `SONAR_ENABLED`   | variable  | `true` (enabled)            | Toggle the SonarCloud job        |
| `SONAR_ORG`       | variable  | `auto-repo`                 | SonarCloud organization key      |
| `SONAR_PROJECT_KEY` | variable | `chienthangbui_auto-repo` | SonarCloud project key           |
| `SONAR_TOKEN`     | secret    | none (required)             | SonarCloud API token             |
| `NVD_API_KEY`     | secret    | none (required)             | NVD API key for OWASP check      |

