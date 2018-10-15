package org.upesacm.acmacmw.listener;

import android.view.View;

public interface OnRecyclerItemSelectListener<E> {
    public void onRecyclerItemSelect(View view, E dataItem, int position);
}
