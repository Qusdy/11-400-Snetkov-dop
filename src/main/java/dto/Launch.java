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

    public String rocket;

    public String rocketName;

    public Links links;
    public Boolean upcoming;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateUtc() {
        return dateUtc;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getDetails() {
        return details;
    }

    public String getRocket() {
        return rocket;
    }

    public String getRocketName() {
        return rocketName;
    }

    public Links getLinks() {
        return links;
    }

    public Boolean getUpcoming() {
        return upcoming;
    }
}
