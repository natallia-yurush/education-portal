package by.nyurush.portal.service;

public interface RedisService {

    void addCode(String key, String value);

    boolean isValidCode(String key, String value);

    String getValue(String key);

    boolean isCodeExist(String code);
}
