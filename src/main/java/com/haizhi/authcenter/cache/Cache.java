package com.haizhi.authcenter.cache;

/**
 * Created by JuniFire on 2017/10/18.
 */
public interface Cache<K,V> {

    void set(K key,V value,Long expireTime);

    void incAtomic(String key, Long expireTime);

    V get(K key);

    void del(K key);

}
