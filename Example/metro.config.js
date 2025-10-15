/**
 * Metro configuration for React Native
 * https://github.com/facebook/react-native
 *
 * @format
 */


const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const path = require('path');

// The project root is where your metro.config.js is located
const projectRoot = path.resolve(__dirname);

// The path to the `cobrowse-sdk-react-native` library at the root of this repo. 
const libraryRoot = path.resolve(projectRoot, '..');

const config = {
  // Add the watchFolders option to let Metro know it needs to watch the library's directory
  watchFolders: [libraryRoot],

  resolver: {
    // Enable symlink support to allow Metro to correctly resolve file-based dependency to `cobrowse-sdk-react-native`
    unstable_enableSymlinks: true,
    // Always resolve modules (e.g. react) from the Example app’s node_modules
    extraNodeModules: new Proxy(
      {},
      {
        get: (_, name) => path.join(projectRoot, 'node_modules', name),
      }
    ),
    nodeModulesPaths: [path.join(projectRoot, 'node_modules')],
  },

  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
};

const defaultConfig = getDefaultConfig(projectRoot);

module.exports = mergeConfig(defaultConfig, config);
