# Test Coverage

Analyze test coverage, identify gaps, and generate missing tests to reach 80%+ coverage.

## Step 1: Run Coverage

```bash
./gradlew test jacocoTestReport
```

If JaCoCo is not configured, add it to `build.gradle` first:
```groovy
plugins {
    id 'jacoco'
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
    }
}
```

## Step 2: Analyze Coverage Report

1. Parse `build/reports/jacoco/test/html/index.html`
2. List classes **below 80% coverage**, sorted worst-first
3. For each under-covered class, identify:
   - Untested methods
   - Missing branch coverage (if/else, switch, error paths)

## Step 3: Generate Missing Tests

For each under-covered class, generate tests following this priority:

1. **Happy path** — Core functionality with valid inputs
2. **Error handling** — Invalid inputs, exceptions
3. **Edge cases** — Empty collections, null values, boundary values
4. **Branch coverage** — Each if/else, switch case

### Test Generation Rules

- Place tests in `src/test/java/` mirroring source structure
- Use JUnit 5 (`@Test`, `@BeforeEach`, `@DisplayName`)
- Each test should be independent — no shared mutable state
- Name tests descriptively: `shouldExportValidUsersToCSV`

## Step 4: Verify

1. Run `./gradlew test` — all tests must pass
2. Re-run `./gradlew test jacocoTestReport` — verify improvement
3. If still below 80%, repeat Step 3 for remaining gaps

## Step 5: Report

```
Coverage Report
-------------------------------
Class                    Before  After
CsvExporter              45%     88%
JsonExporter             32%     82%
-------------------------------
Overall:                 67%     84%
```
