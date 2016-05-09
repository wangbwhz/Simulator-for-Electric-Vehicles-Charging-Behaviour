package com.graphhopper.api;

import com.squareup.okhttp.MediaType;

/**
 *
 * @author Peter Karich
 */
public class GraphHopperMatrixWeb {

    public static final MediaType MT_JSON = MediaType.parse("application/json; charset=utf-8");
    private final GHMatrixAbstractRequester requester;
    private String key = "";

    public GraphHopperMatrixWeb() {
        this(new GHMatrixBatchRequester());
    }

    public GraphHopperMatrixWeb(String serviceUrl) {
        this(new GHMatrixBatchRequester(serviceUrl));
    }

    public GraphHopperMatrixWeb(GHMatrixAbstractRequester requester) {
        this.requester = requester;
    }

    public GraphHopperMatrixWeb setKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("Key cannot be empty");
        }

        this.key = key;
        return this;
    }

    public MatrixResponse route(GHMRequest request) {
        return requester.route(request, key);
    }
}
