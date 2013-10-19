package net.nkzn.android.sample.maps.effective_android;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

/**
 * 場所を表す
 * 
 * @author nkzn
 */
public class Field {

	/**
	 * 場所の名前
	 */
	private String name;

	/**
	 * 場所への覚え書き
	 */
	private String memo;

	/**
	 * 区画の頂点の座標
	 */
	private LatLng[] vertexes;

	/**
	 * ピンや区画の色
	 */
	private int[] colorRgb;

	public Field(String name, LatLng[] vertexes) {
		this.name = name;
		this.vertexes = vertexes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo
	 *            the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the vertexes
	 */
	public LatLng[] getVertexes() {
		return vertexes;
	}

	/**
	 * @return the colorHue
	 */
	public float getColorHue() {
		
		float[] hsv = new float[3];
		
		if(colorRgb == null) {
			Color.RGBToHSV(255, 0, 0, hsv); // default:red
		} else {
			Color.RGBToHSV(colorRgb[0], colorRgb[1], colorRgb[2], hsv);
		}
		
		return hsv[0];
	}

	/**
	 * RGB values.
	 * 
	 * @return {red, green, blue}
	 */
	public int[] getColorRgb() {
		return colorRgb;
	}
	
	/**
	 * 
	 * @param red red component value [0..255]
	 * @param green green component value [0..255]
	 * @param blue blue component value [0..255]
	 */
	public void setColorRgb(int red, int green, int blue) {
		this.colorRgb = new int[]{red, green, blue};
	}

}
