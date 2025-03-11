package hr.java.enums;

public enum CarType {
    GORIVO(Long.valueOf(1)), BATERIJA(Long.valueOf(2));

    private Long id;


    CarType(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
