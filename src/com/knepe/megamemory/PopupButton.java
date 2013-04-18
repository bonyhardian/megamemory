package com.knepe.megamemory;

import java.util.concurrent.Callable;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class PopupButton extends AnimatedSprite{
	public PopupButton(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, String buttonText, Font font) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);
		
		AddText(buttonText, font);
	}
	
	public void AddText(String text, Font mFont){
		Text txt = new Text(0,0, mFont, text, this.getVertexBufferObjectManager());
		txt.setPosition(((this.getWidth() / 2) - txt.getWidth() / 2), (this.getHeight() / 2) - 20);
		this.attachChild(txt);
	}
}
