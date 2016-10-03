package be.betalife.cordova.plugin.epsonposprinter;

import com.ionicframework.posprintertest664842.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HtmlToBitmap {
	private WebView webView;
	private Bitmap bitmap = null;

	public Bitmap HtmlToBitmap(final String html, final Activity activity) {

		if (html == null || html.equals("")) {
			return null;
		}

		Log.e("info", html);

		activity.runOnUiThread(new Runnable() {
			public void run() {
				activity.setContentView(R.layout.cordova_activity);
				webView = new WebView(activity);

				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
				WebSettings settings = webView.getSettings();
				settings.setBuiltInZoomControls(true);
				settings.setUseWideViewPort(false);
				settings.setJavaScriptEnabled(false);
				settings.setSupportMultipleWindows(false);

				settings.setLoadsImagesAutomatically(true);
				settings.setDomStorageEnabled(true);
				settings.setLoadWithOverviewMode(true);
				webView.loadData(html, "text/html", "UTF8");

				int w = webView.getWidth();
				int h = webView.getHeight();
				Log.d(String.valueOf(webView.getWidth()), "Width = " + w);
				Log.d(String.valueOf(webView.getHeight()), "Height = " + h);
				bitmap = convert(webView);
			}
		});

		return bitmap;
	}

	public Bitmap convert(WebView webView) {

		int w = webView.getWidth();
		int h = webView.getHeight();
		Log.d(String.valueOf(webView.getWidth()), "Width = " + w);
		Log.d(String.valueOf(webView.getHeight()), "Height = " + h);
		Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		webView.draw(canvas);

		return bitmap;
	}

}
