package io.cobrowse.reactnative;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DynamicSessionControlInvocationHandler implements InvocationHandler {
  private CobrowseIOCommonDelegates delegates;
  private final Map<String, Method> methods = new HashMap<>();

  public DynamicSessionControlInvocationHandler(CobrowseIOCommonDelegates delegates) {
    this.delegates = delegates;

    for(Method method: delegates.getClass().getDeclaredMethods()) {
      this.methods.put(method.getName(), method);
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
    throws Throwable {
    if(method.getName() == "hideSessionControls" || method.getName() == "showSessionControls") {
      return null;
    }

    Object result = methods.get(method.getName()).invoke(delegates, args);
    return result;
  }
}
