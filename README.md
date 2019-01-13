<img height='175' src="https://user-images.githubusercontent.com/37406965/51083189-d5dc3a80-173b-11e9-8ca0-28015e0893ac.png" align="left" hspace="1" vspace="1">

# Mobile Wallet

Mobile wallet is an Android based frame-work for mobile wallets based on top of <a href='https://github.com/openMF/mobile-wallet/wiki/Fineract-backend'>Fineract</a>. The app follows 
<a href='https://github.com/openMF/mobile-wallet/wiki/Architecture'>clean architecture</a> and contains a core library module
that can be used as a dependency in any other wallet based project.It is developed at <a href='https://mifos.org/'>MIFOS</a> together with a global community.


| Master | Development | Chat |
|------------|-----------------|-----------------|
| [![Build Status](https://travis-ci.org/openMF/mobile-wallet.svg?branch=master)](https://travis-ci.org/openMF/mobile-wallet) | [![Build Status](https://travis-ci.org/openMF/mobile-wallet.svg?branch=dev)](https://travis-ci.org/openMF/mobile-wallet) | [![Join the chat at https://gitter.im/openMF/mobile-wallet](https://badges.gitter.im/openMF/mobile-wallet.svg)](https://gitter.im/openMF/mobile-wallet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |

## Screenshots

<p>
  <img src="https://user-images.githubusercontent.com/37406965/51085243-86f2cd00-175c-11e9-9f5e-8a2324cfda4a.jpg" width="288" height="500" />
  <img src="https://user-images.githubusercontent.com/37406965/51085245-8823fa00-175c-11e9-949a-c5292037b970.jpg" width="288" height="500" /> 
  <img src="https://user-images.githubusercontent.com/37406965/51085246-89552700-175c-11e9-9a5b-5a85ecb5bfae.jpg" width="288" height="500" />
</p>

## How to Contribute

This is an OpenSource project and we would be happy to see new contributors. The issues should be raised via the github issue tracker.
For Issue tracker guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/CONTRIBUTING.md">here</a>. All fixes should be proposed via pull requests. For pull request guidelines please click <a href="https://github.com/openMF/mobile-wallet/blob/master/CONTRIBUTING.md">here</a>. For commit style guidelines please click <a href="https://github.com/openMF/mobile-wallet/wiki/Commit-style-guide">here</a>. 

### Branch Policy

We have the following branches

 * **dev**
     All the contributions should be pushed to this branch. If you're making a contribution,
     you are supposed to make a pull request to _dev_.
     Please make sure it passes a build check on Travis

     It is advisable to clone only the development branch using the following command:

    `git clone -b <branch> <remote_repo>`
    
    With Git 1.7.10 and later, add --single-branch to prevent fetching of all branches. Example, with development branch:

    `git clone -b development --single-branch https://github.com/username/phimpme-android.git`

 * **master**
   The master branch contains all the stable and bug-free working code. The developement branch once complete will be merged with this branch.
   
 * **redesign**
   The app is presently being redesigned. All the work done under redesign domain should be commited to this branch.
   
## Development Setup

Before you begin, you should have already downloaded the Android Studio SDK and set it up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio).

## Building the Code

1. Clone the repository using HTTP : git clone https://github.com/openMF/mobile-wallet.git

2. Open Android studio

3. Click on 'Open an existing android studio project'

4. Browse to the directory where you cloned mobile-wallet repo and click OK.

5. Let Android studio import the project

6. You will see an error in gradle build related to missing RblClientIdProp and RblClientSecretProp. Add RblClientIdProp and RblClientSecretProp as environment variables with any value

7. Sync the project again in Android studio

## Wiki

https://github.com/openMF/mobile-wallet/wiki

## Maintainers

The project is maintained by
- Ankur Sharma ([@ankurs287](https://github.com/ankurs287))
- Chirag Gupta ([@luckyman20](http://github.com/luckyman20))

