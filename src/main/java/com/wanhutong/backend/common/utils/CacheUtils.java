package com.wanhutong.backend.common.utils;

/**
 * Cache工具类
 * @author ThinkGem
 * @version 2013-5-29
 */
public class CacheUtils {


	private static final int DEF_CACHE_VALIDITY_TIME = 3600;

	private static final String SYS_CACHE = "sysCache";

	/**
	 * 获取SYS_CACHE缓存
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		return get(SYS_CACHE, key);
	}

	/**
	 * 写入SYS_CACHE缓存
	 * @param key
	 * @return
	 */
	public static void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}

	/**
	 * 从SYS_CACHE缓存中移除
	 * @param key
	 * @return
	 */
	public static void remove(String key) {
		remove(SYS_CACHE, key);
	}

	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, String key) {
		return JedisUtils.getObject(cacheName + key);
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {
		CacheUtils.put(cacheName + key, value, DEF_CACHE_VALIDITY_TIME);
	}

	/**
	 * 写入缓存
	 * @param key
	 * @param value
	 */
	public static void put(String key, Object value, int validityTime) {
		JedisUtils.setObject(key, value, validityTime);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {
		JedisUtils.del(cacheName + key);
	}
//
//	/**
//	 * 获得一个Cache，没有则创建一个。
//	 * @param cacheName
//	 * @return
//	 */
//	private static Cache getCache(String cacheName){
//		Cache cache = cacheManager.getCache(cacheName);
//		if (cache == null){
//			cacheManager.addCache(cacheName);
//			cache = cacheManager.getCache(cacheName);
//			cache.getCacheConfiguration().setEternal(true);
//		}
//		return cache;
//	}
//
//	public static CacheManager getCacheManager() {
//		return cacheManager;
//	}
//
}
