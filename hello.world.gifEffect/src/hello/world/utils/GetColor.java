package hello.world.utils;

import android.graphics.Color;

public class GetColor {
	private static int[] colors = new int[] { Color.parseColor("#FF0000"),
			Color.parseColor("#8B0000"), Color.parseColor("#DC143C"), Color.parseColor("#00FF00"),
			Color.parseColor("#008000"), Color.parseColor("#3CB771"), Color.parseColor("#0000FF"),
			Color.parseColor("#0000008B"), Color.parseColor("#6495ED"), Color.parseColor("#FFFF00"),
			Color.parseColor("#FFD700"), Color.parseColor("#808000"), Color.parseColor("#00FFFF"),
			Color.parseColor("#00CED1"), Color.parseColor("#008B8B"), Color.parseColor("#FF00FF"),
			Color.parseColor("#8B008B"), Color.parseColor("#9400D3"), Color.parseColor("#FFA500"),
			Color.parseColor("#DAA520"), Color.parseColor("#FFDEAD"), Color.parseColor("#7FFF00"),
			Color.parseColor("#556B2F"), Color.parseColor("#6B8E23"), Color.parseColor("#00FF7F"),
			Color.parseColor("#00FA9A"), Color.parseColor("#90EE90"), Color.parseColor("#00BFFF"),
			Color.parseColor("#1E90FF"), Color.parseColor("#87CEFA"), Color.parseColor("#9370DB"),
			Color.parseColor("#4B0082"), Color.parseColor("#9932CC"), Color.parseColor("#FF1493"),
			Color.parseColor("#FF69B4"), Color.parseColor("#DDA0DD")
	};
	public static boolean getColor(int color){
	int mColor = Color.parseColor(argbToRgb(color));
	
		for(int i = 0;i<colors.length;i++){
			if(colors[i] == mColor ){
				return true;
			}
		}
		return false;
	}
	
	public static String argbToRgb(int color) {
	    String hexColor = Integer.toHexString((color & 0xFFFFFF));
	    int length = hexColor.length();
	    if(length < 6) {
	        for(int i=0;i< 6 - length; i++) {
	            hexColor = "0" + hexColor;
	        }
	    }
	    return "#" + hexColor;
	}
	
}
