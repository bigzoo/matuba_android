package com.tatusafety.matuba.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "href",
        "type",
        "behaviour",
        "distance",
        "duration",
        "waypoints",
        "geometry",
        "directions"
})

public class LegObject {

    @JsonProperty("href")
    private String href;
    @JsonProperty("type")
    private String type;
    @JsonProperty("behaviour")
    private String behaviour;
    @JsonProperty("distance")
    private Distance_ distance;
    @JsonProperty("duration")
    private Integer duration;
    @JsonProperty("waypoints")
    private List<Waypoint> waypoints = null;

    @JsonProperty("directions")
    private List<Direction> directions = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("behaviour")
    public String getBehaviour() {
        return behaviour;
    }

    @JsonProperty("behaviour")
    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    @JsonProperty("distance")
    public Distance_ getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Distance_ distance) {
        this.distance = distance;
    }

    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @JsonProperty("waypoints")
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    @JsonProperty("waypoints")
    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }
    
    @JsonProperty("directions")
    public List<Direction> getDirections() {
        return directions;
    }

    @JsonProperty("directions")
    public void setDirections(List<Direction> directions) {
        this.directions = directions;
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
}
