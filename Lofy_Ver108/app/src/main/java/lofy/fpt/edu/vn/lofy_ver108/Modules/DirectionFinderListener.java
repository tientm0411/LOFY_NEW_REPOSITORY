package lofy.fpt.edu.vn.lofy_ver108.Modules;

import java.util.List;

import lofy.fpt.edu.vn.lofy_ver108.entity.Route;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
