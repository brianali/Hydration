package itp341.liang.briana.finalproject.model.objects;

import java.io.Serializable;
import java.util.UUID;

public class NamedObject implements Serializable {
    private static final long serialVersionUID = 2L;

    private String name = null;
    private String identifier = null;

    /**
     * Create a new NamedObject with a specific name and identifier.
     * @param name The string name of the object.
     * @param identifier The string identifier of the object.
     */
    public NamedObject(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Create a new NamedObject with a name and automatically generate an identifier.
     * @param name The string name of the object.
     */
    public NamedObject(String name) {
        this.name = name;
        UUID id = UUID.randomUUID();
        this.identifier = "" + id;
    }

    public void setName(String name) {this.name =  name;}

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NamedObject)) {
            return false;
        }

        NamedObject other = (NamedObject)obj;
        return (other.getIdentifier().equals(this.getIdentifier()));
    }

    @Override
    public int hashCode() {
        return this.getIdentifier().hashCode();
    }
}
