package com.example.backdraw2;

import hello.world.builder.gifeffect.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.drawpic.DrawPic;
import com.example.picgif.PicGif;

public class Splash extends BaseSetupActivity {

	private int father;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		father = getIntent().getIntExtra("father", 0);

	}

	public void draw(View v) {
		startActivity(new Intent(this, com.example.drawpic.DrawPic.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	public void gif(View v) {
		startActivity(new Intent(this, com.example.picgif.PicGif.class));
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showNext() {
		if (father == 0) {
			startActivity(new Intent(this, DrawPic.class));
		} else {
			startActivity(new Intent(this, PicGif.class));
		}
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {

	}

}
