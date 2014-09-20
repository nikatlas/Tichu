package com.kiklopas.titchu;

import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class OnEndMoveToAction extends MoveToAction {
	boolean isFinished = false;
	Runnable onFinish;
	
	OnEndMoveToAction(){
		onFinish = null;
	}
	
	//Override
	protected void update (float percent) {
		super.update(percent);
		//System.out.println("Percent:"+percent);
		if( percent == 1.0 ){
			isFinished = true;
			this.onFinish.run();
		}
	}

	public void dispose() {
	}
	
}
