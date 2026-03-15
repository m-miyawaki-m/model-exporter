# Verification Command

Run comprehensive verification on current codebase state.

## Instructions

Execute verification in this exact order:

1. **Build Check**
   - Run `./gradlew compileJava`
   - If it fails, report errors and STOP

2. **Test Suite**
   - Run `./gradlew test`
   - Report pass/fail count

3. **Coverage Check**
   - Run `./gradlew test jacocoTestReport` (if JaCoCo configured)
   - Report coverage percentage

4. **Debug Statement Audit**
   - Search for `System.out.println` in source files (not test files)
   - Report locations

5. **Git Status**
   - Show uncommitted changes
   - Show files modified since last commit

## Output

Produce a concise verification report:

```
VERIFICATION: [PASS/FAIL]

Build:     [OK/FAIL]
Tests:     [X/Y passed]
Coverage:  [Z%]
Debug:     [OK/X System.out.println found]

Ready for PR: [YES/NO]
```

If any critical issues, list them with fix suggestions.

## Arguments

$ARGUMENTS can be:
- `quick` — Only build check
- `full` — All checks (default)
- `pre-commit` — Build + tests
- `pre-pr` — Full checks plus security scan
