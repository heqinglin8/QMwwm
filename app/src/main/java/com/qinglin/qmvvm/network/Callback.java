package com.qinglin.qmvvm.network;

/**
 * Created by erlin on 2017/5/27.
 *
 * RESPONSE : 在这儿并不明确是什么对象，当具体使用时RESPONSE才是确定的。
 */

public interface Callback<RESPONSE> {
    /**
     * 任务执行成功时调用onSuccess(RESPONSE response),与onError(RESPONSE error, String errorMsg)方法互斥。
     * @param response
     */
    void onSuccess(RESPONSE response);

    /**
     * 任务执行异常时调用onError(RESPONSE error, String errorMsg)，与onSuccess(RESPONSE response)方法互斥。
     * @param error
     * @param errorMsg
     */
    void onError(RESPONSE error, String errorMsg);

    /**
     * 任务执行过程中如果有需要传递出的状态，可以调用doWhat(RESPONSE doWhat)方法，该方法在用户指定的线程执行。
     * @param doWhat
     */
    void doWhat(RESPONSE doWhat);
}
