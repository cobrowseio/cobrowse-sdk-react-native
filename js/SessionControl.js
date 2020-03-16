import React, { Component } from 'react';
import CobrowseIO from './CobrowseIO';

export default class SessionControl extends Component {

    constructor() {
        super();
        this.state = { session: null };
    }

    componentDidMount() {
        this.updateListener = CobrowseIO.addListener(CobrowseIO.SESSION_UPDATED, (session) => {
           this.setState({ session });
        });
    }

    componentWillUnmount() {
        if (this.updateListener) this.updateListener.remove();
    }

    render() {
        if (this.state.session && this.state.session.state === 'active') return this.props.children;
        else return null;
    }
}
