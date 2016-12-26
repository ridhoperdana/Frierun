package net.ridhoperdana.Frierun.pojo_class;

/**
 * Created by RIDHO on 12/17/2016.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by RIDHO on 4/23/2016.
 */
public class Predictions implements Serializable{

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private String id;

    @SerializedName("matched_substrings")
    private List<Substring> matched_substrings;

    @SerializedName("place_id")
    private String place_id;

    @SerializedName("reference")
    private String reference;

    @SerializedName("terms")
    private List<Terms> terms;

    @SerializedName("types")
    private List<String> types;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List getMatched_substrings() {
        return matched_substrings;
    }

    public void setMatched_substrings(List matched_substrings) {
        this.matched_substrings = matched_substrings;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List getTerms() {
        return terms;
    }

    public void setTerms(List terms) {
        this.terms = terms;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}

class Substring implements Serializable
{
    @SerializedName("length")
    private String length;

    @SerializedName("offset")
    private String offset;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}

class Terms implements Serializable
{
    @SerializedName("offset")
    private String offset;

    @SerializedName("value")
    private String value;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
