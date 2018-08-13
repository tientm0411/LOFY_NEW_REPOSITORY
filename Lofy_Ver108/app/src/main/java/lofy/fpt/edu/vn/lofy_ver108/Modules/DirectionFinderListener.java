package lofy.fpt.edu.vn.lofy_ver108.Modules;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
    void onDirectionFinderSuccessGray(List<Route> routes);
}
