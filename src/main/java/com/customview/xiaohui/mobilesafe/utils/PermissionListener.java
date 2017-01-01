package com.customview.xiaohui.mobilesafe.utils;

import java.util.List;

/**
 * Created by wizardev on 2016/12/28.
 */

public interface PermissionListener {
    void onGranted();
    void onDenied(List<String> s);
}
