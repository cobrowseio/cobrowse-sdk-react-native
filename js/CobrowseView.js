import React, { Component } from 'react';
import {
    View,
    Text,
    ActivityIndicator,
    TouchableOpacity
} from 'react-native';
import CobrowseIO from './CobrowseIO';

export default class CobrowseView extends Component {

    constructor() {
        super();
        this.state = { error: null, session: null };
    }

    componentDidMount() {
        if (this.props.api) CobrowseIO.api = this.props.api;
        if (this.props.license) CobrowseIO.license = this.props.license;

        if (this.props.code) {
            CobrowseIO.loadSession(this.props.code, (err, session) => {
                this.setState({ error: err, session });
            });
        } else {
            CobrowseIO.createSession((err, session) => {
                this.setState({ error: err, session });
            });
        }

        this.listener = CobrowseIO.addListener('session_updated', (session) => {
           this.setState({ session });
       });
    }

    componentWillUnmount() {
        if (this.listener) this.listener.remove();
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
            <Text>{this.state.error}</Text>
        );
    }

    renderCode() {
        return (
            <View>
                <Text>{this.state.session && this.state.session.code}</Text>
                <Text>Provide this code to your support agent to begin screen sharing.</Text>
                <ActivityIndicator/>
            </View>
        );
    }

    renderApproval() {
        return (
            <View>
                <Text>A support agent would like to use this app with you. If you accept, they will only be able to see screens from this app.</Text>
                <TouchableOpacity onPress={()=>this.approveSession()}><Text>Approve</Text></TouchableOpacity>
                <TouchableOpacity onPress={()=>this.endSession()}><Text>Decline</Text></TouchableOpacity>
            </View>
        )
    }

    renderManageSession() {
        return (
            <View>
                <Text>You{"''"}re sharing screens from this app with a support agent.</Text>
                <TouchableOpacity onPress={()=>this.endSession()}><Text>End Session</Text></TouchableOpacity>
            </View>
        );
    }

    render() {
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
}
