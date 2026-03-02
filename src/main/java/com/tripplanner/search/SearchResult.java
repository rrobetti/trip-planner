package com.tripplanner.search;

public abstract class SearchResult {
    protected String url;
    protected String price;
    protected boolean fetched;
    protected String error;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public boolean isFetched() { return fetched; }
    public void setFetched(boolean fetched) { this.fetched = fetched; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public abstract String getSummary();
}
