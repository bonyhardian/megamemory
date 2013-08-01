package com.knepe.megamemory.fonts;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.ITexture;
import org.andengine.util.color.Color;

import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;

public class GradientFont extends Font {
    //region Constructors
    public GradientFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                        final Color pColorFrom, final Color pColorTo) {
        this(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, pColorFrom.getARGBPackedInt(), pColorTo.getARGBPackedInt());
    }
    public GradientFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                        final int pColorFromARGBPackedInt, final int pColorToARGBPackedInt) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, Color.WHITE_ARGB_PACKED_INT);
        LinearGradient gradient = new LinearGradient(0, 0, 0, mPaint.getTextSize() / 2, pColorFromARGBPackedInt, pColorToARGBPackedInt, TileMode.CLAMP);
        mPaint.setDither(true);
        mPaint.setShader(gradient);
    }

    public GradientFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                        final Color[] pColors) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, Color.WHITE_ARGB_PACKED_INT);
        int[] colorsFromARGBPackedInts = new int[pColors.length];
        for (int i = 0; i < pColors.length; i++) {
            colorsFromARGBPackedInts[i] = pColors[i].getARGBPackedInt();
        }
        LinearGradient gradient = new LinearGradient(0, 0, 0, mPaint.getTextSize() - (mPaint.getTextSize() / colorsFromARGBPackedInts.length), colorsFromARGBPackedInts, null, TileMode.CLAMP);
        mPaint.setDither(true);
        mPaint.setShader(gradient);
    }

    public GradientFont(final FontManager pFontManager, final ITexture pTexture, final Typeface pTypeface, final float pSize, final boolean pAntiAlias,
                        final int[] pColorsFromARGBPackedInt) {
        super(pFontManager, pTexture, pTypeface, pSize, pAntiAlias, Color.WHITE_ARGB_PACKED_INT);
        LinearGradient gradient = new LinearGradient(0, 0, 0, mPaint.getTextSize() - (mPaint.getTextSize() / pColorsFromARGBPackedInt.length), pColorsFromARGBPackedInt, null, TileMode.CLAMP);
        mPaint.setDither(true);
        mPaint.setShader(gradient);
    }
    //endregion Constructors
}
