import React from 'react';
import { Button, View, Text } from 'react-native';
import { StackNavigator } from 'react-navigation';
import { CobrowseView } from 'cobrowse-sdk-react-native';

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
            <CobrowseView
                license='trial'
                onEnded={() => this.props.navigation.pop()}
                api='https://api.staging.cobrowse.io' 
            />
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
