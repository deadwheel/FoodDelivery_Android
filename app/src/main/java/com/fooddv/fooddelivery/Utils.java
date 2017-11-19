package com.fooddv.fooddelivery;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import com.fooddv.fooddelivery.models.ApiError;
import com.fooddv.fooddelivery.network.ApiService;
import com.fooddv.fooddelivery.network.RetrofitBuilder;

public class Utils {

    public static ApiError converErrors(ResponseBody response){
        Converter<ResponseBody, ApiError> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError apiError = null;

        try {
            apiError = converter.convert(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiError;
    }

    /**
     * Created by vr on 2017-10-27.
     */

    public static class OffersFragment {
    }
}

