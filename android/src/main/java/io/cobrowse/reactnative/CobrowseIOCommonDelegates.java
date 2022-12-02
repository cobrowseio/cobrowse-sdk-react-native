package io.cobrowse.reactnative;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;

import io.cobrowse.CobrowseIO;

interface CobrowseIOCommonDelegates  extends io.cobrowse.CobrowseIO.Delegate, io.cobrowse.CobrowseIO.SessionRequestDelegate,
  io.cobrowse.CobrowseIO.SessionLoadDelegate, io.cobrowse.CobrowseIO.RedactionDelegate,
  io.cobrowse.CobrowseIO.RemoteControlRequestDelegate, CobrowseIO.FullDeviceRequestDelegate {

  public void findNodeManager();
  public void setUnredactedTags(final ReadableArray reactTags, final Promise promise);
}
