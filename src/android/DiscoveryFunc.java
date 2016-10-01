package be.betalife.cordova.plugin.epsonposprinter;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Iterator;
// import java.lang.System;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
// import org.json.JSONObject;
import org.json.JSONObject;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import android.util.Log;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.DialogInterface;


public class DiscoveryFunc extends CordovaPlugin{

  public DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
    @Override
    public void onDiscovery(final DeviceInfo deviceInfo) {
      Log.i("测试", "测试5");
      HashMap<String, String> item = new HashMap<String, String>();
      item.put("PrinterName", deviceInfo.getDeviceName());
      item.put("Target", deviceInfo.getTarget());
      Log.i("测试", "PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());

      mPrinterList.add(item);
      for (HashMap<String, String> one : mPrinterList) {
        Log.i("测试", "mPrinterList: " + one.get("PrinterName") + " ~ " + one.get("Target"));
      }
      Log.i("测试", "测试6");
      this.showToast(deviceInfo);
      // mPrinterListAdapter.notifyDataSetChanged();
      // return item;
      Log.i("测试", "测试7");
      Log.i("测试", "测试8");
    }

    public void showToast(final DeviceInfo deviceInfo) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(cordova.getActivity(), "PrinterName: " + deviceInfo.getDeviceName(),
          Toast.LENGTH_SHORT).show();
          Toast.makeText(cordova.getActivity(), "Target: " + deviceInfo.getTarget(), Toast.LENGTH_SHORT)
          .show();
        }
      });
    }

  };
}
