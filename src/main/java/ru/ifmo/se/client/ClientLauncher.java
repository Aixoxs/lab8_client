package ru.ifmo.se.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.ifmo.se.client.message.MessageReader;
import ru.ifmo.se.client.message.MessageWriter;
import ru.ifmo.se.clientUI.Context;
import ru.ifmo.se.clientUI.controllers.LoginController;
import ru.ifmo.se.clientUI.controllers.LoginRegisterController;
import ru.ifmo.se.manager.CollectionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

public class ClientLauncher extends Application {
    private static Client client;

    public ClientLauncher() {
    }

    public static void main(String[] args)  {
        InetSocketAddress address = null;

        String host = null;

        try {
            int port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                host = args[1];
                new InetSocketAddress(host, port);
            }if (host == null) {
                address = new InetSocketAddress("localhost", port);
            }else address = new InetSocketAddress(host, port);
        } catch (ArrayIndexOutOfBoundsException var10) {
            System.err.println("Необходимо указать порт(java -jar Client.jar port host)");
            System.exit(-1);
        } catch (IllegalArgumentException var11) {
            System.err.println("Порт введен неверно(java -jar Client.jar port host)");
            System.exit(-1);
        }
        try {
            client = new Client(address);
            launch(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NoSuchElementException var13){
            System.out.println("Программа завершена");
        }


    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param stage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login_register.fxml"));
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.BundleLang", new Locale("en"));
        loader.setResources(bundle);
        loader.setController(new LoginRegisterController(new Context() {
            @Override
            public Client getClient() {
                return client;
            }

            @Override
            public Reader getReader() {
                return new Reader();
            }
        }));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
