# Architecture Overview

This document provides a comprehensive overview of the architecture used in the KMP Multi-Module
Project Generator. Understanding this architecture will help you maintain and extend the project
effectively.

## Design Philosophy

The architecture of this project is designed with the following principles in mind:

1. **Clean Architecture**: Separation of concerns with distinct layers
2. **Multi-Module Design**: Modular, reusable components with clear boundaries
3. **Feature-First Organization**: Independent feature modules that can evolve separately
4. **Maximum Code Sharing**: Efficient reuse of code across platforms via Kotlin Multiplatform
5. **Platform-Specific Optimizations**: Native capabilities are leveraged when appropriate

## High-Level Architecture

The project follows a layered architecture pattern, combined with modular organization:

```
┌───────────────────────────────────────────────────────────────────┐
│                      Application Layer                            │
│  ┌────────────┐   ┌──────────┐   ┌────────────┐   ┌──────────┐    │
│  │cmp-android │   │ cmp-ios  │   │cmp-desktop │   │ cmp-web  │    │
│  └────────────┘   └──────────┘   └────────────┘   └──────────┘    │
└───────────────────────────────────────────────────────────────────┘
                          │
┌───────────────────────────────────────────────────────────────┐
│                        Feature Layer                          │
│     ┌────────┐      ┌─────────┐       ┌──────────┐            │
│     │  home  │      │ profile │       │ settings │            │
│     └────────┘      └─────────┘       └──────────┘            │
└───────────────────────────────────────────────────────────────┘
                          │
┌───────────────────────────────────────────────────────────────┐
│                   Domain & Data Layers                        │
│  ┌─────────┐  ┌────────┐  ┌─────────┐  ┌─────────┐  ┌───────┐ │
│  │  domain │  │  data  │  │ network │  │datastore│  │ model │ │
│  └─────────┘  └────────┘  └─────────┘  └─────────┘  └───────┘ │
└───────────────────────────────────────────────────────────────┘
                          │
┌───────────────────────────────────────────────────────────────┐
│                      Core Components                          │
│  ┌────────┐  ┌────────────┐  ┌─────────┐   ┌───────────┐      │
│  │ common │  │designsystem│  │   ui    │   │ analytics │      │
│  └────────┘  └────────────┘  └─────────┘   └───────────┘      │
└───────────────────────────────────────────────────────────────┘
```

## Module Structure

The project is organized into several types of modules:

### Platform Modules

These modules contain the platform-specific application entry points and UI implementations:

- **cmp-android**: Android application using Jetpack Compose
- **cmp-ios**: iOS application with SwiftUI integration
- **cmp-desktop**: Desktop application using Compose for Desktop
- **cmp-web**: Web application using Kotlin/JS and Compose Web
- **cmp-shared**: Common code shared across all platforms
- **cmp-navigation**: Navigation components and routing logic

### Feature Modules

Feature modules encapsulate specific user-facing functionality:

- **feature/home**: Home screen and related features
- **feature/profile**: User profile management features
- **feature/settings**: Application settings and configuration

### Core Modules

Core modules provide the essential infrastructure and shared functionality:

- **core/analytics**: Analytics and tracking capabilities
- **core/common**: Common utilities, extensions, and helpers
- **core/data**: Data repositories and sources
- **core/datastore**: Local storage management
- **core/domain**: Business logic and use cases
- **core/model**: Data models and entities
- **core/network**: Network communication
- **core/ui**: Shared UI components
- **core/designsystem**: Design system components

### Core Base Modules

Foundational components that provide essential infrastructure:

- **core-base/database**: Shared database layer
- **core-base/datastore**: Preference storage
- **core-base/network**: API communication infrastructure

### Build Logic

- **build-logic**: Custom Gradle plugins and build configuration

## Clean Architecture Layers

The project implements Clean Architecture with the following layers:

### 1. Presentation Layer

- **Responsibility**: UI components, user interaction, and view models
- **Location**: Platform modules and UI-related portions of feature modules
- **Dependencies**: Domain layer
- **Technologies**: Jetpack Compose, SwiftUI, Compose for Desktop, Compose Web

### 2. Domain Layer

- **Responsibility**: Business logic and use cases
- **Location**: core/domain module
- **Dependencies**: Model entities only (no data layer dependencies)
- **Technologies**: Pure Kotlin with no external dependencies

### 3. Data Layer

- **Responsibility**: Data management, repositories, and data sources
- **Location**: core/data, core/network, core/datastore modules
- **Dependencies**: Domain models, networking, and storage libraries
- **Technologies**: Ktor, SQLDelight, Datastore

### 4. Model Layer

- **Responsibility**: Data models and entities
- **Location**: core/model module
- **Dependencies**: None (or minimal shared utilities)
- **Technologies**: Pure Kotlin data classes

## Data Flow

The data flows through the architecture in the following way:

1. UI components in **platform modules** or **feature modules** interact with users and trigger
   actions
2. These actions are processed by **ViewModels** or **Presenters** that communicate with the domain
   layer
3. **Use Cases** in the domain layer execute business logic and interact with repositories
4. **Repositories** in the data layer coordinate data operations, choosing between remote and local
   sources
5. **Data Sources** interact with external systems (APIs) or local storage
6. Data flows back up through the same layers, transformed at each step to match the layer's
   requirements

## Source Set Hierarchy

One of the key architectural features is the hierarchical organization of source sets that enables
efficient code sharing:

```
common
  ├── nonAndroid
  │     ├── jvm
  │     ├── jsCommon
  │     └── native
  ├── jsCommon
  │     ├── js
  │     └── wasmJs
  ├── nonJsCommon
  │     ├── jvmCommon
  │     └── native
  ├── jvmCommon
  │     ├── android
  │     └── jvm
  ├── nonJvmCommon
  │     ├── jsCommon
  │     └── native
  ├── jvmJsCommon
  │     ├── jvm
  │     ├── js
  │     └── wasmJs
  ├── native
  │     └── apple
  │         ├── ios
  │         └── macos
  └── nonNative
        ├── jsCommon
        └── jvmCommon
```

For more details on the source set hierarchy, refer to
the [Source Set Hierarchy](PROJECT_HIERARCHY_TEMPLATE.md) document.

## Dependency Injection

The project uses Koin for dependency injection across all platforms:

- **Module Definition**: Dependencies are defined in Koin modules
- **Scope Management**: Features can have their own scopes when needed
- **ViewModels**: Integrated with Koin's ViewModel implementation

Example of a Koin module definition:

```kotlin
val dataModule = module {
    single { NetworkClient(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}
```

## Navigation

Navigation is handled through a dedicated module (cmp-navigation) with platform-specific
implementations:

- **Android**: Jetpack Navigation or Voyager
- **iOS**: SwiftUI NavigationView
- **Desktop**: Custom Compose for Desktop navigation
- **Web**: Custom routing implementation

## Error Handling

The project implements a consistent error handling strategy:

1. **Domain Errors**: Defined in the domain layer and represent business rule violations
2. **Data Errors**: Represent data access issues, converted to domain errors at the repository
   boundary
3. **Presentation Errors**: User-friendly error representations in the UI

## Testing Strategy

The architecture supports multiple testing approaches:

- **Unit Tests**: For business logic and domain use cases
- **Integration Tests**: For repositories and data sources
- **UI Tests**: For platform-specific UI components
- **End-to-End Tests**: For complete feature workflows

## Configuration and Build System

The project uses a custom Gradle plugin system for configuration:

- **Version Catalog**: Central dependency management in `gradle/libs.versions.toml`
- **Custom Plugins**: Defined in the `build-logic` module
- **Type-Safe Accessors**: For improved build script maintainability

## Communication Patterns

The project uses several communication patterns:

- **Coroutines and Flow**: For asynchronous operations and reactive data streams
- **StateFlow/SharedFlow**: For UI state and event management
- **Use Case Results**: For domain logic outcomes

## Design System

The architecture includes a dedicated design system module:

- **Theme**: Colors, typography, and spacing
- **Components**: Reusable UI elements
- **Tokens**: Design constants and values

## Security Considerations

The architecture addresses security through:

- **Secure Storage**: For sensitive data like credentials
- **Network Security**: HTTPS, certificate pinning
- **Input Validation**: At domain layer boundaries

## Conclusion

This architecture provides a solid foundation for cross-platform development using Kotlin
Multiplatform. By following clean architecture principles and organizing the codebase into focused
modules, the project achieves high maintainability, testability, and scalability.

For more detailed information, refer to:

- [Setup Guide](../setup/SETUP.md) for environment configuration
- [Source Set Hierarchy](PROJECT_HIERARCHY_TEMPLATE.md) for code sharing structure
- [Code Style Guide](../architecture/STYLE_GUIDE.md) for coding conventions