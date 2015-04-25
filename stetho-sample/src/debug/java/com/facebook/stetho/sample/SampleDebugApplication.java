/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.sample;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.inspector.protocol.module.Runtime;
import com.facebook.stetho.inspector.protocol.module.repl.GroovyRumtimeRepl;
import com.facebook.stetho.inspector.protocol.module.repl.RuntimeRepl;

import java.util.ArrayList;
import java.util.List;

public class SampleDebugApplication extends SampleApplication {
  private static final String TAG = "SampleDebugApplication";

  @Override
  public void onCreate() {
    super.onCreate();

    long startTime = SystemClock.elapsedRealtime();
    final Context context = this;

    // Find the runtime module and add it a REPL, Mozilla's Rhino (JavaScript)
    final List<ChromeDevtoolsDomain> modules = new ArrayList<>();
    for (ChromeDevtoolsDomain module : Stetho.defaultInspectorModulesProvider(context).get()) {
      if (module instanceof Runtime) {
        Runtime runtime = (Runtime) module;
        RuntimeRepl repl = new GroovyRumtimeRepl(this);
        runtime.setRepl(repl);
      }
      modules.add(module);
    }

    InspectorModulesProvider runtimeModulesProvider = new InspectorModulesProvider() {
      @Override
      public Iterable<ChromeDevtoolsDomain> get() {
        return modules;
      }
    };

    Stetho.initialize(
        Stetho.newInitializerBuilder(context)
            .enableDumpapp(new SampleDumperPluginsProvider(context))
            .enableWebKitInspector(runtimeModulesProvider)
            .build());
    long elapsed = SystemClock.elapsedRealtime() - startTime;
    Log.i(TAG, "Stetho initialized in " + elapsed + " ms");
  }

  private static class SampleDumperPluginsProvider implements DumperPluginsProvider {
    private final Context mContext;

    public SampleDumperPluginsProvider(Context context) {
      mContext = context;
    }

    @Override
    public Iterable<DumperPlugin> get() {
      ArrayList<DumperPlugin> plugins = new ArrayList<DumperPlugin>();
      for (DumperPlugin defaultPlugin : Stetho.defaultDumperPluginsProvider(mContext).get()) {
        plugins.add(defaultPlugin);
      }
      plugins.add(new HelloWorldDumperPlugin());
      plugins.add(new APODDumperPlugin(mContext.getContentResolver()));
      return plugins;
    }
  }

}
