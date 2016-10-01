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
		final int millSeconds = args.optInt(0, 10 * 1000);
		Log.i("测试", "测试1");
		if (action.equals("search")) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					mPrinterList = new ArrayList<HashMap<String, String>>();
					mFilterOption = new FilterOption();
					mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
					mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
					mFilterOption.setPortType(Discovery.PORTTYPE_ALL);
					try {
						Log.i("测试", "测试2");
						onPreExecute();
						Discovery.start(cordova.getActivity(), mFilterOption, mDiscoveryListener);

						Thread.sleep(millSeconds);
						Log.i("测试", "测试3");
					} catch (Epos2Exception e) {
						Log.i("测试", "测试4");
						Log.i("测试", "e:" + e.getErrorStatus());
						onPostExecute();
						ShowMsg.showException(e, "start", cordova.getActivity());
						//EpsonPrinter.this.callbackContext.error("e:" + e.getErrorStatus());
					} catch (InterruptedException e) {
						Log.i("测试", "InterruptedException: " + e.getMessage());
					} finally {
						stopDiscovery();
					}
				}
			});
			return true;
		}else if (action.equals("print")) {
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					runPrintReceiptSequence();
				}
			});
			return true;
		}
		return false;

	}

	private boolean runPrintReceiptSequence() {
		if (!initializeObject()) {
			return false;
		}

		if (!createReceiptData()) {
			finalizeObject();
			return false;
		}

		if (!printData()) {
			finalizeObject();
			return false;
		}

		return true;
	}


	private boolean initializeObject() {
		try {
			mPrinter = new Printer(6,0,cordova.getActivity());
		}
		catch (Exception e) {
			ShowMsg.showException(e, "Printer", cordova.getActivity());
			return false;
		}

		mPrinter.setReceiveEventListener(this);

		return true;
	}


	private boolean printData() {
		if (mPrinter == null) {
			return false;
		}

		if (!connectPrinter()) {
			return false;
		}

		PrinterStatusInfo status = mPrinter.getStatus();

		dispPrinterWarnings(status);

		if (!isPrintable(status)) {
			ShowMsg.showMsg(makeErrorMessage(status), cordova.getActivity());
			try {
				mPrinter.disconnect();
			}
			catch (Exception ex) {
				// Do nothing
			}
			return false;
		}

		try {
			mPrinter.sendData(Printer.PARAM_DEFAULT);
		}
		catch (Exception e) {
			ShowMsg.showException(e, "sendData", cordova.getActivity());
			try {
				mPrinter.disconnect();
			}
			catch (Exception ex) {
				// Do nothing
			}
			return false;
		}

		return true;
	}




	private boolean createReceiptData() {
		String method = "";
		Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
		StringBuilder textData = new StringBuilder();
		final int barcodeWidth = 2;
		final int barcodeHeight = 100;

		if (mPrinter == null) {
			return false;
		}

		try {
			method = "addTextAlign";
			mPrinter.addTextAlign(Printer.ALIGN_CENTER);

			method = "addImage";
			mPrinter.addImage(logoData, 0, 0,
			logoData.getWidth(),
			logoData.getHeight(),
			Printer.COLOR_1,
			Printer.MODE_MONO,
			Printer.HALFTONE_DITHER,
			Printer.PARAM_DEFAULT,
			Printer.COMPRESS_AUTO);

			method = "addFeedLine";
			mPrinter.addFeedLine(1);
			textData.append("THE STORE 123 (555) 555 – 5555\n");
			textData.append("STORE DIRECTOR – John Smith\n");
			textData.append("\n");
			textData.append("7/01/07 16:58 6153 05 0191 134\n");
			textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
			textData.append("------------------------------\n");
			method = "addText";
			mPrinter.addText(textData.toString());
			textData.delete(0, textData.length());

			textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
			textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
			textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
			textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
			textData.append("474 TRIPOD              8.99 R\n");
			textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
			textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
			textData.append("493 30L BLK FF DRESS   16.99 R\n");
			textData.append("407 LEVITATING DESKTOP  7.99 R\n");
			textData.append("441 **Blue Overprint P  2.99 R\n");
			textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
			textData.append("461 WESTGATE BLACK 25  59.99 R\n");
			textData.append("------------------------------\n");
			method = "addText";
			mPrinter.addText(textData.toString());
			textData.delete(0, textData.length());

			textData.append("SUBTOTAL                160.38\n");
			textData.append("TAX                      14.43\n");
			method = "addText";
			mPrinter.addText(textData.toString());
			textData.delete(0, textData.length());

			method = "addTextSize";
			mPrinter.addTextSize(2, 2);
			method = "addText";
			mPrinter.addText("TOTAL    174.81\n");
			method = "addTextSize";
			mPrinter.addTextSize(1, 1);
			method = "addFeedLine";
			mPrinter.addFeedLine(1);

			textData.append("CASH                    200.00\n");
			textData.append("CHANGE                   25.19\n");
			textData.append("------------------------------\n");
			method = "addText";
			mPrinter.addText(textData.toString());
			textData.delete(0, textData.length());

			textData.append("Purchased item total number\n");
			textData.append("Sign Up and Save !\n");
			textData.append("With Preferred Saving Card\n");
			method = "addText";
			mPrinter.addText(textData.toString());
			textData.delete(0, textData.length());
			method = "addFeedLine";
			mPrinter.addFeedLine(2);

			method = "addBarcode";
			mPrinter.addBarcode("01209457",
			Printer.BARCODE_CODE39,
			Printer.HRI_BELOW,
			Printer.FONT_A,
			barcodeWidth,
			barcodeHeight);

			method = "addCut";
			mPrinter.addCut(Printer.CUT_FEED);
		}
		catch (Exception e) {
			ShowMsg.showException(e, method, cordova.getActivity());
			return false;
		}

		textData = null;

		return true;
	}


	private void finalizeObject() {
		if (mPrinter == null) {
			return;
		}

		mPrinter.clearCommandBuffer();

		mPrinter.setReceiveEventListener(null);

		mPrinter = null;
	}

	private boolean connectPrinter() {
		boolean isBeginTransaction = false;

		if (mPrinter == null) {
			return false;
		}

		try {
			mPrinter.connect("USB:/dev/bus/usb/002/006", Printer.PARAM_DEFAULT);
		}
		catch (Exception e) {
			ShowMsg.showException(e, "connect", cordova.getActivity());
			return false;
		}

		try {
			mPrinter.beginTransaction();
			isBeginTransaction = true;
		}
		catch (Exception e) {
			ShowMsg.showException(e, "beginTransaction", cordova.getActivity());
		}

		if (isBeginTransaction == false) {
			try {
				mPrinter.disconnect();
			}
			catch (Epos2Exception e) {
				// Do nothing
				return false;
			}
		}

		return true;
	}

	private void disconnectPrinter() {
		if (mPrinter == null) {
			return;
		}

		try {
			mPrinter.endTransaction();
		}
		catch (final Exception e) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public synchronized void run() {
					ShowMsg.showException(e, "endTransaction", cordova.getActivity());
				}
			});
		}

		try {
			mPrinter.disconnect();
		}
		catch (final Exception e) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public synchronized void run() {
					ShowMsg.showException(e, "disconnect", cordova.getActivity());
				}
			});
		}

		finalizeObject();
	}


	@Override
	public void onDestroy() {
		Log.i("停止搜索", "停止1");
		super.onDestroy();

		stopDiscovery();
		mFilterOption = null;
	}

	private void stopDiscovery() {
		Log.i("停止搜索", "停止2");
		while (true) {
			try {
				Discovery.stop();
				break;
			} catch (Epos2Exception e) {
				if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
					break;
				}
			}
		}

		JSONArray jsonArray = new JSONArray();
		for (HashMap<String, String> one : mPrinterList) {
			JSONObject jsonObject = new JSONObject();
			Log.i("测试", "mPrinterList: " + one.get("PrinterName") + " ~ " + one.get("Target"));
			try {
				jsonObject.put("PrinterName", one.get("PrinterName"));
				jsonObject.put("Target", one.get("Target"));
			} catch (JSONException e) {
				Log.i("测试", "JSONException: " + e.getMessage());
				onPostExecute();
			}

			jsonArray.put(jsonObject);

		}
		callbackContext.success(jsonArray);
		onPostExecute();
	}

	private ProgressDialog progressDialog;   // class variable

	private void showProgressDialog(final String title, final String message)
	{
		cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				progressDialog = new ProgressDialog(cordova.getActivity());

				progressDialog.setTitle(title); //title

				progressDialog.setMessage(message); // message

				progressDialog.setCancelable(false);

				progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stopDiscovery();
						dialog.dismiss();
					}
				});

				progressDialog.show();
			}
		});
	}

	protected void onPreExecute()
	{
		showProgressDialog("Searching Printers","Please wait...");
	}

	protected void onPostExecute()
	{
		if(progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
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
