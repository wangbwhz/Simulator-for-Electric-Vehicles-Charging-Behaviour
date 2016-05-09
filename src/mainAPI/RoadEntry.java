package mainAPI;

import com.graphhopper.util.PointList;


/**
 *
 * @author wangb
 */
public class RoadEntry {

    private PointList points;
    private double value;
    private String valueType;
    private String mode;
    private String id;

    public RoadEntry() {
    }

    public RoadEntry(String id, PointList points, double value, String valueType, String mode) {
        this.points = points;
        this.value = value;
        this.valueType = valueType;
        this.mode = mode;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PointList getPoints() {
        return points;
    }

    public void setPoints(PointList points) {
        this.points = points;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValueType(String type) {
        this.valueType = type;
    }

    
    /**
     * 
     * @return speed or any
     */
    public String getValueType() {
        return valueType;
    }

  
     // Currently 'replace', 'multiply' and 'add' are supported
    
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "points:" + points + ", value:" + value + ", type:" + valueType + ", mode:" + mode;
    }
}
