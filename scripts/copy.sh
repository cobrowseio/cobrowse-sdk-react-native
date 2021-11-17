cd $(dirname "$0")/../

rm -rf ./Example/node_modules/cobrowse-sdk-react-native/js
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/ios
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/android
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/ts

cp -R ./js ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./ios ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./android ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./ts ./Example/node_modules/cobrowse-sdk-react-native/
