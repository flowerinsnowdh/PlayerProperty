package online.flowerinsnow.playerproperty.object;

public class PlayerProperty {
    /**
     * 水分
     */
    private int water;
    /**
     * 神智
     */
    private int mental;

    public PlayerProperty(int water, int mental) {
        this.water = water;
        this.mental = mental;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getMental() {
        return mental;
    }

    public void setMental(int mental) {
        this.mental = mental;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlayerProperty that = (PlayerProperty) object;
        return water == that.water && mental == that.mental;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + water;
        result = 31 * result + mental;
        return result;
    }

    @Override
    public String toString() {
        return "PlayerProperty{" +
                "water=" + water +
                ", mental=" + mental +
                '}';
    }
}
