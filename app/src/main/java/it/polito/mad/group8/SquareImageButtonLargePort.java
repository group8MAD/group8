package it.polito.mad.group8;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

/**
 * Created by Cozmin on 10/04/18.
 */

public class SquareImageButtonLargePort extends AppCompatImageButton {



    public SquareImageButtonLargePort(Context context) {
        super(context);
    }

    public SquareImageButtonLargePort(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageButtonLargePort(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width*5/10, width*5/10);
    }

}

