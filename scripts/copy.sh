cd $(dirname "$0")/../

rm -rf ./Example/node_modules/cobrowse-sdk-react-native
mkdir ./Example/node_modules/cobrowse-sdk-react-native

cp -R ./android ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./ios ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./js ./Example/node_modules/cobrowse-sdk-react-native/
cp -R ./package.json ./Example/node_modules/cobrowse-sdk-react-native/


