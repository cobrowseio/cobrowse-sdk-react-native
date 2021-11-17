# Prerequisites

It is assumed that the developer is familiar with the following tools, or with how to find the relelvant instructions.

- Android Studio must be installed
- An Android emulator must be set up in the Android Virtual Device (AVD) config
- Gradle must be set up globally for your user, with repositories for `mavenCentral` and `google`
- Set up two envrionment variables named "ANDROID_HOME" and "ANDROID_SDK_ROOT" pointing to your local Android SDK

> NOTE: When developing on Windows and MacOS, the Android Studio installers may set the environment variables for you.
> This is not true for Linux.

# One-time local development run

1. On a terminal, run `npm version prerelease --no-git-tag-version`
2. Run `npm pack` and make not of the file name ending in ".tgz"
3. CD into './Example'
4. Run `npm install` once
5. Run `npm install ../<NAME OF TGZ FILE>` to install the local development changes
6. Run `npm start`
7. Without quitting the current terminal, open a seperate terminal
8. On the new terminal, run `npm run android`

# Watch for changes in local development

1. Run `npm run watch`

This command runs a script which monitors for changes and dynamically reloads React Native with those changes.
