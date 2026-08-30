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

## CI Configuration Convention (general)

When creating or editing `.github/workflows/*.yml`, follow the global convention
(also in `~/.copilot/copilot-instructions.md`):

- Variables (non-secret) have defaults baked into the workflow and can be overridden
  at run time via `gh variable set` — no commit needed to change a value.
- Secrets never have defaults; fail fast with a clear error if missing.
- Feature toggles are boolean variables that default to enabled.

