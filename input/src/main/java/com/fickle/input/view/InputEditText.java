package com.fickle.input.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fickle.input.R;
import com.fickle.input.annotation.CheckType;
import com.fickle.input.util.CheckUtil;

public class InputEditText extends LinearLayout {

    private TextView tvPrompt, tvAreaCode, tvSendCode, tvCustomize;
    private EditText editText;
    private ImageView btClear, btPwd, ivCaptcha;
    private View viewLine;

    private int edTextSize = dip2px(14);//输入文本大小
    private int edTextColor = Color.WHITE;//输入文本颜色
    private String hintText = null;//提示输入文字
    private int edHintColor = Color.GRAY;//提示颜色
    private int inputMax = Integer.MAX_VALUE;//输入最大值

    /**
     * 输入类型默认
     */
    public static final int OPERATE_NONE = 1;
    /**
     * 输入类型：数字
     */
    public static final int OPERATE_NUMBER = 2;
    /**
     * 输入类型：手机号
     */
    public static final int OPERATE_PHONE = 3;
    /**
     * 输入类型：密码
     */
    public static final int OPERATE_PWD = 4;

    /**
     * 校验类型：无
     */
    public static final int OPERATE_CHECK_NONE = 1;
    /**
     * 校验类型：手机
     */
    public static final int OPERATE_CHECK_PHONE = 2;
    /**
     * 校验类型：u验证码
     */
    public static final int OPERATE_CHECK_CODE = 3;
    /**
     * 校验类型：密码
     */
    public static final int OPERATE_CHECK_PWD = 4;
    /**
     * 校验类型：用户名
     */
    public static final int OPERATE_CHECK_NAME = 5;
    /**
     * 校验类型：用户名或手机号
     */
    public static final int OPERATE_CHECK_NAME_OR_PHONE = 6;

    private int operateInputType = OPERATE_NONE;//输入类型
    private int operateCheckType = OPERATE_CHECK_NONE;//校验类型
    private boolean operateTxt = false;//是否显示提示文本
    private boolean operateClear = false;//是否显示清除按钮
    private boolean operatePWD = false;//是否显示显示密码按钮
    private boolean operateSendCode = false;//是否显示发送验证码按钮
    private boolean operateCaptcha = false;//是否显示图形验证码
    private boolean operateAreaCode = false;//是否显示手机区号
    private String operatePromptDefault = "请设置提示语";
    private String operatePromptError = "请设置提示语";
    private String operatePromptCustomize = "";//自定义文本按钮

    private boolean errorPromptColorShow = true;//用于判断是否显示红色错误提示
    private boolean inputError = false;//记录是否输入错误
    private boolean mHasFocus;
    private Handler handler;
    private int countDown = 60;

    private OnInputEditTxtListener mOnInputEditxtListener;
    private OnSendCodeListener mOnSendCodeListener;
    private OnClickCaptchaListener mOnClickCaptchaListener;
    private OnCustomizeClickListener mOnCustomizeClickListener;
    AnimationSet promptShowAnim;
    AnimationSet promptHideAnim;


    public InputEditText(Context context) {
        super(context);
        init(context, null);
    }

    public InputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context != null && attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputEditText);
            operateCheckType = a.getInt(R.styleable.InputEditText_ient_openInputCheck, operateCheckType);
            operateInputType = a.getInt(R.styleable.InputEditText_ient_openInputType, operateInputType);
            operateTxt = a.getBoolean(R.styleable.InputEditText_ient_operateTxt, operateTxt);
            operateClear = a.getBoolean(R.styleable.InputEditText_ient_operateClear, operateClear);
            operatePWD = a.getBoolean(R.styleable.InputEditText_ient_operatePWD, operatePWD);
            operateSendCode = a.getBoolean(R.styleable.InputEditText_ient_operateSendCode, operateSendCode);
            operateCaptcha = a.getBoolean(R.styleable.InputEditText_ient_operateCaptcha, operateCaptcha);
            operateAreaCode = a.getBoolean(R.styleable.InputEditText_ient_operateAreaCode, operateAreaCode);
            edTextSize = a.getDimensionPixelSize(R.styleable.InputEditText_ient_ed_textSize, edTextSize);
            edTextColor = a.getColor(R.styleable.InputEditText_ient_ed_textColor, edTextColor);
            hintText = a.getString(R.styleable.InputEditText_ient_ed_hintText);
            edHintColor = a.getColor(R.styleable.InputEditText_ient_ed_hintColor, edHintColor);
            inputMax = a.getInt(R.styleable.InputEditText_ient_ed_inputMax, inputMax);
            operatePromptDefault = a.getString(R.styleable.InputEditText_ient_operatePromptDefault);
            operatePromptError = a.getString(R.styleable.InputEditText_ient_operatePromptError);
            operatePromptCustomize = a.getString(R.styleable.InputEditText_ient_operateCustomize);
            if (TextUtils.isEmpty(operatePromptDefault)) {
                operatePromptDefault = hintText;
            }
        }
        applyView(context, attrs);
    }

    private void applyView(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        removeAllViews();
        View view = View.inflate(context, R.layout.view_input_edit, null);

        tvCustomize = view.findViewById(R.id.iet_tv_customize);
        editText = view.findViewById(R.id.iet_ed_content);
        tvPrompt = view.findViewById(R.id.iet_tv_prompt);
        tvAreaCode = view.findViewById(R.id.iet_tv_area_code);
        tvSendCode = view.findViewById(R.id.iet_tv_send_code);
        btClear = view.findViewById(R.id.iet_iv_clear);
        btPwd = view.findViewById(R.id.iet_iv_show_close_pwd);
        viewLine = view.findViewById(R.id.iet_view_line);
        ivCaptcha = view.findViewById(R.id.iet_iv_captcha);
        ivCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickCaptchaListener != null) {
                    mOnClickCaptchaListener.click();
                }
            }
        });
        setOperatePromptCustomize(operatePromptCustomize);
        tvCustomize.setOnClickListener(customizeOnClick);
        btClear.setBackgroundResource(R.mipmap.common_close1);
        btClear.setVisibility(GONE);
        btClear.setOnClickListener(clearOnClick);
        editText.setTextColor(edTextColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, edTextSize);
        editText.setHint(hintText);
        editText.setHintTextColor(edHintColor);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputMax)});
        editText.addTextChangedListener(mTextWatcher);
        editText.setOnFocusChangeListener(changeListener);
        btClear.setVisibility(GONE);
        tvSendCode.setTextColor(context.getResources().getColor(R.color.common_unselected));

        setTvSendCode();
        setOperateCaptcha(operateCaptcha);
        setOperateSendCode(operateSendCode);
        setOperateAreaCode(operateAreaCode);
        setOperatePWD(operatePWD);
        initEditText(operateInputType);
        btPwd.setOnClickListener(pwdOnClick);
        addView(view);
    }


    public EditText getEditText() {
        return editText;
    }

    /**
     * 设置自定义文本
     *
     * @param operatePromptCustomize
     */
    public void setOperatePromptCustomize(String operatePromptCustomize) {
        this.operatePromptCustomize = operatePromptCustomize;
        if (!TextUtils.isEmpty(operatePromptCustomize)) {
            tvCustomize.setText(operatePromptCustomize);
            tvCustomize.setVisibility(VISIBLE);
        } else {
            tvCustomize.setVisibility(GONE);
        }
    }

    /**
     * 设置文本 提示
     *
     * @param hintText
     */
    public InputEditText setHintText(String hintText) {
        this.hintText = hintText;
        editText.setHint(hintText);
        return this;
    }

    /**
     * 获取当前文本
     *
     * @return
     */
    public String getText() {
        return editText.getText().toString();
    }

    /**
     * 判断是否输入正确
     *
     * @return true 正确
     */
    public boolean isProper() {
        return !TextUtils.isEmpty(editText.getText().toString()) && !inputError;
    }

    /**
     * 外部自定义规则时设置
     *
     * @param inputError
     */
    public InputEditText setInputError(boolean inputError) {
        this.inputError = inputError;
        setTvPrompt();
        return this;
    }

    /**
     * 设置图形验证码
     *
     * @param bitmap
     */
    public InputEditText setCaptchaBitmap(Bitmap bitmap) {
        if (bitmap == null) return this;
        ivCaptcha.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置是否显示红色错误提示
     *
     * @param errorPromptColorShow
     */
    public InputEditText setErrorPromptColorShow(boolean errorPromptColorShow) {
        this.errorPromptColorShow = errorPromptColorShow;
        return this;
    }

    /**
     * 设置默认提示文本 蓝色字体
     *
     * @param operatePromptDefault
     */
    public InputEditText setPromptDefault(String operatePromptDefault) {
        this.operatePromptDefault = operatePromptDefault;
        return this;
    }

    /**
     * 设置错误提示文本
     *
     * @param operatePromptError
     */
    public InputEditText setPromptError(String operatePromptError) {
        this.operatePromptError = operatePromptError;
        return this;
    }

    /**
     * 是否显示提示文本
     *
     * @param operateTxt
     */
    public InputEditText setOperateTxt(boolean operateTxt) {
        this.operateTxt = operateTxt;
        return this;
    }

    /**
     * 是否显示清除按钮
     *
     * @param operateClear
     */
    public InputEditText setOperateClear(boolean operateClear) {
        this.operateClear = operateClear;
        btClear.setVisibility(GONE);
        return this;
    }

    /**
     * 是否显示密码隐藏按钮
     *
     * @param operatePWD
     */
    public InputEditText setOperatePWD(boolean operatePWD) {
        this.operatePWD = operatePWD;
        if (operatePWD) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btPwd.setSelected(false);
            editText.setSelection(editText.getText().toString().length());
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btPwd.setVisibility(GONE);
            btPwd.setSelected(true);
            editText.setSelection(editText.getText().toString().length());
        }
        return this;
    }

    /**
     * 是否显示发送验证码按钮
     *
     * @param operateSendCode
     */
    public InputEditText setOperateSendCode(boolean operateSendCode) {
        this.operateSendCode = operateSendCode;
        if (operateSendCode) {
            tvSendCode.setVisibility(VISIBLE);
        } else {
            tvSendCode.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 是否显示图形验证码
     *
     * @param operateCaptcha
     */
    public InputEditText setOperateCaptcha(boolean operateCaptcha) {
        this.operateCaptcha = operateCaptcha;
        if (operateCaptcha) {
            ivCaptcha.setVisibility(VISIBLE);
        } else {
            ivCaptcha.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 是否显示手机区号
     *
     * @param operateAreaCode
     */
    public InputEditText setOperateAreaCode(boolean operateAreaCode) {
        this.operateAreaCode = operateAreaCode;
        if (operateAreaCode) {
            tvAreaCode.setVisibility(VISIBLE);
        } else {
            tvAreaCode.setVisibility(GONE);
        }
        return this;
    }

    /**
     * @param onInputEditxtListener
     */
    public void setOnInputEditxtListener(OnInputEditTxtListener onInputEditxtListener) {
        mOnInputEditxtListener = onInputEditxtListener;
    }

    /**
     * 发送验证码点击监听
     *
     * @param onSendCodeListener
     */
    public void setOnSendCodeListener(OnSendCodeListener onSendCodeListener) {
        mOnSendCodeListener = onSendCodeListener;
    }

    /**
     * @param onClickCaptchaListener
     */
    public void setOnClickCaptchaListener(OnClickCaptchaListener onClickCaptchaListener) {
        mOnClickCaptchaListener = onClickCaptchaListener;
    }


    /**
     * 自定义文本点击事件
     *
     * @param mOnCustomizeClickListener
     */
    public void setmOnCustomizeClickListener(OnCustomizeClickListener mOnCustomizeClickListener) {
        this.mOnCustomizeClickListener = mOnCustomizeClickListener;
    }

    public interface OnClickCaptchaListener {
        void click();
    }

    public interface OnSendCodeListener {
        boolean send();
    }

    public interface OnInputEditTxtListener {
        void checkResult(@CheckType String type);
    }

    public interface OnCustomizeClickListener {
        void customizeClick();
    }

    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    private void setTvSendCode() {
        if (handler == null) {
            handler = new Handler();
        }
        tvSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSendCodeListener != null && mOnSendCodeListener.send()) {
                    tvSendCode.setEnabled(false);
                    handler.postDelayed(mRunnable, 1000);
                } else {
                    tvSendCode.setEnabled(true);
                }
            }
        });
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (countDown == 0) {
                countDown = 60;
                tvSendCode.setText(getResources().getString(R.string.login_code_resend));
                tvSendCode.setEnabled(true);
                return;
            }
            tvSendCode.setText(--countDown + "s");
            handler.postDelayed(mRunnable, 1000);
        }
    };


    private void setViewLine() {
        if (inputError && errorPromptColorShow && !mHasFocus) {
            viewLine.setBackgroundResource(R.color.common_line_error);
        } else {
            if (mHasFocus) {
                viewLine.setBackgroundResource(R.color.common_line_select);
            } else {
                viewLine.setBackgroundResource(R.color.common_line_default);
            }
        }

    }

    private void setTvPrompt() {
        setViewLine();
        if (mHasFocus && operateTxt) {
            tvPrompt.setVisibility(VISIBLE);
        } else {
            tvPrompt.setVisibility(INVISIBLE);
        }
        if (inputError && errorPromptColorShow && !mHasFocus) {
            tvPrompt.setTextColor(getContext().getResources().getColor(R.color.common_prompt_error));
            tvPrompt.setText(operatePromptError);
            if (operateTxt) tvPrompt.setVisibility(VISIBLE);
        } else {
            tvPrompt.setTextColor(getContext().getResources().getColor(R.color.common_prompt_default));
            tvPrompt.setText(operatePromptDefault);
        }
    }

    private void setTvPromptFocus() {
        if (inputError && errorPromptColorShow && !mHasFocus) {
            tvPrompt.setTextColor(getContext().getResources().getColor(R.color.common_prompt_error));
            tvPrompt.setText(operatePromptError);
        } else {
            tvPrompt.setTextColor(getContext().getResources().getColor(R.color.common_prompt_default));
            tvPrompt.setText(operatePromptDefault);
            if (mHasFocus && operateTxt) {
                tvPrompt.setVisibility(VISIBLE);
                editText.setHint("");
                promptShowAnim();
            } else {
                editText.setHint(hintText);
                promptHideAnim();
            }
        }
    }

    private void promptShowAnim() {
        if (promptShowAnim == null) {
            promptShowAnim = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, editText.getY() + (editText.getHeight() / 2), tvPrompt.getY());
            promptShowAnim.setDuration(300);
            promptShowAnim.setInterpolator(new DecelerateInterpolator());
            promptShowAnim.addAnimation(alphaAnimation);
            promptShowAnim.addAnimation(translateAnimation);
        }
        tvPrompt.setAnimation(promptShowAnim);
        promptShowAnim.start();
    }


    private void promptHideAnim() {
        if (promptHideAnim == null) {
            promptHideAnim = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, tvPrompt.getY(), editText.getY() + (editText.getHeight() / 2));
            promptHideAnim.setDuration(300);
            promptHideAnim.setInterpolator(new DecelerateInterpolator());
            promptHideAnim.addAnimation(alphaAnimation);
            promptHideAnim.addAnimation(translateAnimation);
            promptHideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tvPrompt.setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        tvPrompt.setAnimation(promptHideAnim);
        promptHideAnim.start();
    }


    private View.OnFocusChangeListener changeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mHasFocus = hasFocus;
            if (hasFocus) {
                if (operatePWD) {
                    btPwd.setVisibility(VISIBLE);
                }
                if (editText.getText().toString().length() != 0 && operateClear) {
                    btClear.setVisibility(VISIBLE);
                }
                tvSendCode.setTextColor(getContext().getResources().getColor(R.color.common_selected));
            } else {
                btPwd.setVisibility(GONE);
                btClear.setVisibility(GONE);
                viewLine.setBackgroundResource(R.color.common_line_default);
                tvSendCode.setTextColor(getContext().getResources().getColor(R.color.common_unselected));
            }
            setTvPromptFocus();
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (btClear != null && operateClear) {
                btClear.setVisibility(s.length() == 0 ? GONE : VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String user = editText.getText().toString().trim();
            if (TextUtils.isEmpty(user)) {
                inputError = false;
                setTvPrompt();
                if (mOnInputEditxtListener != null) {
                    mOnInputEditxtListener.checkResult(CheckType.NONE);
                }
                return;
            }
            switch (operateCheckType) {
                case OPERATE_CHECK_PWD:
                    if (CheckUtil.isInputtingPwd(user)) {
                        //密码格式正确
                        inputError = false;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.PWD_SUCCESS);
                        }
                        setTvPrompt();
                    } else {
                        //密码格式错误
                        inputError = true;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.PWD_ERROR);
                        }
                        setTvPrompt();
                    }
                    break;
                case OPERATE_CHECK_CODE:
                    if (CheckUtil.isInputtingCode(user)) {
                        //验证码格式正确
                        inputError = false;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.CODE_SUCCESS);
                        }
                        setTvPrompt();
                    } else {
                        //验证码格式错误
                        inputError = true;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.CODE_ERROR);
                        }
                        setTvPrompt();
                    }
                    break;
                case OPERATE_CHECK_PHONE:
                    if (!checkPhone(user)) {
                        //用于提示错误的手机号
                        inputError = true;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.PHONE_ERROR);
                        }
                        setTvPrompt();
                    }
                    break;
                case OPERATE_CHECK_NAME:
                    if (!checkName(user)) {
                        //用于提示错误的账号
                        inputError = true;
                        if (mOnInputEditxtListener != null) {
                            mOnInputEditxtListener.checkResult(CheckType.NAME_ERROR);
                        }
                        setTvPrompt();
                    }
                    break;
                case OPERATE_CHECK_NAME_OR_PHONE:
                    if (!checkPhone(user) && !checkName(user)) {
                        //错误
                        if (user.startsWith("1")) {
                            //用于提示错误的手机号
                            inputError = true;
                            if (mOnInputEditxtListener != null) {
                                mOnInputEditxtListener.checkResult(CheckType.PHONE_ERROR);
                            }
                            setTvPrompt();
                        } else {
                            //用于提示错误的账号
                            inputError = true;
                            if (mOnInputEditxtListener != null) {
                                mOnInputEditxtListener.checkResult(CheckType.NAME_ERROR);
                            }
                            setTvPrompt();
                        }
                    }
                    break;
                default:
                    inputError = false;
                    setTvPrompt();
                    if (mOnInputEditxtListener != null) {
                        mOnInputEditxtListener.checkResult(CheckType.NONE);
                    }
                    break;
            }
        }
    };

    private void initEditText(int type) {
        switch (type) {
            case OPERATE_NONE:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case OPERATE_NUMBER:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case OPERATE_PHONE:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case OPERATE_PWD:
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
    }

    private boolean checkPhone(String user) {
        if (CheckUtil.isInputtingMobile(user)) {
            inputError = false;
            if (mOnInputEditxtListener != null) {
                mOnInputEditxtListener.checkResult(CheckType.PHONE_SUCCESS);
            }
            setTvPrompt();
            return true;
        } else {
            return false;
        }
    }

    private boolean checkName(String user) {
        if (CheckUtil.isInputtingUsername(user)) {
            //账号
            inputError = false;
            if (mOnInputEditxtListener != null) {
                mOnInputEditxtListener.checkResult(CheckType.NAME_SUCCESS);
            }
            setTvPrompt();
            return true;
        } else {
            return false;
        }
    }


    private View.OnClickListener clearOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editText.getText().clear();
            editText.setSelection(0);
        }
    };

    private View.OnClickListener customizeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnCustomizeClickListener != null) {
                mOnCustomizeClickListener.customizeClick();
            }
        }
    };

    private View.OnClickListener pwdOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btPwd.isSelected()) {
                btPwd.setSelected(false);
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setSelection(editText.getText().toString().length());
            } else {
                btPwd.setSelected(true);
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setSelection(editText.getText().toString().length());
            }
        }
    };

    private int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
