## "@react-native-community/netinfo": "^3.2.1"

no known issue while updating version

used to monitor network information (connected, expensive, connection type)

## "bignumber.js": "git+https://github.com/status-im/bignumber.js.git#v4.0.2-status"

used to work with big numbers in JS 
BN.js (used in web3-utils) doesn't support decimals, though we could work without decimals

should be replaced by JSBI https://github.com/GoogleChromeLabs/jsbi/blob/master/README.md
see web-utils issue: https://github.com/ethereum/web3.js/issues/2171

## "buffer": "^5.4.2"

required by bignumber.js otherwise there is a compilation error

## "chance": "^1.1.0"

used to generate guids and random values in `status-im.utils.random`

## "emojilib": "^2.4.0"

used for emojis

## "eth-phishing-detect": "^1.1.13"

used to check if URL is known for phishing

## "hi-base32": "^0.5.0"

used only in `status-im.browser.core` to convert base58 hash into base32 hash
TODO: could be replaced by a simple implementation of ascii to base32

## "i18n-js": "^3.3.0"

used to manage translations

## "identicon.js": "git+https://github.com/status-im/identicon.js.git#v1.2.1-status"

TODO: switch to latest upstream version, fork is seriously outdated and changes have been merged to master long time ago
used to generate identicons

## "qrcode": "^1.4.1"

used to generate QR code

## "react": "16.8.3"

react library, update based on react-native recommendation

## "react-dom": "16.4.2"

react library, update based on react-native recommendation

## "react-native": "git+https://github.com/status-im/react-native.git#v0.59.10"

react-native

TODO: forked because of specific markdown handling, should be bountied to be turned into a library so we can go back to upstream version

## "react-native-background-timer": "^2.1.1"

used to have setTimeout and setInterval that don't trigger yellow warning about long timeouts

## "react-native-camera": "git+https://github.com/status-im/react-native-camera.git#v1.1.5-1-status"

used to read QR code and take profile picture
TODO: update version, this one doesn't work with Android (done in react-native upgrade branch)

## "react-native-config": "git+https://github.com/status-im/react-native-config.git#0.11.2-1"

used to fetch config values from env
TODO: not sure fork usage is justified

## "react-native-dialogs": "^1.0.5"

used to show dialogs but only in android in list selection `status-im.ui.components.list-selection`, iOS uses action-sheet
TODO: see if we should actually remove it

## "react-native-fetch-polyfill": "^1.1.3"

used to be able to put a timeout on fetch request
TODO: check if the fetch timeout is actually aborting the request or only ignoring the callback on the response, if it
doesn't then this is useless and we are better of using a simple seTimeout

## "react-native-fs": "^2.14.1"

used for some filesystem related functions, for instance to get no-backup directory in `status-im.utils.platform`

## "react-native-gesture-handler": "1.3.0"

library used by react-navigation
use version recommended by react-navigation

## "react-native-image-crop-picker": "^0.18.2"

used for profile picture

## "react-native-image-resizer": "git+https://github.com/status-im/react-native-image-resizer.git#1.0.##-status"

used for profile picture

## "react-native-keychain": "git+https://github.com/status-im/react-native-keychain.git#v.3.0.0-status"

used for storing password when user saves password
TODO: check if fork is necessary, merge with upstream?

## "react-native-mail": "git+https://github.com/status-im/react-native-mail.git#v3.0.8_status"

used to send email to support when phone is shaked

## "react-native-screens": "1.0.0-alpha.22"

used by react-navigation

TODO: update once bug is fixed for Android that causes crash when app is back from background after being put in background with hardware backbutton press

## "react-native-shake": "^3.3.1"

used to send email to support when phone is shaked

## "react-native-lottie-splash-screen": "^1.0.1"

used to make splash screen with lottie animation during initial loading for android, also used as replacement of "react-native-splash-screen" for iOS

## "react-native-status-keycard": "^2.5.7"

used for keycard

## "react-native-svg": "9.7.1"

used for svg icons, mostly collectibles are using svg
TODO: updating requires a pod update on iOS

## "react-native-biometrics": "^3.0.1"

used for biometric authentication on Android and iOS 

## "react-native-webview": "git+https://github.com/status-im/react-native-webview.git#v8.0.7_3"

used for browser

## "react-navigation": "3.11.0"

used for native navigation
fixed because even bugfix version upgrade causes runtime errors with current version of react-native

## "react-native-draggable-flatlist": "https://github.com/computerjazz/react-native-draggable-flatlist"

A drag-and-drop-enabled FlatList component for React Native

## "react-native-shadow-2": "https://github.com/SrBrahma/react-native-shadow-2"

A shadow component that is consistent across both platforms
