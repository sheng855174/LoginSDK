package tw.playground.sheng.FloatingView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import tw.playground.sheng.R;

import static android.content.Context.WINDOW_SERVICE;
/*
* 用法，在想要的Activity加入
    private FloatingView floatingView;
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FloatingViewConfig config = new FloatingViewConfig.Builder().build();
        floatingView = new FloatingView(this, config);
        floatingView.showOverlayActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (floatingView != null) {
            floatingView.hide();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (floatingView != null) {
            floatingView.showOverlayActivity();
        }
    }
* */
public class FloatingView {
    private String TAG = "sheng050";
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParamsWindowManager;
    private ViewGroup.MarginLayoutParams mParamsViewGroup;
    private boolean isShowing = false;
    private boolean isRight = false;
    private TYPE type = TYPE.OVERLAY_SYSTEM;

    private FloatingViewConfig config;
    private int width, height;
    private int screen_widht,screen_height;
    private LinearLayout mFloatLayout;
    private LinearLayout listview_right;
    private LinearLayout listview_left;
    private ImageView floating_menu;
    private int x,y;

    private enum TYPE{
        OVERLAY_SYSTEM, OVERLAY_ACTIVITY, OVERLAY_VIEWGROUP
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                //再次點擊，全部隱藏
                if(listview_right.getVisibility()==View.VISIBLE || listview_left.getVisibility()==View.VISIBLE){
                    listview_left.setVisibility(View.GONE);
                    listview_right.setVisibility(View.GONE);
                    isRight = false;
                }
                else {
                    //展開右邊
                    if (x < screen_widht / 2) {
                        listview_right.setVisibility(View.VISIBLE);
                        listview_left.setVisibility(View.GONE);
                    }
                    //展開左邊
                    else {
                        floating_menu.setVisibility(View.GONE);
                        listview_left.setVisibility(View.VISIBLE);
                        listview_right.setVisibility(View.GONE);
                        floating_menu.setVisibility(View.VISIBLE);
                        isRight = true;
                    }
                }
            } catch (Exception e) {}
        }
    };


    public FloatingView(Context context, FloatingViewConfig config) {
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        this.config = config;
        if (config.displayWidth == Integer.MAX_VALUE) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            config.displayWidth = metrics.widthPixels;
        }
        if (config.displayHeight == Integer.MAX_VALUE) {
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            config.displayHeight = (int) (metrics.heightPixels - 25 * metrics.density);
        }
        config.paddingLeft = dp2px(config.paddingLeft);
        config.paddingTop = dp2px(config.paddingTop);
        config.paddingRight = dp2px(config.paddingRight);
        config.paddingBottom = dp2px(config.paddingBottom);

        screen_widht = mWindowManager.getDefaultDisplay().getWidth();
        screen_height = mWindowManager.getDefaultDisplay().getHeight();
        mFloatLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.playground_float_view, null);
        mFloatLayout.measure(0, 0);
        width = mFloatLayout.getMeasuredWidth();
        height = mFloatLayout.getMeasuredHeight();
        listview_left =  mFloatLayout.findViewById(R.id.listview_left);
        listview_right =  mFloatLayout.findViewById(R.id.listview_right);
        floating_menu = mFloatLayout.findViewById(R.id.float_image);

        x = 0;
        y = 0;
    }

    /**
     * 在当前Activity上方悬浮，可被其他Activity遮挡
     * 无需跳转到系统设置中去同意在其他APP上方显示遮盖
     * 需要等待当前Activity创建完成，如果在onCreate中直接调用会报错
     */
    public void showOverlayActivity() {
        if (isShowing) {
            return;
        }
        type = TYPE.OVERLAY_ACTIVITY;
        initParams();
        initPosition();
        initWindowView();
        isShowing = true;
        mWindowManager.addView(mFloatLayout, mParamsWindowManager);
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (!isShowing) {
            return;
        }
        isShowing = false;
        if (type == TYPE.OVERLAY_VIEWGROUP) {
            if (mFloatLayout.getParent() != null) {
                ((ViewGroup)mFloatLayout.getParent()).removeView(mFloatLayout);
            }
        } else if (type == TYPE.OVERLAY_SYSTEM || type == TYPE.OVERLAY_ACTIVITY){
            mWindowManager.removeView(mFloatLayout);
        }
    }

    private void initParams(){
        if (type == TYPE.OVERLAY_VIEWGROUP) {
            if (mParamsViewGroup == null) {
                mParamsViewGroup = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } else if (type == TYPE.OVERLAY_SYSTEM){
            if (mParamsWindowManager == null) {
                mParamsWindowManager = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mParamsWindowManager.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                }
                mParamsWindowManager.gravity = Gravity.LEFT | Gravity.TOP;
                mParamsWindowManager.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mParamsWindowManager.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
        } else if (type == TYPE.OVERLAY_ACTIVITY) {
            if (mParamsWindowManager == null) {
                mParamsWindowManager = new WindowManager.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                mParamsWindowManager.gravity = Gravity.LEFT | Gravity.TOP;
                mParamsWindowManager.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mParamsWindowManager.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
        }
    }

    private void initPosition() {
        int x = 0, y = 0;
        switch (config.gravity) {
            case LEFT_CENTER:
                x = config.paddingLeft;
                y = config.displayHeight / 2 - height / 2;
                break;
            case LEFT_TOP:
                x = config.paddingLeft;
                y = config.paddingTop;
                break;
            case TOP_CENTER:
                x = config.displayWidth / 2 - width / 2;
                y = config.paddingTop;
                break;
            case TOP_RIGHT:
                x = config.displayWidth - width - config.paddingRight;
                y = config.paddingTop;
                break;
            case RIGHT_CENTER:
                x = config.displayWidth - width - config.paddingRight;
                y = config.displayHeight / 2 - height / 2;
                break;
            case RIGHT_BOTTOM:
                x = config.displayWidth - width - config.paddingRight;
                y = config.displayHeight - height - config.paddingBottom;
                break;
            case BOTTOM_CENTER:
                x = config.displayWidth / 2 - width / 2;
                y = config.displayHeight - height - config.paddingBottom;
                break;
            case LEFT_BOTTOM:
                x = config.paddingLeft;
                y = config.displayHeight - height - config.paddingBottom;
                break;
            case CENTER:
                x = config.displayWidth / 2 - width / 2;
                y = config.displayHeight / 2 - height / 2;
                break;
        }
        if (type == TYPE.OVERLAY_SYSTEM || type == TYPE.OVERLAY_ACTIVITY) {
            mParamsWindowManager.x = 30;
            mParamsWindowManager.y = 30;
        } else if (type == TYPE.OVERLAY_VIEWGROUP){
            int marginLeft = mFloatLayout.getLeft() + x;
            marginLeft = marginLeft < 0? 0: marginLeft;
            marginLeft = marginLeft > config.displayWidth - width? config.displayWidth - width: marginLeft;

            int marginTop = mFloatLayout.getTop() + y;
            marginTop = marginTop < 0? 0: marginTop;
            marginTop = marginTop > config.displayHeight - height? config.displayHeight - height: marginTop;

            mParamsViewGroup.setMargins(marginLeft, marginTop, 0, 0);
        }
    }

    private void initWindowView(){
        final GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (onClickListener != null) {
                    onClickListener.onClick(mFloatLayout);
                }
                return true;
            }
        });

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            float[] temp = new float[]{0, 0};
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                width = mFloatLayout.getMeasuredWidth();
                height = mFloatLayout.getMeasuredHeight();
                if (gestureDetector.onTouchEvent(motionEvent)){
                    return true;
                }
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (type == TYPE.OVERLAY_VIEWGROUP) {
                            temp[0] = motionEvent.getRawX();
                            temp[1] = motionEvent.getRawY();
                        } else if (type == TYPE.OVERLAY_SYSTEM || type == TYPE.OVERLAY_ACTIVITY){
                            if(!isRight) {
                                temp[0] = motionEvent.getX();
                                temp[1] = motionEvent.getY();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //拖曳馬上隱藏
                        listview_left.setVisibility(View.GONE);
                        listview_right.setVisibility(View.GONE);
                        if (type == TYPE.OVERLAY_VIEWGROUP) {
                            int offsetX = (int)(motionEvent.getRawX() - temp[0]);
                            int offsetY = (int)(motionEvent.getRawY() - temp[1]);
                            moveWindow(offsetX, offsetY);
                            temp[0] = motionEvent.getRawX();
                            temp[1] = motionEvent.getRawY();
                        } else if (type == TYPE.OVERLAY_SYSTEM || type == TYPE.OVERLAY_ACTIVITY){
                            x = (int)(motionEvent.getRawX() - temp[0]);
                            y = (int)(motionEvent.getRawY() - temp[1]);
                            moveWindow(x, y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isRight) {
                            isRight = false;
                        }
                        if (x < screen_widht / 2) {
                            x = 0;
                        }
                        else{
                            x = screen_widht;
                        }
                        moveWindow(x, y);
                        break;
                }
                return true;
            }
        });
    }

    private void moveWindow(int x, int y){

        if (type == TYPE.OVERLAY_VIEWGROUP) {
            int marginLeft = mFloatLayout.getLeft() + x;
            marginLeft = marginLeft < 0? 0: marginLeft;
            marginLeft = marginLeft > config.displayWidth - width? config.displayWidth - width: marginLeft;

            int marginTop = mFloatLayout.getTop() + y;
            marginTop = marginTop < 0? 0: marginTop;
            marginTop = marginTop > config.displayHeight - height? config.displayHeight - height: marginTop;

            mParamsViewGroup.setMargins(marginLeft, marginTop, 0, 0);
            mFloatLayout.requestLayout();
        } else if (type == TYPE.OVERLAY_SYSTEM || type == TYPE.OVERLAY_ACTIVITY){
            Rect r = new Rect();
            mFloatLayout.getWindowVisibleDisplayFrame(r);
            int statusBarHeight = r.top;

            mParamsWindowManager.x = x;
            mParamsWindowManager.y = y - statusBarHeight;
            updateWindowSize();
        }
    }

    private void updateWindowSize(){
        mWindowManager.updateViewLayout(mFloatLayout, mParamsWindowManager);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }

}
