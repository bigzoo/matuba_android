package com.tatusafety.matuba.objects.response;

/**
 * Created by dickson-incentro on 5/3/18.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "agencies",
        "modes"
})
public class Only {

    @JsonProperty("agencies")
    private List<Object> agencies = null;
    @JsonProperty("modes")
    private List<Object> modes = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("agencies")
    public List<Object> getAgencies() {
        return agencies;
    }

    @JsonProperty("agencies")
    public void setAgencies(List<Object> agencies) {
        this.agencies = agencies;
    }

    @JsonProperty("modes")
    public List<Object> getModes() {
        return modes;
    }

    @JsonProperty("modes")
    public void setModes(List<Object> modes) {
        this.modes = modes;
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
