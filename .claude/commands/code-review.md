# Code Review

Comprehensive security and quality review of uncommitted changes:

1. Get changed files: `git diff --name-only HEAD`

2. For each changed file, check for:

**Security Issues (CRITICAL):**
- Hardcoded credentials, API keys, tokens
- SQL injection vulnerabilities
- Missing input validation
- Path traversal risks
- Insecure dependencies

**Code Quality (HIGH):**
- Methods > 50 lines
- Files > 800 lines
- Nesting depth > 4 levels
- Missing error handling
- System.out.println debugging statements
- TODO/FIXME comments
- Missing Javadoc for public APIs

**Best Practices (MEDIUM):**
- Mutation patterns (use immutable instead)
- Missing tests for new code
- Unchecked exceptions without documentation
- Raw types (use generics)

3. Generate report with:
   - Severity: CRITICAL, HIGH, MEDIUM, LOW
   - File location and line numbers
   - Issue description
   - Suggested fix

4. Block commit if CRITICAL or HIGH issues found

Never approve code with security vulnerabilities!
