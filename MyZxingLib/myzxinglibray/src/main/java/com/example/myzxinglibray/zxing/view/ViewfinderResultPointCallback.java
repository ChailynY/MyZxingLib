package com.example.myzxinglibray.zxing.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

/**
 * Created by yqy on 2017/6/27.
 */

public class ViewfinderResultPointCallback implements ResultPointCallback {
    private  ViewfinderView viewfinderView;

    public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint resultPoint) {
        if(viewfinderView!=null)
            viewfinderView.addPossibleResultPoint(resultPoint);
    }
}
