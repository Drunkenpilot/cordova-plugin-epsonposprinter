package be.betalife.cordova.plugin.epsonposprinter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.content.Context;


public class HtmlToBitmap{
	private WebView webView;
  private final Bitmap bitmap = null;

	public Bitmap HtmlToBitmap( final String html, final Activity activity) {

		if (html == null || html.equals("")) {
			return null;
		}

		Log.e("info", html);

		activity.runOnUiThread(new Runnable() {
			public void run(){
				webView = new WebView(activity);

				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webView.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_PARENT, ViewGroup.LayoutParams.WRAP_PARENT));
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
				Log.d(String.valueOf(webView.getWidth()),"Width = "+w);
				Log.d(String.valueOf(webView.getHeight()),"Height = "+h);
				bitmap = convert(webView);
			}
		});

		return bitmap;
	}


	public Bitmap convert(WebView webView) {
		if (bitmap != null) {
		    bitmap.recycle();
		    bitmap = null;
		}
		int w = webView.getWidth();
		int h = webView.getHeight();
		Log.d(String.valueOf(webView.getWidth()),"Width = "+w);
		Log.d(String.valueOf(webView.getHeight()),"Height = "+h);
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		webView.draw(canvas);

		return bitmap;
	}

}
