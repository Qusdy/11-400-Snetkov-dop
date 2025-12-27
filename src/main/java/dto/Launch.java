package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Launch {
    public String id;
    public String name;

    @JsonProperty("date_utc")
    public String dateUtc;

    public Boolean success;
    public String details;

    public Object rocket;

    public Links links;
    public Boolean upcoming;
}
