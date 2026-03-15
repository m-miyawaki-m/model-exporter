# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project Overview

Java/Gradle data export utility. Exports model objects to CSV and JSON formats.

- **Language:** Java 11
- **Build tool:** Gradle 7.2
- **Dependencies:** OpenCSV 5.7.1, Jackson 2.13.4
- **Package:** `com.example.exporter`

## Build & Run

```bash
# Build
./gradlew compileJava

# Run
./gradlew run

# Test
./gradlew test

# Test with coverage (JaCoCo)
./gradlew test jacocoTestReport
```

## Core Principles

1. **Test-Driven** — Write tests before implementation, 80%+ coverage required
2. **Security-First** — Never compromise on security; validate all inputs
3. **Immutability** — Prefer immutable objects. Use final fields, return defensive copies
4. **Plan Before Execute** — Plan complex features before writing code

## Coding Style

### Immutability (CRITICAL)

Prefer immutable patterns in Java:
- Use `final` fields where possible
- Return defensive copies of mutable collections (`Collections.unmodifiableList()`, `List.copyOf()`)
- Prefer records (Java 16+) or value objects with no setters when feasible

### File Organization

- **200-400 lines typical, 800 max** per file
- Functions/methods: **<50 lines**
- No deep nesting: **>4 levels is a red flag**
- Organize by feature/domain, not by type
- High cohesion, low coupling

### Error Handling

- Handle errors explicitly at every level
- Use checked exceptions for recoverable errors, unchecked for programming errors
- Never silently swallow exceptions
- Log detailed context server-side
- Never expose stack traces or internal details to users

### Input Validation

- Validate all user input at system boundaries
- Fail fast with clear error messages
- Never trust external data (API responses, user input, file content)
- Use `Objects.requireNonNull()` for null checks

### Code Quality Checklist

Before marking work complete:
- [ ] Code is readable and well-named
- [ ] Methods are small (<50 lines)
- [ ] Files are focused (<800 lines)
- [ ] No deep nesting (>4 levels)
- [ ] Proper error handling
- [ ] No hardcoded values (use constants or config)

## Testing Requirements

### Minimum Coverage: 80%

Test Types (ALL required):
1. **Unit Tests** — Individual methods, utilities
2. **Integration Tests** — File I/O, external library interactions

### TDD Workflow (MANDATORY)

1. Write test first (RED) — test should FAIL
2. Write minimal implementation (GREEN) — test should PASS
3. Refactor (IMPROVE) — verify coverage 80%+

Troubleshoot failures: check test isolation -> verify mocks -> fix implementation (not tests, unless tests are wrong).

## Security Guidelines

Before ANY commit:
- [ ] No hardcoded secrets (API keys, passwords, tokens)
- [ ] All user inputs validated
- [ ] SQL injection prevention (parameterized queries) if applicable
- [ ] Error messages don't leak sensitive data
- [ ] File path inputs are sanitized (no path traversal)

Secret management: NEVER hardcode secrets. Use environment variables or config files excluded from VCS.

## Git Workflow

### Commit Format

```
<type>: <description>

<optional body>
```

Types: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`, `perf`, `ci`

### PR Workflow

1. Analyze full commit history (not just latest commit)
2. Use `git diff [base-branch]...HEAD` to see all changes
3. Draft comprehensive PR summary
4. Include test plan

## Design Patterns

### Repository Pattern

Encapsulate data access behind consistent interfaces:
- Define standard operations: findAll, findById, create, update, delete
- Business logic depends on abstract interface, not storage mechanism

### API Response Format

Use consistent envelope for all API responses:
- Success/status indicator
- Data payload
- Error message field
- Metadata for paginated responses

## Key Commands

- `/tdd` — Test-driven development workflow
- `/plan` — Implementation planning
- `/code-review` — Quality review
- `/build-fix` — Fix build errors
- `/verify` — Comprehensive verification
- `/quality-gate` — Quality pipeline check
- `/refactor-clean` — Dead code cleanup
- `/test-coverage` — Coverage analysis and gap filling
