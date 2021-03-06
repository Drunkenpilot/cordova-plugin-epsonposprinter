package be.betalife.cordova.plugin.epsonposprinter;

import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
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

import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.Epos2CallbackCode;

import android.util.Log;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import be.betalife.betarestoapp.R;


public class EpsonPrinter extends CordovaPlugin implements ReceiveListener {

	private ArrayList<HashMap<String, String>> mPrinterList = null;
	private FilterOption mFilterOption = null;
	private CallbackContext callbackContext = null;
	private Printer  mPrinter = null;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		// your init code here
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;

		if (action.equals("search")) {
			final int millSeconds = args.optInt(0, 10 * 1000);
			Log.d(String.valueOf(args.optInt(0)),"time = "+millSeconds);
			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					mPrinterList = new ArrayList<HashMap<String, String>>();
					mFilterOption = new FilterOption();
					mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
					mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
					mFilterOption.setPortType(Discovery.PORTTYPE_ALL);
					try {
						onPreExecute();
						Discovery.start(cordova.getActivity(), mFilterOption, mDiscoveryListener);
						Thread.sleep(millSeconds);
					} catch (Epos2Exception e) {
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
			final JSONArray printContent = args.optJSONArray(0);
			final int printTemplate = args.optInt(1);
			final int printMode = args.optInt(2);
			final int printerSeries = args.optInt(3);
			final int lang = args.optInt(4);
			final String printTarget = args.optString(5);

			Log.d("printTemplate","printTemplate = "+printTemplate);

			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					runPrintReceiptSequence(printContent,printTemplate,printMode,printerSeries,lang,printTarget);
				}
			});
			return true;
		}
		return false;

	}

	private boolean runPrintReceiptSequence(final JSONArray printContent, final int printTemplate, final int printMode, final int printerSeries, final int lang, final String printTarget) {
		if (!initializeObject(printerSeries, lang)) {
			return false;
		}

		if (!createReceiptData(printContent,printTemplate,printMode)) {
			finalizeObject();
			return false;
		}

		if (!printData(printTarget)) {
			finalizeObject();
			return false;
		}

		return true;
	}


	private boolean initializeObject(final int printerSeries, final int lang) {
		try {
			mPrinter = new Printer(printerSeries,lang,cordova.getActivity());
		}
		catch (Exception e) {
			EpsonPrinter.this.callbackContext.error("e:" + ((Epos2Exception) e).getErrorStatus());
			ShowMsg.showException(e, "Printer", cordova.getActivity());
			return false;
		}

		mPrinter.setReceiveEventListener(this);

		return true;
	}


	private boolean printData(final String printTarget) {
		if (mPrinter == null) {
			return false;
		}

		if (!connectPrinter(printTarget)) {
			return false;
		}

		PrinterStatusInfo status = mPrinter.getStatus();

		// dispPrinterWarnings(status);

		if (!isPrintable(status)) {
			EpsonPrinter.this.callbackContext.error("e:" + makeErrorMessage(status));
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



	private boolean createReceiptData(final JSONArray printContent,final int printTemplate, final int printMode) {
		if (mPrinter == null) {
			return false;
		}
		String method = "";
		// 		Line mode
		StringBuilder textData = new StringBuilder();

		try {
			//				printTemplate = 1 Receipt with logo
			//				printTemplate = 2 Receipt for kitchen
			//				printTemplate = 3 Online order
			if(printTemplate == 1){
				// Receipt with logo
				method = "addTextAlign";
				mPrinter.addTextAlign(Printer.ALIGN_CENTER);

				Bitmap logoData = BitmapFactory.decodeResource(cordova.getActivity().getResources(), R.drawable.store);

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
			}

			//			Generate main content
			try{
				method = "addTextAlign";
				mPrinter.addTextAlign(Printer.ALIGN_CENTER);
				ReceiptBuilderExt receiptBuilder = new ReceiptBuilderExt(cordova.getActivity());
				Bitmap testImg = receiptBuilder.build(printContent);

				method = "addImage";
				mPrinter.addImage(testImg, 0, 0,
				testImg.getWidth(),
				testImg.getHeight(),
				Printer.COLOR_1,
				Printer.MODE_MONO,
				Printer.HALFTONE_DITHER,
				Printer.PARAM_DEFAULT,
				Printer.COMPRESS_AUTO);

				method = "addFeedLine";
				mPrinter.addFeedLine(1);

			} catch(JSONException e){

			}
			if(printTemplate == 1) {

				method = "addTextAlign";
				mPrinter.addTextAlign(Printer.ALIGN_CENTER);
				//			QR code
				method = "addSymbol";
				mPrinter.addSymbol(cordova.getActivity().getResources().getString(R.string.url), Printer.SYMBOL_QRCODE_MODEL_2, Printer.LEVEL_L, 3, 3, 3);

				method = "addText";
				mPrinter.addText(cordova.getActivity().getResources().getString(R.string.website));

				method = "addFeedLine";
				mPrinter.addFeedLine(1);
				//			code bar
				//		  final int barcodeWidth = 2;
				//		  final int barcodeHeight = 100;
				//			method = "addBarcode";
				//			mPrinter.addBarcode("01209457",
				//			Printer.BARCODE_CODE39,
				//			Printer.HRI_BELOW,
				//			Printer.FONT_A,
				//			barcodeWidth,
				//			barcodeHeight);
			}
			method = "addCut";
			mPrinter.addCut(Printer.CUT_FEED);

			// printMode = 1 normal mode;
			// printMode = 2 silent mode;
			if(printMode == 1){
				method = "addPulse";
				mPrinter.addPulse(Printer.DRAWER_2PIN,Printer.PULSE_500);
				method = "addPulse";
				mPrinter.addPulse(Printer.DRAWER_2PIN,Printer.PULSE_500);
			}

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

	private boolean connectPrinter(final String printTarget) {
		boolean isBeginTransaction = false;

		if (mPrinter == null) {
			return false;
		}

		try {
			mPrinter.connect(printTarget, Printer.PARAM_DEFAULT);
		}
		catch (Exception e) {
			EpsonPrinter.this.callbackContext.error("e:" + ((Epos2Exception) e).getErrorStatus());
			ShowMsg.showException(e, "connect", cordova.getActivity());
			return false;
		}

		try {
			mPrinter.beginTransaction();
			isBeginTransaction = true;
		}
		catch (Exception e) {
			EpsonPrinter.this.callbackContext.error("e:" + ((Epos2Exception) e).getErrorStatus());
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
			// Log.i("停止打印","停止打印1");
			mPrinter.disconnect();
			// Log.i("停止打印","停止打印2");
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

	private boolean isPrintable(PrinterStatusInfo status) {
		if (status == null) {
			return false;
		}

		if (status.getConnection() == Printer.FALSE) {
			return false;
		}
		else if (status.getOnline() == Printer.FALSE) {
			return false;
		}
		else {
			;//print available
		}

		return true;
	}

	private String makeErrorMessage(PrinterStatusInfo status) {
		String msg = "";
		if (status.getOnline() == Printer.FALSE) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_offline);
		}
		if (status.getConnection() == Printer.FALSE) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_no_response);
		}
		if (status.getCoverOpen() == Printer.TRUE) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_cover_open);
		}
		if (status.getPaper() == Printer.PAPER_EMPTY) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_receipt_end);
		}
		if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_paper_feed);
		}
		if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_autocutter);
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_need_recover);
		}
		if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_unrecover);
		}
		if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
			if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_overheat);
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_head);
			}
			if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_overheat);
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_motor);
			}
			if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_overheat);
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_battery);
			}
			if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
				msg += cordova.getActivity().getString(R.string.handlingmsg_err_wrong_paper);
			}
		}
		if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
			msg += cordova.getActivity().getString(R.string.handlingmsg_err_battery_real_end);
		}

		return msg;
	}

	// private void dispPrinterWarnings(PrinterStatusInfo status) {
	// 	EditText edtWarnings = (EditText)findViewById(R.id.edtWarnings);
	// 	String warningsMsg = "";
	//
	// 	if (status == null) {
	// 		return;
	// 	}
	//
	// 	if (status.getPaper() == Printer.PAPER_NEAR_END) {
	// 		warningsMsg += cordova.getActivity().getString(R.string.handlingmsg_warn_receipt_near_end);
	// 	}
	//
	// 	if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
	// 		warningsMsg += cordova.getActivity().getString(R.string.handlingmsg_warn_battery_near_end);
	// 	}
	//
	// 	edtWarnings.setText(warningsMsg);
	// }

	// @Override
	public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
		cordova.getActivity().runOnUiThread(new Runnable() {
			@Override
			public synchronized void run() {
				EpsonPrinter.this.callbackContext.success();
				ShowMsg.showResult(code, makeErrorMessage(status), cordova.getActivity());

				// dispPrinterWarnings(status);

				new Thread(new Runnable() {
					@Override
					public void run() {
						disconnectPrinter();
					}
				}).start();
			}
		});
	}

	@Override
	public void onDestroy() {
		Log.i("停止搜索", "停止1");
		super.onDestroy();

		stopDiscovery();
		mFilterOption = null;
	}

	private void stopDiscovery() {
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

			try {
				jsonObject.put("PrinterName", one.get("PrinterName"));
				jsonObject.put("Target", one.get("Target"));
			} catch (JSONException e) {
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
