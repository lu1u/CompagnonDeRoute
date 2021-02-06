package com.lpi.compagnonderoute;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.report.Report;

/**
 * TODO: document your custom view class.
 */
public class CustomOnOffSwitch extends View
{
	private static final String PROPERTY_VALEUR = "valeur";
	/**
	 * Attributs configurables
	 */
	Drawable _drawableThumb, _drawableTrack;
	String _texteOn, _texteOff;
	TextPaint _textPaint;
	// Drawables On et Off
	Drawable _drawableOn, _drawableOff;
	float _tailleDrawable;
	float _paddingThumb;
	int _dureeAnimation;
	float _paddingDrawable;
	/***
	 * Memorisation des dimensions
	 */
	int _paddingLeft;
	int _paddingTop;
	int _paddingRight;
	int _paddingBottom;
	int _contentWidth;
	int _contentHeight;
	float _hauteurTrack;
	float _largeurTrack;
	float _largeurThumb;
	float _hauteurThumb;
	float _milieuYThumb;
	int _topDrawable;
	int _bottomDrawable;
	// Animation
	ValueAnimator _animator;
	float _valeurADessiner;
	boolean _on = false;
	private float _tailleThumb;
	private OnCheckedChangeListener _listener;

	public CustomOnOffSwitch(Context context)
	{
		super(context);
		init(null, 0);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomOnOffSwitch, defStyle, 0);

		_tailleThumb = a.getFraction(R.styleable.CustomOnOffSwitch_CCOS_thumbSize, 1, 1, 0.5f);
		_texteOn = a.getString(R.styleable.CustomOnOffSwitch_COOS_texteOn);
		_texteOff = a.getString(R.styleable.CustomOnOffSwitch_COOS_texteOff);
		_dureeAnimation = a.getInt(R.styleable.CustomOnOffSwitch_CCOS_dureeAnimation, 500);

		_on = a.getBoolean(R.styleable.CustomOnOffSwitch_CCOS_on, false);
		if (_on)
			_valeurADessiner = 0f;
		else
			_valeurADessiner = 1f;

		_drawableThumb = loadDrawable(a, R.styleable.CustomOnOffSwitch_COOS_drawableThumb);
		_drawableTrack = loadDrawable(a, R.styleable.CustomOnOffSwitch_COOS_drawableTrack);

		_drawableOn = loadDrawable(a, R.styleable.CustomOnOffSwitch_COOS_drawableOn);
		_drawableOff = loadDrawable(a, R.styleable.CustomOnOffSwitch_COOS_drawableOff);
		_tailleDrawable = a.getDimension(R.styleable.CustomOnOffSwitch_COOS_tailleDrawable, 10);

		_paddingThumb = a.getDimension(R.styleable.CustomOnOffSwitch_COOS_paddingThumb, 10);
		_paddingDrawable = a.getDimension(R.styleable.CustomOnOffSwitch_COOS_paddingDrawable, 10);
		float tailleTexte = a.getDimension(R.styleable.CustomOnOffSwitch_COOS_tailleTexte, 10);
		int couleurTexte = a.getColor(R.styleable.CustomOnOffSwitch_COOS_couleurTexte, Color.WHITE);
		_textPaint = new TextPaint();
		_textPaint.setTextSize(tailleTexte);
		_textPaint.setColor(couleurTexte);

		a.recycle();
		retaille();
	}

	private Drawable loadDrawable(final TypedArray a, final int attr)
	{
		if (a.hasValue(attr))
		{
			Drawable d = a.getDrawable(attr);
			if (d != null)
				d.setCallback(this);
			return d;
		}

		return null;
	}

	public CustomOnOffSwitch(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public CustomOnOffSwitch(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	@Override protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		retaille();
	}

	private void retaille()
	{
		_paddingLeft = getPaddingLeft();
		_paddingTop = getPaddingTop();
		_paddingRight = getPaddingRight();
		_paddingBottom = getPaddingBottom();

		_contentWidth = getWidth() - _paddingLeft - _paddingRight;
		_contentHeight = getHeight() - _paddingTop - _paddingBottom;
		_hauteurTrack = _paddingTop + _contentHeight;
		_largeurTrack = _contentWidth;

		_largeurThumb = (getWidth() - 2 * (_paddingLeft + _paddingRight)) * _tailleThumb;
		_hauteurThumb = (getHeight() - (_paddingTop + _paddingBottom));

		_milieuYThumb = (_paddingTop + _hauteurTrack) / 2.0f;
		_topDrawable = (int) (_milieuYThumb - _tailleDrawable / 2.0f);
		_bottomDrawable = (int) (_topDrawable + _tailleDrawable);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// Dessine le fond (track)
		{
			final int left = _paddingLeft;
			final int top = _paddingTop;
			final int right = _paddingLeft + _contentWidth;
			final int bottom = (int) _hauteurTrack;

			if (_drawableTrack != null)
			{
				_drawableTrack.setBounds(left, top, right, bottom);
				_drawableTrack.draw(canvas);
			}
			else
			{
				Paint paint = new Paint();
				paint.setColor(getResources().getColor(R.color.Accent));
				canvas.drawRoundRect(left, top, right, bottom, 16, 16, paint);
			}
		}

		// Icone pour ON
		if (_drawableOn != null)
		{
			int right = (int) (_paddingLeft + _contentWidth - _paddingDrawable);
			float left = right - _tailleDrawable;
			_drawableOn.setBounds((int) left, _topDrawable, right, _bottomDrawable);
			_drawableOn.draw(canvas);

		}

		// Icone pour OFF
		if (_drawableOff != null)
		{
			int left = (int) (_paddingLeft + _paddingDrawable);
			int right = (int) (left + _tailleDrawable);
			_drawableOff.setBounds((int) left, _topDrawable, right, _bottomDrawable);
			_drawableOff.draw(canvas);

		}

		// Dessine le curseur (thumb)
		{
			final float decalage = (_largeurTrack - _largeurThumb - (_paddingLeft + _paddingRight + _paddingThumb * 2)) * _valeurADessiner;
			final int left = (int) (decalage + _paddingLeft + _paddingThumb);
			final int right = (int) (left + _largeurThumb);
			final int top = (int) (_paddingTop + _paddingThumb);
			final int bottom = (int) (_hauteurThumb - _paddingThumb);

			if (_drawableThumb != null)
			{
				_drawableThumb.setBounds(left, top, right, bottom);
				_drawableThumb.draw(canvas);
			}
			else
			{
				Paint paint = new Paint();
				paint.setColor(getResources().getColor(R.color.AccentLight));
				final float rayonThumb = 16 * _hauteurThumb / _hauteurTrack;
				canvas.drawRoundRect(left, top, right, bottom, rayonThumb, rayonThumb, paint);
			}

			String message;
			if (_on)
				message = _texteOn;
			else
				message = _texteOff;

			if (message != null)
				afficheTextCentre(canvas, _textPaint, message, left, top, right, bottom);
		}
	}

	/***********************************************************************************************
	 * Afficher un texte centré
	 * @param canvas
	 * @param paint
	 * @param text
	 */
	private static void afficheTextCentre(@NonNull Canvas canvas, @NonNull Paint
			paint, @NonNull String text, int left, int top, int right, int bottom)
	{
		Rect r = new Rect(left, top, right, bottom);
		int cHeight = r.height();
		int cWidth = r.width();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.getTextBounds(text, 0, text.length(), r);
		float x = cWidth / 2f - r.width() / 2f - r.left;
		float y = cHeight / 2f + r.height() / 2f - r.bottom;
		canvas.drawText(text, left + x, top + y, paint);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		Report.getInstance(getContext()).log(Report.DEBUG, "onToucheEvent " + event.getAction());
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			click();
			return true;
		}
		return true;
	}

	/***
	 * Reagir au click sur le controle: changer la valeur et animer le controle
	 */
	public void click()
	{
		if (_animator != null)
			// Animation deja en cours
			return;

		float valeurdepart, valeurcible;
		if (_on)
		{
			_on = false;
			valeurdepart = 0;
			valeurcible = 1;
		}
		else
		{
			_on = true;
			valeurdepart = 1;
			valeurcible = 0;
		}

		_animator = ValueAnimator.ofFloat(valeurdepart, valeurcible);
		_animator.setDuration(_dureeAnimation);
		PropertyValuesHolder valeur = PropertyValuesHolder.ofFloat(PROPERTY_VALEUR, valeurdepart, valeurcible);
		_animator.setValues(valeur);
		_animator.setInterpolator(new AccelerateDecelerateInterpolator());
		_animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(@NonNull ValueAnimator animation)
			{
				try
				{
					_valeurADessiner = (float) animation.getAnimatedValue(PROPERTY_VALEUR);
					invalidate();
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});

		_animator.addListener(new Animator.AnimatorListener()
		{
			@Override public void onAnimationStart(final Animator animator)
			{

			}

			@Override public void onAnimationEnd(final Animator animator)
			{
				_animator = null;
				if (_on)
					_valeurADessiner = 0f;
				else
					_valeurADessiner = 1f;
				invalidate();

				if (_listener != null)
					_listener.onCheckedChanged(_on);
			}

			@Override public void onAnimationCancel(final Animator animator)
			{

			}

			@Override public void onAnimationRepeat(final Animator animator)
			{

			}
		});
		_animator.start();
	}

	/***
	 * Donner le listener qui sera averti des changement du controle
	 * @param listener
	 */
	public void setOnCheckedChangeListener(@Nullable final OnCheckedChangeListener listener)
	{
		_listener = listener;
	}

	public void setChecked(boolean b)
	{
		_on = b;
		if (_on)
			_valeurADessiner = 0f;
		else
			_valeurADessiner = 1f;

		invalidate();
	}

	public interface OnCheckedChangeListener
	{
		public void onCheckedChanged(boolean checked);
	}
}