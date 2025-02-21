![Frame 9 (2)](https://github.com/user-attachments/assets/4d53ff45-5348-41a5-98cd-a3dbabb52703)

<div align="center">

# Mobile Wallet
Mobile Wallet is a Kotlin Multiplatform(KMP) based project built on top of Apache <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a> API.
Following recommended architecture and design patterns, the application is developed using the latest technologies and frameworks/libraries, such as Jetpack Compose, Ktor, Ktorfit, and Koin. It is designed to be cross-platform, supporting Android, iOS, Desktop, and Web platforms.

![Kotlin](https://img.shields.io/badge/Kotlin-7f52ff?style=flat-square&logo=kotlin&logoColor=white)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-4c8d3f?style=flat-square&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Jetpack%20Compose%20Multiplatform-000000?style=flat-square&logo=android&logoColor=white)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-DB413D.svg?style=flat)
![badge-js](http://img.shields.io/badge/platform-web-FDD835.svg?style=flat)


[![PR Checks](https://github.com/openMF/mobile-wallet/actions/workflows/pr-check.yml/badge.svg)](https://github.com/openMF/mobile-wallet/actions/workflows/pr-check.yml)
[![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white)](https://join.slack.com/t/mifos/shared_invite/zt-2wvi9t82t-DuSBdqdQVOY9fsqsLjkKPA)
[![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=flat-square&logo=jira&logoColor=white)](https://mifosforge.jira.com/jira/software/c/projects/MW/boards/66)
[![Zoom](https://img.shields.io/badge/Zoom-2D8CFF?style=flat-square&logo=zoom&logoColor=white)](https://us02web.zoom.us/meeting/register/PIZxFF_3Qi2H056amyjj2Q#/registration)

</div>

> \[!Note]
> **We're moving towards to Jira for issue tracking. Please use [Jira](https://mifosforge.jira.com/jira/software/c/projects/MW/boards/66) for issue tracking.**
> **And Join our [slack](https://join.slack.com/t/mifos/shared_invite/zt-2wvi9t82t-DuSBdqdQVOY9fsqsLjkKPA) community channel `mobile-wallet` to discuss all things about Mobile Wallet development. Please keep discussions focused and avoid cross-posting across channels.**
> **Please join our daily Mobile Stand-Up on [Zoom](https://us02web.zoom.us/meeting/register/PIZxFF_3Qi2H056amyjj2Q#/registration).**

<div align="center"><a name="readme-top"></a></div>

### Run the Project
![Screenshot (154)](https://github.com/user-attachments/assets/761063ed-83f8-4443-b58f-2b68a4c74c5d)

- **Android App**: Select the `mifospay-android` run configuration and click **Run**.
- **Desktop App**: Select the `mifospay-desktop` run configuration and click **Run**.
- **Web App (JavaScript)**: Select the `mifospasy-web-js` run configuration and click **Run**.
- **iOS App**: Select the `mifospay-ios` run configuration and click **Run**.

> \[!Important]
> To run the iOS app, you must have a macOS device with Xcode installed. Currently, the `mifospay-web-wasm` app is not working as expected. We are working on it and it will be available soon.

### Demo Credentials
- **Fineract Instance**: `venus.mifos.io`
- **Username**: `venus`
- **Password**: `Venus2023#`

### Join Us on Slack
Mifos boasts an active and vibrant contributor community, Please join us on [slack](https://join.slack.com/t/mifos/shared_invite/zt-2f4nr6tk3-ZJlHMi1lc0R19FFEHxdvng). Once you've joined the mifos slack community, please join the `#mobile-wallet` channel to engage with mobile-wallet development. If you encounter any difficulties joining our Slack channel, please don't hesitate to open an issue. This will allow us to assist you promptly or send you an invitation.

### How to Contribute
Thank you for your interest in contributing to the Mobile Wallet project by Mifos! We welcome all contributions and encourage you to follow these guidelines to ensure a smooth and efficient collaboration process.

The issues should be raised via the GitHub issue tracker. For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#issue-tracker">here</a>. All fixes should be proposed via pull requests. For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#pull-requests">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>.

### Branch Policy
For development purposes, always pull from the **dev** branch, as all contributions and updates are merged into this branch. Upon completion of development, changes are subsequently merged into the **master** branch, which represents the stable and bug-free version of the code.

### Development Setup
Please refer to the  [Development Setup Guide](https://github.com/openMF/mobile-wallet/wiki/Set-up-an-environment) for detailed instructions on configuring the development environment.

### Committing Your Changes
After making changes in your local repository, you will need to commit them to your GitHub repository.
If you are unfamiliar with the process of committing changes, please refer to the [Committing Your Changes](https://github.com/openMF/mobile-wallet/wiki/Committing-Your-Changes) guide.

### Squashing Your Commits
To ensure a clean and organized Git history, contributors are encouraged to squash their commits before merging.  
Instructions on how to squash commits can be found in the [Squashing Your Commits](https://github.com/openMF/mobile-wallet/wiki/Squashing-Your-Commits) guide.

### Resolving Merge Conflicts
Occasionally, merge conflicts may arise when your pull request is being reviewed. These conflicts need to be resolved manually.  
To learn how to resolve merge conflicts, please refer to the [Solving Merge Conflicts](https://github.com/openMF/mobile-wallet/wiki/Solving-Merge-Conflicts) guide.

### Conclusion
By following these contribution guidelines, you're all set to start contributing to the Mobile Wallet (Mifos Pay) project. We appreciate your efforts and look forward to your valuable contributions. Happy coding!

### Instructions to Get the Latest APK

To download the latest APK navigate to the latest release [here](https://github.com/openMF/mobile-wallet/releases), and download the APK file from the assets section.

### Wiki
To know more about the project details and architecture guidelines, visit our [Wiki](https://github.com/openMF/mobile-wallet/wiki).

### Contributors

Special thanks to the incredible code contributors who continue to drive this project forward.

<a href="https://github.com/openMF/mobile-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openMF/mobile-wallet"  alt="mobile wallet contributors"/>
</a>

<div align="right">

[![Back To Top](https://img.shields.io/badge/Back%20To%20Top-Blue?style=flat)](#readme-top)

</div>
