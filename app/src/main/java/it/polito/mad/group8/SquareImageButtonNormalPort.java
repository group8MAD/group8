package it.polito.mad.group8;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

/**
 * Created by Cozmin on 10/04/18.
 */

public class SquareImageButtonNormalPort extends AppCompatImageButton {



    public SquareImageButtonNormalPort(Context context) {
        super(context);
    }

    public SquareImageButtonNormalPort(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageButtonNormalPort(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width*7/10, width*7/10);
    }

}
