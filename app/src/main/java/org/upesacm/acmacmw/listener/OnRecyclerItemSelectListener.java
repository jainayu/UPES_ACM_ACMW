package org.upesacm.acmacmw.listener;

import android.view.View;

public interface OnRecyclerItemSelectListener<E> {
    void onRecyclerItemSelect(View view, E dataItem, int position);

    void onRecyclerAddToCartClick(E event);
}
