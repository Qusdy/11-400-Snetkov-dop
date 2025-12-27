package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    public Patch patch;

    @JsonProperty("youtube_id")
    public String youtubeId;

    public String webcast;
    public String article;
    public String wikipedia;
}