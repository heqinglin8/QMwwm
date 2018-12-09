package com.qinglin.qmvvm.network;


public abstract class BaseTask<REQUEST extends BaseTask.RequestValues, RESPONSE extends BaseTask.ResponseValue> implements IBaseTask {
    private boolean isCancelTask = false;
    private REQUEST mRequestValues;

    private Callback<RESPONSE> mTaskCallback;

    public void setRequestValues(REQUEST requestValues) {
        mRequestValues = requestValues;
    }

    public REQUEST getRequestValues() {
        return mRequestValues;
    }

    public void setTaskCallback(Callback<RESPONSE> callback) {
        mTaskCallback = callback;
    }

    public void setCancelTask(boolean cancelTask) {
        isCancelTask = cancelTask;
    }

    public boolean isCancelTask() {
        return isCancelTask;
    }

    @Override
    public void run() {
        executeTask(mRequestValues);
    }

    protected abstract void executeTask(REQUEST requestValues);

    public void doWhat(RESPONSE doWhat){
        mTaskCallback.doWhat(doWhat);
    }

    public void onSuccess(RESPONSE response){
        mTaskCallback.onSuccess(response);
    }

    public void onError(RESPONSE response,String errorMsg){
        mTaskCallback.onError(response,errorMsg);
    }

    /**
     * 任务数据参数值。
     */
    public interface RequestValues {
    }

    /**
     * 任务结果返回值。
     */
    public interface ResponseValue {
    }

}
