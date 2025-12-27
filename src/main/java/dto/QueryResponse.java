package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResponse<T> {
    public List<T> docs;
    public int totalDocs;
    public int limit;
    public int page;
    public int totalPages;
    public boolean hasNextPage;
    public boolean hasPrevPage;
}
