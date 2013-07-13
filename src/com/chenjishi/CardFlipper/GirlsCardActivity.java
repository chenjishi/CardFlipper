package com.chenjishi.CardFlipper;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GirlsCardActivity extends CardsActivity {
    private int[] mDataArray;

    @Override
    protected void createCards(int index, FrameLayout.LayoutParams layoutParams) {
        FrameLayout card = (FrameLayout) mInflater.inflate(R.layout.card_girl, null);
        card.setLayoutParams(layoutParams);

        if (index >= 0) {
            ImageView frontImage = (ImageView) card.findViewById(R.id.img_front);
            frontImage.setImageResource(mDataArray[index]);

            mContainer.addView(card);
        } else {
            mContainer.addView(card);
            card.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void loadData() {
        mDataArray = new int[] {
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6,
                R.drawable.image7,
                R.drawable.image8
        };

        mCardsCount = mDataArray.length;
        mCurrentCardId = mCardsCount - 1;
        setupView();
    }

    @Override
    protected FrameLayout getCardView() {
        FrameLayout card = (FrameLayout) mInflater.inflate(R.layout.card_girl, null);

        if (mCurrentCardId - INIT_CARD_COUNT >= 0) {
            ImageView frontImage = (ImageView) card.findViewById(R.id.img_front);
            frontImage.setImageResource(mDataArray[mCurrentCardId - INIT_CARD_COUNT]);
        } else {
            card.setVisibility(View.INVISIBLE);
        }

        return card;
    }

    @Override
    protected void handleFlipBack() {
        int count = mContainer.getChildCount();
        View view = mContainer.getChildAt(count - 1);

        ImageView backImg = (ImageView) view.findViewById(R.id.img_back);
        backImg.setImageResource(mDataArray[mCurrentCardId]);
    }

    @Override
    protected void setFrontView(View v, int index) {
        ImageView frontImage = (ImageView) v.findViewById(R.id.img_front);
        frontImage.setImageResource(mDataArray[index]);
    }
}
