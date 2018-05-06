## User initiated Sessions

We've provided a view that will do all the session creation and management for you. All you need to do is include this somewhere in your react native view hierarchy. It's not a requirement to use this UI, you can easily build your own if you like!

```javascript
import { CobrowseView } from 'cobrowse-sdk-react-native';

export default class App extends Component {
    render() {
        return (
            <View>
                <CobrowseView />
            </View>
        );
    }
}
```
