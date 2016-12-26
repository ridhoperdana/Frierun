package net.ridhoperdana.Frierun.pojo_class;

/**
 * Created by RIDHO on 12/26/2016.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Tempat implements Serializable {
    @SerializedName("html_attributions")
    private List html_attributions;

    //    private Results results;
    @SerializedName("results")
    private List<Results> results;

    @SerializedName("next_page_token")
    private String next_page_token;

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public List getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List html_attributions) {
        this.html_attributions = html_attributions;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "Tempat{" +
                "results=" + results +
                '}';
    }
}
