package dst.ass1.jpa.model;

import java.util.Map;

public interface IPreferences {

    Long getId();

    void setId(Long id);

    Map<String, String> getData();

    void setData(Map<String, String> data);

    void putData(String key, String value);
}
