package com.cbw.web.test;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by cbw on 2018/11/19.
 */
public class RetrofitTest {

    public RetrofitTest() {

        init();
    }

    private void init() {

        final Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.apiopen.top/")
                // 我们自定义的一定要放在Gson这类的Converter前面
//                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        LogService service = retrofit.create(LogService.class);

//        Call<ResponseBody> call = service.getLog();
//        Call<ResponseBody> call = service.getLog2("李白");
//        Map<String, Object> map = new HashMap<>();
//        map.put("page", 1);
//        map.put("count", 20);
//        Call<Result<User>> call = service.getLog4(map);

       /* call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("bbb", "onResponse: " + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });*/

//        Call<Result<List<User>>> call = service.findBlog3();

        /*call.enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
//                User result = response.body();
//                if (result != null) {
//                    Log.i("bbb", "onResponse: " + result.toString());
//                }
                Log.i("bbb", "onResponse: " + response.body().toString());
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                t.printStackTrace();
            }
        });*/

        /*service.findBlog4(1,20)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Result<List<User>>>() {
                    @Override
                    public void onCompleted() {
                        Log.i("bbb", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("bbb", "onError: ");
                    }

                    @Override
                    public void onNext(Result<List<User>> resultResponse) {
                        Log.i("bbb", "onNext: " + resultResponse.toString());
                    }
                });*/

        service.findBlog5()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Result<List<User>>>>() {

                    @Override
                    public void onComplete() {
                        Log.i("bbb", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("bbb", "onError: ");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Result<List<User>>> resultResponse) {
                        Log.i("bbb", "onNext: " + resultResponse.body().toString());
                    }
                });

    }

    public interface LogService {

        @GET("EmailSearch?number=1012002")
        Call<ResponseBody> getLog();

        @GET("likePoetry")
        Call<ResponseBody> getLog2(@Query("name") String name);

        @GET("recommendPoetry")
        Call<ResponseBody> getLog3();

        @GET("getTangPoetry")
        Call<ResponseBody> getLog4(@QueryMap Map<String, Object> map);

        @GET("recommendPoetry")
        Call<String> findBlog();

        @GET("recommendPoetry")
        Call<Result<User>> findBlog2();

        @GET("getSongPoetry?page=1&count=20")
        Call<Result<List<User>>> findBlog3();

        @GET("getSongPoetry")
        Observable<Result<List<User>>> findBlog4(@Query("page") int page, @Query("count") int count);

        @GET("getSongPoetry?page=1&count=20")
        Observable<Response<Result<List<User>>>> findBlog5(); //

        @GET("getSongPoetry?page=1&count=20")
        Observable<Result<List<User>>> findBlog6();
    }

    /**
     * 自定义Converter实现RequestBody到 * 的转换
     */
    public static class StringConverter implements Converter<ResponseBody, User> {

        public static final StringConverter INSTANCE = new StringConverter();

        @Override
        public User convert(ResponseBody value) throws IOException {
            Gson gson = new Gson();
            User user;
            user = gson.fromJson(value.string(), User.class);
            return user;

//            return value.string();
        }
    }

    /**
     * 用于向Retrofit提供StringConverter
     */
    public static class StringConverterFactory extends Converter.Factory {

        public static final StringConverterFactory INSTANCE = new StringConverterFactory();

        public static StringConverterFactory create() {
            return INSTANCE;
        }

        // 我们只关实现从ResponseBody 到 String 的转换，所以其它方法可不覆盖
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, retrofit2.Retrofit retrofit) {
            if (type == String.class) {
                return StringConverter.INSTANCE;
            }
            //其它类型我们不处理，返回null就行
            return null;
        }
    }

    public class Result<T> {
        public int code;
        public String message;
        public T result;

        @Override
        public String toString() {
            return "Result{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", result='" + result + '\'' +
                    ", data=" + result.toString() + '\'' +
                    '}';
        }
    }

    public class User {

        public int code;
        public String message;

        @SerializedName("title")
        public String title;
        @SerializedName("content")
        public String content;
        @SerializedName("authors")
        public String authors;

        @Override
        public String toString() {
            return "User{" +
                    "title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", authors='" + authors + '\'' +
                    '}';
        }
    }
}
