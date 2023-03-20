package io.cobrowse.reactnative;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;

import io.cobrowse.CobrowseIO;

interface CobrowseIOCommonDelegates  extends CobrowseIO.Delegate, CobrowseIO.SessionRequestDelegate,
  CobrowseIO.SessionLoadDelegate, CobrowseIO.RedactionDelegate, CobrowseIO.UnredactionDelegate,
  CobrowseIO.RemoteControlRequestDelegate, CobrowseIO.FullDeviceRequestDelegate {

  public void findNodeManager();
  public void setUnredactedTags(final ReadableArray reactTags, final Promise promise);
}
