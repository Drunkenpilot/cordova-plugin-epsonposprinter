package be.betalife.cordova.plugin.epsonposprinter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
			mFilterOption.setPortType(Discovery.PORTTYPE_ALL);
			try {
				Log.i("测试", "测试2");
				Discovery.start(cordova.getActivity().getApplicationContext(), mFilterOption, mDiscoveryListener);
				for (HashMap<String, String> one : mPrinterList) {
					Log.i("测试", "mPrinterList: " + one.get("PrinterName") + " ~ " +  one.get("Target"));
				}
				JSONArray mPrinterListJson = new JSONArray(mPrinterList);
				for(int i = 0; i < mPrinterListJson.length();i++) {
					JSONObject innerObj = mPrinterListJson.getJSONObject(i);
					for(Iterator it = innerObj.keys(); it.hasNext(); ) {
						String key = (String)it.next();
						Log.i(key + ":" + innerObj.get(key));
					}
				}
				callbackContext.success(mPrinterListJson);
				Log.i("测试", "测试3");
			} catch (Epos2Exception e) {
				Log.i("测试", "测试4");
				Log.i("测试", "e:" + e.getErrorStatus());
				ShowMsg.showException(e, "start", cordova.getActivity().getApplicationContext());
				callbackContext.error("e:" + e.getErrorStatus());

			}

			return true;
		}else if(action.equals("stopSearch")){
			try{
				Log.i("停止测试", "停止测试2");
				Discovery.stop();
				Log.i("停止测试", "停止测试3");
			}catch(Epos2Exception e){
				Log.i("停止测试", "停止测试4");
				Log.i("停止测试", "e:" + e.getErrorStatus());
				// callbackContext.error("e:" + e.getErrorStatus());
			}
			return true;
		}else if(action.equals("print")){

		}
		return false;
	}

	@Override
	public void onDestroy() {
		Log.i("停止搜索", "停止1");
		super.onDestroy();

		while (true) {
			try {
				Discovery.stop();
				break;
			}
			catch (Epos2Exception e) {
				if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
					break;
				}
			}
		}

		mFilterOption = null;
	}


	private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
		@Override
		public void onDiscovery(final DeviceInfo deviceInfo) {
			Log.i("测试", "测试5");
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("PrinterName", deviceInfo.getDeviceName());
			item.put("Target", deviceInfo.getTarget());
			Log.i("测试", "PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());

			mPrinterList.add(item);
			// for (HashMap<String, String> one : mPrinterList) {
			// 	Log.i("测试", "mPrinterList: " + one.get("PrinterName") + " ~ " +  one.get("Target"));
			// }
			Log.i("测试", "测试6");
			// Toast.makeText(cordova.getActivity(), "PrinterName: " + deviceInfo.getDeviceName(), Toast.LENGTH_SHORT)
			// 		.show();
			// Toast.makeText(cordova.getActivity(), "Target: " + deviceInfo.getTarget(), Toast.LENGTH_SHORT).show();
			// mPrinterListAdapter.notifyDataSetChanged();
			// return item;
			Log.i("测试", "测试7");
			// callbackContext
			// .success("PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());
			Log.i("测试", "测试8");
		}

	};


}
