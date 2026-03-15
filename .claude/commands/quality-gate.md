# Quality Gate Command

Run the quality pipeline on demand.

## Usage

`/quality-gate [path|.] [--fix] [--strict]`

- default target: current directory (`.`)
- `--fix`: allow auto-format/fix where configured
- `--strict`: fail on warnings

## Pipeline

1. **Build check:** `./gradlew compileJava`
2. **Test suite:** `./gradlew test`
3. **Coverage:** `./gradlew test jacocoTestReport` (if configured)
4. **Style check:** Check for coding style violations
5. Produce a concise remediation list

## Arguments

$ARGUMENTS:
- `[path|.]` optional target path
- `--fix` optional
- `--strict` optional
