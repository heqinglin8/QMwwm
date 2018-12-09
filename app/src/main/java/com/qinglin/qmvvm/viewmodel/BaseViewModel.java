package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.IModel;

import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel<MODEL extends IModel> extends ViewModel {

    /**
     * 此属性为创建时唯一生成的tag标识，每次创建均会不同，不用担心生成相同的标识。
     */
    protected final String mPresenterTag;
    private MODEL mModel;

    public BaseViewModel() {
        mPresenterTag = getClass().getSimpleName() + "/" + System.nanoTime() + "/" + (int) (Math.random() * Integer.MAX_VALUE);
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
