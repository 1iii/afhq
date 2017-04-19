package com.example.afhq.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;

import com.example.afhq.R;
import com.example.afhq.utils.DensityUtils;

public class HealthView extends View {
	  private static String TAG = "QQHealthView";

      private int mWidth;//�Զ���View��
      private int mHeight;//�Զ���View��
      private int mBackgroundCorner;//�����ĽǵĻ���

      private int mArcCenterX;
      private int mArcCenterY;
      private RectF mArcRect;

      private Paint mBackgroundPaint;
      private Paint mArcPaint;//�����满�ߵĻ���
      private Paint mTextPaint;
      private Paint mDashLinePaint;//���ߵĻ���
      private Paint mBarPaint;//�����Ļ���

      private int[] mSteps;
      private float mRatio;

      private Context mContext;

      private int mDefaultThemeColor;//����ɫ
      private int mDefaultUpBackgroundColor;//�ϲ�Ĭ�ϵı���ɫ

      private int mThemeColor;
      private int mUpBackgroundColor;
      private float mArcWidth;
      private float mBarWidth;
      private int mMaxStep;
      private int mAverageStep;
      private int mTotalSteps;

      private int step = 25;
      private float percent = 0.5f;

      private Paint mAvatarPaint;

      public HealthView(Context context) {
          this(context, null);
      }

      public HealthView(Context context, AttributeSet attrs) {
          this(context, attrs, 0);
      }

      public HealthView(Context context, AttributeSet attrs, int defStyleAttr) {
          super(context, attrs, defStyleAttr);
          mContext = context;
          init();
      }

      private void init() {
          //��������ǹر�Ӳ�����٣���ֹĳЩ4.0���豸������ʾΪʵ�ߵ�����
          //������AndroidManifest.xmlʱ��Application��ǩ����android:hardwareAccelerated=��false��,
          // ��������Ӧ�ö��ر���Ӳ�����٣����߿���������ʾ�����ǣ��ر�Ӳ�����ٶ�������ЩӰ�죬
          setLayerType(View.LAYER_TYPE_SOFTWARE, null);
          //�Զ���View�Ŀ�߱���
          mRatio = 450.f / 525.f;
          //��ʼ��һЩĬ�ϵĲ���
          mBackgroundCorner = DensityUtils.dp2px(mContext, 5);
          mDefaultThemeColor = Color.parseColor("#2EC3FD");
          mDefaultUpBackgroundColor = Color.WHITE;
          mThemeColor = mDefaultThemeColor;
          mUpBackgroundColor = mDefaultUpBackgroundColor;
          mSteps = new int[]{10050, 15280, 8900, 9200, 6500, 5660, 9450};
          calculateSteps();
          //��������
          mBackgroundPaint = new Paint();
          mBackgroundPaint.setAntiAlias(true);
          mBackgroundPaint.setColor(mThemeColor);
          //Բ���Ļ���
          mArcPaint = new Paint();
          mArcPaint.setColor(mThemeColor);//������ɫ
          mArcPaint.setAntiAlias(true);//�����
          mArcPaint.setStyle(Paint.Style.STROKE);//����
          mArcPaint.setDither(true);//������
          mArcPaint.setStrokeJoin(Paint.Join.ROUND);//�ڻ��ʵ����Ӵ���Բ����
          mArcPaint.setStrokeCap(Paint.Cap.ROUND);//�ڻ��ʵ���ʼ����Բ����
          mArcPaint.setPathEffect(new CornerPathEffect(10));//����Ч��
          //���ֻ���
          mTextPaint = new Paint();
          mTextPaint.setAntiAlias(true);
          //���߻���
          mDashLinePaint = new Paint();
          mDashLinePaint.setAntiAlias(true);
          mDashLinePaint.setColor(Color.parseColor("#C1C1C1"));
          mDashLinePaint.setStyle(Paint.Style.STROKE);
          mDashLinePaint.setPathEffect(new DashPathEffect(new float[]{8, 4}, 0));//������
          //��������
          mBarPaint = new Paint();
          mBarPaint.setColor(mThemeColor);
          mBarPaint.setAntiAlias(true);
          mBarPaint.setStrokeCap(Paint.Cap.ROUND);
          //ͷ�񻭱�
          mAvatarPaint = new Paint();
          mAvatarPaint.setAntiAlias(true);

          //���붯��
          AnimatorSet animatorSet = new AnimatorSet();

          //�����Ķ���
          ValueAnimator stepAnimator = ValueAnimator.ofInt(0, mSteps[mSteps.length - 1]);
          stepAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
              @Override
              public void onAnimationUpdate(ValueAnimator animation) {
                  step = (Integer) animation.getAnimatedValue();
                  invalidate();
              }
          });
//        stepAnimator.setDuration(1000);
//        stepAnimator.start();

          //Բ������
          ValueAnimator percentAnimator = ValueAnimator.ofFloat(0, 1);
          percentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
              @Override
              public void onAnimationUpdate(ValueAnimator animation) {
                  percent = (Float) animation.getAnimatedValue();
                  invalidate();
              }
          });
          // percentAnimator.setDuration(1000);
          // percentAnimator.start();
          animatorSet.setDuration(1000);
          animatorSet.playTogether(stepAnimator, percentAnimator);
          animatorSet.start();


      }

      public void setThemeColor(int color) {
          mThemeColor = color;
          mBackgroundPaint.setColor(mThemeColor);
          mArcPaint.setColor(mThemeColor);
          mBarPaint.setColor(mThemeColor);
          invalidate();
      }

      public void setSteps(int[] steps) {
          if (steps == null || steps.length == 0) throw new IllegalArgumentException("�Ƿ�����");
          mSteps = steps;
          calculateSteps();
          invalidate();

      }

      //��ԭʼͼƬת��ΪԲ��ͼƬ
      public Bitmap toRoundBitmap(Bitmap bitmap) {
          int width = bitmap.getWidth();
          int height = bitmap.getHeight();
          int r;
          if (width > height) {
              r = height;
          } else {
              r = width;
          }
          Bitmap backgroundBmp = Bitmap.createBitmap(width,
                  height, Bitmap.Config.ARGB_8888);
          Canvas canvas = new Canvas(backgroundBmp);
          Paint paint = new Paint();
          paint.setAntiAlias(true);
          RectF rect = new RectF(0, 0, r, r);
          BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                  Shader.TileMode.CLAMP);
          paint.setShader(shader);
          canvas.drawRoundRect(rect, r / 2, r / 2, paint);
          return backgroundBmp;
      }

      //���㲽��
      private void calculateSteps() {
          mTotalSteps = 0;
          mMaxStep = 0;
          mAverageStep = 0;
          for (int i = 0; i < mSteps.length; i++) {
              mTotalSteps += mSteps[i];
              if (mMaxStep < mSteps[i]) mMaxStep = mSteps[i];

          }
          mAverageStep = (int) (mTotalSteps * 1.f / mSteps.length);
      }

      //�������²㱳��
      private void drawBelowBackground(int left, int top, int right, int bottom, int radius, Canvas canvas, Paint paint) {
          Path path = new Path();

          path.moveTo(left, top);

          path.lineTo(right - radius, top);
          path.quadTo(right, top, right, top + radius);

          path.lineTo(right, bottom - radius);
          path.quadTo(right, bottom, right - radius, bottom);

          path.lineTo(left + radius, bottom);
          path.quadTo(left, bottom, left, bottom - radius);

          path.lineTo(left, top + radius);
          path.quadTo(left, top, left + radius, top);

          canvas.drawPath(path, paint);
      }

      //�����ϲ㱳��
      private void drawUpBackground(int left, int top, int right, int bottom, int radius, Canvas canvas, Paint paint) {
          Path path = new Path();

          path.moveTo(left, top);

          path.lineTo(right - radius, top);
          path.quadTo(right, top, right, top + radius);

          path.lineTo(right, bottom);

          path.lineTo(left, bottom);

          path.lineTo(left, top + radius);
          path.quadTo(left, top, left + radius, top);

          canvas.drawPath(path, paint);
      }

      @Override
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          int defaultWidth = Integer.MAX_VALUE;
          int width;
          int height;
          int widthMode = MeasureSpec.getMode(widthMeasureSpec);
          int widthSize = MeasureSpec.getSize(widthMeasureSpec);
          //  int heightMode = MeasureSpec.getMode(heightMeasureSpec);
          //  int heightSize = MeasureSpec.getSize(heightMeasureSpec);
          if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
              width = widthSize;
          } else {
              width = defaultWidth;
          }
          int defaultHeight = (int) (width * 1.f / mRatio);
          height = defaultHeight;
          setMeasuredDimension(width, height);
          Log.i(TAG, "width:" + width + "| height:" + height);
      }

      @Override
      protected void onSizeChanged(int w, int h, int oldw, int oldh) {
          super.onSizeChanged(w, h, oldw, oldh);
          mWidth = w;
          mHeight = h;

          mArcCenterX = (int) (mWidth / 2.f);
          mArcCenterY = (int) (160.f / 525.f * mHeight);
          mArcRect = new RectF();
          mArcRect.left = mArcCenterX - 125.f / 450.f * mWidth;
          mArcRect.top = mArcCenterY - 125.f / 525.f * mHeight;
          mArcRect.right = mArcCenterX + 125.f / 450.f * mWidth;
          mArcRect.bottom = mArcCenterY + 125.f / 525.f * mHeight;

          mArcWidth = 20.f / 450.f * mWidth;
          mBarWidth = 16.f / 450.f * mWidth;

          //���ʵĿ��һ��Ҫ���������ò�������Ӧ
          mArcPaint.setStrokeWidth(mArcWidth);
          mBarPaint.setStrokeWidth(mBarWidth);
      }

      @SuppressLint("DrawAllocation")
      @Override
      protected void onDraw(Canvas canvas) {
          float startX;
          float startY;
          float stopX;
          float stopY;
          float xPos;
          float yPos;
          //1.�������²㱳��
          mBackgroundPaint.setColor(mThemeColor);
          drawBelowBackground(0, 0, mWidth, mHeight, mBackgroundCorner, canvas, mBackgroundPaint);
          //2.��������ı���
          mBackgroundPaint.setColor(mUpBackgroundColor);
          drawUpBackground(0, 0, mWidth, mWidth, mBackgroundCorner, canvas, mBackgroundPaint);
          //3.����Բ��
          canvas.drawArc(mArcRect, 120, 300 * percent, false, mArcPaint);
          //4.����Բ�����������
          xPos = mArcCenterX;
          yPos = (int) (mArcCenterY - 40.f / 525.f * mHeight);
          mTextPaint.setTextAlign(Paint.Align.CENTER);
          mTextPaint.setTextSize(15.f / 450.f * mWidth);
          mTextPaint.setColor(Color.parseColor("#C1C1C1"));
         // canvas.drawText("����22:50������", xPos, yPos, mTextPaint);
          mTextPaint.setTextAlign(Paint.Align.CENTER);
          mTextPaint.setTextSize(42.f / 450.f * mWidth);
          mTextPaint.setColor(mThemeColor);
         // canvas.drawText(step + "", mArcCenterX, mArcCenterY, mTextPaint);
          yPos = (int) (mArcCenterY + 50.f / 525.f * mHeight);
          mTextPaint.setColor(Color.parseColor("#C1C1C1"));
          mTextPaint.setTextSize(13.f / 450.f * mWidth);
          canvas.drawText("����ƽ��5620��", mArcCenterX, yPos, mTextPaint);
          xPos = (int) (mArcCenterX - 35.f / 450.f * mWidth);
          yPos = (int) (mArcCenterY + 120.f / 525.f * mHeight);
          canvas.drawText("��", xPos, yPos, mTextPaint);
          xPos = (int) (mArcCenterX + 35.f / 450.f * mWidth);
          canvas.drawText("��", xPos, yPos, mTextPaint);
          mTextPaint.setColor(mThemeColor);
          mTextPaint.setTextSize(24.f / 450.f * mWidth);
          canvas.drawText("10", mArcCenterX, yPos, mTextPaint);
          //5.����Բ�����������
          xPos = (int) (25.f / 450.f * mWidth);
          yPos = (int) (330.f / 525.f * mHeight);
          mTextPaint.setTextAlign(Paint.Align.LEFT);
          mTextPaint.setColor(Color.parseColor("#C1C1C1"));
          mTextPaint.setTextSize(12.f / 450.f * mWidth);
          canvas.drawText("���7��", xPos, yPos, mTextPaint);
          xPos = (int) ((450.f - 25.f) / 450.f * mWidth);
          yPos = (int) (330.f / 525.f * mHeight);
          mTextPaint.setTextAlign(Paint.Align.RIGHT);
          mTextPaint.setColor(Color.parseColor("#C1C1C1"));
          mTextPaint.setTextSize(12.f / 450.f * mWidth);
          canvas.drawText("ƽ��" + mAverageStep + "��/��", xPos, yPos, mTextPaint);
          //6.������
          xPos = (int) (25.f / 450.f * mWidth);
          yPos = (int) (352.f / 525.f * mHeight);
          stopX = xPos + (450.f - 50.f) / 450.f * mWidth;
          stopY = yPos;
          canvas.drawLine(xPos, yPos, stopX, stopY, mDashLinePaint);
          //7.�������������
          mTextPaint.setTextAlign(Paint.Align.CENTER);
          mTextPaint.setTextSize(10.f / 450.f * mWidth);
          startY = 388.f / 525.f * mHeight;

          for (int i = 0; i < mSteps.length; i++) {
              float barHeight = mSteps[i] * 1.f / mAverageStep * 35.f / 525.f * mHeight;
              startX = 55.f / 450.f * mWidth + i * (57.f / 450.f * mWidth);
              stopX = startX;
              stopY = startY - barHeight;
              if (mSteps[i] < mAverageStep) mBarPaint.setColor(Color.parseColor("#C1C1C1"));
              else mBarPaint.setColor(mThemeColor);
              canvas.drawLine(startX, startY, stopX, stopY, mBarPaint);
              canvas.drawText("0" + (i + 1) + "��", startX, startY + 25.f / 525.f * mHeight, mTextPaint);
          }
          //8.������ɫ��������Լ�ͷ��
          yPos = (mHeight - mWidth) / 2.f + mWidth + 20.f / 450.f * mWidth / 2;
          xPos = 80.f / 450.f * mWidth;
          mTextPaint.setColor(Color.WHITE);
          mTextPaint.setTextSize(20.f / 450.f * mWidth);
          mTextPaint.setTextAlign(Paint.Align.LEFT);

          canvas.drawText("double����ý��չھ�", xPos, yPos, mTextPaint);

          Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app);
          Rect dst = new Rect();//ͷ����Ƶ��ľ���
          int rectWidth = (int) (30.f / 525.f * mHeight);//���εĿ��
          dst.top = (int) ((mHeight - mWidth) / 2.f + mWidth - rectWidth / 2.f);
          dst.left = (int) (xPos - 40.f / 450 * mWidth);
          dst.bottom = (int) ((mHeight - mWidth) / 2.f + mWidth + rectWidth / 2.f);
          dst.right = (int) (xPos - 10.f / 450 * mWidth);
          bitmap = toRoundBitmap(bitmap);
          canvas.drawBitmap(bitmap, null, dst, mAvatarPaint);//����ͷ��

          xPos = 425.f / 450.f * mWidth;
          mTextPaint.setTextAlign(Paint.Align.RIGHT);
          mTextPaint.setTextSize(15.f / 450.f * mWidth);
          canvas.drawText("�鿴 >", xPos, yPos, mTextPaint);
      }

      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouchEvent(MotionEvent event) {

          RectF rectF = new RectF();
          rectF.top = mWidth;
          rectF.left = 380.f / 450.f * mWidth;
          rectF.right = mWidth;
          rectF.bottom = mHeight;
          if (rectF.contains(event.getX(), event.getY())) {//��ǰ��������������½ǵķ�Χ��
              //���������������¼��ļ���
            Toast.makeText(mContext, "������", Toast.LENGTH_LONG).show();
              return false;
          } else {
              return super.onTouchEvent(event);
          }
      }
}
