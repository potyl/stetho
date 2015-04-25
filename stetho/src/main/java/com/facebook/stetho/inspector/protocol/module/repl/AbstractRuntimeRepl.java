/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.protocol.module.repl;

import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.inspector.protocol.module.Runtime.EvaluateResponse;
import com.facebook.stetho.inspector.protocol.module.Runtime.ObjectType;
import com.facebook.stetho.inspector.protocol.module.Runtime.RemoteObject;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractRuntimeRepl implements RuntimeRepl {

  @Override
  public EvaluateResponse evaluate(JSONObject params) {
    try {
      return eval(params);
    } catch (Exception e) {
      LogUtil.e(e, "Failed to evaluate expression");
      return buildEvaluateResponse(ObjectType.STRING, e.getMessage(), true);
    }
  }

  @Nonnull
  protected abstract EvaluateResponse eval(@Nonnull JSONObject params) throws Exception;

  @Nonnull
  protected EvaluateResponse buildEvaluateResponse(@Nonnull ObjectType type, @Nullable Object value, boolean wasThrown) {
    RemoteObject remoteObject = new RemoteObject();
    remoteObject.type = type;
    remoteObject.value = value;
    EvaluateResponse response = new EvaluateResponse();
    response.result = remoteObject;
    response.wasThrown = wasThrown;
    return response;
  }
}