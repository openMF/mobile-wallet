fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android deploy_internal

```sh
[bundle exec] fastlane android deploy_internal
```

Deploy internal tracks to Google Play

### android promote_to_beta

```sh
[bundle exec] fastlane android promote_to_beta
```

Promote internal tracks to beta on Google Play

### android promote_to_production

```sh
[bundle exec] fastlane android promote_to_production
```

Promote beta tracks to production on Google Play

### android deploy_on_firebase

```sh
[bundle exec] fastlane android deploy_on_firebase
```

Upload Android application to Firebase App Distribution

----


## iOS

### ios build_ios

```sh
[bundle exec] fastlane ios build_ios
```

Build iOS application

### ios deploy_on_firebase

```sh
[bundle exec] fastlane ios deploy_on_firebase
```

Upload iOS application to Firebase App Distribution

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
