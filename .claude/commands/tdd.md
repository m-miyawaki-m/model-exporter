---
description: Enforce test-driven development workflow. Write tests FIRST, then implement minimal code to pass. Ensure 80%+ coverage.
---

# TDD Command

## What This Command Does

1. **Define Interfaces** — Define types/interfaces first
2. **Write Tests First** — Write failing tests (RED)
3. **Implement Minimal Code** — Write just enough to pass (GREEN)
4. **Refactor** — Improve code while keeping tests green (REFACTOR)
5. **Verify Coverage** — Ensure 80%+ test coverage

## TDD Cycle

```
RED -> GREEN -> REFACTOR -> REPEAT

RED:      Write a failing test
GREEN:    Write minimal code to pass
REFACTOR: Improve code, keep tests passing
REPEAT:   Next feature/scenario
```

## How It Works

1. **Define interfaces** for inputs/outputs
2. **Write tests that FAIL** (because code doesn't exist yet)
3. **Run tests** with `./gradlew test` and verify they fail for the right reason
4. **Write minimal implementation** to make tests pass
5. **Run tests** and verify they pass
6. **Refactor** code while keeping tests green
7. **Check coverage** with `./gradlew test jacocoTestReport` and add more tests if below 80%

## Test Framework

- **JUnit 5** for test execution
- **JaCoCo** for coverage
- Test files: `src/test/java/` mirroring `src/main/java/` structure
- Naming: `<ClassName>Test.java`

## Coverage Requirements

- **80% minimum** for all code
- **100% required** for:
  - Security-critical code
  - Core business logic
  - Data validation logic

## Best Practices

**DO:**
- Write the test FIRST, before any implementation
- Run tests and verify they FAIL before implementing
- Write minimal code to make tests pass
- Refactor only after tests are green
- Add edge cases and error scenarios

**DON'T:**
- Write implementation before tests
- Skip running tests after each change
- Write too much code at once
- Ignore failing tests
- Test implementation details (test behavior)

## Integration

- Use `/plan` first to understand what to build
- Use `/tdd` to implement with tests
- Use `/build-fix` if build errors occur
- Use `/code-review` to review implementation
- Use `/test-coverage` to verify coverage
