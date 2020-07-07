package com.getcapacitor.plugin;

import android.app.Activity;
import android.content.SharedPreferences;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@NativePlugin()
public class WebView extends Plugin {
  public static final String WEBVIEW_PREFS_NAME = "CapWebViewSettings";
  public static final String CAP_SERVER_PATH = "serverBasePath";

  @PluginMethod()
  public void setServerBasePath(PluginCall call) {
    String path = call.getString("path");
    Map<String, String> additionalHttpHeaders = new HashMap<>();
    JSObject headers = call.getObject("headers");

    Iterator<String> it = headers.keys();

    while (it.hasNext()) {
      String key = (String) it.next();

      if (key.isEmpty()) {
        call.error("one of headers key is missing");
        break;
      }

      String val = headers.getString("key");

      if (val.isEmpty()) {
        call.error("value for " + key + " is missing");
        break;
      }

      additionalHttpHeaders.put(key, val);
    }

    if (!additionalHttpHeaders.isEmpty()) {
      bridge.setServerBasePath(path, additionalHttpHeaders);
    } else {
      bridge.setServerBasePath(path);
    }
    
    call.success();
  }

  @PluginMethod()
  public void getServerBasePath(PluginCall call) {
    String path = bridge.getServerBasePath();
    JSObject ret = new JSObject();
    ret.put("path", path);
    call.success(ret);
  }

  @PluginMethod()
  public void persistServerBasePath(PluginCall call) {
    String path = bridge.getServerBasePath();
    SharedPreferences prefs = getContext().getSharedPreferences(WEBVIEW_PREFS_NAME, Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(CAP_SERVER_PATH, path);
    editor.apply();
    call.success();
  }
}
