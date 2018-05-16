package com.tatusafety.matuba.objects.request;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by dickson-incentro on 5/2/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "coordinates"
})
public class Geometry {

    @JsonProperty("type")
    private String type;
    @JsonProperty("coordinates")
    private List<List<Double>> coordinates = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Geometry(String type, List<List<Double>> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("coordinates")
    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    @JsonProperty("coordinates")
    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
