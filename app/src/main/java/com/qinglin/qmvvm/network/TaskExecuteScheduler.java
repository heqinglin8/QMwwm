package com.qinglin.qmvvm.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import androidx.annotation.IntDef;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 任务执行调度器，用于执行各种复杂的任务计算，而不必担心高效能损失。
 */
public final class TaskExecuteScheduler {
    @IntDef({SINGLE, COMPUTATION, IO, TRAMPOLINE, NEW_THREAD, MAIN_THREAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskSchedulers {
    }

    /**
     * 使用定长为1 的线程池（newScheduledThreadPool(1)），重复利用这个线程
     */
    public static final int SINGLE = 0;

    /**
     * 计算所使用的Scheduler。这个计算是指CPU密集型计算，即不会被I/O等操作限制性的操作， 例如图形的计算。这个Sheduler使用的固定的线程池，大小为cpu核数。不要把I/O放在computation中，
     * 否则I/O操作等待时间会浪费cpu。用于计算任务，如事件循环或和回调处理， 不要用于IO操作(IO操作请使用Schedulers.io())；默认线程数等于处理器 
     */
    public static final int COMPUTATION = 1;

    /**
     * I/O操作(读写文件、读写数据库、网络信息交互等)所使用的Scheduler，行为模式和newThread()差不多， 区别在于io()的内部实现是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下，
     * io()比newThread()更有效率。不要把计算工作放在io(),可以避免穿件不必要的线程。
     */
    public static final int IO = 2;

    /**
     * 直接在当前线程运行（继续上一个操作中，最后处理完成的数据源所处线程，并不一定是主线程），相当于不指定线程
     */
    public static final int TRAMPOLINE = 3;

    /**
     * 总是启动新线程，在新的线程中执行操作
     */
    public static final int NEW_THREAD = 4;

    /**
     * 操作将在Android主线程中执行
     */
    public static final int MAIN_THREAD = 5;
    private static TaskExecuteScheduler INSTANCE;

    private HashMap<String, HashMap<BaseTask, Disposable>> mCancelTask;

    private TaskExecuteScheduler() {
        mCancelTask = new HashMap<>();
    }

    /**
     * IO 密集型计算方式，用于网络加载，数据查询，文件操作等和io操作的相关计算。
     *
     * @param presenterTag 任务执行所在的Presenter标记，用于Presenter退出时，取消Presenter中未执行或正在执行的任务
     * @param task         任务核心计算
     * @param values       任务执行请求参数
     * @param callback     核心任务结果的处理回调,在Android主线程中
     */
    public final <REQUEST extends BaseTask.RequestValues, RESPONSE extends BaseTask.ResponseValue> void execute(final String presenterTag,
                                                                                                                final BaseTask<REQUEST, RESPONSE> task,
                                                                                                                final REQUEST values,
                                                                                                                final QResponseCallback<RESPONSE> callback) {

        execute(presenterTag, task, values, callback, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    /**
     * CPU 密集型计算方式，用于计算复杂任务，如图形图像处理。能够执行的任务数为当前CPU的核心数
     *
     * @param presenterTag 任务执行所在的Presenter标记，用于Presenter退出时，取消Presenter中未执行或正在执行的任务
     * @param task         任务核心计算
     * @param values       任务执行请求参数
     * @param callback     核心任务结果的处理回调,在Android主线程中
     */
    public final <REQUEST extends BaseTask.RequestValues, RESPONSE extends BaseTask.ResponseValue> void executeCPU(final String presenterTag,
                                                                                                                   final BaseTask<REQUEST, RESPONSE> task,
                                                                                                                   final REQUEST values,
                                                                                                                   final QResponseCallback<RESPONSE> callback) {

        execute(presenterTag, task, values, callback, Schedulers.computation(), AndroidSchedulers.mainThread());
    }

    /**
     * 更为灵活的任务调度，可以指定复杂任务执行线程，可以指定复杂任务结果处理线程。
     *
     * @param presenterTag       任务执行所在的Presenter标记，用于Presenter退出时，取消Presenter中未执行或正在执行的任务
     * @param task               任务核心计算
     * @param values             任务执行请求参数
     * @param callback           核心任务结果的处理回调
     * @param subscribeScheduler 核心任务计算线程
     * @param observerScheduler  核心任务结果回调线程
     */
    public final <REQUEST extends BaseTask.RequestValues, RESPONSE extends BaseTask.ResponseValue> void execute(final String presenterTag,
                                                                                                                final BaseTask<REQUEST, RESPONSE> task,
                                                                                                                final REQUEST values,
                                                                                                                final QResponseCallback<RESPONSE> callback,
                                                                                                                @TaskSchedulers int subscribeScheduler,
                                                                                                                @TaskSchedulers int observerScheduler) {
        Scheduler subscribe = getTaskScheduler(subscribeScheduler);
        Scheduler observer = getTaskScheduler(observerScheduler);

        execute(presenterTag, task, values, callback, subscribe, observer);
    }


    private <REQUEST extends BaseTask.RequestValues, RESPONSE extends BaseTask.ResponseValue> void execute(final String presenterTag,
                                                                                                           final BaseTask<REQUEST, RESPONSE> task,
                                                                                                           final REQUEST values,
                                                                                                           final QResponseCallback<RESPONSE> callback,
                                                                                                           Scheduler subscribeScheduler,
                                                                                                           Scheduler observerScheduler) {

        ObserverImpl observer = new ObserverImpl(presenterTag, task, callback);
        ObservableOnSubscribeImpl onSubscribe = new ObservableOnSubscribeImpl(task, values, observer);

        Observable.create(onSubscribe)
                .subscribeOn(subscribeScheduler)
                .observeOn(observerScheduler)
                .subscribe(observer);
    }

    private Scheduler getTaskScheduler(@TaskSchedulers int scheduler) {
        Scheduler result = null;
        switch (scheduler) {
            case SINGLE:
                result = Schedulers.single();
                break;
            case COMPUTATION:
                result = Schedulers.computation();
                break;
            case IO:
                result = Schedulers.io();
                break;
            case TRAMPOLINE:
                result = Schedulers.trampoline();
                break;
            case NEW_THREAD:
                result = Schedulers.newThread();
                break;
            case MAIN_THREAD:
                result = AndroidSchedulers.mainThread();
                break;
        }
        return result;
    }


    /**
     * 将待执行的任务加入到取消任务列表
     */
    private void putCancelTask(String tag, BaseTask task, Disposable disposable) {
        if (tag != null && !tag.isEmpty()) {
            HashMap<BaseTask, Disposable> taskMap = mCancelTask.get(tag);
            if (taskMap == null) {
                taskMap = new HashMap<>();
                taskMap.put(task, disposable);
                mCancelTask.put(tag, taskMap);
            } else {
                taskMap.put(task, disposable);
            }
        }
    }

    /**
     * 忽略task中isCancelTask属性，直接取消未执行或正在执行的任务
     */
    public void cancelTask(String tag, BaseTask task) {
        if (tag != null && !tag.isEmpty()) {
            HashMap<BaseTask, Disposable> taskMap = mCancelTask.get(tag);
            if (taskMap != null) {
                Disposable disposable = taskMap.get(task);
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        }
    }

    /**
     * 取消tag下所task任务属性设置isCancelTask = true的任务。
     */
    public void cancelTask(String tag) {
        if (tag != null && !tag.isEmpty()) {
            HashMap<BaseTask, Disposable> taskMap = mCancelTask.remove(tag);
            if (taskMap != null) {
                Iterator iterator = taskMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<BaseTask, Disposable> entry = (Map.Entry<BaseTask, Disposable>) iterator.next();
                    BaseTask task = entry.getKey();
                    if (task != null && task.isCancelTask()) {
                        Disposable disposable = entry.getValue();
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }
                }
            }
        }
    }

    public static TaskExecuteScheduler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TaskExecuteScheduler();
        }
        return INSTANCE;
    }

    /**
     * 任务执行结果反馈给Observer时的回调，并设置RESPONSE
     */
    private static final class TaskCallBack<RESPONSE> implements QResponseCallback<RESPONSE> {

        private final ObservableEmitter<RESPONSE> mObservableEmitter;
        private final ObserverImpl<RESPONSE> mObserver;

        public TaskCallBack(ObservableEmitter observableEmitter, ObserverImpl observer) {
            mObservableEmitter = observableEmitter;
            this.mObserver = observer;
        }

        @Override
        public void onSuccess(RESPONSE response) {
            mObserver.setResponse(response);
            mObservableEmitter.onComplete();
        }

        @Override
        public void onError(RESPONSE error, String errorMsg) {
            mObserver.setResponse(error);
            mObservableEmitter.onError(new Throwable(errorMsg));
        }

        @Override
        public  void doWhat(RESPONSE doWhat) {
            mObservableEmitter.onNext(doWhat);
        }
    }

    /**
     * RxJava2 ObservableOnSubscribe的接口实现类
     */
    public final class ObservableOnSubscribeImpl implements ObservableOnSubscribe {
        private BaseTask mTask;
        private BaseTask.RequestValues mRequestValues;
        private ObserverImpl mObserver;

        public ObservableOnSubscribeImpl(BaseTask task, BaseTask.RequestValues requestValues, ObserverImpl observer) {
            mTask = task;
            mRequestValues = requestValues;
            mObserver = observer;
        }

        @Override
        public void subscribe(@NonNull ObservableEmitter e) throws Exception {
            mTask.setRequestValues(mRequestValues);
            mTask.setTaskCallback(new TaskCallBack(e, mObserver));
            mTask.run();
        }
    }

    /**
     * RxJava2 Observer的接口实现类
     */
    public final class ObserverImpl<RESPONSE> implements Observer<RESPONSE> {
        private RESPONSE mResponseValues;
        private Disposable mDisposable;

        private BaseTask mTask;
        private String mTaskCancelTag;
        private QResponseCallback<RESPONSE> mPresenterCallback;


        public ObserverImpl(String tag, BaseTask task, QResponseCallback callback) {
            this.mTaskCancelTag = tag;
            this.mTask = task;
            this.mPresenterCallback = callback;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            mDisposable = d;
            putCancelTask(mTaskCancelTag, mTask, mDisposable);
        }


        @Override
        public void onNext(@NonNull RESPONSE doWhat) {
            if(mPresenterCallback != null){
                mPresenterCallback.doWhat(doWhat);
            }
        }

        public void setResponse(RESPONSE response) {
            mResponseValues = response;
        }

        @Override
        public void onError(@NonNull Throwable e) {
            if (mPresenterCallback != null) {
                mPresenterCallback.onError(mResponseValues, e.getMessage());
            }
            mDisposable.dispose();
            cancelTask(mTaskCancelTag, mTask);
        }

        @Override
        public void onComplete() {
            if (mPresenterCallback != null) {
                mPresenterCallback.onSuccess(mResponseValues);
            }

            mDisposable.dispose();
            cancelTask(mTaskCancelTag, mTask);
        }
    }

}
