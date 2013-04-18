package com.knepe.megamemory;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Popup extends Scene {
	private Font mFont;
	private Sprite mBackgroundSprite;
	private PopupButton mPopupButton;
	private VertexBufferObjectManager mVertextBufferObjectManager;
	private float x;
	private float y;
	private Scene mParentScene;
	
	public Popup(float pX, float pY, Scene pParentScene, Font font, Sprite backgroundSprite, PopupButton popupButton, VertexBufferObjectManager pVertexBufferObjectManager){
		super();
		mFont = font;
		mBackgroundSprite = backgroundSprite;
		mPopupButton = popupButton;
		mVertextBufferObjectManager = pVertexBufferObjectManager;
		x = pX;
		y = pY;
		mParentScene = pParentScene;
	}
	
	public void Add(Object... texts){
		this.setBackgroundEnabled(false);
		mBackgroundSprite.setPosition(x, y);
		this.attachChild(mBackgroundSprite);
		AddToScene(texts);
		mParentScene.setChildScene(this, false, true, true);
	}
	
	private void AddToScene(Object... texts){
		int index = 0;
		
		for(Object text : texts){
			AddText(text.toString(), index);
			index++;
		}
		
		mPopupButton.setPosition((mBackgroundSprite.getX() / 2) + (mBackgroundSprite.getWidth() / 2), (mBackgroundSprite.getY() + mBackgroundSprite.getHeight()) - (mPopupButton.getHeight() * 2));
		mPopupButton.AddText("Play again", mFont);
		
		this.registerTouchArea(mPopupButton);
		this.attachChild(mPopupButton);
	}
	
	private void AddText(String text, int index){
		Text txt = new Text(0,0, mFont, text, mVertextBufferObjectManager);
		txt.setPosition((mBackgroundSprite.getX() + 40), mBackgroundSprite.getY() + ((txt.getHeight() * index) + txt.getHeight()));
		this.attachChild(txt);
	}
}

