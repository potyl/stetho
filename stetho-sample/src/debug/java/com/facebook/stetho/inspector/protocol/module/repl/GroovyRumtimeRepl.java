/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.protocol.module.repl;

import android.content.Context;
import android.text.TextUtils;
import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.inspector.protocol.module.Runtime;
import com.facebook.stetho.inspector.protocol.module.Runtime.EvaluateResponse;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;


public class GroovyRumtimeRepl extends AbstractRuntimeRepl {

  private final GroovyShell groovy;
  private final Context context;

  public GroovyRumtimeRepl(@Nonnull Context context) {
    CompilerConfiguration config = new CompilerConfiguration();
    config.setTargetBytecode(CompilerConfiguration.JDK5);

    Binding binding = new Binding();
    binding.setVariable("context", context.getApplicationContext());
    groovy = new GroovyShell(binding, config);
    this.context = context;
  }

  protected EvaluateResponse eval(JSONObject params) throws JSONException {
    LogUtil.i("groovy) Got params: %s", params.toString());

    String operation = params.getString("objectGroup");
    LogUtil.i("eval Operation: %s", operation);
    // {"expression":"app.gc();","objectGroup":"console","includeCommandLineAPI":true,"doNotPauseOnExceptionsAndMuteConsole":false,"contextId":1,"returnByValue":false,"generatePreview":true}

    if (TextUtils.equals("console", operation)) {
      String expression = params.getString("expression");
      LogUtil.i("groovy) eval expression: %s", expression);
      //Object result = Eval.me(expression);

      GrooidShell shell = new GrooidShell(context.getDir("dynclasses", 0), this.getClass().getClassLoader());
      Object result = shell.evaluate(expression);

      //Object result = groovy.evaluate(expression);
      Object value = result == null ? null : result.toString();
      return buildEvaluateResponse(Runtime.ObjectType.STRING, value, false);
    }

    LogUtil.i("groovy) Can't handle: %s", params.toString());
    return buildEvaluateResponse(Runtime.ObjectType.STRING, "Unsupported operation", false);
  }
}
