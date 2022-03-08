package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IPreferences;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class Preferences implements IPreferences {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Map<String, String> data;

    public Preferences() {}

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }

    @Override
    public Map<String, String> getData() {
        return null;
    }

    @Override
    public void setData(Map<String, String> data) {

    }

    @Override
    public void putData(String key, String value) {

    }
}
