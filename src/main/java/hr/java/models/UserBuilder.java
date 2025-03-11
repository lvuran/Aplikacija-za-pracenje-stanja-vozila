package hr.java.models;

public class UserBuilder<T extends Electric, U extends FuelCar> {
    private Long id;
    private String name;

    public UserBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public User createUser() {
        return new User(id, name);
    }
}