import React, { Component } from 'react'
import { WebView } from 'react-native-webview'
import { unredact } from 'cobrowse-sdk-react-native'

const html = `
  <!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1" />
      <title>WebView</title>

      <style>
        .red { 
          color: red; 
        }
      </style>
    </head>
    <body>
      <h2>WebView</h2>
      <p>This paragraph won't be redacted</p>
      <p class="red redact-me">This paragraph will be redacted</p>
      <label for="sensitive">Sensitive Input</label>
      <input id="sensitive" type="text" />
    </body>
  </html>
`

class MyWebView extends Component {
  render (): JSX.Element {
    return (
      <WebView
        source={{ html }}
        style={{ height: 300, width: 300 }}
      />
    )
  }
}

export default unredact(MyWebView)
