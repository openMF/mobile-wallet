<img height='175' src="https://user-images.githubusercontent.com/44283521/78983673-455cf780-7b42-11ea-849e-ecd2009dd562.png" align="left" hspace="1" vspace="1">

# Mobile Wallet

Mobile Wallet is an Android-based framework for mobile wallets based on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows 
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet based project. It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.

## Notice

:warning: We are fully committed to implement [Jetpack Compose](https://developer.android.com/jetpack/compose) and moving ourself to support 
`kotlin multi-platform`. **If you are sending any PR regarding `XML changes` we will `not` consider at this moment but converting XML to jetpack compose are most welcome.** If you sending any PR regarding logical changes in Activity/Fragment you are most welcome. 



Development | Chat |
|-----------------|-----------------|
![Mobile-Wallet CI[Master/Dev]](https://github.com/openMF/mobile-wallet/workflows/Mobile-Wallet%20CI%5BMaster/Dev%5D/badge.svg?branch=dev) | [![Join the chat at https://mifos.slack.com/](https://img.shields.io/badge/Join%20Our%20Community-Slack-blue)](https://mifos.slack.com/) |


## Join Us on Slack

Mifos boasts an active and vibrant contributor community, Please join us on [slack](https://join.slack.com/t/mifos/shared_invite/zt-2f4nr6tk3-ZJlHMi1lc0R19FFEHxdvng). Once you've joined the mifos slack community, please join the `#mobile-wallet` channel to engage with mobile-wallet development. If you encounter any difficulties joining our Slack channel, please don't hesitate to open an issue. This will allow us to assist you promptly or send you an invitation.

### [How to Contribute](https://github.com/openMF/mobile-wallet/wiki/How-to-Contribute)

### [Branch Policy](https://github.com/openMF/mobile-wallet/wiki/Branch-Policy)

### [Demo credentials](https://github.com/openMF/mobile-wallet/wiki/Demo-credentials)

### [Development Setup](https://github.com/openMF/mobile-wallet/wiki/Development-Setup)


### [Committing Your Changes](https://github.com/openMF/mobile-wallet/wiki/Committing-Your-Changes)


### [**Making a Pull Request**](https://github.com/openMF/mobile-wallet/wiki/Making-a-Pull-Request)

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

### Conclusion
By following these contribution guidelines, you're all set to start contributing to the Mobile Wallet (Mifos Pay) project. We appreciate your efforts and look forward to your valuable contributions. Happy coding!

## Instructions to get the latest APK

To get the latest apk fom the Github actions artifacts, follow these steps:

1. Navigate to the [Actions](https://github.com/openMF/mobile-wallet/actions?query=workflow%3A%22Mobile-Wallet+CI%5BMaster%2FDev%5D%22+event%3Apush) tab of this repository.
2. Click the latest workflow from the workflows list.
3. Scroll down to the **Artifacts** section and click the **mobile-wallet** hyperlink.
4. After successful download, extract the zip file to your preferred location.

## Wiki

https://github.com/openMF/mobile-wallet/wiki


## Screenshots

<p>
  <img src="https://user-images.githubusercontent.com/37406965/51085243-86f2cd00-175c-11e9-9f5e-8a2324cfda4a.jpg" width="288" height="500" />
  <img src="https://user-images.githubusercontent.com/37406965/51085245-8823fa00-175c-11e9-949a-c5292037b970.jpg" width="288" height="500" /> 
  <img src="https://user-images.githubusercontent.com/37406965/51085246-89552700-175c-11e9-9a5b-5a85ecb5bfae.jpg" width="288" height="500" />
</p>

<p>
  Click <a href= "https://github.com/openMF/mobile-wallet/blob/dev/Screenshot.md">here </a> for more screenshots
</p>


## Contributors

Special thanks to the incredible code contributors who continue to drive this project forward.

<a href="https://github.com/openMF/mobile-wallet/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=openMF/mobile-wallet" />
</a>


