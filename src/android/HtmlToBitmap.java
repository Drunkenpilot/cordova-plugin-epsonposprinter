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
import android.webkit.WebViewClient;
import android.graphics.Picture;
import android.graphics.Paint;
import android.graphics.Color;


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
				webView = new WebView(activity);
				// webView = this.findViewById(R.id.cordova_activity);
				activity.setContentView(webView);
				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
				webView.setWebViewClient(new WebViewClient() {

					@Override
					public void onPageFinished(WebView view, String url) {
						int w = webView.getWidth();
						int h = webView.getHeight();
						Log.d("onPageFinished", "Width = " + w);
						Log.d("onPageFinished", "Height = " + h);

					}
				});


				WebSettings settings = webView.getSettings();
				settings.setBuiltInZoomControls(true);
				settings.setUseWideViewPort(false);
				settings.setJavaScriptEnabled(true);
				settings.setSupportMultipleWindows(false);

				settings.setLoadsImagesAutomatically(true);
				settings.setLightTouchEnabled(true);
				settings.setDomStorageEnabled(true);
				settings.setLoadWithOverviewMode(true);

				webView.loadData(html, "text/html;charset=utf-8", "UTF8");
				//webView.loadDataWithBaseURL(null,html,"text/html;charset=utf-8", "UTF8",null);

				int w = webView.getWidth();
				int h = webView.getHeight();
				Log.d(String.valueOf(webView.getWidth()), "Width = " + w);
				Log.d(String.valueOf(webView.getHeight()), "Height = " + h);
				bitmap = convert(webView);
			}
		});

		return bitmap;
	}

	public Bitmap convert(final WebView webView) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth((float) 5.0);

		// int w = picture.getWidth();
		// int h = picture.getHeight();
		// Log.d(String.valueOf(picture.getWidth()), "Width = " + w);
		// Log.d(String.valueOf(picture.getHeight()), "Height = " + h);
		float[] pts={0,10,99,10};
		Bitmap bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.BLACK);
	//	canvas.drawLines(pts,4,12, paint);
		return bitmap;

	}

}
