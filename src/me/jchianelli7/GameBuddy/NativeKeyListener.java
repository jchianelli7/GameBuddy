package me.jchianelli7.GameBuddy;

import org.jnativehook.keyboard.NativeKeyEvent;

public class NativeKeyListener implements org.jnativehook.keyboard.NativeKeyListener {

	final Miner miner;

	public NativeKeyListener(Miner miner) {
		this.miner = miner;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		int key = e.getKeyCode();
		if (key == NativeKeyEvent.VC_PAUSE) {
			if (miner.arePressed) {
				System.out.println("Releasing.");
				try {
					miner.bot.mouseRelease(miner.mouseButton);
					miner.releaseKeys();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				System.out.println("Pressing.");
				try {
					miner.bot.mousePress(miner.mouseButton);
					miner.pressKeys();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			miner.arePressed = !miner.arePressed;
		} else {
			if (miner.listening) {
				miner.getKeyList().addKey(e.getRawCode(), NativeKeyEvent.getKeyText(key));
				miner.listening=false;
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
	}

}
