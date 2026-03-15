# Build Fix

Incrementally fix build and compilation errors with minimal, safe changes.

## Step 1: Run Build

```bash
./gradlew compileJava 2>&1
```

## Step 2: Parse and Group Errors

1. Run the build command and capture output
2. Group errors by file path
3. Sort by dependency order (fix imports/types before logic errors)
4. Count total errors for progress tracking

## Step 3: Fix Loop (One Error at a Time)

For each error:

1. **Read the file** — Full context around the error line
2. **Diagnose** — Root cause (missing import, wrong type, syntax error)
3. **Fix minimally** — Smallest change that resolves the error
4. **Re-run build** — Verify the error is gone and no new errors introduced
5. **Move to next** — Continue with remaining errors

## Step 4: Guardrails

Stop and ask the user if:
- A fix introduces **more errors than it resolves**
- The **same error persists after 3 attempts**
- The fix requires **architectural changes**
- Build errors stem from **missing dependencies** (need to update build.gradle)

## Step 5: Summary

Show results:
- Errors fixed (with file paths)
- Errors remaining (if any)
- New errors introduced (should be zero)
- Suggested next steps

## Common Java/Gradle Fixes

| Error | Fix |
|-------|-----|
| Cannot find symbol | Check imports; add missing import or dependency |
| Incompatible types | Read both type definitions; fix the narrower type |
| Package does not exist | Check build.gradle dependencies |
| Duplicate class | Check for conflicting dependencies |
| Annotation processing | Verify annotation processor dependency |

Fix one error at a time for safety. Prefer minimal diffs over refactoring.
