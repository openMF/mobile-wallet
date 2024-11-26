![Kotlin](https://img.shields.io/badge/Kotlin-7f52ff?style=flat-square&logo=kotlin&logoColor=white) 
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-4c8d3f?style=flat-square&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Jetpack%20Compose%20Multiplatform-000000?style=flat-square&logo=android&logoColor=white)
[![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/actions/workflows/master_dev_ci.yml/badge.svg?branch=dev)](https://github.com/openMF/mobile-wallet/actions/workflows/master_dev_ci.yml)
 [![Join the chat at https://mifos.slack.com/](https://img.shields.io/badge/Join%20Our%20Community-Slack-blue)](https://mifos.slack.com/) 

<img height='175' src="https://user-images.githubusercontent.com/44283521/78983673-455cf780-7b42-11ea-849e-ecd2009dd562.png" align="left" hspace="1" vspace="1">

# Mobile Wallet
Mobile Wallet is a Kotlin Multiplatform (KMP)-based application for mobile wallets built on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet-based project. It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.

## Run the Project
![Screenshot](https://github.com/user-attachments/assets/8023c529-1215-4c4b-b212-630f0233223f)
- **Android App**: Select the `mifospay-android` run configuration and click **Run**.
- **Desktop App**: Select the `mifospay-desktop` run configuration and click **Run**.
- **Web App (JavaScript)**: Select the `mifospay-web-js` run configuration and click **Run**.

### Demo Credentials
- **Fineract Instance**: demo.mifos.io
- **Username**: `venus`
- **Password**: `Venus2023#`


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


The project fully utilizes [Jetpack Compose](https://developer.android.com/jetpack/compose) with complete support for **Kotlin Multiplatform**.

We invite contributions in the following areas:
- Development of new features and enhancements using Kotlin Multiplatform.
- Improvements and refinements to existing Jetpack Compose-based functionalities.

We appreciate your contributions and look forward to collaborating with you!


## Join Us on Slack
Mifos boasts an active and vibrant contributor community, Please join us on [slack](https://join.slack.com/t/mifos/shared_invite/zt-2f4nr6tk3-ZJlHMi1lc0R19FFEHxdvng). Once you've joined the mifos slack community, please join the `#mobile-wallet` channel to engage with mobile-wallet development. If you encounter any difficulties joining our Slack channel, please don't hesitate to open an issue. This will allow us to assist you promptly or send you an invitation.

## How to Contribute
Thank you for your interest in contributing to the Mobile Wallet project by Mifos! We welcome all contributions and encourage you to follow these guidelines to ensure a smooth and efficient collaboration process.

The issues should be raised via the GitHub issue tracker. For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#issue-tracker">here</a>. All fixes should be proposed via pull requests. For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#pull-requests">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>.

### Branch Policy
For development purposes, always pull from the **dev** branch, as all contributions and updates are merged into this branch. Upon completion of development, changes are subsequently merged into the **master** branch, which represents the stable and bug-free version of the code.

For more detailed information about the branch policies, please refer to the [Branch Policy](https://github.com/openMF/mobile-wallet/wiki/Branch-Policy).


### Development Setup
Please refer to the  [Development Setup Guide](https://github.com/openMF/mobile-wallet/wiki/Development-Setup) for detailed instructions on configuring the development environment.

### **Committing Your Changes**
After making changes in your local repository, you will need to commit them to your GitHub repository.  
If you are unfamiliar with the process of committing changes, please refer to the [Committing Your Changes](https://github.com/openMF/mobile-wallet/wiki/Committing-Your-Changes) guide.

### **Making a Pull Request**
Once your changes have been pushed to your forked repository, you can create a pull request to propose integrating your updates into the main project.  
For guidance on creating a pull request, please visit the [Making a Pull Request](https://github.com/openMF/mobile-wallet/wiki/Making-a-Pull-Request) guide.

### **Squashing Your Commits**
To ensure a clean and organized Git history, contributors are encouraged to squash their commits before merging.  
Instructions on how to squash commits can be found in the [Squashing Your Commits](https://github.com/openMF/mobile-wallet/wiki/Squashing-Your-Commits) guide.

### **Resolving Merge Conflicts**
Occasionally, merge conflicts may arise when your pull request is being reviewed. These conflicts need to be resolved manually.  
To learn how to resolve merge conflicts, please refer to the [Solving Merge Conflicts](https://github.com/openMF/mobile-wallet/wiki/Solving-Merge-Conflicts) guide.

### Conclusion
By following these contribution guidelines, you're all set to start contributing to the Mobile Wallet (Mifos Pay) project. We appreciate your efforts and look forward to your valuable contributions. Happy coding!

## Instructions to Get the Latest APK

To download the latest APK from the GitHub Actions artifacts, follow these steps:

### Step 1: Access the Actions Tab
- Navigate to the [Actions Tab](https://github.com/openMF/mobile-wallet/actions?query=workflow%3A%22Mobile-Wallet+CI%5BMaster%2FDev%5D%22+event%3Apush) of this repository.

### Step 2: Select the Latest Workflow
- Click on the most recent workflow from the workflows list.

### Step 3: Locate the Artifacts Section
- Scroll down to the **Artifacts** section, where you will find:
    - **Android APKs**
    - **Linux-App**
    - **MacOS-App**
    - **Windows-Apps**
- Download all the available zip files.

  ![Artifacts Section](https://github.com/user-attachments/assets/ab7783ff-3834-4be2-9ce8-6342746b22a2)

### Step 4: Extract the Files
- After downloading the files, extract the zip archives to your preferred location.

### Step 5: Install the Application
- Open the extracted files and install the application on your device.


## Wiki
To know more about the project, visit our [Wiki](https://github.com/openMF/mobile-wallet/wiki).

## Screenshots

|        OS         |                                                                                          Image                                                                                          |                                            More Images                                            |
|:-----------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
|      Android      | <img src="https://github.com/user-attachments/assets/2753a54c-201d-4570-b363-46c930793d37" style="width:30%;"/> <img src="https://github.com/user-attachments/assets/8d75922e-fd9f-42c1-8c94-2fdfc69658a9" style="width:30%;"/> <img src="https://github.com/user-attachments/assets/ce6c4523-292a-46d9-b38c-b0a6b9aba052" style="width:30%;"/> | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Android.md">Load More</a> |
|        iOS        | <img src="https://github.com/user-attachments/assets/43dd37bf-93a7-4905-a28b-eb60e1ead2e3" style="width:30%;"/> <img src="https://github.com/user-attachments/assets/2753a54c-201d-4570-b363-46c930793d37" style="width:30%;"/> <img src="https://github.com/user-attachments/assets/42ec8984-7224-4aab-84f3-2a8f42022186" style="width:30%;"/> | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Android.md">Load More</a> |
|      Windows      | <img src="https://github.com/user-attachments/assets/ee4ed3ce-ea15-42f3-8c2a-0f55dcff81af" style="width:45%;"/> <img src="https://github.com/user-attachments/assets/edf0eb72-9cba-401e-8732-7d272fdaf125" style="width:45%;"/> | <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Windows.md">Load More</a> |
|       Linux       | <img src="https://github.com/user-attachments/assets/72b373df-b247-4554-94ab-373fc46e7599" style="width:45%;"/> <img src="https://github.com/user-attachments/assets/ce4fa873-c0ee-46c8-8517-82bbf30f65c4" style="width:45%;"/> |  <a href= "https://github.com/openMF/mobile-wallet/blob/dev/docs/readmes/Linux.md">Load More</a>  |

## Contributors

Special thanks to the incredible code contributors who continue to drive this project forward.

<a href="https://github.com/openMF/mobile-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openMF/mobile-wallet" />
</a>
