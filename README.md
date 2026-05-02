# Privacy TaskList
####  **TaskList** was made as a simple and effective solution to one problem -> A private place to keep notes like your kid's birthday, a driver's license number, a pin code, and whatever else sensitive info. 
## Install
1. [Download the .zip file](./download_apk_here/android/TaskList.zip)
2. Extract/Upload the **TaskList.apk** on your phone.
3. **From your phone:**
    - You need to have 'Install unknown apps' enabled on your phone.
    - Navigate to the **TaskList.apk** file from your file browsing app, and tap on it\*. 
        - \*If it still says you have to give permission to install unknown apps, just follow the steps and allow the app that appears (probably your file browser app) to install unknown apps. *You can, and should turn this back off once the app has successfully installed!*
    - Find the app **'TaskList'** > start it up > make an account > start using!

#### Dev:
1. Open [the repo](https://github.com/universitytraining/mobile-tasklist.git) in Android Studio.
2. Sync Gradle.
3. Have fun.

## Usage and functionality
1. Privacy
    - A password protected account with biometrics capability for open sessions.
    - The app is completely offline - everything is saved on your device - no cloud, no need for internet connection, no one can see your sensitive info but you.
    - The DB is fully encrypted, so even if someone compromises your device and gets their hands on the DB somehow, all they see is gibberish.

2. Usage
    - Creating an account is very straightforward. There aren't any special requirements for username or password.
    - Once logged in, create tasks with the 'Add Task' button. Title field is required.
    - Tap anywhere on the task card to open the Edit dialog.
    - 'Delete' button in task card deletes the task. 
    - The checkbox marks tasks as complete.
        - 'Clear Completed' button becomes available once a task is checked as completed. This button will delete *ALL* completed tasks.
    - 'Delete Account' button deletes the account and all associated tasks. *This, and any other task deletion process is permanent!*
    - 'Logout' button logs the user out. 
3. Behavior 
    - App detects mobile device theme and adapts accordingly. There is no manual switching of the app's theme.
    - If the app is closed, *without logging the user out*, the next time the app opens, the tasks are hidden behind the login screen. *This doesn't work on iPhone at this moment.*
        * In such cases, the 'Use Fingerprint' button becomes available for ease of use.