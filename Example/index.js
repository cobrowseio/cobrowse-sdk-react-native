/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';

AppRegistry.registerComponent(appName, () => App);

import CobrowseIO from 'cobrowse-sdk-react-native';
CobrowseIO.license = 'trial';
CobrowseIO.start();
