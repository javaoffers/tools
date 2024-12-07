package com.javaoffers.brief.common;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    private Map<String,Object> params = new HashMap<String,Object>();

    public static MapUtils startBuildParam(String key,Object value){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(key,value);
        MapUtils mapUtils = new MapUtils();
        mapUtils.params = params;
        return mapUtils;
    }

    public MapUtils buildParam(String key,Object value){
        this.params.put(key,value);
        return this;
    }

    public Map<String,Object> endBuildParam(){
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.putAll(params);
        return objectHashMap;
    }

    public Map<String, String> endBuildStringParam() {
        HashMap<String, String> map = new HashMap<>();
        this.params.forEach((k,v)->{
           map.put(k,v.toString());
        });
        return  map;
    }
}
