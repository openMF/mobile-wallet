<img height='175' src="https://user-images.githubusercontent.com/44283521/78983673-455cf780-7b42-11ea-849e-ecd2009dd562.png" align="left" hspace="1" vspace="1">

# Mobile Wallet

Mobile Wallet is an Android-based framework for mobile wallets based on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows 
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet based project. It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.


| Master | Development | Chat |
|------------|-----------------|-----------------|
| ![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/workflows/Mobile-Wallet%20CI%5BMaster/Dev%5D/badge.svg?branch=master) | ![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/workflows/Mobile-Wallet%20CI%5BMaster/Dev%5D/badge.svg?branch=dev) | [![Join the chat at https://gitter.im/openMF/mobile-wallet](https://badges.gitter.im/openMF/mobile-wallet.svg)](https://gitter.im/openMF/mobile-wallet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |

## Screenshots

<p>
  <img src="https://user-images.githubusercontent.com/37406965/51085243-86f2cd00-175c-11e9-9f5e-8a2324cfda4a.jpg" width="288" height="500" />
  <img src="https://user-images.githubusercontent.com/37406965/51085245-8823fa00-175c-11e9-949a-c5292037b970.jpg" width="288" height="500" /> 
  <img src="https://user-images.githubusercontent.com/37406965/51085246-89552700-175c-11e9-9a5b-5a85ecb5bfae.jpg" width="288" height="500" />
</p>
<p align="center">
  Click <a href= "https://github.com/openMF/mobile-wallet/blob/dev/Screenshot.md">here </a> for more screenshots
</p>

## How to Contribute

This is an OpenSource project and we would be happy to see new contributors. The issues should be raised via the GitHub issue tracker.
For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#issue-tracker">here</a>. All fixes should be proposed via pull requests.
For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#pull-requests">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>.

### Branch Policy

We have the following branches :

 * **dev**
     All the contributions should be pushed to this branch. If you're making a contribution,
     you are supposed to make a pull request to _dev_.
     Please make sure it passes a build check on Travis.

     It is advisable to clone only the development branch using the following command:

    `git clone -b <branch> <remote_repo>`
    
    With Git 1.7.10 and later, add --single-branch to prevent fetching of all branches. Example, with development branch:

    `git clone -b dev --single-branch https://github.com/username/mobile-wallet.git`

 * **master**
   The master branch contains all the stable and bug-free working code. The development branch once complete will be merged with this branch.
   
 * **redesign**
   The app is presently being redesigned. All the work done under the redesign domain should be committed to this branch.
   
## Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

## Building the Code

1. Fork the repository.

2. Go to your fork and clone only the dev branch using `git clone -b dev <remote_repo>`(remote_repo url refers to your fork).

3. Click on 'Open an existing Android Studio project'.

4. Browse to the directory where you cloned the mobile-wallet repo and click OK.

5. Let Android Studio import the project.

6. Let the gradle sync.

7. There should be no errors in gradle build.

8. Set your remote upstream to the remote repository to pull changes whenever needed, using
`git remote add upstream https://github.com/openMF/mobile-wallet.git` 

9. Pull changes from dev branch of upstream, whenever needed, using
`git checkout dev`
`git pull upstream dev`

## GitHub Actions CI
<a href="https://docs.github.com/en/free-pro-team@latest/actions">GitHub Actions CI</a> is a continuous integration service used to build and test software projects hosted at GitHub. We use GitHub Actions for continous integration and clean maintainence of code. All your pull requests must pass the CI build only then, it will be allowed to merge. Sometimes,when the build doesn't pass you can use these commands in your local terminal and check for the errors,</br>

For Mac OS, you can use the following commands:

* `./gradlew check` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `./gradlew spotlessApply` an check and apply formatting to any plain-text file.</br>
* `./gradlew build`  provides a command line to execute build script.</br>


For Windows, you can use the following commands:

* `gradlew check` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `gradlew spotlessApply` an check and apply formatting to any plain-text file.</br>
* `gradlew build`  provides a command line to execute build script.</br>

### Instructions to get the latest APK

To get the latest apk fom the Github actions artifacts, follow these steps:

1. Navigate to the [Actions](https://github.com/openMF/mobile-wallet/actions?query=workflow%3A%22Mobile-Wallet+CI%5BMaster%2FDev%5D%22+event%3Apush) tab of this repository.
2. Click the latest workflow from the workflows list.
3. Scroll down to the **Artifacts** section and click the **mobile-wallet** hyperlink.
4. After successful download, extract the zip file to your preferred location.

## Wiki

https://github.com/openMF/mobile-wallet/wiki
