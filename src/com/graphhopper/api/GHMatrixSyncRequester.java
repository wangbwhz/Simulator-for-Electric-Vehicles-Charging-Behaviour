package com.graphhopper.api;

import com.graphhopper.util.StopWatch;
import com.graphhopper.util.shapes.GHPoint;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Peter Karich
 */
public class GHMatrixSyncRequester extends GHMatrixAbstractRequester {

    public GHMatrixSyncRequester() {
        super();
    }

    public GHMatrixSyncRequester(String serviceUrl) {
        super(serviceUrl);
    }

    @Override
    public MatrixResponse route(GHMRequest ghRequest, String key) {
        StopWatch sw = new StopWatch().start();

        int fromCount, toCount;
        String pointsStr;
        if (ghRequest.identicalLists) {
            fromCount = toCount = ghRequest.getFromPoints().size();
            pointsStr = createPointQuery(ghRequest.getFromPoints(), "point");
        } else {
            fromCount = ghRequest.getFromPoints().size();
            toCount = ghRequest.getToPoints().size();
            pointsStr = createPointQuery(ghRequest.getFromPoints(), "from_point");
            pointsStr += "&" + createPointQuery(ghRequest.getToPoints(), "to_point");
        }

        String outArrayStr = "";
        List<String> outArraysList = new ArrayList<>(ghRequest.getOutArrays());
        if (outArraysList.isEmpty()) {
            outArraysList.add("weights");
        }

        for (String type : outArraysList) {
            if (!type.isEmpty()) {
                outArrayStr += "&";
            }

            outArrayStr += "out_array=" + type;
        }

        // TODO allow elevation for full path
        boolean hasElevation = false;
        String url = serviceUrl + "?"
                + pointsStr
                + "&" + outArrayStr
                + "&vehicle=" + ghRequest.getVehicle()
                + "&key=" + key;

        MatrixResponse matrixResponse = new MatrixResponse(
                ghRequest.getFromPoints().size(),
                ghRequest.getToPoints().size());

        try {
            String str = getJson(url);
            JSONObject json = null;
            try {
                json = new JSONObject(str);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot parse json " + str + " from " + url);
            }

            GraphHopperWeb.readErrors(matrixResponse.getErrors(), json);
            if (!matrixResponse.hasErrors()) {
                fillResponseFromJson(ghRequest, outArraysList,
                        matrixResponse, json, hasElevation);
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return matrixResponse;
    }

    private String createPointQuery(List<GHPoint> list, String pointName) {
        String pointsStr = "";
        for (GHPoint p : list) {
            if (!pointsStr.isEmpty()) {
                pointsStr += "&";
            }

            pointsStr += pointName + "=" + encode(p.lat + "," + p.lon);
        }
        return pointsStr;
    }

    public String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception ex) {
            return str;
        }
    }
}
