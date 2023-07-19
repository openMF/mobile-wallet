<img height='175' src="https://user-images.githubusercontent.com/44283521/78983673-455cf780-7b42-11ea-849e-ecd2009dd562.png" align="left" hspace="1" vspace="1">

# Mobile Wallet

Mobile Wallet is an Android-based framework for mobile wallets based on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows 
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet based project. It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.


Development | Chat |
|-----------------|-----------------|
![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/workflows/Mobile-Wallet%20CI%5BMaster/Dev%5D/badge.svg?branch=dev) | [![Join the chat at https://gitter.im/openMF/mobile-wallet](https://badges.gitter.im/openMF/mobile-wallet.svg)](https://gitter.im/openMF/mobile-wallet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |

## How to Contribute

Thank you for your interest in contributing to the Mobile Wallet project by Mifos! We welcome all contributions and encourage you to follow these guidelines to ensure a smooth and efficient collaboration process.

The issues should be raised via the GitHub issue tracker. For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#issue-tracker">here</a>. All fixes should be proposed via pull requests. For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/.github/CONTRIBUTING.md#pull-requests">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>.

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

### Demo credentials
Fineract Instance: demo.mifos.io
username: demoUser123
password: password
   
### Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

1. **Fork the Git Repository**
    
    Forking the repository is the first step to start contributing. Click on the "Fork" button in the top right corner of the **[Mobile Wallet (Mifos Pay) repository](https://github.com/openMF/mobile-wallet)** to create your own fork.
    
    ![image](https://user-images.githubusercontent.com/44428198/254533248-3016c6eb-30b7-492b-91e8-dbbb61f76775.png)

    Forking creates a copy of the project under your GitHub account. This enables you to work on changes without affecting the original repository directly.
    
2. **Clone the Forked Repository**
    
    Once you have forked the repository, you need to clone it to your local development environment. Open a terminal or Git Bash and use the following command:
    
    ```bash
    git clone https://github.com/yourUsername/mobile-wallet.git
    ```
    
    Replace **`yourUsername`** with your actual GitHub username. Cloning creates a local copy of the repository on your machine, allowing you to make changes and contributions.
    
3. **Working Locally on Your Cloned Repository**
    
    After cloning, navigate to the project directory using the terminal or Git Bash. 
    
    Before making any changes, create a new branch dedicated to the feature or bug fix you'll be working on:
    
    ```bash
    git checkout -b "new-branch-name"
    ```
    
    Ensure that **`new-branch-name`** reflects the purpose of your changes (e.g., **`add-payment-feature`** or **`fix-bug-123`**).
    
    Make the necessary changes to the files to address the issue you're working on. Once you're done, you will be ready to proceed with verifying and committing your changes.
    
4. **Perform a Gradle Check**
    
All your pull requests must pass the CI build only then, it will be allowed to merge. Sometimes, when the build doesn't pass you can use these commands in your local terminal and check for the errors,</br>

For Mac OS, you can use the following commands:

* `./gradlew check` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `./gradlew spotlessApply` an check and apply formatting to any plain-text file.</br>
* `./gradlew build`  provides a command line to execute build script.</br>


For Windows, you can use the following commands:

* `gradlew check` quality checks on your project’s code using Checkstyle and generates reports from these checks.</br>
* `gradlew spotlessApply` an check and apply formatting to any plain-text file.</br>
* `gradlew build`  provides a command line to execute build script.</br>

### **Committing Your Changes**

When you've finished making your changes and have tested them locally, it's time to commit your work:

1. **Stage Changes**
    
    Use the following command to stage all changes:
    
    ```bash
    git add .
    ```
    
    This adds all modified and new files to the staging area, preparing them for the commit.
    
2. **Commit Changes**
    
    Commit your changes with a meaningful commit message that describes the purpose of your changes:
    
    ```bash
    git commit -m "Your commit message goes here"
    ```
    
    A good commit message is concise and provides enough context about the changes made. Mifos follows its own commit style guidelines that you must follow. Learn more about it [here](https://github.com/openMF/mifos-mobile-cn/blob/master/COMMIT_STYLE.md).
    
3. **Push Changes**
    
    Push your changes to your forked repository on GitHub:
    
    ```bash
    git push origin new-branch-name
    ```
    
    Replace **`new-branch-name`** with the name of the branch you created earlier.

### **Making a Pull Request**

Once your changes are pushed to your forked repository, you can initiate a pull request to merge your changes into the main project:

1. Navigate to the [Mobile Wallet (Mifos Pay) repository](https://github.com/openMF/mobile-wallet) on GitHub.
2. Click on the "Pull Requests" tab and then click "New Pull Request."

![image](https://user-images.githubusercontent.com/44428198/254533309-2df4dca7-aec3-4197-8b86-8b80988e08d7.png)

1. Ensure the base repository is set to "openMF/mobile-wallet" and the base branch is the main development branch `dev`. 
2. Set the compare repository to your forked repository and the compare branch to the branch you just created with your changes (e.g., in my case, the head repository was set to “rchtgpt/mobile-wallet” and the comparison branch was `kotlin-migration-common`.
3. Fill out the pull request template, providing a clear description of your changes, why they are necessary, and any relevant information for the reviewers.
4. Click "Create Pull Request" to submit your changes for review.

### **Squashing Your Commits**

It is common for pull requests to undergo multiple rounds of review before being merged. To keep the Git history clean and organized, you should always squash your commits before finalizing the merge. Here's how you can do it:

1. Squash your commits:
    
    ```bash
    git rebase -i HEAD~x
    ```
    
    Replace **`x`** with the number of commits you want to squash. An interactive rebase will open, allowing you to choose how to combine the commits. Change **`pick`** to **`squash`** (or simply **`s`**) for all but the topmost commit. Save and exit the editor.
    
2. Amend the commit message if needed.
    
    ```bash
    git commit --amend
    ```
    
3. Force push the changes to your forked repository:
    
    ```bash
    git push --force origin your-branch-name
    ```
    
    Please note that force pushing rewrites the Git history, so use it with caution.
    

### **Solving Merge Conflicts**

In some cases, your pull request might encounter merge conflicts when the changes cannot be automatically merged with the main branch. To resolve merge conflicts:

1. Update your local branch with the latest changes from the main repository:
    
    ```bash
    git fetch upstream
    git checkout your-branch-name
    git rebase upstream/master
    ```
    
2. Git will pause when encountering conflicts. Open the affected files, resolve the conflicts manually, and save the changes.
3. After resolving all conflicts, stage the changes and continue with the rebase:
    
    ```bash
    git add .
    git rebase --continue
    ```
    
4. Finally, force push the changes to your forked repository:
    
    ```
    git push --force origin your-branch-name
    ```
    
Your pull request will be updated with the resolved conflicts, and the reviewers can proceed with the review process. Again, don’t forget to squash your commits.

### Instructions to get the latest APK

To get the latest apk fom the Github actions artifacts, follow these steps:

1. Navigate to the [Actions](https://github.com/openMF/mobile-wallet/actions?query=workflow%3A%22Mobile-Wallet+CI%5BMaster%2FDev%5D%22+event%3Apush) tab of this repository.
2. Click the latest workflow from the workflows list.
3. Scroll down to the **Artifacts** section and click the **mobile-wallet** hyperlink.
4. After successful download, extract the zip file to your preferred location.

### Wiki

https://github.com/openMF/mobile-wallet/wiki


### Screenshots

<p>
  <img src="https://user-images.githubusercontent.com/37406965/51085243-86f2cd00-175c-11e9-9f5e-8a2324cfda4a.jpg" width="288" height="500" />
  <img src="https://user-images.githubusercontent.com/37406965/51085245-8823fa00-175c-11e9-949a-c5292037b970.jpg" width="288" height="500" /> 
  <img src="https://user-images.githubusercontent.com/37406965/51085246-89552700-175c-11e9-9a5b-5a85ecb5bfae.jpg" width="288" height="500" />
</p>
<p align="center">
  Click <a href= "https://github.com/openMF/mobile-wallet/blob/dev/Screenshot.md">here </a> for more screenshots
</p>

### Conclusion
By following these contribution guidelines, you're all set to start contributing to the Mobile Wallet (Mifos Pay) project. We appreciate your efforts and look forward to your valuable contributions. Happy coding!
