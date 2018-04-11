package it.polito.mad.group8;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

/**
 * Created by Cozmin on 10/04/18.
 */

public class SquareImageButtonLargeLand extends AppCompatImageButton {



    public SquareImageButtonLargeLand(Context context) {
        super(context);
    }

    public SquareImageButtonLargeLand(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageButtonLargeLand(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension(height*8/10, height*8/10);
    }

}

