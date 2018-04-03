import React, { Component } from 'react';
import {
    View,
    Text,
    ActivityIndicator,
    TouchableOpacity,
    StyleSheet,
    Linking
} from 'react-native';
import CobrowseIO from './CobrowseIO';

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'flex-end'
    },
    text: {
        textAlign: 'center',
        margin: 15,
        fontSize: 15,
        lineHeight: 20
    },
    code: {
        fontSize: 29,
        padding: 20,
        fontWeight: 'bold',
        textAlign: 'center'
    },
    poweredby: {
        color: '#444',
        marginTop: 40,
        marginBottom: 25
    },
    button: {
        color: 'rgb(0, 122, 255)',
        fontSize: 18,
        margin: 10
    }
});

export default class CobrowseView extends Component {

    constructor() {
        super();
        this.state = { error: null, session: null };
    }

    componentDidMount() {
        if (this.props.license) {
            console.warn('Passing license to view is deprecated. Use CobrowseIO.license = "..." instead');
            CobrowseIO.license = this.props.license;
        }

        if (this.props.code) {
            CobrowseIO.loadSession(this.props.code, (err, session) => {
                this.setState({ error: err, session });
            });
        } else {
            CobrowseIO.currentSession((err, current) => {
                if (current) {
                    this.setState({error:err, session:current});
                } else {
                    CobrowseIO.createSession((err, session) => {
                        this.setState({ error: err, session });
                    });
                }
            });
        }

        this.updateListener = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, (session) => {
           this.setState({ session });
        });
        this.endListener = CobrowseIO.addListener(CobrowseIO.SESSION_ENDED, (session) => {
            if (this.props.onEnded) this.props.onEnded();
        });
    }

    componentWillUnmount() {
        if (this.updateListener) this.updateListener.remove();
        if (this.endListener) this.endListener.remove();
    }

    approveSession() {
        CobrowseIO.activateSession((err, session) => {
            this.setState({ error: err, session });
        });
    }

    endSession() {
        CobrowseIO.endSession((err, session) => {
            this.setState({ error: err, session: null });
        });
    }

    renderError() {
        return (
            <Text style={[styles.text]}>{this.state.error}</Text>
        );
    }

    renderCode() {
        let code = this.state.session && this.state.session.code;
        if (code) code = code.substr(0, 3) + '-' + code.substr(3);
        return (
            <View>
                <Text style={[styles.code, {opacity:code?1:0.2}]}>{code || '000-000'}</Text>
                <Text style={[styles.text]}>Provide this code to your support agent to begin screen sharing.</Text>
                <ActivityIndicator/>
            </View>
        );
    }

    renderApproval() {
        return (
            <View>
                <Text style={[styles.text]}>A support agent would like to use this app with you. If you accept, they will only be able to see screens from this app.</Text>
                <TouchableOpacity onPress={()=>this.approveSession()}>
                    <Text style={[styles.text, styles.button]}>Approve</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={()=>this.endSession()}>
                    <Text style={[styles.text, styles.button]}>Decline</Text>
                </TouchableOpacity>
            </View>
        )
    }

    renderManageSession() {
        return (
            <View>
                <Text style={[styles.text]}>You{"'"}re sharing screens from this app with a support agent.</Text>
                <TouchableOpacity onPress={()=>this.endSession()}>
                    <Text style={[styles.text, styles.button]}>End Session</Text>
                </TouchableOpacity>
            </View>
        );
    }

    renderContent() {
        const { error, session } = this.state;
        if (error) {
            return this.renderError();
        } else if ((!session) || ((session.state === 'pending') && !session.agent)) {
            return this.renderCode();
        } else if (!this.state.session.approved) {
            return this.renderApproval();
        } else {
            return this.renderManageSession();
        }
    }

    render() {
        return (
            <View style={styles.container}>
                {this.renderContent()}
                <TouchableOpacity
                    onPress={()=>Linking.openURL('https://cobrowse.io/sdk-powered-by')} >
                    <Text style={[styles.text, styles.poweredby]}>Powered by Cobrowse.io</Text>
                </TouchableOpacity>
            </View>
        );
    }
}
