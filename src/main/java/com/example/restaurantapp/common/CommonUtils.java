package com.example.restaurantapp.common;

import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class CommonUtils {

    public static boolean isEmpty(Collection collection) {
        return !CollectionUtils.isEmpty(collection);
    }
}
