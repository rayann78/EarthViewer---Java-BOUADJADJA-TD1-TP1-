import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.PointLight;
import javafx.stage.Stage;


public class Interface extends Application {
    private Earth earth;
    private World world;
    private JsonFlightFiller filler;
    private double lastY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Catch me if you can – Partie 6");
        Group root = new Group();
        Scene scene = new Scene(root, 1200, 800, true);
        stage.setScene(scene);

        // Caméra
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(5000.0);
        camera.setFieldOfView(35);
        scene.setCamera(camera);

        // Terre + lumière
        earth = new Earth();
        root.getChildren().add(earth);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-500);
        root.getChildren().add(light);

        // Données
        world = new World("./data/airport-codes_no_comma.csv");
        filler = new JsonFlightFiller(world);

        // Interactions
        scene.addEventHandler(MouseEvent.ANY, e -> {
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                lastY = e.getSceneY();
            }
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED && e.getButton() == MouseButton.PRIMARY) {
                double dy = e.getSceneY() - lastY;
                lastY = e.getSceneY();
                camera.setTranslateZ(clamp(camera.getTranslateZ() + dy * 2.0, -2500, -400));
            }
            if (e.getEventType() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseButton.SECONDARY) {
                var pick = e.getPickResult();
                if (pick != null && pick.getIntersectedNode() != null) {
                    Point2D tex = pick.getIntersectedTexCoord();
                    if (tex != null) {
                        double[] lonlat = Earth.texToLonLat(tex);
                        Aeroport nearest = world.findNearestAirport(lonlat[0], lonlat[1]);
                        if (nearest != null) {
                            System.out.println("Click lon=" + lonlat[0] + " lat=" + lonlat[1] + " -> " + nearest);
                            // Marqueur rouge (aéroport cliqué le plus proche)
                            earth.displayRedSphere(nearest);
                            // Origines en JAUNE via JSON
                            for (Aeroport origin : filler.distinctOriginsArrivingTo(nearest.getIata())) {
                                earth.displayYellowSphere(origin);
                            }
                        }
                    }
                }
            }
        });

        stage.show();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public static void main(String[] args) { launch(args); }
}
