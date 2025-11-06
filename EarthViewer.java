package com.earth; 

import java.awt.Desktop;
import java.net.URI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EarthViewer extends Application {

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final double ROTATE_MODIFIER = 0.1;
    private Rotate rotateX;
    private Rotate rotateY;

    @Override
    public void start(Stage primaryStage) {
        Sphere earth = createEarth();

        Group root = new Group();
        root.getChildren().add(earth);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setCamera(new PerspectiveCamera());

        initMouseControl(earth, scene);
        startEarthRotation();

        primaryStage.setTitle("3D Earth Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Sphere createEarth() {
        Sphere earth = new Sphere(200);
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image("https://www.solarsystemscope.com/textures/download/2k_earth_daymap.jpg"));
        earth.setMaterial(earthMaterial);

        // Center the sphere in the scene
        earth.setTranslateX(400); // Half of the scene width
        earth.setTranslateY(300); // Half of the scene height

        return earth;
    }

    private void initMouseControl(Sphere earth, Scene scene) {
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        earth.getTransforms().addAll(rotateX, rotateY);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - anchorX;
            double deltaY = event.getSceneY() - anchorY;

            rotateX.setAngle(anchorAngleX + deltaY * ROTATE_MODIFIER);
            rotateY.setAngle(anchorAngleY - deltaX * ROTATE_MODIFIER);
        });

        scene.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && isClickOnSphere(event, earth)) {
                double[] latLong = calculateLatLong(event, earth, rotateX, rotateY);
                openWebPage(latLong[0], latLong[1]);
            }
        });
    }

    private boolean isClickOnSphere(MouseEvent event, Sphere earth) {
        double sceneX = event.getSceneX() - earth.getTranslateX();
        double sceneY = event.getSceneY() - earth.getTranslateY();
        double radius = earth.getRadius();
        return sceneX * sceneX + sceneY * sceneY <= radius * radius;
    }

    private double[] calculateLatLong(MouseEvent event, Sphere earth, Rotate rotateX, Rotate rotateY) {
        double sceneX = event.getSceneX() - earth.getTranslateX();
        double sceneY = event.getSceneY() - earth.getTranslateY();
        double radius = earth.getRadius();

        // Calculate the ray direction
        double[] rayDir = calculateRayDirection(sceneX, sceneY, radius);

        // Find the intersection point on the sphere
        double[] intersection = findIntersection(rayDir, radius);

        // Apply inverse rotation to get the original coordinates
        double[] rotatedCoords = inverseRotate(intersection[0], intersection[1], intersection[2], rotateX.getAngle(), rotateY.getAngle());

        double latitude = Math.toDegrees(Math.asin(rotatedCoords[1] / radius));
        double longitude = Math.toDegrees(Math.atan2(rotatedCoords[0], rotatedCoords[2]));

        return new double[] { latitude, longitude };
    }

    private double[] calculateRayDirection(double sceneX, double sceneY, double radius) {
        double x = sceneX;
        double y = -sceneY; // Invert Y to correct latitude inversion
        double z = Math.sqrt(radius * radius - x * x - y * y);
        return new double[] { x, y, z };
    }

    private double[] findIntersection(double[] rayDir, double radius) {
        double x = -rayDir[0];
        double y = rayDir[1];
        double z = rayDir[2];
        double length = Math.sqrt(x * x + y * y + z * z);
        return new double[] { x / length * radius, y / length * radius, z / length * radius };
    }

    private double[] inverseRotate(double x, double y, double z, double angleX, double angleY) {
        // Inverse rotation around Y-axis
        double cosY = Math.cos(Math.toRadians(angleY));
        double sinY = Math.sin(Math.toRadians(angleY));
        double x1 = x * cosY - z * sinY;
        double z1 = x * sinY + z * cosY;

        // Inverse rotation around X-axis
        double cosX = Math.cos(Math.toRadians(angleX));
        double sinX = Math.sin(Math.toRadians(angleX));
        double y1 = y * cosX + z1 * sinX;
        double z2 = -y * sinX + z1 * cosX;

        return new double[] { -x1, y1, z2 };
    }

    private void openWebPage(double latitude, double longitude) {
        try {
            String url = "https://www.google.com/maps?q=" + latitude + "," + longitude;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startEarthRotation() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, event -> rotateY.setAngle(rotateY.getAngle() + 0.1)),
            new KeyFrame(Duration.millis(10))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
