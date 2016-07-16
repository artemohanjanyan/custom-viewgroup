package ru.yandex.yamblz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.*;
import static android.view.ViewGroup.LayoutParams.*;

public class MyLayout extends ViewGroup {

    public MyLayout(Context context) {
        super(context);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int widthSpecMode = getMode(widthMeasureSpec);
        int widthSpecSize = getSize(widthMeasureSpec);

        int maxChildHeight = 0;
        View childWithMatchParent = null;

        int sumChildWidth = 0;
        int maxAllowedWidth = 0;
        switch (widthSpecMode) {
            case AT_MOST:
            case EXACTLY:
                maxAllowedWidth = widthSpecSize;
                break;
            case UNSPECIFIED:
                maxAllowedWidth = -1;
                break;
        }

        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams childParams = child.getLayoutParams();
                if (childParams.width == MATCH_PARENT) {
                    if (widthSpecMode == UNSPECIFIED) {
                        childParams.width = WRAP_CONTENT;
                        child.setLayoutParams(childParams);
                    }
                }

                if (childParams.width == MATCH_PARENT) {
                    childWithMatchParent = child;
                } else {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    sumChildWidth += child.getMeasuredWidth();
                    maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
                }
            }
        }

        if (childWithMatchParent != null) {
            if (maxAllowedWidth != -1 && sumChildWidth > maxAllowedWidth) {
                childWithMatchParent
                        .measure(makeMeasureSpec(0, EXACTLY), makeMeasureSpec(0, EXACTLY));
            } else {
                childWithMatchParent.measure(
                        makeMeasureSpec(maxAllowedWidth - sumChildWidth, EXACTLY),
                        getChildMeasureSpec(heightMeasureSpec, 0,
                                childWithMatchParent.getLayoutParams().height));

                maxChildHeight = Math.max(maxChildHeight, childWithMatchParent.getMeasuredHeight());
                sumChildWidth += childWithMatchParent.getMeasuredWidth();
            }
        }

        setMeasuredDimension(
                resolveSize(sumChildWidth, widthMeasureSpec),
                resolveSize(maxChildHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();

        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int nextLeft = left + child.getMeasuredWidth();

                child.layout(left, top, nextLeft,
                        Math.min(bottom, top + child.getMeasuredHeight()));
                left = nextLeft;
            }
        }
    }
}
