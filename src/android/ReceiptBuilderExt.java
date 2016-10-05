package be.betalife.cordova.plugin.epsonposprinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ionicframework.posprintertest664842.R;
import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class ReceiptBuilderExt {
	private ReceiptBuilder builder;

	// attribute: Typeface(string), Align(LEFT,CENTER,RIGHT;),
	// TextSize(float)
	private Activity activity;

	/**
	 *
	 * <pre>
	 *  element:
	 *  	Text(string,[boolean]),Image(Bitmap),BlankSpace(int),Paragraph,Line([int]);
	 *  	Typeface(string), Align(LEFT,CENTER,RIGHT), TextSize(float)

	 * {
	 * 	name: element, required
	 * 	value: string / int,
	 * 	newLine: boolean, (Text)
	 * }
	 *
	 * </pre>
	 */

	public ReceiptBuilderExt(Activity activity) {
		int width = activity.getResources().getInteger(R.string.width);
		int marginBottom = activity.getResources().getInteger(R.string.marginBottom);
		int marginLeft = activity.getResources().getInteger(R.string.marginLeft);
		int marginRight = activity.getResources().getInteger(R.string.marginRight);
		int marginTop = activity.getResources().getInteger(R.string.marginTop);
		this.activity = activity;
		builder = new ReceiptBuilder(width);
		builder.setColor(Color.BLACK);
		builder.setMarginBottom(marginBottom).setMarginLeft(marginLeft).setMarginRight(marginRight).setMarginTop(marginTop);
	}

	public Bitmap build(JSONArray html) throws JSONException {
		if (html == null || html.length() == 0) {
			return null;
		}

		for (int i = 0; i < html.length(); i++) {
			JSONArray one = html.getJSONArray(i);
			line(one);
		}

		return builder.build();
	}

	private void line(JSONArray oneLine) throws JSONException {
		for (int i = 0; i < oneLine.length(); i++) {
			JSONObject elem = oneLine.getJSONObject(i);
			String name = elem.getString("name");
			if (name == null || name.length() == 0) {
				continue;
			}
			if (name.equals("Text")) {
				buildText(elem);
			} else if (name.equals("Image")) {
				buildImage(elem);
			} else if (name.equals("BlankSpace")) {
				buildBlankSpace(elem);
			} else if (name.equals("Paragraph")) {
				buildParagraph(elem);
			} else if (name.equals("Line")) {
				buildLine(elem);
			} else if (name.equals("Typeface")) {
				buildTypeface(elem);
			} else if (name.equals("Align")) {
				buildAlign(elem);
			} else if (name.equals("TextSize")) {
				buildTextSize(elem);
			} else {
				continue;
			}
		}

	}

	private void buildTextSize(JSONObject elem) {
		Double value = elem.optDouble("value");
		builder.setTextSize(value.floatValue());
	}

	private void buildAlign(JSONObject elem) {
		String value = elem.optString("value");
		builder.setAlign(Paint.Align.valueOf(value));
	}

	private void buildTypeface(JSONObject elem) {
		String value = elem.optString("value");
		builder.setTypeface(activity, value);
	}

	private void buildLine(JSONObject elem) {
		if (elem.isNull("value")) {
			builder.addLine();
		} else {
			int value = elem.optInt("value");
			builder.addLine(value);
		}
	}

	private void buildParagraph(JSONObject elem) {
		builder.addParagraph();
	}

	private void buildBlankSpace(JSONObject elem) {
		int value = elem.optInt("value");
		builder.addBlankSpace(value);
	}

	private void buildImage(JSONObject elem) {
//		Bitmap barcode = BitmapFactory.decodeResource(activity.getResources(), R.drawable.barcode);
//		builder.addImage(bitmap);
	}

	private void buildText(JSONObject elem) {
		String value=elem.optString("value");
		if(elem.isNull("newLine")){
			builder.addText(value);
		} else {
			boolean newLine = elem.optBoolean("newLine");
			builder.addText(value, newLine);
		}

	}
}