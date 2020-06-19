package com.qimalocl.manage.activity;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.fitsleep.sunshinelibrary.utils.ToastUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qimalocl.manage.core.common.HttpHelper;
import com.qimalocl.manage.core.common.SharedPreferencesUrls;
import com.qimalocl.manage.core.common.UIHelper;
import com.qimalocl.manage.core.common.Urls;
import com.qimalocl.manage.model.CarsBean;
import com.qimalocl.manage.model.NearbyBean;
import com.qimalocl.manage.model.ResultConsel;

import org.apache.http.Header;
import org.json.JSONArray;

//public static String host2 = HTTPS + "testnewmapi.7mate.cn/api";

public class BackUp {
}

//    private void initNearby(double latitude, double longitude){
//        RequestParams params = new RequestParams();
//        params.put("latitude",latitude);
//        params.put("longitude",longitude);
//
//        LogUtil.e("initNearby===", latitude+"==="+carmodel_id);
//
//        if(carmodel_id==1){
//            params.put("type", 1);
//            HttpHelper.get(context, Urls.nearby, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在加载");
//                        loadingDialog.show();
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            JSONArray array = new JSONArray(result.getData());
//
//                            LogUtil.e("initNearby===Bike", "==="+array.length());
//
//                            for (Marker marker : bikeMarkerList){
//                                if (marker != null){
//                                    marker.remove();
//                                }
//                            }
//                            if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                                bikeMarkerList.clear();
//                            }
//                            if (0 == array.length()){
//                                ToastUtils.showMessage("附近没有单车");
//                            } else {
//                                for (int i = 0; i < array.length(); i++){
//                                    NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
//                                    // 加入自定义标签
//                                    MarkerOptions bikeMarkerOption = new MarkerOptions().position(new LatLng(
//                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude()))).icon(bikeDescripter);
//                                    Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                    bikeMarkerList.add(bikeMarker);
//
//
//
//                                }
//                            }
//                        } else {
//                            ToastUtils.showMessage(result.getMsg());
//                        }
//                    } catch (Exception e) {
//
//                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                }
//            });
//        }else{
//            String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//            if (access_token == null || "".equals(access_token)){
//                Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//                UIHelper.goToAct(context, LoginActivity.class);
//            }else {
//                HttpHelper.get(context, Urls.nearbyEbikeScool, params, new TextHttpResponseHandler() {
//                    @Override
//                    public void onStart() {
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("正在加载");
//                                    loadingDialog.show();
//                                }
//                            }
//                        });
//                    }
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {
//
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (loadingDialog != null && loadingDialog.isShowing()){
//                                    loadingDialog.dismiss();
//                                }
//                                UIHelper.ToastError(context, throwable.toString());
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, final String responseString) {
//                        m_myHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                                    LogUtil.e("nearbyEbikeScool===", "==="+responseString);
//
//                                    if (result.getFlag().equals("Success")) {
//                                        JSONArray array = new JSONArray(result.getData());
//
//                                        if (curMarker != null){
//                                            curMarker.remove();
//                                        }
//
//                                        for (Marker marker : bikeMarkerList){
//                                            if (marker != null){
//                                                marker.remove();
//                                            }
//                                        }
//                                        if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                                            bikeMarkerList.clear();
//                                        }
//                                        if (0 == array.length()){
//                                            Toast.makeText(context,"附近没有电单车",Toast.LENGTH_SHORT).show();
//                                        }else {
//                                            for (int i = 0; i < array.length(); i++){
//                                                NearbyBean bean = JSON.parseObject(array.getJSONObject(i).toString(), NearbyBean.class);
//                                                // 加入自定义标签
//
//                                                MarkerOptions bikeMarkerOption = null;
//                                                if("4".equals(bean.getType())){
//                                                    bikeMarkerOption = new MarkerOptions().title(bean.getCodenum()+"-"+bean.getQuantity()+"%").position(new LatLng(
//                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
//                                                            .icon("1".equals(bean.getQuantity_level())?bikeDescripter_green:"2".equals(bean.getQuantity_level())?bikeDescripter_yellow:"3".equals(bean.getQuantity_level())?bikeDescripter_red:"4".equals(bean.getQuantity_level())?bikeDescripter_blue:bikeDescripter_brown);
//
//                                                }else{
//                                                    bikeMarkerOption = new MarkerOptions().title(bean.getCodenum()+"-"+bean.getQuantity()+"%").position(new LatLng(
//                                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
//                                                            .icon("1".equals(bean.getQuantity_level())?bikeDescripter_xa_green:"2".equals(bean.getQuantity_level())?bikeDescripter_xa_yellow:"3".equals(bean.getQuantity_level())?bikeDescripter_xa_red:"4".equals(bean.getQuantity_level())?bikeDescripter_xa_blue:bikeDescripter_xa_brown);
//                                                }
//
//                                                if(switcher.isChecked() || switcher_bad.isChecked()){
//                                                    if(switcher.isChecked()){
//                                                        if("2".equals(bean.getQuantity_level()) || "3".equals(bean.getQuantity_level()) || "4".equals(bean.getQuantity_level())){
//                                                            Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                                            bikeMarkerList.add(bikeMarker);
//                                                        }
//                                                    }
//
//                                                    if(switcher_bad.isChecked()){
//                                                        if("6".equals(bean.getQuantity_level())){
//                                                            Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                                            bikeMarkerList.add(bikeMarker);
//                                                        }
//                                                    }
//                                                }else{
//
//                                                    Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                                    bikeMarkerList.add(bikeMarker);
//                                                }
//
//                                                if(bean.getQuantity_level_2_count_xyt() != null){
//                                                    tvYellow_xyt.setText("黄色："+bean.getQuantity_level_2_count_xyt());
//                                                }
//                                                if(bean.getQuantity_level_3_count_xyt() != null){
//                                                    tvRed_xyt.setText("红色："+bean.getQuantity_level_3_count_xyt());
//                                                }
//
//                                                if(bean.getQuantity_level_2_count_xa() != null){
//                                                    tvYellow_xa.setText("黄色："+bean.getQuantity_level_2_count_xa());
//                                                }
//                                                if(bean.getQuantity_level_3_count_xa() != null){
//                                                    tvRed_xa.setText("红色："+bean.getQuantity_level_3_count_xa());
//                                                }
//
////                                                LogUtil.e("nearbyEbike===", bean.getCodenum()+"==="+bean.getType()+"==="+bean.getQuantity()+"==="+bean.getQuantity_level());
//
////                                    if("80001651".equals(bean.getCodenum())){
////                                        LogUtil.e("initNearby===", bean.getQuantity()+"==="+bean.getQuantity_level());
////                                    }
//
//                                            }
//                                        }
//                                    } else {
//                                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (Exception e) {
//                                }
//                                if (loadingDialog != null && loadingDialog.isShowing()){
//                                    loadingDialog.dismiss();
//                                }
//                            }
//                        });
//
//                    }
//                });
//            }
//
//        }
//    }

//    private void carsLoop(){
//        RequestParams params = new RequestParams();
//
//        int type = 0;
//        if(switcher_bad.isChecked()){
//            type = 1;
//        }
//
//        if(switcher.isChecked()){
//            if(switcher_bad.isChecked()){
//                type = 3;
//            }else{
//                type = 2;
//            }
//        }
//
//
//        params.put("type", type);  //0全部 1只看坏车 2只看低电
//
//        LogUtil.e("carsLoop===", type+"==="+carmodel_id);
//
//        HttpHelper.get(context, Urls.cars, params, new TextHttpResponseHandler() {     //TODO
//            @Override
//            public void onStart() {
////                if (loadingDialog != null && !loadingDialog.isShowing()) {
////                    loadingDialog.setTitle("正在加载");
////                    loadingDialog.show();
////                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//
//                LogUtil.e("carsLoop===fail", "==="+throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//
//                    LogUtil.e("carsLoop===0", "==="+responseString);
//
//                    JSONArray array = new JSONArray(result.getData());
//
//                    LogUtil.e("carsLoop===1", "==="+array.length());
//
//                    for (Marker marker : bikeMarkerList){
//                        if (marker != null){
//                            marker.remove();
//                        }
//                    }
//
//                    if (!bikeMarkerList.isEmpty() || 0 != bikeMarkerList.size()){
//                        bikeMarkerList.clear();
//                    }
//
//                    if (0 == array.length()){
//                        ToastUtils.showMessage("附近没有单车");
//                    } else {
//                        for (int i = 0; i < array.length(); i++){
//
//                            CarsBean bean = JSON.parseObject(array.getJSONObject(i).toString(), CarsBean.class);
//
//                            LogUtil.e("carsLoop===2", bean.getNumber()+"==="+array.getJSONObject(i).toString());
//
//                            // 加入自定义标签
//                            MarkerOptions bikeMarkerOption = null;
//
//                            int lock_id = bean.getLock_id();
//
//                            if(!"".equals(bean.getLatitude()) && !"".equals(bean.getLongitude())){
//                                if(lock_id==4 || lock_id==8){
//
//                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
//                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
//                                            .icon(bean.getLevel()==1?bikeDescripter_green:bean.getLevel()==2?bikeDescripter_yellow:bean.getLevel()==3?bikeDescripter_red:bean.getLevel()==4?bikeDescripter_blue:bikeDescripter_brown);
//
//                                }else if(lock_id==7){
//                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
//                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
//                                            .icon(bean.getLevel()==1?bikeDescripter_xa_green:bean.getLevel()==2?bikeDescripter_xa_yellow:bean.getLevel()==3?bikeDescripter_xa_red:bean.getLevel()==4?bikeDescripter_xa_blue:bikeDescripter_xa_brown);
//
//                                }else{
//                                    bikeMarkerOption = new MarkerOptions().title(bean.getNumber()).position(new LatLng(
//                                            Double.parseDouble(bean.getLatitude()),Double.parseDouble(bean.getLongitude())))
//                                            .icon(bean.getLevel()==0?bikeDescripter:bikeDescripter_bad);
//
//                                }
//
//                                Marker bikeMarker = aMap.addMarker(bikeMarkerOption);
//                                bikeMarkerList.add(bikeMarker);
//                            }
//
////                            if("40004690".equals(bean.getNumber())){
////                                LogUtil.e("cars===3", lock_id+"==="+bean.getLevel()+"==="+bean.getLatitude()+"==="+bean.getLongitude());
////                            }
////
////                            if("30005053".equals(bean.getNumber())){
////                                LogUtil.e("cars===4", lock_id+"==="+bean.getLevel()+"==="+bean.getLatitude()+"==="+bean.getLongitude());
////                            }
////
////                            if("40001".equals(bean.getNumber())){
////                                LogUtil.e("cars===5", lock_id+"==="+bean.getLevel()+"==="+bean.getLatitude()+"==="+bean.getLongitude());
////                            }
//                        }
//                    }
//
//
////                    if (result.getFlag().equals("Success")) {
////
////                    } else {
////                        ToastUtils.showMessage(result.getMsg());
////                    }
//                } catch (Exception e) {
//
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//
//
//    }

//        initNearby(latitude, longitude);

//        mapView.onResume();

//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token != null && !"".equals(access_token)){
//            rightBtn.setText("退出登录");
//        }else {
//            rightBtn.setText("登录");
//        }

//    private void lock(String result){
//
////        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
//        }else {
//            RequestParams params = new RequestParams();
////            params.put("uid",uid);
////            params.put("access_token",access_token);
//            params.put("codenum",result);
//            HttpHelper.post(context, Urls.lock, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在提交");
//                        loadingDialog.show();
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
//                }
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"恭喜您，锁定成功",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                }
//            });
//        }
//    }
//    private void unLock(String result){
//
////        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
//        }else {
//            RequestParams params = new RequestParams();
////            params.put("uid",uid);
////            params.put("access_token",access_token);
//            params.put("codenum",result);
//            HttpHelper.post(context, Urls.unLock, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在提交");
//                        loadingDialog.show();
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
//                }
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"恭喜您，解锁成功",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                    }
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            try{
//                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//                customBuilder.setTitle("温馨提示").setMessage("确认退出吗?")
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        AppManager.getAppManager().AppExit(context);
//                    }
//                });
//                customBuilder.create().show();
//                return true;
//            }catch (Exception e){
//
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//		String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//		String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//		if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//			Toast.makeText(ActivityScanerCode.this,"请先登录账号",Toast.LENGTH_SHORT).show();
//			UIHelper.goToAct(ActivityScanerCode.this, LoginActivity.class);
//		}else {
//
//		}


//    private void changKey() {
//        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
//            Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
//            return;
//        }
//        RequestParams params = new RequestParams();
//
//        LogUtil.e("changKey===", uid+"==="+access_token+"==="+codenum);
//
//        params.put("uid", uid);
//        params.put("access_token", access_token);
//        params.put("codenum", codenum);
//
//
//
//        HttpHelper.post(context, Urls.changeKey, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在提交");
//                    loadingDialog.show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        Toast.makeText(context, "恭喜您，数据提交成功", Toast.LENGTH_SHORT).show();
////                        BaseApplication.getInstance().getIBLE().setChangKey(false);
//
//
//
////                        isChangePsd = true;
//                        loadingDialog2 = DialogUtils.getLoadingDialog(context, "正在修改密码");
//                        loadingDialog2.show();
//
////                        loadingDialog.setTitle("正在修改密码");
////                        loadingDialog.show();
//
//                        isPwd = false;
//
//                        byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
//                                Config.passwordnew[2], Config.passwordnew[3],
//                                Config.passwordnew[4], Config.passwordnew[5]};
//                        BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD, bytes);
//
//                        m_myHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
////                                byte[] bytes = {Config.passwordnew[0], Config.passwordnew[1],
////                                        Config.passwordnew[2], Config.passwordnew[3],
////                                        Config.passwordnew[4], Config.passwordnew[5]};
//                                byte[] bytes = {Config.password[0], Config.password[1],
//                                        Config.password[2], Config.password[3],
//                                        Config.password[4], Config.password[5]};
//
//                                BaseApplication.getInstance().getIBLE().setPassword(Order.TYPE.RESET_PASSWORD2, bytes);
//                            }
//                        }, 2000);
//
//                        isPwd = true;
//
//                    } else {
//                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss();
//                }
//
//            }
//        });
//    }
//
//    private void changPsd() {
//        String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
//            Toast.makeText(context, "请先登录您的账号", Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
//            return;
//        }
//        RequestParams params = new RequestParams();
//        params.put("uid", uid);
//        params.put("access_token", access_token);
//        params.put("codenum", codenum);
//        params.put("latitude", SharedPreferencesUrls.getInstance().getString("latitude", ""));
//        params.put("longitude", SharedPreferencesUrls.getInstance().getString("longitude", ""));
//        HttpHelper.post(context, Urls.changePsd, params, new TextHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                if (loadingDialog2 != null && !loadingDialog2.isShowing()) {
//                    loadingDialog2.setTitle("正在提交");
//                    loadingDialog2.show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog2 != null && loadingDialog2.isShowing()) {
//                    loadingDialog2.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        BaseApplication.getInstance().getIBLE().setChangPsd(true);
//                        Toast.makeText(context, "恭喜您，密码提交成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loadingDialog2 != null && loadingDialog2.isShowing()) {
//                    loadingDialog2.dismiss();
//                }
//            }
//        });
//    }

//    private void addCar2(String result,String codenum){
//
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//            UIHelper.goToAct(context, LoginActivity.class);
//        }else {
//            RequestParams params = new RequestParams();
//            params.put("uid",uid);
//            params.put("access_token",access_token);
//            params.put("tokencode",result);    //二维码链接地址
//            params.put("codenum",codenum);     //车辆编号
//            params.put("macinfo",mac);    //mac地址
//
//            LogUtil.e("addCar===", uid+"==="+access_token+"==="+result+"==="+codenum+"==="+mac);
//
//            HttpHelper.post(context, Urls.addCar, params, new TextHttpResponseHandler() {
//                @Override
//                public void onStart() {
//                    if (loadingDialog != null && !loadingDialog.isShowing()) {
//                        loadingDialog.setTitle("正在提交");
//                        loadingDialog.show();
//                    }
//                }
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//                    UIHelper.ToastError(context, throwable.toString());
//                }
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    try {
//                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                        if (result.getFlag().equals("Success")) {
//                            Toast.makeText(context,"恭喜您，入库成功",Toast.LENGTH_SHORT).show();
//                            //修改密钥
////                            loadingDialog = DialogUtils.getLoadingDialog(context, "正在修改密钥");
////                            loadingDialog.show();
////                            byte[] bytes = {Config.newKey[0],
////                                    Config.newKey[1], Config.newKey[2], Config.newKey[3], Config.newKey[4],
////                                    Config.newKey[5], Config.newKey[6], Config.newKey[7]};
////                            BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
////                            m_myHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    byte[] bytes1 = {Config.newKey[8],
////                                            Config.newKey[9], Config.newKey[10], Config.newKey[11], Config.newKey[12],
////                                            Config.newKey[13], Config.newKey[14], Config.newKey[15]};
////                                    BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
////                                }
////                            }, 2000);
//
//
////                            byte[] bytes = {Config.newKey2[0],
////                                    Config.newKey2[1], Config.newKey2[2], Config.newKey2[3], Config.newKey2[4],
////                                    Config.newKey2[5], Config.newKey2[6], Config.newKey2[7]};
////                            BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY, bytes);
////
////                            m_myHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    byte[] bytes1 = {Config.newKey2[8],
////                                            Config.newKey2[9], Config.newKey2[10], Config.newKey2[11], Config.newKey2[12],
////                                            Config.newKey2[13], Config.newKey2[14], Config.newKey2[15]};
////                                    BaseApplication.getInstance().getIBLE().setKey(Order.TYPE.RESET_KEY2, bytes1);
////                                }
////                            }, 2000);
//
////                            return;
//                        }else {
//                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        LogUtil.e("addCar===eee", "==="+e);
//
//                    }
//
//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }
//
//                }
//            });
//        }
//    }

//btWx.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//        String bikeName = edbikeNum.getText().toString().trim();
//        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//        UIHelper.goToAct(context,LoginActivity.class);
//        Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//        }else {
//        if (bikeName == null || "".equals(bikeName)){
//        ToastUtils.showMessage("请输入车编号");
//        return;
//        }
//        if (Build.VERSION.SDK_INT >= 23) {
//        int checkPermission = LockStorageActivity.this.checkSelfPermission(Manifest.permission.CAMERA);
//        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
//        requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
//        } else {
//        CustomDialog.Builder customBuilder = new CustomDialog.Builder(LockStorageActivity.this);
//        customBuilder.setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
//        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//public void onClick(DialogInterface dialog, int which) {
//        dialog.cancel();
//        }
//        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
//public void onClick(DialogInterface dialog, int which) {
//        dialog.cancel();
//        LockStorageActivity.this.requestPermissions(
//        new String[] { Manifest.permission.CAMERA },
//        100);
//        }
//        });
//        customBuilder.create().show();
//        }
//        return;
//        }
//        }
//        try {
//        Intent intent = new Intent();
//
//        intent.setClass(context, ActivityScanerCode.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isChangeKey",false);
//        intent.putExtra("isAdd",true);
//
//        startActivityForResult(intent, 1);
//
////                        37===010580b015b14bb200a444f8a765f4a1===http://www.7mate.cn/app.php?randnum=bqk0008804===60008804===CB:12:F0:0C:60:33
//
////                        addCar("http://www.7mate.cn/app.php?randnum=bqk0008804","60008804");
//
//
//        } catch (Exception e) {
//        UIHelper.showToastMsg(context, "相机打开失败,请检查相机是否可正常使用", R.drawable.ic_error);
//        }
//        }
//        }
//        });