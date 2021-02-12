import React from 'react';
import { View, requireNativeComponent } from 'react-native';
const CBIOCobrowseRedacted = requireNativeComponent('CBIOCobrowseRedacted');

module.exports = function(props) {
    return (
        <CBIOCobrowseRedacted><View>{props.children}</View></CBIOCobrowseRedacted>
    );
}
