package io.cobrowse.reactnative;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class DynamicSessionControlInvocationHandler implements InvocationHandler {
  private CobrowseIOCommonDelegates delegates;
  private final Map<String, Method> methods = new HashMap<>();

  DynamicSessionControlInvocationHandler(CobrowseIOCommonDelegates delegates) {
    this.delegates = delegates;

    for(Method method: delegates.getClass().getDeclaredMethods()) {
      this.methods.put(method.getName(), method);
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable {

    if (method.getName() == "showSessionControls" || method.getName() == "hideSessionControls") {
      return null;
    }

    Object result = methods.get(method.getName()).invoke(delegates, args);
    return result;
  }
}
