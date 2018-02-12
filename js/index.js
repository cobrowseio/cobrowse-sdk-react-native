'use strict';

const CobrowseIO = require('react-native').NativeModules.CobrowseIO;

module.exports = {

    createSession: function(callback) {
        CobrowseIO.createSession((err, code) => {
            callback(err, code);
        });
    },

    getSession: function(code, callback) {
        callback(null, { code:"123456" });
    }

}
