# Refactor Clean

Safely identify and remove dead code with test verification at every step.

## Step 1: Detect Dead Code

Use Grep to find:
- Unused private methods
- Unused imports
- Unused local variables
- Unreachable code

## Step 2: Categorize Findings

| Tier | Examples | Action |
|------|----------|--------|
| **SAFE** | Unused private methods, unused imports | Delete with confidence |
| **CAUTION** | Public methods, interface implementations | Verify no external consumers |
| **DANGER** | Config classes, entry points | Investigate before touching |

## Step 3: Safe Deletion Loop

For each SAFE item:

1. **Run full test suite** — Establish baseline (all green)
2. **Delete the dead code** — Use Edit tool for surgical removal
3. **Re-run test suite** — Verify nothing broke
4. **If tests fail** — Immediately revert and skip this item
5. **If tests pass** — Move to next item

## Step 4: Consolidate Duplicates

After removing dead code, look for:
- Near-duplicate methods (>80% similar) — merge into one
- Redundant type definitions — consolidate
- Wrapper methods that add no value — inline them

## Step 5: Summary

```
Dead Code Cleanup
-------------------------------
Deleted:   X unused methods
           Y unused imports
Skipped:   Z items (tests failed)
Saved:     ~N lines removed
-------------------------------
All tests passing
```

## Rules

- **Never delete without running tests first**
- **One deletion at a time** — Atomic changes make rollback easy
- **Skip if uncertain** — Better to keep dead code than break production
- **Don't refactor while cleaning** — Separate concerns
