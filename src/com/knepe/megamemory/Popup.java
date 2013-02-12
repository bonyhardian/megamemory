package com.knepe.megamemory;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Popup extends Sprite {
	private Font mFont;
	
	public Popup(float pX, float pY,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, Font font) {
		super(pX, pY, pTextureRegion, vertexBufferObjectManager);
		mFont = font;
	}
	
	public void AddText(String text){
		Text txt = new Text(0,0, mFont, text, this.getVertexBufferObjectManager());
		txt.setPosition(((this.getWidth() / 2) - txt.getWidth() / 2), (this.getHeight() / 2) - 20);
		this.attachChild(txt);
	}
	
	public void AddButtons(){
		
	}

}
