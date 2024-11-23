<img height='175' src="https://user-images.githubusercontent.com/44283521/78983673-455cf780-7b42-11ea-849e-ecd2009dd562.png" align="left" hspace="1" vspace="1">

# Mobile Wallet

Mobile Wallet is an Kotlin Multiplatform (KMP)-based framework for mobile wallets based on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows 
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet based project. It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.

## Run the Project

![Screenshot](https://github.com/user-attachments/assets/8023c529-1215-4c4b-b212-630f0233223f)

- **Android App**: Select the `mifospay-android` run configuration and click **Run**.
- **Desktop App**: Select the `mifospay-desktop` run configuration and click **Run**.
- **Web App (JavaScript)**: Select the `mifospay-web-js` run configuration and click **Run**.


## KMP Status for modules

| Module                        | Progress | Desktop supported | Android supported | iOS supported | Web supported(JS) | Web supported(WASM-JS)  | 
|-------------------------------|----------|-------------------|-------------------|---------------|-------------------|-------------------------| 
| mifospay-android              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ❔                       | 
| mifospay-desktop              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ❔                       | 
| mifospay-web                  | Done     | ✅                 | ✅                 | ❔             | ✅                 | ❔                       |
| mifospay-ios                  | NO OP    | ❌                 | ❌                 | ❌             | ❌                 | ❌                       |
| :core:analytics               | Done     | ❌                 | ✔️                | ❔             | ❌                 | ❔                       |
| :core:common                  | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:data                    | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:datastore               | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:datastore-proto         | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:designsystem            | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:domain                  | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:model                   | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:network                 | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :core:ui                      | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:auth                 | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:editpassword         | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:faq                  | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:history              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:home                 | Done     | ✅                 | ✅                 | ❔             | ✅                 | ❌                       | 
| :feature:profile              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:settings             | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:payments             | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |
| :feature:finance              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |
| :feature:account              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |
| :feature:invoices             | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:kyc                  | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:make-transfer        | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:merchants            | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:notification         | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |
| :feature:qr                   | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:receipt              | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:request-money        | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:saved-cards          | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |
| :feature:send-money           | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:standing-instruction | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       | 
| :feature:upi-setup            | Done     | ✅                 | ✅                 | ❔             | ✅                 | ✅                       |  

✅: Functioning properly  
❔: Not yet tested, but expected to work  
✔️: Successfully compiled  
❌: Not functioning, requires further attention

⚠️ **Notice:**  
We are fully using [Jetpack Compose](https://developer.android.com/jetpack/compose) and are now in the process of converting to support **Kotlin Multiplatform**.


Contributions are welcome in the following areas:
- Development and enhancements related to Kotlin Multiplatform.
- Logical changes or improvements in existing Jetpack Compose-based features.

We appreciate your contributions and look forward to collaborating with you!

| Development                                                                                                                                | Chat                                                                                                                                     |
|--------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| ![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/workflows/Mobile-Wallet%20CI%5BMaster/Dev%5D/badge.svg?branch=dev) | [![Join the chat at https://mifos.slack.com/](https://img.shields.io/badge/Join%20Our%20Community-Slack-blue)](https://mifos.slack.com/) |

## Join Us on Slack

Mifos boasts an active and vibrant contributor community, Please join us on [slack](https://join.slack.com/t/mifos/shared_invite/zt-2f4nr6tk3-ZJlHMi1lc0R19FFEHxdvng). Once you've joined the mifos slack community, please join the `#mobile-wallet` channel to engage with mobile-wallet development. If you encounter any difficulties joining our Slack channel, please don't hesitate to open an issue. This will allow us to assist you promptly or send you an invitation.

## How to Contribute

Thank you for your interest in contributing to the Mobile Wallet project by Mifos! We welcome all contributions and encourage you to follow these guidelines to ensure a smooth and efficient collaboration process.

The issues should be raised via the GitHub issue tracker. For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#issue-tracker">here</a>. All fixes should be proposed via pull requests. For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#pull-requests">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>.

### Branch Policy

For development, always pull the **dev** branch, as all contributions and updates are pushed here. Once development is complete, changes are merged into the **master** branch, which contains stable and bug-free code.

To know more about branch policies, click [here](https://github.com/openMF/mobile-wallet/wiki/Branch-Policy).

### Demo credentials
Fineract Instance: demo.mifos.io

Username: `venus`

Password: `Venus2023#`

### Development Setup

To learn about the development setup, please visit the following link:  
[Development Setup Guide](https://github.com/openMF/mobile-wallet/wiki/Development-Setup)

### **Committing Your Changes**

Once you make changes in your local repository, you need to commit them to your GitHub repository.  
Don't know how to commit? Click [here](https://github.com/openMF/mobile-wallet/wiki/Committing-Your-Changes) to learn more.

### **Making a Pull Request**

After pushing your changes to your forked repository, you can create a pull request to propose merging your changes into the main project.  
Don't know how to create a pull request? Click [here](https://github.com/openMF/mobile-wallet/wiki/Making-a-Pull-Request) to learn more.

### **Squashing Your Commits**

To maintain a clean and organized Git history, it is recommended to squash your commits before merging.  
Don't know how to squash commits? Click [here](https://github.com/openMF/mobile-wallet/wiki/Squashing-Your-Commits) to learn more.

### **Solving Merge Conflicts**

Sometimes, your pull request may encounter merge conflicts that need to be resolved manually.  
Don't know how to resolve merge conflicts? Click [here](https://github.com/openMF/mobile-wallet/wiki/Solving-Merge-Conflicts) to learn more.

### Conclusion
By following these contribution guidelines, you're all set to start contributing to the Mobile Wallet (Mifos Pay) project. We appreciate your efforts and look forward to your valuable contributions. Happy coding!

## Instructions to get the latest APK

To get the latest apk fom the Github actions artifacts, follow these steps:

1. Navigate to the [Actions](https://github.com/openMF/mobile-wallet/actions?query=workflow%3A%22Mobile-Wallet+CI%5BMaster%2FDev%5D%22+event%3Apush) tab of this repository.
2. Click the latest workflow from the workflows list.
3. Scroll down to the **Artifacts** section and click the **mobile-wallet** hyperlink.
4. After successful download, extract the zip file to your preferred location.

## Wiki

To know more about the project, visit our [Wiki](https://github.com/openMF/mobile-wallet/wiki).

## Screenshots
|        OS         |                                                                                                                                                        Image                                                                                                                                                        |                                            More Images                                            |
|:-----------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
|      Android      | <img src="https://github.com/user-attachments/assets/2753a54c-201d-4570-b363-46c930793d37" width="120" height="210" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/8d75922e-fd9f-42c1-8c94-2fdfc69658a9" width="120" height="210" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/ce6c4523-292a-46d9-b38c-b0a6b9aba052" width="120" height="210"/> | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Android.md">Load More</a> |
|        iOS        |       <img src="https://github.com/user-attachments/assets/43dd37bf-93a7-4905-a28b-eb60e1ead2e3" width="120" height="210" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/2753a54c-201d-4570-b363-46c930793d37" width="120" height="210" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/42ec8984-7224-4aab-84f3-2a8f42022186" width="120" height="210"/>       | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Android.md">Load More</a> |
|      Windows      |                                                                       <img src="https://github.com/user-attachments/assets/ee4ed3ce-ea15-42f3-8c2a-0f55dcff81af" width="200" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/edf0eb72-9cba-401e-8732-7d272fdaf125" width="200"/>                                                                       | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Windows.md">Load More</a> |
|       Linux       |                                                                      <img src="https://github.com/user-attachments/assets/72b373df-b247-4554-94ab-373fc46e7599" width="200" style="margin-right:10px;"/> <img src="https://github.com/user-attachments/assets/ce4fa873-c0ee-46c8-8517-82bbf30f65c4" width="200"/>                                                                      |  <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Linux.md">Load More</a>  |

## Contributors

Special thanks to the incredible code contributors who continue to drive this project forward.

<a href="https://github.com/openMF/mobile-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openMF/mobile-wallet" />
</a>
