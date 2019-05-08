# Redact sensitive data (optional)

Please see full documentation at [https://cobrowse.io/docs](https://cobrowse.io/docs).

Try our **online demo** at the bottom of our homepage at <https://cobrowse.io/#tryit>.

## Implementation

Redaction allows you to remove specific elements from the agents view. This allows you to keep private user data private.

To redact an element in your React native application you can wrap it in a <Redacted> tag provided by the Cobrowse module:


```javascript
import React, { Component } from 'react';
import { Text, View } from 'react-native';
import { Redacted } from 'cobrowse-sdk-react-native';

export default class MyComponent extends Component {
  render() {
    return (
      <View style={styles.container}>
        <Redacted>
            <Text style={styles.instructions}>This text should be secret</Text>
        </Redacted>
      </View>
    );
  }
}

// ... stylesheets etc...

```
