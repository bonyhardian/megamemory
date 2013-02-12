package com.knepe.megamemory;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Balloon extends Sprite {
	
	private Engine mEngine;
	private PhysicsWorld mPhysicsWorld;
	private boolean isPopped;
	private Body body;
	
	public Balloon(float pX, float pY,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, Engine engine) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		mEngine = engine;
		this.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	
	public void addToScene(Scene scene) {
		scene.attachChild(this);
		scene.registerTouchArea(this);
	}
	
	public boolean IsPopped(){
		return isPopped;
	}
	
	public void setPhysicsWorld(PhysicsWorld physicsWorld, Body b){
		mPhysicsWorld = physicsWorld;
		body = b;
	}
	public void Pop(final Balloon b){
		isPopped = true;
		mEngine.runOnUpdateThread(new Runnable(){

            @Override
            public void run() {
                b.detachSelf();
            }});
	}
	
	public boolean IsOutOfBounds(){
		return (this.getY() + this.getHeight() > 800 && this.IsPopped()) || this.getY() + this.getHeight() < 0;
	}
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {

		Pop(this);
		
		return true;
	}
}
