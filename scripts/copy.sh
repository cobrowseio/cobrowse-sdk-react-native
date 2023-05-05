cd $(dirname "$0")/../

rm -rf ./Example/node_modules/cobrowse-sdk-react-native/js
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/lib
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/package.json
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/ios
rm -rf ./Example/node_modules/cobrowse-sdk-react-native/android

cp -R ./js ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./lib ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./package.json ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./ios ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./android ./Example/node_modules/cobrowse-sdk-react-native/
