package cs1302.api;

/**
 * Represents a result in a response from the Jelly Belly API. This is
 * used by Gson to create an object from the JSON response body. This class
 * is provided with project's starter code, and the instance variables are
 * intentionally set to package private visibility.
 */

public class JellyBellyResult {
    String flavorName;
    String[] groupName;
    String description;
    String backgroundColor;
    String imageUrl;
    String[] ingredients;
    boolean glutenFree;
    boolean sugarFree;
    boolean seasonal;
    boolean kosher;
} // JellyBellyResult
