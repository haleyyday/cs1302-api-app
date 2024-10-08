package cs1302.api;

/**
 * Represents an object in a result from the Color API. This is
 * used by Gson to create an object from the JSON response body. This class
 * is provided with project's starter code, and the instance variables are
 * intentionally set to package private visibility.
 */
public class ColorResponse {
    String mode;
    String count;
    ColorResult[] colors;
} // Item
