package hello.world.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;

public class LPHelper_Large {

	public static Bitmap loadBitmap(String imgpath, int sw, int sh) {

		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgpath, opts);
		int ow = opts.outWidth;
		int oh = opts.outHeight;
		float scalex = Math.max(ow, oh) / (float) (800.0);
		float scaley = Math.min(ow, oh) / (float) (600.0);
		float max = Math.max(scalex, scaley);
		opts.inSampleSize = (int) max;
		opts.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(imgpath, opts);
//		int width = bm.getWidth();
//		int height = bm.getHeight();
//		 scalex = Math.max(width, height) / (float) (800.0);
//		 scaley = Math.min(width, height) / (float) (300.0);
//		 max = Math.max(scalex, scaley);
//		 
//		bm = BS.small(bm, 1/max);
//		 
		
		return bm;
	}

	public static Bitmap loadBitmap(String imgpath, boolean adjustOritation,
			int sw, int sh) {
		Bitmap bm = loadBitmap(imgpath, sw, sh);
		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imgpath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			// 读取图片中相机方向信息
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			// 计算旋转角度
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;

				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
			}
		}
		if (digree != 0) {
			// 旋转图片

			Matrix m = new Matrix();
			m.postRotate(digree);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
					m, true);
		}
		int ow = bm.getWidth();
		int oh = bm.getHeight();
		float scalex = ow/ (float) (sw);
		float scaley = oh / (float) (sh);
		float max = Math.max(scalex, scaley);
		if(max > 1){
			Matrix m = new Matrix();
			m.postScale(1/max, 1/max);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
					m, true);
			
		}
		return bm;
	}

}
