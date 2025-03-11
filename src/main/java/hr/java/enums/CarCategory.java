package hr.java.enums;

public enum CarCategory {
    AM(1L),
    A(2L),
    B(3L),
    C(4L),
    D(5L),
    F(Long.valueOf(6)),
    G(7L),
    H(8L);





    private Long id;


    CarCategory(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
