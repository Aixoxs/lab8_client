package ru.ifmo.se.manager;

import ru.ifmo.se.musicians.MusicBand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionManager {
    private final List<MusicBand> localList;

    public CollectionManager() {
        this.localList = new ArrayList();
    }

    public CollectionManager(List<MusicBand> list) {
        this.localList = Collections.synchronizedList(list);
    }

    public MusicBand getByID(int id) {
        return (MusicBand)this.localList.stream().filter((musicBand) -> {
            return musicBand.getId() == id;
        }).findAny().orElse((MusicBand) null);
    }

    public List<MusicBand> getLocalList() {
        return this.localList;
    }
}
