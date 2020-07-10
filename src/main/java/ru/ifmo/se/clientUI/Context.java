package ru.ifmo.se.clientUI;

import ru.ifmo.se.client.Client;
import ru.ifmo.se.client.Reader;

public interface Context {
    Client getClient();

    Reader getReader();
}
