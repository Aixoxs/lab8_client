package ru.ifmo.se.client;

import ru.ifmo.se.client.message.MessageReader;
import ru.ifmo.se.client.message.MessageWriter;
import ru.ifmo.se.clientUI.controllers.MainController;
import ru.ifmo.se.commands.ClassCommand;
import ru.ifmo.se.commands.ShowCommand;
import ru.ifmo.se.manager.CollectionManager;
import ru.ifmo.se.model.User;
import ru.ifmo.se.musicians.MusicBand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Client {
    private MessageReader messageReader;
    private SocketAddress address;
    private boolean connect = false;
    private Reader reader = new Reader();
    private DatagramChannel channel = DatagramChannel.open();
    private MessageWriter messageWriter;
    private User currentUser;
    private CollectionManager collectionManager = new CollectionManager();
    private MainController mainController;

    public Client(InetSocketAddress inetSocketAddress) throws IOException {
        address = inetSocketAddress;
        channel.configureBlocking(false);
        this.messageReader = new MessageReader(channel);
        messageWriter = new MessageWriter(channel, address);
        messageReader.start();
    }


    public Object sendResponse(Object object) {
        Object answer;
        messageWriter.writeRequest(object);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        answer = messageReader.getReceivedObject();
        setReceivedObjectToNull();
        return answer != null ? answer : "Сервер не отвечает, попробуйте еще раз";
    }

    private void setReceivedObjectToNull(){
        messageReader.setReceivedObject(null);
    }

    public void refreshCollection() {
        collectionManager.getLocalList().clear();
        ClassCommand classCommand = new ShowCommand();
        classCommand.setUser(currentUser);
        messageWriter.writeRequest(classCommand);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object answer = messageReader.getReceivedObject();
        if (answer instanceof List){
            collectionManager.getLocalList().addAll((List<MusicBand>)answer);
            setReceivedObjectToNull();
        }else if (answer == null){
//            System.err.println("net soedineniya");
        }
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public MessageReader getMessageReader() {
        return messageReader;
    }

    public MessageWriter getMessageWriter() {
        return messageWriter;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        messageReader.setMainController(mainController);
    }

    //Создает сокет, ридер райтер и запускает логику
    public void start() throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException {
        MessageWriter messageWriter = new MessageWriter(channel, address);
        this.messageReader = new MessageReader(channel);
        Object answer;
        User user = reader.readUser(new Scanner(System.in, "UTF-8"));
        while (true) {
            metka:
            while (true) {
                if (connect) {
                    ClassCommand classCommand = reader.readCommand(new Scanner(System.in, "UTF-8"));
                    classCommand.setUser(user);
                    messageWriter.writeRequest(classCommand);
                    messageReader.setAnswerWasRead(true);
                    long start = System.currentTimeMillis();
                    while (messageReader.isAnswerWasRead()) {
                        answer = messageReader.getReceivedObject();
                        if (answer != null && answer.equals("connect")) {
                            continue;
                        } else if (answer != null) {
                            System.out.println(answer);
                            messageReader.setAnswerWasRead(false);
                        }
                        if (messageReader.isAnswerWasRead() && System.currentTimeMillis() - start > 3000L) {
                            System.out.println("Ожидается получение ответа на запрос");
                            Thread.sleep(1000);
                        }
                        if (messageReader.isAnswerWasRead() && System.currentTimeMillis() - start > 10000L) {
                            connect = false;
                            continue metka;
                        }
                    }
                } else {
                    System.out.println("Идет подключение к серверу");
                    messageWriter.writeRequest(user);
                    Thread.sleep(1000);
                    Object answer1 = messageReader.getReceivedObject();
                    if (answer1 != null && answer1.equals("connect")) {
                        System.out.println("Connect successful");
                        connect = true;
                    } else if (answer1 != null) {
                        System.out.println(answer1);
                        System.out.println("Повторите попытку");
                        user = reader.readUser(new Scanner(System.in, "UTF-8"));
                    }
                }
            }
        }
    }
}
