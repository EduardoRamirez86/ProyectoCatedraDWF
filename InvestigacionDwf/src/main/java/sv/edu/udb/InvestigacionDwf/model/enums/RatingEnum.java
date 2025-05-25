package sv.edu.udb.InvestigacionDwf.model.enums;

public enum RatingEnum {
    ZERO(0.0), HALF(0.5), ONE(1.0), ONE_HALF(1.5), TWO(2.0),
    TWO_HALF(2.5), THREE(3.0), THREE_HALF(3.5), FOUR(4.0),
    FOUR_HALF(4.5), FIVE(5.0);

    private final double value;

    RatingEnum(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static RatingEnum fromValue(double value) {
        for (RatingEnum r : values()) {
            if (r.getValue() == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value);
    }
}


