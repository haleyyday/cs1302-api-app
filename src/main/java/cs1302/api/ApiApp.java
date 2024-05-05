package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import cs1302.api.JellyBellyResponse;
import cs1302.api.JellyBellyResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private static final String JELLYBELLY_API =
        "https://jellybellywikiapi.onrender.com/api/beans";
    private static final String BG_COLOR = "lavenderblush";

    private Stage stage;
    private Scene scene;
    private Scene infoScene;
    private VBox root;

    private HBox searchBar;
    private Label searchLabel;
    private ComboBox<String> dropdown;
    private Button getButton;

    private HBox mainContent;
    private ScrollPane scrollPane;
    private TilePane window;
    private Rectangle defaultRect;
    private Button[] buttons;

    private HBox infoBox;
    private VBox imgButtonBox;
    private ImageView beanImgView;
    private Button backButton;
    private TilePane palette;
    private ComboBox<String> modeDropdown;
    private Button modeButton;
    private ScrollPane textPane;
    private VBox textBox;
    private Text nameText;
    private Text infoText;

    private HBox statusBar;
    private ProgressBar progressBar;
    private Label jellyBellyMessage;

    private ArrayList<String> urlList;
    private ArrayList<String> nameList;
    private ArrayList<Image> imgList;
    private ArrayList<JellyBellyResult> resultList;
    private String uri;
    private boolean updated;
    private HttpResponse<String> response;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;
        this.root = new VBox();

        searchBar = new HBox(3);

        searchLabel = new Label("Select a Group: ");
        dropdown = new ComboBox<String>();
        dropdown.getItems().addAll(
            "Jelly Belly Official Flavors", "Kids Mix Flavors",
            "Cold Stone® Flavors", "Sunkist® Citrus Mix Flavors",
            "Krispy Kreme® Doughnuts Flavors", "Soda Pop Shoppe® Flavors",
            "Superfruit Flavors", "Sours Flavors", "Tropical Mix Flavors",
            "Boba Milk Tea Flavors");
        dropdown.getSelectionModel().select("Jelly Belly Official Flavors");
        getButton = new Button("Get Flavors");

        mainContent = new HBox();
        scrollPane = new ScrollPane();
        window = new TilePane(20.0, 20.0);
        window.setPrefColumns(2);
        defaultRect = new Rectangle(555, 295);

        infoBox = new HBox();
        imgButtonBox = new VBox(5);
        beanImgView = new ImageView();
        backButton = new Button("Back");
        palette = new TilePane(0.0, 0.0);
        modeDropdown = new ComboBox<String>();
        modeDropdown.getItems().addAll(
            "monochrome", "monochrome-dark", "monochrome-light", "analogic",
            "complement", "analogic-complement", "triad", "quad");
        modeDropdown.getSelectionModel().select("monochrome");
        modeButton = new Button("New Palette");
        textPane = new ScrollPane();
        textBox = new VBox();
        nameText = new Text();
        infoText = new Text();

        statusBar = new HBox(3);
        progressBar = new ProgressBar(0.0);
        jellyBellyMessage = new Label("Images and info provided by Jelly Belly API.");

        urlList = new ArrayList<String>();
        nameList = new ArrayList<String>();
        imgList = new ArrayList<Image>();
        resultList = new ArrayList<JellyBellyResult>();

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        System.out.println("init() called");
        root.getChildren().addAll(searchBar, mainContent, statusBar);

        // searchBar
        searchBar.getChildren().addAll(searchLabel, dropdown, getButton);
        searchBar.setPadding(new Insets(3, 3, 3, 3));
        getButton.setPrefWidth(100);
        searchBar.setPrefWidth(540);
        searchBar.setAlignment(Pos.BASELINE_CENTER);
        getButton.setOnAction(e -> getImages());

        // scrollPane
        mainContent.setPrefWidth(550);
        mainContent.setPrefHeight(325);
        mainContent.getChildren().addAll(scrollPane);
        defaultRect.setStyle("-fx-fill: " + BG_COLOR);
        scrollPane.setContent(defaultRect);
        window.setStyle("-fx-background-color: " + BG_COLOR);
        scrollPane.setMinViewportHeight(280);
        scrollPane.setMinViewportWidth(540);
        window.setPadding(new Insets(20, 20, 20, 20));
        window.setTileAlignment(Pos.CENTER);

        // infoBox
        infoBox.setPrefWidth(550);
        infoBox.setPrefHeight(325);
        infoBox.setPadding(new Insets(5, 10, 5, 10));
        infoText.setWrappingWidth(320);
        nameText.setWrappingWidth(320);
        textPane.setPrefHeight(276);
        textPane.setPrefWidth(365);
        imgButtonBox.setPrefWidth(155);
        imgButtonBox.setMinHeight(275);
        imgButtonBox.setAlignment(Pos.CENTER_LEFT);
        backButton.setOnAction(e -> {
            root.getChildren().setAll(searchBar, mainContent, statusBar);
        });
        palette.setPrefWidth(120);
        palette.setPrefHeight(120);
        palette.setPrefColumns(2);
        palette.setAlignment(Pos.CENTER);
        HBox.setMargin(imgButtonBox, new Insets(0, 10, 0, 0));
        VBox.setMargin(nameText, new Insets(10, 10, 10, 10));
        VBox.setMargin(infoText, new Insets(0, 10, 10, 10));
        imgButtonBox.getChildren().addAll(backButton, beanImgView,
            palette, modeDropdown, modeButton);
        textPane.setContent(textBox);
        textBox.getChildren().addAll(nameText, infoText);
        infoBox.getChildren().addAll(imgButtonBox, textPane);

        // statusBar
        statusBar.getChildren().addAll(progressBar, jellyBellyMessage);
        statusBar.setPadding(new Insets(3, 3, 3, 3));
        progressBar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        System.out.println("init() complete");
    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.root);
        this.scene.getStylesheets().add("cs1302/api/style.css");
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("Jelly Belly Color Palette App");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));
    } // start

    /**
     * Retrives API response, loads images, and displays images.
     *
     * Action for event handler for {@code getButton}.
     */
    private void getImages() {

        Runnable taskForGetImages = () -> {
            this.jsonResponseJB();
            if (urlList.size() >= 1 && response.statusCode() == 200) {
                this.loadImages();
                this.displayImages();
                Platform.runLater(() -> {
                    getButton.setDisable(false);
                });
                updated = true;
            } else {
                Platform.runLater(() -> {
                    if (!updated) {
                        setProgress(1.0);
                    }
                    getButton.setDisable(false);
                });
            } // if
        };
        runInThread(taskForGetImages, "load thread");
    } // getImages

    /**
     * Retrieves the JSON response string for a query to the Jelly Belly
     * API based on a query string.
     *
     * Helper method for {@code getImages()}.
     */
    private void jsonResponseJB() {
        try {
            // form URI
            String groupName = URLEncoder.encode(dropdown.getValue(), StandardCharsets.UTF_8);
            String query = String.format("?groupName=%s", groupName);
            query = query.replace("+", "%20");
            uri = JELLYBELLY_API + query;
            Platform.runLater(() -> setProgress(0.1));

            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

            // send request / receive response in the form of a String
            response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());

            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if

            // get request body (the content we requested)
            String jsonString = response.body();

            // parse the JSON-formatted string using GSON
            JellyBellyResponse jellyBellyResponse = GSON
                .fromJson(jsonString, JellyBellyResponse.class);

            this.makeUrlList(jellyBellyResponse);

        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> {
                alertError(e);
            });
        } // try

    } // jsonResponse

    /**
     * Adds urls to {@code urlList}, avoiding duplicates.
     *
     * Helper method for {@code jsonResponse()}.
     * @param jellyBellyResponse
     */
    private void makeUrlList(JellyBellyResponse jellyBellyResponse) {
        try {
            urlList.clear();
            nameList.clear();
            resultList.clear();
            for (int i = 0; i < jellyBellyResponse.items.length; i++) {
                String imageUrl = jellyBellyResponse.items[i].imageUrl;
                urlList.add(imageUrl);
                String flavorName = jellyBellyResponse.items[i].flavorName;
                nameList.add(flavorName);
                resultList.add(jellyBellyResponse.items[i]);
            } // for
            if (urlList.size() < 1) {
                throw new IllegalArgumentException(urlList.size() +
                " distinct results found, but 1 or more are needed.");
            } // if
        } catch (IllegalArgumentException e) {
            Platform.runLater(() -> {
                alertError(e);
            });
        } // try
    } // makeUrlList

    /**
     * Loads each imageUrl from {@code urlList} into an Image and adds each to {@code imgList}.
     *
     * Helper method for {@code getImages()}.
     */
    private void loadImages() {
        imgList.clear();
        int size = urlList.size();
        for (int i = 0; i < size; i++) {
            Image img = new Image(urlList.get(i));
            imgList.add(img);
            setProgress(0.9 * i / size + 0.1);
        } // for
        setProgress(1.0);
    } // loadImages

    /**
     * Displays the first 20 images from {@code imgList} in the Image array {@code mainContent}.
     *
     * Helper method for {@code getImages}.
     */
    private void displayImages() {
        int size = urlList.size();
        Platform.runLater(() -> window.getChildren().clear());
        buttons = new Button[size];
        for (int i = 0; i < size; i++) {
            ImageView imgView = new ImageView();
            imgView.setFitWidth(90);
            imgView.setFitHeight(60);
            imgView.setImage(imgList.get(i));
            buttons[i] = new Button(nameList.get(i), imgView);
            addButton(buttons[i], i);
        } // for
        Platform.runLater(() -> {
            scrollPane.setContent(window);
            root.getChildren().setAll(searchBar, mainContent, statusBar);
        });
    } // displayImages

    /**
     * Adds buttons to the {@code window} appropriately.
     * @param button
     * @param i
     */
    private void addButton(final Button button, int i) {
        Platform.runLater(() -> {
            window.getChildren().addAll(button);
            button.setPrefWidth(240);
            button.setAlignment(Pos.CENTER_LEFT);
        });
        button.getStyleClass().add("button-style");
        button.setOnMouseEntered(e -> button.setStyle("-fx-border-color: crimson;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-border-color: white"));
        button.setOnAction(e -> beanButton(i));
    } // addButtons

    /**
     * Displays relevant information about the selected bean.
     *
     * Action for event handler for {@code button[i]}.
     *
     * @param i
     */
    private void beanButton(int i) {
        root.getChildren().setAll(searchBar, infoBox, statusBar);
        beanImgView.setFitWidth(150);
        beanImgView.setFitHeight(100);
        beanImgView.setImage(imgList.get(i));
        modeDropdown.getSelectionModel().select("monochrome");

        JellyBellyResult bean = resultList.get(i);
        String flavorName = bean.flavorName + "\n";
        String groupName = "Group: " + bean.groupName[0] + "\n\n";
        String desc = "\n" + bean.description + "\n";

        int ingredientsSize = bean.ingredients.length;
        String ingredients = "Ingredients: \n";
        for (int j = 0; j < ingredientsSize; j++) {
            ingredients += "    - " + bean.ingredients[j] + "\n";
        } // for

        String glutenFree = "\nGluten Free: " + bean.glutenFree + "\n";
        String sugarFree = "Sugar Free: " + bean.sugarFree + "\n";
        String kosher = "Kosher: " + bean.kosher + "\n";

        String all = groupName + ingredients + glutenFree + sugarFree + kosher;
        nameText.setText(flavorName + desc);
        infoText.setText(all);

        String hexColor = bean.backgroundColor.substring(1);
        this.jsonResponseColor(hexColor, "monochrome");
        modeButton.setOnAction(e -> {
            this.jsonResponseColor(hexColor, modeDropdown.getValue());
        });
    } // beanButton

    /**
     * Retrieves the JSON response string for a query to the Jelly Belly
     * API based on a query string.
     *
     * Helper method for {@code getImages()}.
     * @param hexColor to generate palette from.
     * @param mode to generate different palettes.
     */
    private void jsonResponseColor(String hexColor, String mode) {
        try {
            // form URI
            String hexQuery = String.format("?hex=%s", hexColor);
            String modeQuery = "&mode=" + mode;
            uri = "https://www.thecolorapi.com/scheme" + hexQuery + modeQuery + "&count=4";

            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

            // send request / receive response in the form of a String
            HttpResponse<String> httpResponse = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());

            // ensure the request is okay
            if (httpResponse.statusCode() != 200) {
                throw new IOException(httpResponse.toString());
            } // if

            // get request body (the content we requested)
            String jsonString = httpResponse.body();

            // parse the JSON-formatted string using GSON
            ColorResponse colorResponse = GSON
                .fromJson(jsonString, ColorResponse.class);

            this.makePalette(colorResponse);

        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> {
                alertError(e);
            });
        } // try

    } // jsonResponseColor

    /**
     * Creates a color palette based on the response from
     * the Color API.
     *
     * @param colorResponse
     */
    private void makePalette(ColorResponse colorResponse) {
        palette.getChildren().clear();
        int size = Integer.parseInt(colorResponse.count);
        for (int i = 0; i < size; i++) {
            String hexCode = colorResponse.colors[i].hex.clean;
            System.out.println(hexCode);
            Rectangle colorRect = new Rectangle(60, 60, Color.web(hexCode));
            palette.getChildren().add(colorRect);
        } // for
    } // makePalette

    /**
     * Updates the {@code progressBar} appropriately.
     * @param progress
     */
    private void setProgress(final double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    } // setProgress

    /**
     * Method to run a task in a separate thread.
     *
     * @param task to be run
     * @param name of task
     */
    private static void runInThread(Runnable task, String name) {
        Thread t = new Thread(task, name);
        t.start();
    } // runInThread

    /**
     * Show a modal error alert based on {@code cause}.
     *
     * @param cause a {@link java.lang.Throwable Throwable} that caused the alert
     */
    public void alertError(Throwable cause) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContentText(
            "URI: " + uri + "\n\nException: " + cause.toString());
        alert.setResizable(true);
        alert.showAndWait();
    } // alertError

} // ApiApp
