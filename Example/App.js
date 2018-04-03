import React from 'react';
import { Button, View, Text } from 'react-native';
import { StackNavigator } from 'react-navigation';
import CobrowseIO, { CobrowseView } from 'cobrowse-sdk-react-native';

CobrowseIO.license = "trial";
CobrowseIO.customData = {
    user_email: "react-native@example.com"
};

class HomeScreen extends React.Component {
    render() {
        return (
            <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
                <Text>Home Screen</Text>
                <Button
                    title="Start Cobrowse"
                    onPress={() => this.props.navigation.navigate('Cobrowse')}
                />
            </View>
        );
    }
}

class CobrowseScreen extends React.Component {
    render() {
        return (
            <CobrowseView onEnded={() => this.props.navigation.pop()} />
        );
    }
}


const RootStack = StackNavigator({
    Home: {
        screen: HomeScreen,
    },
    Cobrowse: {
        screen: CobrowseScreen,
    },
},{
    initialRouteName: 'Home',
});


export default class App extends React.Component {
    render() {
        return <RootStack />;
    }
}
