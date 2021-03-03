package com.lpi.compagnonderoute;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.compagnonderoute.report.Report;

/***************************************************************************************************
 * Controle personnalisé: switch On/Off
 * Attributs: voir @values/attrs_custom_on_off_switch.xml
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
	int _interpolator;
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

	// Listener, pour prevenir des changements d'etat
	private OnCheckedChangeListener _listener;

	public CustomOnOffSwitch(Context context)
	{
		super(context);
		init(null, 0);
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

	/***********************************************************************************************
	 * Lecture des attributs donnés dans le XML et preparation des objets graphiques
	 * @param attrs
	 * @param defStyle
	 ***********************************************************************************************/
	private void init(AttributeSet attrs, int defStyle)
	{
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomOnOffSwitch, defStyle, 0);
		_interpolator = a.getInt(R.styleable.CustomOnOffSwitch_CCOS_interpolateur, 0);
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
		_textPaint.setAntiAlias(true);
		_textPaint.setElegantTextHeight(true);
		_textPaint.setColor(couleurTexte);

		a.recycle();
		retaille();
	}

	/***********************************************************************************************
	 * Charge un drawable
	 * @param a
	 * @param attr
	 * @return
	 ***********************************************************************************************/
	@Nullable static private Drawable loadDrawable(final TypedArray a, final int attr)
	{
		if (a.hasValue(attr))
		{
			return a.getDrawable(attr);
		}

		return null;
	}

	/***********************************************************************************************
	 * Recalcule les tailles en cas de redimensionnement
	 ***********************************************************************************************/
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

	/***********************************************************************************************
	 * Affichage du controle
	 * @param canvas
	 ***********************************************************************************************/
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
	 * Dessine un texte centré dans un rectangle
	 * @param canvas
	 * @param paint
	 * @param text
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 ***********************************************************************************************/
	private static void afficheTextCentre(@NonNull Canvas canvas, @NonNull Paint
			paint, @NonNull String text, int left, int top, int right, int bottom)
	{
		Rect r = new Rect(left, top, right, bottom);
		final int cHeight = r.height();
		final int cWidth = r.width();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.getTextBounds(text, 0, text.length(), r);
		final float x = cWidth / 2f - r.width() / 2f - r.left;
		final float y = cHeight / 2f + r.height() / 2f - r.bottom;
		canvas.drawText(text, left + x, top + y, paint);
	}

	/***
	 * Changement de taille
	 * @param w
	 * @param h
	 * @param oldw
	 * @param oldh
	 */
	@Override protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		retaille();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		Report.getInstance(getContext()).log(Report.DEBUG, "onToucheEvent " + event.getAction());
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			performClick();
			return true;
		}
		return true;
	}

	/***
	 * Reagir au click sur le controle: changer la valeur et animer le controle
	 */
	@Override
	public boolean performClick()
	{
		// Changement de la valeur
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

		if (_animator == null)
		{
			// Pas d'animation en cours
			_animator = ValueAnimator.ofFloat(valeurdepart, valeurcible);
			_animator.setDuration(_dureeAnimation);
			PropertyValuesHolder valeur = PropertyValuesHolder.ofFloat(PROPERTY_VALEUR, valeurdepart, valeurcible);
			_animator.setValues(valeur);
			_animator.setInterpolator(getInterpolateur());
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

		if (_listener != null)
			_listener.onCheckedChanged(_on);

		invalidate();
		return false;
	}

	/***
	 * Creer le type d'interpolateur en fonction de l'attribut
	 * @return
	 */
	private @NonNull TimeInterpolator getInterpolateur()
	{
		switch (_interpolator)
		{
			case 0:
				return new AccelerateDecelerateInterpolator();
			case 1:
				return new AccelerateInterpolator();
			case 2:
				return new AnticipateInterpolator();
			case 3:
				return new BounceInterpolator();
			case 5:
				return new OvershootInterpolator();
			case 4:
			default:
				return new LinearInterpolator();
		}
	}

	/***
	 * Donner le listener qui sera averti des changement du controle
	 * @param listener Le listener
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

	interface OnCheckedChangeListener
	{
		void onCheckedChanged(boolean checked);
	}
}