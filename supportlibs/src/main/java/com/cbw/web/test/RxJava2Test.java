package com.cbw.web.test;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cbw on 2018/11/21.
 */
public class RxJava2Test {

    public void init() {

        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) { // 最先回调 (比 ObservableEmitter 和 onNext)
                Log.i("bbb", "onSubscribe: ");
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.i("bbb", "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("连载1");
                emitter.onNext("连载2");
                emitter.onNext("连载3");
                Log.i("bbb", "emitter: ");
                emitter.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        //多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
        //每调用一次observeOn() 线程就会切换一次.

        Observable.just("123", "321")
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        if (s.equals("123")) {
                            return true;
                        }
                        return false;
                    }
                })
                .subscribe(observer);

        Disposable disposable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                emitter.onNext("5566");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i("bbb", "accept: " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i("bbb", "error: " + throwable.getMessage());
            }
        });

        Disposable disposable2 = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                emitter.onNext(1);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                return Observable.just("OK :" + integer);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i("bbb", "accept: " + s);
            }
        });

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.apiopen.top/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        RetrofitTest.LogService service = retrofit.create(RetrofitTest.LogService.class);

        Disposable disposable3 = service.findBlog6()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RetrofitTest.Result<List<RetrofitTest.User>>>() {
                    @Override
                    public void accept(RetrofitTest.Result<List<RetrofitTest.User>> listResult) throws Exception {

                        listResult.result.get(0).authors = "cbw";
                        Log.i("bbb", "accept: " + listResult.toString());
                    }
                })
                .flatMap(new Function<RetrofitTest.Result<List<RetrofitTest.User>>, ObservableSource<RetrofitTest.User>>() {
                    @Override
                    public ObservableSource<RetrofitTest.User> apply(RetrofitTest.Result<List<RetrofitTest.User>> listResult) throws Exception {
                        return Observable.fromIterable(listResult.result);
                    }
                })
                .filter(new Predicate<RetrofitTest.User>() {
                    @Override
                    public boolean test(RetrofitTest.User user) throws Exception {
                        return user.authors.equals("cbw");
                    }
                })
                .subscribe(new Consumer<RetrofitTest.User>() {
                    @Override
                    public void accept(RetrofitTest.User user) throws Exception {
                        Log.i("bbb", "accept: " + user.toString());
                    }
                });

        Flowable<Integer> upstream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {

                for (int i = 0; i < 10; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR);

        Subscriber<Integer> downstream = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {

                s.request(5);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d("bbb", "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };

        upstream.subscribe(downstream);

    }


}
