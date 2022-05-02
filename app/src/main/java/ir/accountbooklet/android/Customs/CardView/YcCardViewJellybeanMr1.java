package ir.accountbooklet.android.Customs.CardView;

class YcCardViewJellybeanMr1 extends YcCardViewEclairMr1 {

    @Override
    public void initStatic() {
        YcRoundRectDrawableWithShadow.sRoundRectHelper = (canvas, bounds, cornerRadius, paint) -> canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
    }
}