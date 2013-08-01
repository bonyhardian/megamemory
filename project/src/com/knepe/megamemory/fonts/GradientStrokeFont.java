package com.knepe.megamemory.fonts;

import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.ITexture;
import org.andengine.util.color.Color;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.FloatMath;

public class GradientStrokeFont extends GradientFont {
    //region Fields
    private final Paint mStrokePaint;
    private final float mStrokeWidth;
    //endregion Fields
    //region Constructors
    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final Color pColorFrom, final Color pColorTo, final float pStrokeWidth, final int pStrokeColorARGBPackedInt) {
        this(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorFrom.getARGBPackedInt(), pColorTo.getARGBPackedInt(), pStrokeWidth, pStrokeColorARGBPackedInt);
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final Color pColorFrom, final Color pColorTo, final float pStrokeWidth, final Color pStrokeColor) {
        this(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorFrom.getARGBPackedInt(), pColorTo.getARGBPackedInt(), pStrokeWidth, pStrokeColor.getARGBPackedInt());
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final int pColorFromARGBPackedInt, final int pColorToARGBPackedInt, final float pStrokeWidth, final int pStrokeColorARGBPackedInt) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorFromARGBPackedInt, pColorToARGBPackedInt);
        this.mStrokeWidth = pStrokeWidth;

        this.mStrokePaint = new Paint();
        this.mStrokePaint.setTypeface(pTypeface);
        this.mStrokePaint.setStyle(Style.STROKE);
        this.mStrokePaint.setStrokeWidth(pStrokeWidth);
        this.mStrokePaint.setColor(pStrokeColorARGBPackedInt);
        this.mStrokePaint.setTextSize(pSize);
        this.mStrokePaint.setAntiAlias(pAntiAlias);
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final Color[] pColors, final float pStrokeWidth, final int pStrokeColorARGBPackedInt) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColors);
        this.mStrokeWidth = pStrokeWidth;

        this.mStrokePaint = new Paint();
        this.mStrokePaint.setTypeface(pTypeface);
        this.mStrokePaint.setStyle(Style.STROKE);
        this.mStrokePaint.setStrokeWidth(pStrokeWidth);
        this.mStrokePaint.setColor(pStrokeColorARGBPackedInt);
        this.mStrokePaint.setTextSize(pSize);
        this.mStrokePaint.setAntiAlias(pAntiAlias);
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final Color[] pColors, final float pStrokeWidth, final Color pStrokeColor) {
        this(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColors, pStrokeWidth, pStrokeColor.getARGBPackedInt());
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final int[] pColorsFromARGBPackedInt, final float pStrokeWidth, final int pStrokeColorARGBPackedInt) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorsFromARGBPackedInt);
        this.mStrokeWidth = pStrokeWidth;

        this.mStrokePaint = new Paint();
        this.mStrokePaint.setTypeface(pTypeface);
        this.mStrokePaint.setStyle(Style.STROKE);
        this.mStrokePaint.setStrokeWidth(pStrokeWidth);
        this.mStrokePaint.setColor(pStrokeColorARGBPackedInt);
        this.mStrokePaint.setTextSize(pSize);
        this.mStrokePaint.setAntiAlias(pAntiAlias);
    }

    public GradientStrokeFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                              final int[] pColorsFromARGBPackedInt, final float pStrokeWidth, final Color pStrokeColor) {
        this(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorsFromARGBPackedInt, pStrokeWidth, pStrokeColor.getARGBPackedInt());
    }
    //endregion Constructors
    //region Methods
    @Override
    protected void updateTextBounds(final String pCharacterAsString) {
        this.mStrokePaint.getTextBounds(pCharacterAsString, 0, 1, this.mTextBounds);
        final int inset = -(int)FloatMath.floor(this.mStrokeWidth * 0.5f);
        this.mTextBounds.inset(inset, inset);
    }

    @Override
    protected void drawLetter(final String pCharacterAsString, final float pLeft, final float pTop) {
        super.drawLetter(pCharacterAsString, pLeft, pTop);
        this.mCanvas.drawText(pCharacterAsString, pLeft + LETTER_TEXTURE_PADDING, pTop + LETTER_TEXTURE_PADDING, this.mStrokePaint);
    }
    //endregion Methods
}
