package hello.world.mylayout;

import hello.world.decoder.GifDecoder;
import hello.world.domain.Mapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
/**
 * @since 2015 06 24
 * @author funbee {@link https://github.com/hellofunbee/gifEffect}
 */
public class GifImageView extends ImageView {
	private boolean notInitial = true;
	private GifDecoder gd;
	private int delay;

	public GifImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		app = (Mapp) context.getApplicationContext();
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		notInitial = false;
		super.setImageBitmap(bitmap);
	}

	public void setAddr(String path) {
		delay = app.sp.getInt("savedelay", 100);
		i = 0;
		gd = new GifDecoder(path, app);
		handler.sendEmptyMessageDelayed(0, 100);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (notInitial) {
			return;
		}
		super.onDraw(canvas);

		handler.sendEmptyMessageDelayed(0, delay);
	}

	

	int i;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setImageBitmap(gd.getFrameImage(i));
			i++;
		}

	};

	private Mapp app;

}