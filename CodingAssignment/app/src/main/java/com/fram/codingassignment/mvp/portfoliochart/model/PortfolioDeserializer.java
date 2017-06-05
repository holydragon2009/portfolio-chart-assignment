package com.fram.codingassignment.mvp.portfoliochart.model;

import android.text.TextUtils;

import com.fram.codingassignment.mvp.portfoliochart.model.Portfolio;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thaile on 6/4/17.
 */

public class PortfolioDeserializer implements JsonDeserializer<List<Portfolio>> {

    @Override
    public List<Portfolio> deserialize(JsonElement json, Type typeOfT,
                                       JsonDeserializationContext context) throws JsonParseException {

        ArrayList<Portfolio> array = new ArrayList<>();

        String str = null;

        try {
            str = json.getAsString();
        } catch (IllegalStateException e) {

        }

        if (str != null && TextUtils.isEmpty(str)) {
            return array;
        } else {
            JsonArray jsonArray = json.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                array.add((Portfolio) context.deserialize(jsonArray.get(i), Portfolio.class));
            }
        }
        return array;
    }
}