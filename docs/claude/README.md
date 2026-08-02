# Documentation - Deep-Dive Guides

**Project:** KMP Project Template
**Last Updated:** 2026-02-13

This directory contains comprehensive guides for understanding and working with this Kotlin Multiplatform project.

---

## 📚 Quick Navigation

### Getting Started
- **[Onboarding Guide](onboarding.md)** - For new developers joining the project
- **[Deployment Playbook](deployment-playbook.md)** - Step-by-step deployment instructions

### Deep Technical Guides
- **[Version Handling](version-handling.md)** - Understanding version sanitization across platforms
- **[Secrets Management](secrets-management.md)** - Complete guide to managing secrets and credentials
- **[GitHub Actions Deep Dive](github-actions-deep-dive.md)** - Workflow architecture and design

### Reference
- **[Troubleshooting](troubleshooting.md)** - Comprehensive problem-solving guide
- **[Patterns & Best Practices](patterns.md)** - Coding standards and patterns

---

## 📖 Guide Descriptions

### [Onboarding Guide](onboarding.md)
**For:** New team members
**Time:** 30-60 minutes
**Covers:**
- Project overview and architecture
- Setting up development environment
- Building the app for the first time
- Understanding the codebase structure
- First deployment walkthrough

---

### [Deployment Playbook](deployment-playbook.md)
**For:** Anyone deploying to production
**Time:** Reference (varies by platform)
**Covers:**
- Pre-deployment checklists
- Platform-specific deployment steps
- Post-deployment verification
- Rollback procedures
- Production monitoring

---

### [Version Handling](version-handling.md)
**For:** Understanding version formats
**Time:** 15 minutes
**Covers:**
- Gradle version generation
- Firebase version requirements
- App Store version sanitization
- Version code calculation
- Why versions differ across platforms

---

### [Secrets Management](secrets-management.md)
**For:** Setting up deployment credentials
**Time:** 30-45 minutes
**Covers:**
- Complete secrets inventory (30+ secrets)
- File-to-secret mapping
- Generating secrets with keystore-manager.sh
- Adding secrets to GitHub Actions
- Rotating secrets
- Security best practices

---

### [GitHub Actions Deep Dive](github-actions-deep-dive.md)
**For:** Understanding CI/CD pipeline
**Time:** 30-45 minutes
**Covers:**
- Reusable workflow architecture
- Custom action design
- Job dependency management
- Matrix builds for Desktop
- Workflow optimization
- Debugging workflow failures

---

### [Troubleshooting](troubleshooting.md)
**For:** Fixing issues and errors
**Time:** Reference (as needed)
**Covers:**
- Build failures (all platforms)
- Deployment errors
- Code signing issues (iOS/macOS)
- Firebase integration problems
- Play Store/App Store rejections
- Known infrastructure bugs

---

### [Patterns & Best Practices](patterns.md)
**For:** Writing quality code
**Time:** 20-30 minutes
**Covers:**
- Project conventions
- Code style (Spotless, Detekt)
- Git workflow
- Commit message format
- Testing practices
- Security guidelines

---

## 🗂️ Documentation Structure

```
docs/
├── claude/                          # Deep-dive guides (you are here)
│   ├── README.md                    # This file
│   ├── onboarding.md               # New developer guide
│   ├── deployment-playbook.md      # Deployment reference
│   ├── version-handling.md         # Version formats explained
│   ├── secrets-management.md       # Secrets guide
│   ├── github-actions-deep-dive.md # CI/CD deep dive
│   ├── troubleshooting.md          # Problem solving
│   └── patterns.md                 # Best practices
│
├── analysis/                        # Verification reports
│   ├── BUGS_AND_ISSUES.md          # Known issues
│   └── VERIFICATION_COMPLETE.md    # Infrastructure verification
│
└── plans/                           # Implementation plans
    └── 2026-02-13-claude-code-setup-UPDATED.md
```

---

## 🎯 Documentation by Role

### I'm a New Developer
**Start here:**
1. [Onboarding Guide](onboarding.md)
2. [Patterns & Best Practices](patterns.md)
3. [Troubleshooting](troubleshooting.md) (bookmark for reference)

### I'm Deploying to Production
**Start here:**
1. [Deployment Playbook](deployment-playbook.md)
2. [Secrets Management](secrets-management.md) (if first time)
3. [Troubleshooting](troubleshooting.md) (if issues occur)

### I'm Setting Up CI/CD
**Start here:**
1. [GitHub Actions Deep Dive](github-actions-deep-dive.md)
2. [Secrets Management](secrets-management.md)
3. [Version Handling](version-handling.md)

### I'm Investigating a Bug
**Start here:**
1. [Troubleshooting](troubleshooting.md)
2. [Known Issues](../analysis/BUGS_AND_ISSUES.md)
3. Platform-specific CLAUDE.md (`.github/`, `fastlane/`, `scripts/`)

---

## 📋 Documentation by Platform

### Android
**Primary:**
- [Deployment Playbook](deployment-playbook.md) - Android section
- [fastlane/CLAUDE.md](../../fastlane/CLAUDE.md) - Android lanes

**Related:**
- [Secrets Management](secrets-management.md) - Android secrets
- [Troubleshooting](troubleshooting.md) - Android issues

### iOS
**Primary:**
- [Deployment Playbook](deployment-playbook.md) - iOS section
- [fastlane/CLAUDE.md](../../fastlane/CLAUDE.md) - iOS lanes
- [Version Handling](version-handling.md) - Version sanitization

**Related:**
- [Secrets Management](secrets-management.md) - iOS secrets
- [Troubleshooting](troubleshooting.md) - iOS issues
- [scripts/CLAUDE.md](../../scripts/CLAUDE.md) - iOS scripts

### macOS
**Primary:**
- [Deployment Playbook](deployment-playbook.md) - macOS section
- [.github/CLAUDE.md](../../.github/CLAUDE.md) - macOS actions

### Desktop (Windows/macOS/Linux)
**Primary:**
- [Deployment Playbook](deployment-playbook.md) - Desktop section
- [.github/CLAUDE.md](../../.github/CLAUDE.md) - Desktop action

### Web
**Primary:**
- [Deployment Playbook](deployment-playbook.md) - Web section
- [.github/CLAUDE.md](../../.github/CLAUDE.md) - Web action

---

## 🔍 Finding Information Quickly

### "How do I deploy to..."
→ [Deployment Playbook](deployment-playbook.md)

### "What secrets do I need for..."
→ [Secrets Management](secrets-management.md)

### "Why is the version different on..."
→ [Version Handling](version-handling.md)

### "Build is failing with..."
→ [Troubleshooting](troubleshooting.md)

### "How does the CI/CD work..."
→ [GitHub Actions Deep Dive](github-actions-deep-dive.md)

### "What's the convention for..."
→ [Patterns & Best Practices](patterns.md)

### "I'm new, where do I start..."
→ [Onboarding Guide](onboarding.md)

---

## 🐛 Known Issues

See [Known Issues & Bugs](../analysis/BUGS_AND_ISSUES.md) for infrastructure bugs:
- 🔴 Critical: Firebase `groups` parameter ignored (workaround available)
- 🔴 Critical: Signing parameter naming inconsistency
- 🟡 Medium: Hardcoded keystore filename
- 🟡 Medium: Version generation may fail silently
- 🟡 Medium: Production promotion has no validation
- And 3 more low-priority issues

---

## 📞 Additional Resources

### Primary Documentation
- **[Root CLAUDE.md](../../CLAUDE.md)** - Project overview and quick links
- **[.github/CLAUDE.md](../../.github/CLAUDE.md)** - GitHub Actions reference
- **[fastlane/CLAUDE.md](../../fastlane/CLAUDE.md)** - Fastlane lanes reference
- **[scripts/CLAUDE.md](../../scripts/CLAUDE.md)** - Bash scripts reference

### External Links
- **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)** - KMP official docs
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** - UI framework
- **[Fastlane](https://docs.fastlane.tools/)** - Deployment automation
- **[Firebase App Distribution](https://firebase.google.com/docs/app-distribution)** - Beta testing
- **[Fastlane Match](https://docs.fastlane.tools/actions/match/)** - iOS code signing

---

## 💡 Tips for Using This Documentation

1. **Bookmark frequently used guides** - Especially Troubleshooting and Deployment Playbook
2. **Start with Onboarding if new** - It will reference other guides as needed
3. **Use search (Cmd/Ctrl+F)** - All guides are searchable
4. **Check Known Issues first** - Your problem might already be documented with a fix
5. **Follow links to related docs** - Each guide cross-references others
6. **Use Claude Code** - Ask Claude to guide you through any guide interactively

---

**Last Updated:** 2026-02-13
**Project:** KMP Project Template
**Platforms:** Android | iOS | macOS | Desktop | Web
