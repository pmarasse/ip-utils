package fr.chpoitiers.net.util;

import lombok.Data;

@Data
public class NetworkDescription {

    /** Nom du réseau */
    private String name;

    /** Localisation */
    private String location;

    /** Type */
    private String type;

    public NetworkDescription() {

        super();
    }

    public NetworkDescription(String name, String location, String type) {

        super();
        this.name = name;
        this.location = location;
        this.type = type;
    }

}
