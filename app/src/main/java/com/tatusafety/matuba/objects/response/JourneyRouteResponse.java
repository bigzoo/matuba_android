package com.tatusafety.matuba.objects.response;

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
        "id",
        "href",
        "geometry",
        "time",
        "timeType",
        "profile",
        "fareProducts",
        "maxItineraries",
        "only",
        "omit",
        "itineraries"
})
public class JourneyRouteResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("href")
    private String href;
    @JsonProperty("geometry")
    private Geometry geometry;
    @JsonProperty("time")
    private String time;
    @JsonProperty("timeType")
    private String timeType;
    @JsonProperty("profile")
    private String profile;
    @JsonProperty("fareProducts")
    private List<Object> fareProducts = null;
    @JsonProperty("maxItineraries")
    private Integer maxItineraries;
    @JsonProperty("only")
    private Only only;
    @JsonProperty("omit")
    private Omit omit;
    @JsonProperty("itineraries")
    private List<Itinerary> itineraries = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty("geometry")
    public Geometry getGeometry() {
        return geometry;
    }

    @JsonProperty("geometry")
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("timeType")
    public String getTimeType() {
        return timeType;
    }

    @JsonProperty("timeType")
    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    @JsonProperty("profile")
    public String getProfile() {
        return profile;
    }

    @JsonProperty("profile")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @JsonProperty("fareProducts")
    public List<Object> getFareProducts() {
        return fareProducts;
    }

    @JsonProperty("fareProducts")
    public void setFareProducts(List<Object> fareProducts) {
        this.fareProducts = fareProducts;
    }

    @JsonProperty("maxItineraries")
    public Integer getMaxItineraries() {
        return maxItineraries;
    }

    @JsonProperty("maxItineraries")
    public void setMaxItineraries(Integer maxItineraries) {
        this.maxItineraries = maxItineraries;
    }

    @JsonProperty("only")
    public Only getOnly() {
        return only;
    }

    @JsonProperty("only")
    public void setOnly(Only only) {
        this.only = only;
    }

    @JsonProperty("omit")
    public Omit getOmit() {
        return omit;
    }

    @JsonProperty("omit")
    public void setOmit(Omit omit) {
        this.omit = omit;
    }

    @JsonProperty("itineraries")
    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    @JsonProperty("itineraries")
    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
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