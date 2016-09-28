package be.betalife.cordova.plugin.epsonposprinter;


import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.Epos2Exception;

public class EpsonPrinter extends CordovaPlugin{

  private Context mContext = null;
  private ArrayList<HashMap<String, String>> mPrinterList = null;
  private FilterOption mFilterOption = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      // your init code here
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException{
    final EpsonPrinter currentPluginInstance = this;
    final JSONArray Arguments = args;
    final CallbackContext currentCallbackContext = callbackContext;
    if(action.equals("search")){
      cordova.getThreadPool().execute(new Runnable(){
        public void  run(){
          Context context = cordova.getActivity().getApplicationContext();
          mPrinterList = new ArrayList<HashMap<String, String>>();
          mFilterOption = new FilterOption();
          // mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
          // mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
          try{
            Discovery.start(context, mFilterOption, mDiscoveryListener);
            // currentPluginInstance.search(currentCallbackContext);
          } catch(Exception e){
          //  currentCallbackContext.error(e.getMessage());
          }
        }
      });
      return true;
    }
    return false;
  }

  // private void search(CallbackContext callbackContext){
  //   Context context = this.cordova.getActivity().getApplicationContext();
  //   mFilterOption = new FilterOption();
  //   // mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
  //   // mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
  //   try{
  //     Discovery.start(context, mFilterOption, mDiscoveryListener);
  //   }catch(Exception e){
  //     callbackContext.error(e.getMessage());
  //   }
  //
  // }

  private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
    @Override
    public void onDiscovery(final DeviceInfo deviceInfo) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public synchronized void run() {
          HashMap<String, String> item = new HashMap<String, String>();
          item.put("PrinterName", deviceInfo.getDeviceName());
          item.put("Target", deviceInfo.getTarget());
          mPrinterList.add(item);
          //          mPrinterListAdapter.notifyDataSetChanged();
          //  return item;
        }
      });
    }
  };

}
