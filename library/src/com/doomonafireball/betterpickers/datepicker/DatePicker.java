package com.doomonafireball.betterpickers.datepicker;

import java.util.Calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.doomonafireball.betterpickers.R;
import com.doomonafireball.betterpickers.UnderlinePageIndicatorPicker;

public class DatePicker extends LinearLayout implements Button.OnClickListener, Button.OnLongClickListener {

    protected int mDateInputSize = 2;
    protected int mYearInputSize = 4;
    protected int mMonthInput = -1;
    protected int mDateInput[] = new int[mDateInputSize];
    protected int mYearInput[] = new int[mYearInputSize];
    protected int mDateInputPointer = -1;
    protected int mYearInputPointer = -1;
    protected final Button mMonths[] = new Button[12];
    protected final Button mDateNumbers[] = new Button[10];
    protected final Button mYearNumbers[] = new Button[10];
    protected Button mDateLeft, mDateRight;
    protected Button mYearLeft, mYearRight;
    protected UnderlinePageIndicatorPicker mKeyboardIndicator;
    protected ViewPager mKeyboardPager;
    protected KeyboardPagerAdapter mKeyboardPagerAdapter;
    protected ImageButton mDelete;
    protected DateView mEnteredDate;
    protected String[] mMonthAbbreviations;
    protected final Context mContext;
    private final char[] mDateFormatOrder;

    private static final String KEYBOARD_MONTH = "month";
    private static final String KEYBOARD_DATE = "date";
    private static final String KEYBOARD_YEAR = "year";

    private static int POSITION_KEYBOARD_MONTH = -1;
    private static int POSITION_KEYBOARD_DATE = -1;
    private static int POSITION_KEYBOARD_YEAR = -1;

    private Button mSetButton;

    protected View mDivider;
    private ColorStateList mTextColor;
    private int mKeyBackgroundResId;
    private int mButtonBackgroundResId;
    private int mDividerTitleColor;
    private float mDividerHeight;
    private int mKeyboardIndicatorColor;
    private int mDeleteDrawableSrcResId;
    private int mTheme = -1;

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDateFormatOrder = DateFormat.getDateFormatOrder(mContext);
        mMonthAbbreviations = mContext.getResources().getStringArray(R.array.month_abbreviations);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(getLayoutId(), this);

        // Init defaults
        mTextColor = getResources().getColorStateList(R.color.bp_dialog_text_color_holo_dark);
        mKeyBackgroundResId = R.drawable.bp_key_background_dark;
        mButtonBackgroundResId = R.drawable.bp_button_background_dark;
        mDividerTitleColor = getResources().getColor(R.color.default_divider_color_dark);
        mKeyboardIndicatorColor = getResources().getColor(R.color.default_keyboard_indicator_color_dark);
        mDeleteDrawableSrcResId = R.drawable.bp_ic_backspace_dark;
        mDividerHeight = getResources().getDimension(R.dimen.divider_date_height);
    }

    protected int getLayoutId() {
        return R.layout.bp_date_picker_view;
    }

    public void setTheme(int themeResId) {
        mTheme = themeResId;
        if (mTheme != -1) {
            TypedArray a = getContext().obtainStyledAttributes(themeResId, R.styleable.BetterPickersDialogFragment);

            mTextColor = a.getColorStateList(R.styleable.BetterPickersDialogFragment_bpTextColor);
            mKeyBackgroundResId = a.getResourceId(R.styleable.BetterPickersDialogFragment_bpKeyBackground, mKeyBackgroundResId);
            mButtonBackgroundResId = a.getResourceId(R.styleable.BetterPickersDialogFragment_bpButtonBackground, mButtonBackgroundResId);
            mDividerTitleColor = a.getColor(R.styleable.BetterPickersDialogFragment_bpDividerTitleColor, mDividerTitleColor);
            mKeyboardIndicatorColor = a.getColor(R.styleable.BetterPickersDialogFragment_bpKeyboardIndicatorColor, mKeyboardIndicatorColor);
            mDeleteDrawableSrcResId = a.getResourceId(R.styleable.BetterPickersDialogFragment_bpDeleteIcon, mDeleteDrawableSrcResId);
            mDividerHeight = a.getDimension(R.styleable.BetterPickersDialogFragment_bpDateDividerHeight, mDividerHeight);
        }

        restyleViews();
    }

    private void restyleViews() {
        for (Button month : mMonths) {
            if (month != null) {
                month.setTextColor(mTextColor);
                month.setBackgroundResource(mKeyBackgroundResId);
            }
        }
        for (Button dateNumber : mDateNumbers) {
            if (dateNumber != null) {
                dateNumber.setTextColor(mTextColor);
                dateNumber.setBackgroundResource(mKeyBackgroundResId);
            }
        }
        for (Button yearNumber : mYearNumbers) {
            if (yearNumber != null) {
                yearNumber.setTextColor(mTextColor);
                yearNumber.setBackgroundResource(mKeyBackgroundResId);
            }
        }
        if (mKeyboardIndicator != null) {
            mKeyboardIndicator.setSelectedColor(mKeyboardIndicatorColor);
        }
        if (mDivider != null) {
            mDivider.setBackgroundColor(mDividerTitleColor);
            mDivider.getLayoutParams().height = (int) mDividerHeight;
        }
        if (mDateLeft != null) {
            mDateLeft.setTextColor(mTextColor);
            mDateLeft.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mDateRight != null) {
            mDateRight.setTextColor(mTextColor);
            mDateRight.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mDelete != null) {
            mDelete.setBackgroundResource(mButtonBackgroundResId);
            mDelete.setImageDrawable(getResources().getDrawable(mDeleteDrawableSrcResId));
        }
        if (mYearLeft != null) {
            mYearLeft.setTextColor(mTextColor);
            mYearLeft.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mYearRight != null) {
            mYearRight.setTextColor(mTextColor);
            mYearRight.setBackgroundResource(mKeyBackgroundResId);
        }
        if (mEnteredDate != null) {
            mEnteredDate.setTheme(mTheme);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDivider = findViewById(R.id.dividerDate);

        for (int i = 0; i < mDateInput.length; i++) {
            mDateInput[i] = 0;
        }
        for (int i = 0; i < mYearInput.length; i++) {
            mYearInput[i] = 0;
        }

        mKeyboardIndicator = (UnderlinePageIndicatorPicker) findViewById(R.id.keyboard_indicator);
        mKeyboardPager = (ViewPager) findViewById(R.id.keyboard_pager);
        mKeyboardPager.setOffscreenPageLimit(2);
        mKeyboardPagerAdapter = new KeyboardPagerAdapter((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mKeyboardPager.setAdapter(mKeyboardPagerAdapter);
        mKeyboardIndicator.setViewPager(mKeyboardPager);
        mKeyboardPager.setCurrentItem(0);

        mEnteredDate = (DateView) findViewById(R.id.date_text);
        mEnteredDate.setTheme(mTheme);
        mEnteredDate.setUnderlinePage(mKeyboardIndicator);
        mEnteredDate.setOnClick(this);

        mDelete = (ImageButton) findViewById(R.id.delete);
        mDelete.setOnClickListener(this);
        mDelete.setOnLongClickListener(this);
        setLeftRightEnabled();
        updateDate();
        updateKeypad();
    }

    private class KeyboardPagerAdapter extends PagerAdapter {

        private final LayoutInflater mInflater;

        public KeyboardPagerAdapter(LayoutInflater inflater) {
            super();
            mInflater = inflater;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View view;
            Resources res = mContext.getResources();
            if (mDateFormatOrder[position] == DateFormat.MONTH) {
                // Months
                POSITION_KEYBOARD_MONTH = position;
                view = mInflater.inflate(R.layout.bp_keyboard_text, null);
                View v1 = view.findViewById(R.id.first);
                View v2 = view.findViewById(R.id.second);
                View v3 = view.findViewById(R.id.third);
                View v4 = view.findViewById(R.id.fourth);

                mMonths[0] = (Button) v1.findViewById(R.id.key_left);
                mMonths[1] = (Button) v1.findViewById(R.id.key_middle);
                mMonths[2] = (Button) v1.findViewById(R.id.key_right);

                mMonths[3] = (Button) v2.findViewById(R.id.key_left);
                mMonths[4] = (Button) v2.findViewById(R.id.key_middle);
                mMonths[5] = (Button) v2.findViewById(R.id.key_right);

                mMonths[6] = (Button) v3.findViewById(R.id.key_left);
                mMonths[7] = (Button) v3.findViewById(R.id.key_middle);
                mMonths[8] = (Button) v3.findViewById(R.id.key_right);

                mMonths[9] = (Button) v4.findViewById(R.id.key_left);
                mMonths[10] = (Button) v4.findViewById(R.id.key_middle);
                mMonths[11] = (Button) v4.findViewById(R.id.key_right);

                for (int i = 0; i < 12; i++) {
                    mMonths[i].setOnClickListener(DatePicker.this);
                    mMonths[i].setText(mMonthAbbreviations[i]);
                    mMonths[i].setTextColor(mTextColor);
                    mMonths[i].setBackgroundResource(mKeyBackgroundResId);
                    mMonths[i].setTag(R.id.date_keyboard, KEYBOARD_MONTH);
                    mMonths[i].setTag(R.id.date_month_int, i);
                }
            } else if (mDateFormatOrder[position] == DateFormat.DATE) {
                // Date
                POSITION_KEYBOARD_DATE = position;
                view = mInflater.inflate(R.layout.bp_keyboard_left_right_text, null);
                View v1 = view.findViewById(R.id.first);
                View v2 = view.findViewById(R.id.second);
                View v3 = view.findViewById(R.id.third);
                View v4 = view.findViewById(R.id.fourth);

                mDateNumbers[1] = (Button) v1.findViewById(R.id.key_left);
                mDateNumbers[2] = (Button) v1.findViewById(R.id.key_middle);
                mDateNumbers[3] = (Button) v1.findViewById(R.id.key_right);

                mDateNumbers[4] = (Button) v2.findViewById(R.id.key_left);
                mDateNumbers[5] = (Button) v2.findViewById(R.id.key_middle);
                mDateNumbers[6] = (Button) v2.findViewById(R.id.key_right);

                mDateNumbers[7] = (Button) v3.findViewById(R.id.key_left);
                mDateNumbers[8] = (Button) v3.findViewById(R.id.key_middle);
                mDateNumbers[9] = (Button) v3.findViewById(R.id.key_right);

                mDateNumbers[0] = (Button) v4.findViewById(R.id.key_middle);

                mDateLeft = (Button) v4.findViewById(R.id.key_left);
                mDateLeft.setTextColor(mTextColor);
                mDateLeft.setBackgroundResource(mKeyBackgroundResId);
                mDateLeft.setOnClickListener(DatePicker.this);
                mDateLeft.setText("-1");

                mDateRight = (Button) v4.findViewById(R.id.key_right);
                mDateRight.setTextColor(mTextColor);
                mDateRight.setBackgroundResource(mKeyBackgroundResId);
                mDateRight.setOnClickListener(DatePicker.this);
                mDateRight.setText("+1");

                for (int i = 0; i < 10; i++) {
                    mDateNumbers[i].setOnClickListener(DatePicker.this);
                    mDateNumbers[i].setText(String.format("%d", i));
                    mDateNumbers[i].setTextColor(mTextColor);
                    mDateNumbers[i].setBackgroundResource(mKeyBackgroundResId);
                    mDateNumbers[i].setTag(R.id.date_keyboard, KEYBOARD_DATE);
                    mDateNumbers[i].setTag(R.id.numbers_key, i);
                }

            } else if (mDateFormatOrder[position] == DateFormat.YEAR) {
                // Year
                POSITION_KEYBOARD_YEAR = position;
                view = mInflater.inflate(R.layout.bp_keyboard_left_right_text, null);
                View v1 = view.findViewById(R.id.first);
                View v2 = view.findViewById(R.id.second);
                View v3 = view.findViewById(R.id.third);
                View v4 = view.findViewById(R.id.fourth);

                mYearNumbers[1] = (Button) v1.findViewById(R.id.key_left);
                mYearNumbers[2] = (Button) v1.findViewById(R.id.key_middle);
                mYearNumbers[3] = (Button) v1.findViewById(R.id.key_right);

                mYearNumbers[4] = (Button) v2.findViewById(R.id.key_left);
                mYearNumbers[5] = (Button) v2.findViewById(R.id.key_middle);
                mYearNumbers[6] = (Button) v2.findViewById(R.id.key_right);

                mYearNumbers[7] = (Button) v3.findViewById(R.id.key_left);
                mYearNumbers[8] = (Button) v3.findViewById(R.id.key_middle);
                mYearNumbers[9] = (Button) v3.findViewById(R.id.key_right);
                mYearNumbers[0] = (Button) v4.findViewById(R.id.key_middle);

                mYearLeft = (Button) v4.findViewById(R.id.key_left);
                mYearLeft.setTextColor(mTextColor);
                mYearLeft.setBackgroundResource(mKeyBackgroundResId);
                mYearLeft.setOnClickListener(DatePicker.this);
                mYearLeft.setText("-1");

                mYearRight = (Button) v4.findViewById(R.id.key_right);
                mYearRight.setTextColor(mTextColor);
                mYearRight.setBackgroundResource(mKeyBackgroundResId);
                mYearRight.setOnClickListener(DatePicker.this);
                mYearRight.setText("+1");

                for (int i = 0; i < 10; i++) {
                    mYearNumbers[i].setOnClickListener(DatePicker.this);
                    mYearNumbers[i].setText(String.format("%d", i));
                    mYearNumbers[i].setTextColor(mTextColor);
                    mYearNumbers[i].setBackgroundResource(mKeyBackgroundResId);
                    mYearNumbers[i].setTag(R.id.date_keyboard, KEYBOARD_YEAR);
                    mYearNumbers[i].setTag(R.id.numbers_key, i);
                }
            } else {
                view = new View(mContext);
            }
            setLeftRightEnabled();
            updateDate();
            updateKeypad();
            collection.addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }

    public void updateDeleteButton() {
        boolean enabled = mMonthInput != -1 || mDateInputPointer != -1 || mYearInputPointer != -1;
        if (mDelete != null) {
            mDelete.setEnabled(enabled);
        }
    }

    @Override
    public void onClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        doOnClick(v);
        updateDeleteButton();
    }

    protected void doOnClick(View v) {
        if (v == mDelete) {
            // Delete is dependent on which keyboard
            switch (mDateFormatOrder[mKeyboardPager.getCurrentItem()]) {
            case DateFormat.MONTH:
                if (mMonthInput != -1) {
                    mMonthInput = -1;
                }
                break;
            case DateFormat.DATE:
                if (mDateInputPointer >= 0) {
                    for (int i = 0; i < mDateInputPointer; i++) {
                        mDateInput[i] = mDateInput[i + 1];
                    }
                    mDateInput[mDateInputPointer] = 0;
                    mDateInputPointer--;
                } else if (mKeyboardPager.getCurrentItem() > 0) {
                    mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() - 1, true);
                }
                break;
            case DateFormat.YEAR:
                if (mYearInputPointer >= 0) {
                    for (int i = 0; i < mYearInputPointer; i++) {
                        mYearInput[i] = mYearInput[i + 1];
                    }
                    mYearInput[mYearInputPointer] = 0;
                    mYearInputPointer--;
                } else if (mKeyboardPager.getCurrentItem() > 0) {
                    mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() - 1, true);
                }
                break;
            }
        } else if (v == mDateRight) {

            final int day = getDayOfMonth();
            final int month = getMonthOfYear();
            final int year = getYear();
            if (day >= 28 && month == 1) {
                if (day == 28 && isLeapYear(year)) {
                    setDayOfMonth(day + 1);
                } else {
                    setDayOfMonth(1);
                    setMonthOfYear(getMonthOfYear() + 1);
                }

            } else if (day == 30 && (month == 3 || month == 5 || month == 8 || month == 10)) {
                setDayOfMonth(1);
                setMonthOfYear(getMonthOfYear() + 1);
            } else if (day == 31 && month == 11) {
                setDayOfMonth(1);
                setMonthOfYear(0);
                setYear(getYear() + 1);
            } else if (day == 31) {
                setDayOfMonth(1);
                setMonthOfYear(getMonthOfYear() + 1);
            } else {
                setDayOfMonth(day + 1);
            }
        } else if (v == mDateLeft) {
            final int day = getDayOfMonth();
            final int month = getMonthOfYear();
            final int year = getYear();
            if (day == 1 && month == 2) {
                if (isLeapYear(year)) {
                    setDayOfMonth(29);
                } else {
                    setDayOfMonth(28);
                }
                setMonthOfYear(getMonthOfYear() - 1);

            } else if (day == 1 && (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10)) {
                setDayOfMonth(31);
                setMonthOfYear(getMonthOfYear() - 1);
            } else if (day == 1 && month == 0) {
                setDayOfMonth(31);
                setMonthOfYear(11);
                setYear(getYear() - 1);
            } else if (day == 1) {
                setDayOfMonth(30);
                setMonthOfYear(getMonthOfYear() - 1);
            } else {
                setDayOfMonth(day - 1);
            }

        } else if (v == mYearRight) {
            setYear(getYear() + 1);
            if (getDayOfMonth() == 29 && getMonthOfYear() == 1 && !isLeapYear(getYear())) {
                setDayOfMonth(getDayOfMonth() - 1);
            }
        } else if (v == mYearLeft) {
            setYear(getYear() - 1);
            if (getDayOfMonth() == 29 && getMonthOfYear() == 1 && !isLeapYear(getYear())) {
                setDayOfMonth(getDayOfMonth() - 1);
            }
        } else if (v == mEnteredDate.getDate()) {
            mKeyboardPager.setCurrentItem(POSITION_KEYBOARD_DATE);
        } else if (v == mEnteredDate.getMonth()) {
            mKeyboardPager.setCurrentItem(POSITION_KEYBOARD_MONTH);
        } else if (v == mEnteredDate.getYear()) {
            mKeyboardPager.setCurrentItem(POSITION_KEYBOARD_YEAR);
        } else if (v.getTag(R.id.date_keyboard).equals(KEYBOARD_MONTH)) {
            // A month was pressed
            mMonthInput = (Integer) v.getTag(R.id.date_month_int);
            if (mKeyboardPager.getCurrentItem() < 2) {
                mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() + 1, true);
            }
        } else if (v.getTag(R.id.date_keyboard).equals(KEYBOARD_DATE)) {
            // A date number was pressed
            addClickedDateNumber((Integer) v.getTag(R.id.numbers_key));
        } else if (v.getTag(R.id.date_keyboard).equals(KEYBOARD_YEAR)) {
            // A year number was pressed
            addClickedYearNumber((Integer) v.getTag(R.id.numbers_key));
        }
        updateKeypad();
    }

    @Override
    public boolean onLongClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        if (v == mDelete) {
            mDelete.setPressed(false);
            reset();
            updateKeypad();
            return true;
        }
        return false;
    }

    private void updateKeypad() {
        // Update state of keypad
        // Update the number
        updateLeftRightButtons();
        updateDate();
        // enable/disable the "set" key
        enableSetButton();
        // Update the backspace button
        updateDeleteButton();
        updateMonthKeys();
        updateDateKeys();
        updateYearKeys();
    }

    public void reset() {
        for (int i = 0; i < mDateInputSize; i++) {
            mDateInput[i] = 0;
        }
        for (int i = 0; i < mYearInputSize; i++) {
            mYearInput[i] = 0;
        }
        mDateInputPointer = -1;
        mYearInputPointer = -1;
        mMonthInput = -1;
        mKeyboardPager.setCurrentItem(0, true);
        updateDate();
    }

    // Update the date displayed in the picker:
    protected void updateDate() {
        String month;
        if (mMonthInput < 0) {
            month = "";
        } else {
            month = mMonthAbbreviations[mMonthInput];
        }
        mEnteredDate.setDate(month, getDayOfMonth(), getYear());
    }

    protected void setLeftRightEnabled() {
        if (mDateLeft != null) {
            mDateLeft.setEnabled(true);
        }
        if (mDateRight != null) {
            mDateRight.setEnabled(true);
        }
        if (mYearLeft != null) {
            mYearLeft.setEnabled(true);
        }
        if (mYearRight != null) {
            mYearRight.setEnabled(true);
        }
    }

    private void addClickedDateNumber(int val) {
        if (mDateInputPointer < mDateInputSize - 1 && mDateInput[0] < 4) {
            for (int i = mDateInputPointer; i >= 0; i--) {
                mDateInput[i + 1] = mDateInput[i];
            }
            mDateInputPointer++;
            mDateInput[0] = val;
        } else {
            for (int i = mDateInput.length; i > 0; i--) {
                mDateInput[i - 1] = 0;
            }
            mDateInputPointer = 0;
            mDateInput[0] = val;
        }
        if (getDayOfMonth() >= 4 || (getMonthOfYear() == 1 && getDayOfMonth() >= 3)) {
            if (mKeyboardPager.getCurrentItem() < 2) {
                mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() + 1, true);
            }
        }
    }

    private void addClickedYearNumber(int val) {
        if (mYearInputPointer < mYearInputSize - 1) {
            for (int i = mYearInputPointer; i >= 0; i--) {
                mYearInput[i + 1] = mYearInput[i];
            }
            mYearInputPointer++;
            mYearInput[0] = val;
        } else {
            for (int i = mYearInput.length; i > 0; i--) {
                mYearInput[i - 1] = 0;
            }
            mYearInputPointer = 0;
            mYearInput[0] = val;
        }
        if (mKeyboardPager.getCurrentItem() < 2) {
            mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() + 1, true);
        }
    }

    // Clicking on the date right button advances
    private void onDateRightClicked() {
        if (mKeyboardPager.getCurrentItem() < 2) {
            mKeyboardPager.setCurrentItem(mKeyboardPager.getCurrentItem() + 1, true);
        }
    }

    // Enable/disable keys on the month key pad according to the data entered
    private void updateMonthKeys() {
        int date = getDayOfMonth();
        for (int i = 0; i < mMonths.length; i++) {
            if (mMonths[i] != null) {
                mMonths[i].setEnabled(true);
            }
        }
        if (mMonths[1] != null) {
            if (date == 29 && getYear() > 999) {
                setLeapYearKeyMonth(getYear());

            } else if (date > 29) {
                // Disable February
                mMonths[1].setEnabled(false);
            }
        }
        if (date > 30) {
            // Disable April, June, September, November
            if (mMonths[3] != null) {
                mMonths[3].setEnabled(false);
            }
            if (mMonths[5] != null) {
                mMonths[5].setEnabled(false);
            }
            if (mMonths[8] != null) {
                mMonths[8].setEnabled(false);
            }
            if (mMonths[10] != null) {
                mMonths[10].setEnabled(false);
            }
        }
    }

    // Enable/disable keys on the date key pad according to the data entered
    private void updateDateKeys() {
        int date = getDayOfMonth();
        if (date == 2 && getMonthOfYear() == 1 && getYear() > 999) {
            setLeapYearKeyDay(getYear());
        } else if (date >= 4) {
            setDateMinKeyRange(1);
        } else if (date >= 3) {
            if (mMonthInput == 1) {
                // February
                setDateMinKeyRange(1);
            } else if (mMonthInput == 3 || mMonthInput == 5 || mMonthInput == 8 || mMonthInput == 10) {
                // April, June, September, Novemeber have 30 days
                setDateKeyRange(0);
            } else {
                setDateKeyRange(1);
            }
        } else if (date >= 2) {
            setDateKeyRange(9);
        } else if (date >= 1) {
            setDateKeyRange(9);
        } else {
            setDateMinKeyRange(1);
        }
    }

    // Enable/disable keys on the year key pad according to the data entered
    private void updateYearKeys() {
        int year = getYear();
        if (year <= 999 && year >= 100 && getMonthOfYear() == 1 && getDayOfMonth() == 29) {
            setLeapYearKeyYear(year);
        } else if (year >= 1000) {
            setYearMinKeyRange(1);
        } else if (year >= 1) {
            setYearKeyRange(9);
        } else {
            setYearMinKeyRange(1);
        }
    }

    // enables a range of numeric keys from zero to maxKey. The rest of the keys
    // will be disabled
    private void setDateKeyRange(int maxKey) {
        for (int i = 0; i < mDateNumbers.length; i++) {
            if (mDateNumbers[i] != null) {
                mDateNumbers[i].setEnabled(i <= maxKey);
            }
        }
    }

    // enables a range of numeric keys from minKey up. The rest of the keys will
    // be disabled
    private void setDateMinKeyRange(int minKey) {
        for (int i = 0; i < mDateNumbers.length; i++) {
            if (mDateNumbers[i] != null) {
                mDateNumbers[i].setEnabled(i >= minKey);
            }
        }
    }

    // enables a range of numeric keys from zero to maxKey. The rest of the keys
    // will be disabled
    private void setYearKeyRange(int maxKey) {
        for (int i = 0; i < mYearNumbers.length; i++) {
            if (mYearNumbers[i] != null) {
                mYearNumbers[i].setEnabled(i <= maxKey);
            }
        }
    }

    // enables a range of numeric keys from minKey up. The rest of the keys will
    // be disabled
    private void setYearMinKeyRange(int minKey) {
        for (int i = 0; i < mYearNumbers.length; i++) {
            if (mYearNumbers[i] != null) {
                mYearNumbers[i].setEnabled(i >= minKey);
            }
        }
    }

    private void setLeapYearKeyYear(int year) {
        for (int i = 0; i < 10; i++) {
            final int newYear = (year * 10) + i;
            mYearNumbers[i].setEnabled(isLeapYear(newYear));
        }
    }

    private void setLeapYearKeyMonth(int year) {
        mMonths[1].setEnabled(isLeapYear(year));
    }

    private void setLeapYearKeyDay(int year) {
        mDateNumbers[9].setEnabled(isLeapYear(year));
    }

    private boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return (cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365);
    }

    // Checks if the user is allowed to move to the year
    // private boolean canGoToYear() {
    // return getDayOfMonth() > 0;
    // }

    private void updateLeftRightButtons() {
        if (mDateLeft != null) {
            mDateLeft.setEnabled(true);
        }
        if (mDateRight != null) {
            mDateRight.setEnabled(true);
        }
        if (mYearLeft != null) {
            mYearLeft.setEnabled(true);
        }
        if (mYearRight != null) {
            mYearRight.setEnabled(true);
        }
    }

    // Enable/disable the set key
    private void enableSetButton() {
        if (mSetButton == null) {
            return;
        }
        mSetButton.setEnabled(getDayOfMonth() > 0 && getYear() >= 1000 && getMonthOfYear() >= 0);
    }

    public void setSetButton(Button b) {
        mSetButton = b;
        enableSetButton();
    }

    public int getYear() {
        return mYearInput[3] * 1000 + mYearInput[2] * 100 + mYearInput[1] * 10 + mYearInput[0];
    }

    public void setYear(int year) {
        final String mYear = year + "";
        if (mYear.length() == 4) {
            mYearInput[3] = Integer.parseInt(mYear.substring(0, 1));
            mYearInput[2] = Integer.parseInt(mYear.substring(1, 2));
            mYearInput[1] = Integer.parseInt(mYear.substring(2, 3));
            mYearInput[0] = Integer.parseInt(mYear.substring(3, 4));
        } else if (mYear.length() == 3) {
            mYearInput[3] = 0;
            mYearInput[2] = Integer.parseInt(mYear.substring(0, 1));
            mYearInput[1] = Integer.parseInt(mYear.substring(1, 2));
            mYearInput[0] = Integer.parseInt(mYear.substring(2, 3));
        } else if (mYear.length() == 2) {
            mYearInput[3] = 0;
            mYearInput[2] = 0;
            mYearInput[1] = Integer.parseInt(mYear.substring(0, 1));
            mYearInput[0] = Integer.parseInt(mYear.substring(1, 2));
        } else if (mYear.length() == 1) {
            mYearInput[3] = 0;
            mYearInput[2] = 0;
            mYearInput[1] = 0;
            mYearInput[0] = Integer.parseInt(mYear.substring(0, 1));
        } else {
            mYearInput[3] = 0;
            mYearInput[2] = 0;
            mYearInput[1] = 0;
            mYearInput[0] = 0;
        }
    }

    public int getMonthOfYear() {
        return mMonthInput;
    }

    public void setMonthOfYear(int month) {
        mMonthInput = month;
    }

    public int getDayOfMonth() {
        return mDateInput[1] * 10 + mDateInput[0];
    }

    public void setDayOfMonth(int dateOfMonth) {
        final String mDate = dateOfMonth + "";
        if (mDate.length() == 2) {
            mDateInput[1] = Integer.parseInt(mDate.substring(0, 1));
            mDateInput[0] = Integer.parseInt(mDate.substring(1, 2));
        } else if (mDate.length() == 1) {
            mDateInput[1] = 0;
            mDateInput[0] = Integer.parseInt(mDate.substring(0, 1));
        } else {
            mDateInput[1] = 0;
            mDateInput[0] = 0;
        }
    }

    public void setDate(int year, int monthOfYear, int dayOfMonth) {
        mMonthInput = monthOfYear;
        mYearInput[3] = year / 1000;
        mYearInput[2] = (year % 1000) / 100;
        mYearInput[1] = (year % 100) / 10;
        mYearInput[0] = year % 10;
        if (year >= 1000) {
            mYearInputPointer = 3;
        } else if (year >= 100) {
            mYearInputPointer = 2;
        } else if (year >= 10) {
            mYearInputPointer = 1;
        } else if (year > 0) {
            mYearInputPointer = 0;
        }
        mDateInput[1] = dayOfMonth / 10;
        mDateInput[0] = dayOfMonth % 10;
        if (dayOfMonth >= 10) {
            mDateInputPointer = 1;
        } else if (dayOfMonth > 0) {
            mDateInputPointer = 0;
        }
        for (int i = 0; i < mDateFormatOrder.length; i++) {
            char c = mDateFormatOrder[i];
            if (c == DateFormat.MONTH && monthOfYear == -1) {
                mKeyboardPager.setCurrentItem(i, true);
                break;
            } else if (c == DateFormat.DATE && dayOfMonth <= 0) {
                mKeyboardPager.setCurrentItem(i, true);
                break;
            } else if (c == DateFormat.YEAR && year <= 0) {
                mKeyboardPager.setCurrentItem(i, true);
                break;
            }
        }
        updateKeypad();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable parcel = super.onSaveInstanceState();
        final SavedState state = new SavedState(parcel);
        state.mMonthInput = mMonthInput;
        state.mDateInput = mDateInput;
        state.mDateInputPointer = mDateInputPointer;
        state.mYearInput = mYearInput;
        state.mYearInputPointer = mYearInputPointer;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mDateInputPointer = savedState.mDateInputPointer;
        mYearInputPointer = savedState.mYearInputPointer;
        mDateInput = savedState.mDateInput;
        mYearInput = savedState.mYearInput;
        if (mDateInput == null) {
            mDateInput = new int[mDateInputSize];
            mDateInputPointer = -1;
        }
        if (mYearInput == null) {
            mYearInput = new int[mYearInputSize];
            mYearInputPointer = -1;
        }
        mMonthInput = savedState.mMonthInput;
        updateKeypad();
    }

    private static class SavedState extends BaseSavedState {

        int mDateInputPointer;
        int mYearInputPointer;
        int[] mDateInput;
        int[] mYearInput;
        int mMonthInput;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mDateInputPointer = in.readInt();
            mYearInputPointer = in.readInt();
            in.readIntArray(mDateInput);
            in.readIntArray(mYearInput);
            mMonthInput = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mDateInputPointer);
            dest.writeInt(mYearInputPointer);
            dest.writeIntArray(mDateInput);
            dest.writeIntArray(mYearInput);
            dest.writeInt(mMonthInput);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
