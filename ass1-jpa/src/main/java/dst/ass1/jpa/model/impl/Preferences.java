package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPreferences;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Preferences implements IPreferences {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    private Map<String, String> data;

    public Preferences() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public void putData(String key, String value) {
        data.put(key, value);
    }
}
