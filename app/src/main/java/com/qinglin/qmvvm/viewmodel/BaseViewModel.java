package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.IModel;

import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel<MODEL extends IModel> extends ViewModel {

    private MODEL mModel;

    public BaseViewModel() {
        this.mModel = createModel();
        if (this.mModel == null) {
            throw new RuntimeException("createModel为null，请创建一个model现实IModel！");
        }
    }

    abstract MODEL createModel();

    public final MODEL getModel() {
        return mModel;
    }
}
