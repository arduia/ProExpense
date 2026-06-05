# ProExpense Refactoring Context & Documentation

**Date**: June 5, 2026  
**Session**: Claude Code Refactoring Planning & Documentation  
**Project**: ProExpense - Expense Tracking Mobile App  
**Branch**: `refactor/doc-old-practices`

---

## Project Overview

### Current State
- **Type**: Android native app (Kotlin)
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **UI Framework**: Jetpack Compose (hybrid with Fragments)
- **Networking**: Retrofit + Ktor
- **Testing**: 60+ unit tests with MockK, Robolectric
- **Version**: 1.0.0-beta08

### Target State
- **Type**: KMP (Kotlin Multiplatform) application
- **Platforms**: iOS, Android, Web
- **Architecture**: Multi-module KMP with shared and platform-specific code
- **UI Framework**: Jetpack Compose (shared across platforms)
- **Goal**: Share business logic across all platforms while maintaining clean architecture

---

## Refactoring Goal

**Preserve and document all valuable patterns and domain logic from the current architecture that can be reused in the new KMP Compose Multi-Module design.**

The team needs reference documentation showing:
- ✅ Which patterns are valuable and worth keeping
- ✅ How to implement those patterns in the new architecture
- ✅ What NOT to carry forward (tool-specific implementation details)

---

## What We Discovered

### Initial Exploration (Phase 1)

#### Current Architecture Highlights

**Strengths:**
1. **MVVM Pattern** - Clean separation of UI from business logic
2. **Repository Pattern** - Single point of access to data
3. **Result Type Pattern** - Type-safe error handling (no exceptions)
4. **Value Objects** - `Amount` class handles currency precision beautifully
5. **Multi-Module Structure** - Separate modules for backup, currency, graph
6. **Comprehensive Testing** - 60+ unit tests, good coverage strategy
7. **Dependency Injection** - Well-organized Hilt modules
8. **Reactive Streams** - Coroutines + Flow throughout
9. **Code Conventions** - Consistent naming and organization
10. **Security Practices** - Input validation, no hardcoded secrets

**Key Architectural Insights:**
- Domain layer is **pure Kotlin with no framework dependencies**
- **Value objects encode business rules** (Amount with precision handling)
- **Repositories abstract all data sources** (DB, network, cache)
- **Result type enables functional error handling** without exceptions
- **Builder pattern** used for complex filters
- **Mapper pattern** separates domain from UI concerns
- **Filter models** with validation in constructors

---

## Documentation Journey

### Phase 1: Initial Documentation (12 Guides)
Created comprehensive guides covering all aspects of current architecture:
- Architecture patterns
- Dependency injection (Hilt)
- Reactive programming (Coroutines/Flow)
- Database (Room)
- UI layer (Compose/Fragments)
- Domain modeling
- Testing
- Shared utilities
- Code conventions
- Logging & security
- CI/CD workflow
- Best practices checklist

### Phase 2: KMP Adaptation
Updated documentation to focus on KMP instead of Android:
- Replaced Hilt examples with Koin
- Replaced Room with SQLDelight
- Updated UI examples to pure Compose
- Changed to expect/actual patterns
- Focused on multi-module structure

### Phase 3: Tool Removal (FINAL)
**Realized the core insight**: Tool-specific documentation doesn't transfer between architectures.

Removed 5 guides that were too tool-specific:
- ❌ Dependency Injection (tool: Koin)
- ❌ Reactive Programming (tool: Coroutines/Flow usage)
- ❌ Database Persistence (tool: SQLDelight)
- ❌ UI Layer (tool: Compose)
- ❌ Shared Utilities (tool: Android extensions)

**Kept 7 guides with only principles:**
- ✅ Architecture Patterns (principles, not tools)
- ✅ Domain Modeling (pure business logic)
- ✅ Testing Guidelines (strategies, not frameworks)
- ✅ Code Conventions (Kotlin style)
- ✅ Logging & Security (practices, not implementations)
- ✅ CI/CD Workflow (universal)
- ✅ Best Practices Checklist (tool-agnostic)

---

## Final Documentation (7 Guides, ~80 KB)

### 1. Architecture Patterns (347 lines)
**Transferable Patterns:**
- MVVM (Model-View-ViewModel)
- Repository Pattern
- Result Type Pattern (sealed class for async operations)
- Value Objects (domain concepts with behavior)
- Builder Pattern (complex object construction)
- Mapper Pattern (layer transformations)
- Multi-Layer Data Access (DB, network, cache)

**Why These Carry Forward:** These are principles about how to organize code, not about specific tools.

### 2. Domain Modeling (482 lines)
**Core Concepts:**
- Immutable data classes
- Value objects with business logic
- Validation in constructors
- Operator overloading
- No framework imports

**Example: Amount Class**
```kotlin
class Amount(private val storeValue: Long) {
    // Stores money as cents (Long) to avoid floating-point errors
    // Converts to/from BigDecimal for display
    operator fun plus(other: Amount) = Amount(storeValue + other.storeValue)
    operator fun times(multiplier: Int) = Amount(storeValue * multiplier)
}
```

**Why It Matters:** Domain logic is the most valuable part of an application. It's independent of frameworks and technologies.

### 3. Testing Guidelines (379 lines)
**Principles:**
- Unit testing with AAA pattern (Arrange-Act-Assert)
- Mock external dependencies
- Test behavior, not implementation
- Test error cases
- Pure functions first (domain logic)
- In-memory test implementations for data layer

**Coverage Goals:**
- Domain: 90%+ (pure logic, easy to test)
- Data: 80%+ (core logic with mocks)
- ViewModel: 70%+ (state management)
- UI: 50%+ (framework-dependent)

**Why It Matters:** Testing strategies work in any language/framework.

### 4. Code Conventions (519 lines)
**Kotlin Best Practices:**
- Type-based naming (`ViewModel`, `Repository`, `Mapper`)
- PascalCase for classes, UPPER_CASE for constants
- Prefer `val` over `var` (immutability)
- Data classes and sealed classes
- Extension functions
- Comment philosophy (WHY not WHAT)

**Why It Matters:** These are language conventions, not framework-specific.

### 5. Logging & Security (283 lines)
**Principles:**
- No hardcoded secrets (load from config/environment)
- Validate all external input (at boundaries)
- Never log PII (personally identifiable information)
- Use Result type for error handling
- Parameterized queries (prevent SQL injection)
- Least privilege principle

**Why It Matters:** Security practices apply to all applications.

### 6. CI/CD Workflow (536 lines)
**DevOps Practices:**
- Build automation (CircleCI)
- Semantic versioning
- Build variants (dev/prod)
- Testing pipeline
- Deployment strategy

**Why It Matters:** Build and deployment processes are universal.

### 7. Best Practices Checklist (288 lines)
**Quick Reference:**
- Architecture checklist
- Domain modeling checklist
- Testing checklist
- Code review checklist
- Security validation checklist
- Common mistakes table

**Why It Matters:** Single document for all principles.

---

## Key Decisions Made

### Decision 1: Documentation Scope
**Question**: Should we document the tools or the principles?

**Decision**: Document only principles that transfer between architectures.

**Rationale**: The new architecture will choose its own tools. We want to preserve valuable knowledge about how to structure applications, not how to use specific tools.

### Decision 2: Framework Independence
**Question**: Should we include Hilt/Koin/DI framework documentation?

**Decision**: Remove it. DI is an implementation detail that each architecture chooses.

**Rationale**: MVVM, Repository, and Result Type patterns work with any DI framework. The principle (separation of concerns) is what matters.

### Decision 3: Database Abstraction
**Question**: Should we document Room/SQLDelight specifics?

**Decision**: Remove database tool documentation. Keep domain modeling.

**Rationale**: How data is stored is an implementation detail. What data models look like (domain entities, value objects) is the valuable part.

### Decision 4: UI Framework Details
**Question**: Should we document Compose/Fragments/state management frameworks?

**Decision**: Remove framework specifics. Keep MVVM and state management principles.

**Rationale**: ViewModels and state containers are patterns. Whether you use Compose, SwiftUI, or React, the pattern is the same.

---

## Critical Insights About Current Architecture

### The Amount Class (Brilliant Design)
```kotlin
class Amount(private val storeValue: Long) {
    // Problem: Floating-point arithmetic loses precision
    // Solution: Store as Long (cents), convert to/from BigDecimal
}
```

**Why this matters**: Money handling is critical. This pattern handles precision correctly and should be replicated in new architecture.

### Value Objects Pattern
Current code uses value objects extensively:
- `Amount` (currency handling)
- `DateRange` (date validation)
- `FilterCriteria` (complex filters)

**Why this matters**: These encode business rules that don't change with technology.

### Repository Pattern
Every data access goes through repositories:
- Clear interfaces
- Result-based error handling
- Multiple data source support

**Why this matters**: This abstraction allows switching implementations without changing business logic.

### Result Type Pattern
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Why this matters**: Type-safe error handling without exceptions is elegant and testable.

---

## Multi-Module Architecture Understanding

### Current Structure
```
app/                      # Main Android app
├── domain/               # Pure business logic
├── data/                 # Data layer (Room, Retrofit)
├── ui/                   # UI layer (Compose/Fragments)
└── di/                   # Dependency injection

currency-store/           # Currency exchange utilities
backup/                   # Excel backup/restore
expense-backup/           # Expense-specific backup
week-expense-graph/       # Custom chart widget
shared/                   # Common utilities
```

### Lessons for New Architecture
1. **Separate by layer, not feature**: Domain → Data → UI
2. **Make utilities reusable**: Extract common code to shared modules
3. **Keep domain pure**: No framework imports
4. **Use interfaces**: Abstract repositories, data sources, mappers

---

## Security & Privacy Practices Found

1. **ProGuard Configuration** - Obfuscates release builds
2. **Input Validation** - URIs, file sizes, user data
3. **API Keys from Properties** - Never hardcoded
4. **No PII in Logs** - Only logs safe information
5. **Parameterized Queries** - Prevents SQL injection
6. **Type-Safe Amounts** - Prevents negative amounts

**All these should carry forward to new architecture.**

---

## Testing Insights

### Test Structure
- `src/test/java/` - Unit tests (60+)
- `src/androidTest/java/` - Instrumented tests
- Testing patterns: MockK, Mockito, Robolectric

### What's Tested Well
- Repository layer (with mocked DAOs)
- ViewModel state management
- Domain logic (validation, calculations)
- Mapper transformations

### Testing Philosophy
- AAA pattern (Arrange-Act-Assert)
- Mock external dependencies
- Test behavior, not implementation
- Good coverage of error cases

---

## Dependencies & Tools Used

### Current Stack
| Category | Tool | Version |
|----------|------|---------|
| **Language** | Kotlin | 1.8.10 |
| **Build** | Gradle | 8.0.2 |
| **DI** | Hilt | 2.57 |
| **Database** | Room | 2.7.2 |
| **Network** | Retrofit | 2.9.0 |
| **Async** | Coroutines | 1.6.4 |
| **UI** | Compose | 2025.08.00 |
| **Logging** | Timber | 4.7.1 |
| **Testing** | MockK, JUnit4 | 1.13.8, 4.13.2 |

**Note**: These are implementation details. The patterns remain valuable regardless of tool versions.

---

## What NOT to Carry Forward

❌ **Android-Specific Code**
- Activities, Fragments, Lifecycle management
- Android Context usage
- View Binding
- Android Permissions
- WorkManager (can be replaced with KMP alternatives)

❌ **Tool-Specific Patterns**
- Hilt DI (choose Koin, manual, or others)
- Room database (choose SQLDelight, Realm, or others)
- LiveData (choose StateFlow or other state management)
- Jetpack Navigation (choose framework-appropriate navigation)

---

## What TO Carry Forward

✅ **Domain Logic**
- Amount class (currency precision)
- Expense entity structure
- Validation rules
- Business logic

✅ **Architecture Patterns**
- MVVM separation
- Repository abstraction
- Result type error handling
- Value objects
- Mapper transformations

✅ **Testing Practices**
- AAA pattern
- Mocking strategies
- Unit test organization
- Coverage goals

✅ **Code Quality**
- Kotlin conventions
- Type safety
- Immutability preferences
- Code organization

✅ **Security & Privacy**
- Input validation
- Secret management
- Error handling
- Data minimization

---

## Branch Information

### Current Branch
**Name**: `refactor/doc-old-practices`

**Commits**:
1. Initial 12 comprehensive guides
2. Refactor for KMP (tools updated)
3. Remove Android utilities
4. Update all for KMP Compose
5. Remove tool-specific documentation (FINAL)

**Status**: Ready for team review

### Push Location
All changes pushed to: `origin/refactor/doc-old-practices`

---

## Documentation Files

### Kept (Principles-Based)
1. `01_ARCHITECTURE_PATTERNS.md` - 347 lines
2. `06_DOMAIN_MODELING.md` - 482 lines
3. `07_TESTING_GUIDELINES.md` - 379 lines
4. `09_CODE_CONVENTIONS.md` - 519 lines
5. `10_LOGGING_SECURITY.md` - 283 lines
6. `11_CICD_WORKFLOW.md` - 536 lines
7. `BEST_PRACTICES_CHECKLIST.md` - 288 lines

**Total**: ~2,834 lines, ~80 KB

### Removed (Tool-Specific)
- ❌ 02_DEPENDENCY_INJECTION.md (Koin details)
- ❌ 03_REACTIVE_PROGRAMMING.md (Coroutines/Flow usage)
- ❌ 04_DATABASE_PERSISTENCE.md (SQLDelight details)
- ❌ 05_UI_LAYER.md (Compose specifics)
- ❌ 08_SHARED_UTILITIES.md (Android extensions)

---

## How To Use This Documentation

### For New KMP Architecture

1. **Architecture Design**: Use `01_ARCHITECTURE_PATTERNS.md` to ensure MVVM, Repository, and Result patterns are implemented
2. **Domain Models**: Follow patterns in `06_DOMAIN_MODELING.md` for value objects and business logic
3. **Testing**: Apply strategies from `07_TESTING_GUIDELINES.md`
4. **Code Style**: Follow `09_CODE_CONVENTIONS.md` for Kotlin conventions
5. **Security**: Reference `10_LOGGING_SECURITY.md` for security practices
6. **Workflow**: Use `11_CICD_WORKFLOW.md` for versioning and deployment
7. **Quick Check**: Use `BEST_PRACTICES_CHECKLIST.md` during code review

### Tool Choices
- ✅ Choose ANY DI framework (Koin, manual, other)
- ✅ Choose ANY database (SQLDelight, Realm, Firebase, other)
- ✅ Choose ANY UI framework (Compose, SwiftUI, React, other)
- ✅ Choose ANY async pattern (Coroutines, async/await, other)
- ✅ Choose ANY logging framework (Timber alternative, native logging, other)

**The patterns work with all of them.**

---

## Key Principles to Remember

### 1. Architecture Matters More Than Tools
Tools come and go. Good architecture principles remain valuable.

### 2. Domain Logic is Precious
The business logic in Amount class, expense validation, and filters is the most valuable part. Protect it.

### 3. Patterns Over Frameworks
- MVVM (architecture pattern) vs Hilt (DI framework)
- Repository (architectural pattern) vs Room (database tool)
- Result type (error handling pattern) vs exceptions (language feature)

### 4. Testability is Non-Negotiable
Every architectural decision should consider: "Can this layer be tested independently?"

### 5. Security is Built-In
From day one, enforce:
- No hardcoded secrets
- Input validation at boundaries
- No PII in logs
- Type-safe error handling

### 6. Code Organization Matters
Clear separation of concerns makes code:
- Easier to understand
- Easier to test
- Easier to modify
- Easier to reuse

---

## Next Steps for Team

1. **Review Documentation**: Read the 7 guides to understand current best practices
2. **Design New Architecture**: Use these patterns as foundation
3. **Choose Tools**: Decide on KMP framework, DI, database, UI based on needs
4. **Implement Patterns**: Apply MVVM, Repository, Result type, Value objects
5. **Maintain Quality**: Use checklist during development
6. **Test Thoroughly**: Follow testing guidelines
7. **Secure by Default**: Apply security practices from day one

---

## Summary

We've extracted the **timeless wisdom** from the current ProExpense architecture while removing **tool-specific implementation details**.

The 7 remaining guides contain knowledge about:
- How to structure applications (MVVM)
- How to handle data (Repository)
- How to handle errors (Result type)
- How to represent concepts (Value objects)
- How to test effectively (Testing patterns)
- How to write quality code (Conventions)
- How to keep applications secure (Security practices)

**These principles apply regardless of which specific tools you choose for the new KMP architecture.**

---

**Status**: ✅ Complete and ready for team review  
**Quality**: Framework-agnostic, principle-based  
**Size**: ~80 KB, 7 focused guides  
**Usability**: Reference documentation for new architecture decisions
