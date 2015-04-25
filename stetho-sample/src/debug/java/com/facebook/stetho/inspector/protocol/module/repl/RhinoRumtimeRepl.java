/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.protocol.module.repl;

import android.text.TextUtils;
import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.inspector.protocol.module.Runtime;
import com.facebook.stetho.inspector.protocol.module.Runtime.EvaluateResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import javax.annotation.Nonnull;


public class RhinoRumtimeRepl extends AbstractRuntimeRepl {

  public RhinoRumtimeRepl(@Nonnull android.content.Context context) {
  }

  protected EvaluateResponse eval(JSONObject params) throws JSONException {
    LogUtil.i("js) Got params: %s", params.toString());

    String operation = params.getString("objectGroup");
    LogUtil.i("js) eval Operation: %s", operation);
    // {"expression":"app.gc();","objectGroup":"console","includeCommandLineAPI":true,"doNotPauseOnExceptionsAndMuteConsole":false,"contextId":1,"returnByValue":false,"generatePreview":true}

    if (TextUtils.equals("console", operation)) {
      String expression = params.getString("expression");
      LogUtil.i("js) eval expression: %s", expression);

      final org.mozilla.javascript.Context jsContext = Context.enter();
      jsContext.setLanguageVersion(Context.VERSION_1_7);
      jsContext.setOptimizationLevel(-1);
      final Scriptable jsScope = jsContext.initStandardObjects();

      Object result = jsContext.evaluateString(jsScope, expression, "chrome", 0, null);
      LogUtil.i("js) Result: %s; type: %s", result, result == null ? null : result.getClass().getName());
      Object value = result == null ? null : result.toString();
      return buildEvaluateResponse(Runtime.ObjectType.STRING, value, false);
    }

    LogUtil.i("js) Can't handle: %s", params.toString());
    return buildEvaluateResponse(Runtime.ObjectType.STRING, "Unsupported operation", false);
  }
}
