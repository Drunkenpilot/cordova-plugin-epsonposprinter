package be.betalife.cordova.plugin.epsonposprinter;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;

import android.util.Log;
import android.widget.Toast;

public class EpsonPrinter extends CordovaPlugin {

	private ArrayList<HashMap<String, String>> mPrinterList = null;
	private FilterOption mFilterOption = null;
	private CallbackContext callbackContext = null;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		// your init code here
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		Log.i("测试", "测试1");
		if (action.equals("search")) {
			mPrinterList = new ArrayList<HashMap<String, String>>();
			mFilterOption = new FilterOption();
			mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
			mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
			mFilterOption.setPortType(Discovery.PORTTYPE_USB);
			try {
				Log.i("测试", "测试2");
				Discovery.start(cordova.getActivity(), mFilterOption, mDiscoveryListener);
				Log.i("测试", "测试3");
				// currentPluginInstance.search(currentCallbackContext);
			} catch (Epos2Exception e) {
				Log.i("测试", "测试4");
				Log.i("测试", "e:" + e.getErrorStatus());
				callbackContext.error("e:" + e.getErrorStatus());
			}

			return true;
		}
		return false;
	}

	// private void search(CallbackContext callbackContext){
	// Context context = this.cordova.getActivity().getApplicationContext();
	// mFilterOption = new FilterOption();
	// // mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
	// // mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
	// try{
	// Discovery.start(context, mFilterOption, mDiscoveryListener);
	// }catch(Exception e){
	// callbackContext.error(e.getMessage());
	// }
	//
	// }

	private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
		@Override
		public void onDiscovery(final DeviceInfo deviceInfo) {
			Log.i("测试", "测试5");
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("PrinterName", deviceInfo.getDeviceName());
			item.put("Target", deviceInfo.getTarget());
			Log.i("测试", "PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());

			mPrinterList.add(item);
			Log.i("测试", "测试6");
			Toast.makeText(cordova.getActivity(), "PrinterName: " + deviceInfo.getDeviceName(), Toast.LENGTH_SHORT)
					.show();
			Toast.makeText(cordova.getActivity(), "Target: " + deviceInfo.getTarget(), Toast.LENGTH_SHORT).show();
			// mPrinterListAdapter.notifyDataSetChanged();
			// return item;
			Log.i("测试", "测试7");
			callbackContext
					.success("PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());
			Log.i("测试", "测试8");
		}
	};

}
