package com.av.ringtone.utils.modelcache;

import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * Model cache模块
 * @author LiJiaZhi
 *
 */
public interface IModelCache {
	
	<T extends IBaseCacheModel> boolean putModel(String key, T model);
	
	boolean contains(String key);
	
	<T extends IBaseCacheModel> T getModel(String key, Class<T> clazz);
	
	<T extends IBaseCacheModel> boolean putModelList(String key, List<T> modelList);
	
	<T extends IBaseCacheModel> List<T> getModelList(String key, TypeToken<List<T>> typeToken);
	
	boolean removeModel(String key);
}
