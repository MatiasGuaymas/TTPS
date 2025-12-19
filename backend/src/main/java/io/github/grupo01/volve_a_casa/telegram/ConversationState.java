package io.github.grupo01.volve_a_casa.telegram;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ConversationState {
    private ConversationStep currentStep;
    private Map<String, Object> data = new HashMap<>();
    
    public enum ConversationStep {
        NONE,
        WAITING_EMAIL,
        WAITING_PASSWORD,
        WAITING_NAME,
        WAITING_SIZE,
        WAITING_STATE,
        WAITING_DATE,
        WAITING_COLOR,
        WAITING_TYPE,
        WAITING_RACE,
        WAITING_WEIGHT,
        WAITING_PHOTO,
        WAITING_LOCATION,
        WAITING_DESCRIPTION
    }
    
    public ConversationState() {
        this.currentStep = ConversationStep.NONE;
    }
    
    public void reset() {
        this.currentStep = ConversationStep.NONE;
        this.data.clear();
    }
    
    public void put(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object get(String key) {
        return this.data.get(key);
    }
    
    public String getString(String key) {
        return (String) this.data.get(key);
    }
    
    public Float getFloat(String key) {
        return (Float) this.data.get(key);
    }
    
    public Pet.Size getSize(String key) {
        return (Pet.Size) this.data.get(key);
    }
    
    public Pet.State getState(String key) {
        return (Pet.State) this.data.get(key);
    }
    
    public Pet.Type getType(String key) {
        return (Pet.Type) this.data.get(key);
    }
    
    public Long getLong(String key) {
        return (Long) this.data.get(key);
    }
}
