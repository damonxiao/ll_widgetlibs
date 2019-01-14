package damon.ll.widgetlibs.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import damon.ll.widgetlibs.R;

/**
 * xiaofang
 * 19-1-8
 **/
public class CircleProgressView extends FrameLayout {
    private static final String TAG = "CircleProgressView";
    private ImageView mProgressView;
    private Context mContext;
    private long mProgressPerTime;// 每转一圈的时间,毫秒数
    private long mProgressTotalTime;// 一共转的时间,毫秒数
    private int mCurrentProgress;
    private int mProgressTotal;
    private ProgressCallback mCallback;
    private boolean mReverse;

    public CircleProgressView(@NonNull Context context) {
        this(context, null);
    }

    public CircleProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        if(attrs != null) {
            initStyleAttrs(context, attrs);
        }
    }

    private void init(Context context) {
        mContext = context;
        mProgressView = new ImageView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mProgressView.setLayoutParams(layoutParams);
        addView(mProgressView);
    }

    private void initStyleAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        mProgressTotal = typedArray.getInt(R.styleable.CircleProgressView_progress_total, 1000);
        mProgressTotalTime = typedArray.getInt(R.styleable.CircleProgressView_progress_total_time, 1000);
        setProgress(mProgressTotalTime, mProgressTotal);
        int progressImageResource = typedArray.getResourceId(R.styleable.CircleProgressView_progress_image_src, R.drawable.circle_progress_view_progress_def);
        setProgressImageResource(progressImageResource);

        int backgroundImageResource = typedArray.getResourceId(R.styleable.CircleProgressView_progress_background, R.drawable.circle_progress_view_bg_def);
        setProgressBackgroundResource(backgroundImageResource);
    }

    public void setProgress(final long progressTotalTime, final int progressTotal) {
        mProgressTotalTime = progressTotalTime;
        if (progressTotal != 0) {
            mProgressPerTime = mProgressTotalTime / progressTotal;
        }
        mProgressTotal = progressTotal;
        mCurrentProgress = 0;
    }

    public void setProgressImageResource(int resourceId) {
        mProgressView.setImageResource(resourceId);
    }

    public void setProgressBackgroundResource(int resourceId) {
        setBackgroundResource(resourceId);
    }

    public void setReverse(boolean reverse) {
        mReverse = reverse;
    }

    /**
     * Must be called after {@link #setProgress(long, int)}
     */
    public void showProgress(final ProgressCallback callback) {
        mCallback = callback;
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(mProgressPerTime);
        rotate.setFillAfter(true);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCurrentProgress += 1;
                int progress = mReverse ? (mProgressTotal - (mCurrentProgress -1)) : mCurrentProgress;
                Log.d(TAG, "showProgress.onAnimationStart mCurrentProgress " + mCurrentProgress + ", mProgressTotal " + mProgressTotal + ",progress " + progress);
                if (mCallback != null) {
                    mCallback.onProgress(progress);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "showProgress.onAnimationEnd mCurrentProgress " + mCurrentProgress + ", mProgressTotal " + mProgressTotal);
                if (mCurrentProgress < mProgressTotal) {
                    mProgressView.startAnimation(rotate);
                }

                if (mCallback != null && mCurrentProgress == mProgressTotal) {
                    mCallback.onFinish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rotate.setInterpolator(new LinearInterpolator());
        mProgressView.startAnimation(rotate);
    }

    public interface ProgressCallback {
        void onProgress(int progress);

        void onFinish();
    }


    /**
     * If use Builder should not declare {@link #CircleProgressView(Context)} in a layout xml file.
     */
    public static class Builder {
        private ProgressViewParams mParams;
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
            mParams = new ProgressViewParams();
        }

        public Builder setProgress(final long progressTotalTime, final int progressTotal) {
            mParams.progressTotalTime = progressTotalTime;
            mParams.progressTotal = progressTotal;
            return this;
        }

        public Builder setProgressImageResource(int resourceId) {
            mParams.progressImageResource = resourceId;
            return this;
        }

        public Builder setProgressBackgroundResource(int resourceId) {
            mParams.progressBackgroundResource = resourceId;
            return this;
        }

        public Builder setReverse(boolean reverse) {
            mParams.reverse = reverse;
            return this;
        }

        public CircleProgressView create() {
            CircleProgressView view = new CircleProgressView(mContext);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mParams.apply(view);
            return view;
        }
    }

    public static class ProgressViewParams {
        long progressTotalTime;// 一共转的时间,毫秒数
        int progressTotal;
        int progressImageResource;
        int progressBackgroundResource;
        boolean reverse;

        void apply(CircleProgressView view) {
            view.setProgress(progressTotalTime, progressTotal);
            view.setProgressBackgroundResource(progressBackgroundResource);
            view.setProgressImageResource(progressImageResource);
            view.setReverse(reverse);
        }
    }
}
