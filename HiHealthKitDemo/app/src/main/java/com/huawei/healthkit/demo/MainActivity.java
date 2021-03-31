/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.healthkit.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawei.hihealth.error.HiHealthError;
import com.huawei.hihealth.listener.ResultCallback;
import com.huawei.hihealthkit.HiHealthDataQuery;
import com.huawei.hihealthkit.HiHealthDataQueryOption;
import com.huawei.hihealthkit.auth.HiHealthAuth;
import com.huawei.hihealthkit.auth.HiHealthOpenPermissionType;
import com.huawei.hihealthkit.auth.IAuthorizationListener;
import com.huawei.hihealthkit.data.HiHealthKitConstant;
import com.huawei.hihealthkit.data.HiHealthPointData;
import com.huawei.hihealthkit.data.HiHealthSetData;
import com.huawei.hihealthkit.data.store.HiHealthDataStore;
import com.huawei.hihealthkit.data.store.HiRealTimeListener;
import com.huawei.hihealthkit.data.store.HiSportDataCallback;
import com.huawei.hihealthkit.data.type.HiHealthPointType;
import com.huawei.hihealthkit.data.type.HiHealthSetType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Third-party Demo program page
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Button btn11, btn12, btn13, btn21, btn22, btn23, btn31, btn32, btn33, btn41, btn42, btn43, btn44, btn45,
            btn50, btn51, btn52, btn53, btn54;

    private TextView tvRestult;

    private MyHandler mHandler = new MyHandler();

    private Context mContext;

    private HiSportDataCallback sportDataCallback = new HiSportDataCallback() {
        @Override
        public void onResult(int resultCode) {
            Log.i(TAG, "resultCode:" + resultCode);
            combineResult(resultCode, "status changed");
        }

        @Override
        public void onDataChanged(int state, Bundle bundle) {
            StringBuilder sb = new StringBuilder();
            Log.i(TAG, "onChange state: " + state);
            if (state == 2) {
                for (String key : bundle.keySet()) {
                    sb.append(key + " : " + String.valueOf(bundle.get(key))).append(" ");
                }
            }
            combineResult(state, sb.toString());
        }
    };

    HiRealTimeListener heartCallback = new HiRealTimeListener() {
        @Override
        public void onResult(int state) {
            Log.i(TAG, " onResult state:" + state);
            combineResult(state, "state changed");
        }

        @Override
        public void onChange(int resultCode, String value) {
            StringBuilder sb = new StringBuilder();
            Log.i(TAG, " onChange resultCode: " + resultCode + " value: " + value);
            if (resultCode == HiHealthError.SUCCESS) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    sb.append("hri_info : " + jsonObject.getInt("hri_info"));
                    sb.append("hr_info : " + jsonObject.getInt("hr_info"));
                    sb.append("hrsqi_info : " + jsonObject.getInt("hrsqi_info"));
                    sb.append("time_info : " + jsonObject.getLong("time_info"));
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException e" + e.getMessage());
                }
            }
            combineResult(resultCode, sb.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindDataView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] userAllowTypesToRead =
                    new int[] {HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_USER_PROFILE_INFORMATION,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_USER_PROFILE_FEATURE,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_DATA_POINT_STEP_SUM,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_DATA_SET_RUN_METADATA,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_DATA_SET_WEIGHT,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_REALTIME_HEARTRATE,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_READ_DATA_REAL_TIME_SPORT,
                        HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_WRITE_DATA_SET_WEIGHT,};
                int[] userAllowTypesToWrite =
                    new int[] {HiHealthOpenPermissionType.HEALTH_OPEN_PERMISSION_TYPE_WRITE_DATA_SET_WEIGHT};
                HiHealthAuth.requestAuthorization(mContext, userAllowTypesToWrite, userAllowTypesToRead,
                    new IAuthorizationListener() {
                        @Override
                        public void onResult(int resultCode, Object object) {
                            Log.i(TAG, "requestAuthorization onResult:" + resultCode);
                            if (resultCode == HiHealthError.SUCCESS) {
                                Log.i(TAG, "requestAuthorization success resultContent:" + object);
                            }
                            combineResult(resultCode, object);
                        }
                    });
            }
        });
        btn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getGender(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object gender) {
                        combineResult(resultCode, gender);
                        if (resultCode == HiHealthError.SUCCESS) {
                            int value = (int) gender;
                        }
                    }
                });
            }
        });
        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getBirthday(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object birthday) {
                        combineResult(resultCode, birthday);
                        if (resultCode == HiHealthError.SUCCESS) {
                            // For example, "1978-05-20" would return 19780520
                            int value = (int) birthday;
                        }
                    }
                });
            }
        });
        btn21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getHeight(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object height) {
                        combineResult(resultCode, height);
                        if (resultCode == HiHealthError.SUCCESS) {
                            int value = (int) height;
                        }
                    }
                });
            }
        });
        btn22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getWeight(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object weight) {
                        combineResult(resultCode, weight);
                        if (resultCode == HiHealthError.SUCCESS) {
                            int value = (int) weight;
                        }
                    }
                });
            }
        });
        btn23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeout = 0;
                long endTime = System.currentTimeMillis();
                long startTime = endTime - 1000 * 60 * 60 * 24 * 30L;
                HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthPointType.DATA_POINT_STEP_SUM,
                    startTime, endTime, new HiHealthDataQueryOption());
                HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, timeout, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object data) {
                        Log.i(TAG, "query steps resultCode: " + resultCode);
                        String result = "";
                        if (resultCode == HiHealthError.SUCCESS) {
                            List dataList = (ArrayList) data;
                            if (dataList.size() >= 1) {
                                HiHealthPointData pointData = (HiHealthPointData) dataList.get(dataList.size() - 1);
                                result = result + String.valueOf(pointData.getValue());
                            }
                        }
                        combineResult(resultCode, result);
                    }
                });
            }
        });
        btn31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long endTime = System.currentTimeMillis();
                final long startTime = endTime - 1000L * 60 * 60 * 24 * 30L; // Check Data of the latest 30 days
                HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthPointType.DATA_POINT_STEP_SUM,
                    startTime, endTime, new HiHealthDataQueryOption());
                HiHealthDataStore.getCount(mContext, hiHealthDataQuery, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object data) {
                        combineResult(resultCode, data);
                        if (resultCode == HiHealthError.SUCCESS) {
                            int count = (int) data;
                            Log.i(TAG, "walk track number: " + count);
                        }
                    }
                });
            }
        });
        btn32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    int timeout = 0;
                    long endTime = System.currentTimeMillis();
                    long startTime = endTime - 1000 * 60 * 60 * 24 * 30L;
                    HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthSetType.DATA_SET_RUN_METADATA,
                        startTime, endTime, new HiHealthDataQueryOption());
                    HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, timeout, new ResultCallback() {
                        @Override
                        public void onResult(int i, Object data) {
                            StringBuilder sb = new StringBuilder();
                            List dataList = (List) data;
                            if (dataList.size() >= 1) {
                                HiHealthSetData hiHealthData = (HiHealthSetData) dataList.get(dataList.size() - 1);
                                Map map = hiHealthData.getMap();
                                sb.append("start time : " + hiHealthData.getStartTime());
                                sb.append("total_time : " + map.get("total_time"));
                                sb.append("total_distance : " + map.get("total_distance"));
                                sb.append("total_calories : " + map.get("total_calories"));
                                sb.append("step : " + map.get("step"));
                                sb.append("average_pace : " + map.get("average_pace"));
                                sb.append("average_speed : " + map.get("average_speed"));
                                sb.append("average_step_rate : " + map.get("average_step_rate"));
                                sb.append("step_distance : " + map.get("step_distance"));
                                sb.append("average_heart_rate : " + map.get("average_heart_rate"));
                                sb.append("total_altitude : " + map.get("total_altitude"));
                                sb.append("total_descent : " + map.get("total_descent"));
                            }
                            combineResult(i, sb.toString());
                        }
                    });
                }
            }
        });
        btn33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    int timeout = 0;
                    long endTime = System.currentTimeMillis();
                    long startTime = endTime - 86400000L * 30;
                    HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthSetType.DATA_SET_WEIGHT_EX,
                        startTime, endTime, new HiHealthDataQueryOption());
                    HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, timeout, new ResultCallback() {
                        @Override
                        public void onResult(int resultCode, final Object data) {
                            StringBuilder sb = new StringBuilder();
                            if (resultCode == HiHealthError.SUCCESS) {
                                Log.i(TAG, "query not null,enter set data");
                                List dataList = (List) data;
                                if (dataList.size() >= 1) {
                                    HiHealthSetData hiHealthData = (HiHealthSetData) dataList.get(dataList.size() - 1);
                                    Map map = hiHealthData.getMap();
                                    sb.append("data start time : " + hiHealthData.getStartTime());
                                    sb.append("data end time : " + hiHealthData.getEndTime());
                                    sb.append("weight : weight " + map.get(HiHealthPointType.DATA_POINT_WEIGHT));
                                    sb.append("weight : bmi " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BMI));
                                    sb.append("weight : muscle volume "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MUSCLES));
                                    sb.append("weight : basic metabolism "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BMR));
                                    sb.append(
                                        "weight : moisture " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MOISTURE));
                                    sb.append("weight : visceral fat "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_FATLEVEL));
                                    sb.append("weight : bone mineral content "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BONE_MINERAL));
                                    sb.append(
                                        "weight : protein " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_PROTEIN));
                                    sb.append("weight : body score "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYSCORE));
                                    sb.append("weight : physical age "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYAGE));
                                    sb.append("weight : body fat percentage "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYFAT));
                                    sb.append("weight : body fat scale "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_IMPEDANCE));
                                    sb.append("weight : moisture percentage "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MOISTURERATE));
                                }
                            }
                            combineResult(resultCode, sb.toString());
                        }
                    });
                }
            }
        });
        btn41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.startReadingHeartRate(mContext, heartCallback);
            }
        });

        btn44.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.stopReadingHeartRate(mContext, heartCallback);
            }
        });

        btn42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.registerSportData(mContext, sportDataCallback);
            }
        });

        btn45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.unregisterSportData(mContext, sportDataCallback);
            }
        });

        btn43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                map.put(HiHealthPointType.DATA_POINT_WEIGHT, 70.5D);
                map.put(HiHealthPointType.DATA_POINT_WEIGHT_BMI, 18.8D);
                map.put(HiHealthPointType.DATA_POINT_WEIGHT_MUSCLES, 33.5D);
                final long endTime = System.currentTimeMillis();
                final long startTime = endTime;
                HiHealthSetData hiHealthSetData =
                    new HiHealthSetData(HiHealthSetType.DATA_SET_WEIGHT_EX, map, startTime, endTime);
                HiHealthDataStore.saveSample(mContext, hiHealthSetData, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object object) {
                        combineResult(resultCode, object);
                        Log.i(TAG, "saveSample resultCode is " + resultCode);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "saveSample resultList: " + object);
                        }
                    }
                });
            }
        });
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getApiLevel(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object data) {
                        combineResult(resultCode, data);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "get api level success " + data);
                        }
                    }
                });
            }
        });
        btn51.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                final long endTime = System.currentTimeMillis();
                final long startTime = endTime - 1000L * 60 * 60 * 24;
                HiHealthSetData hiHealthSetData =
                    new HiHealthSetData(HiHealthSetType.DATA_SET_WEIGHT_EX, map, startTime, endTime);
                HiHealthDataStore.deleteSample(mContext, hiHealthSetData, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object data) {
                        Log.i(TAG, "delete sample of the latest 24h onResult: " + resultCode);
                    }
                });
            }
        });
        btn52.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sportType = HiHealthKitConstant.SPORT_TYPE_RUN;
                HiHealthDataStore.startSport(mContext, sportType, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object message) {
                        combineResult(resultCode, message);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "start sport success");
                        }
                    }
                });
            }
        });
        btn53.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.stopSport(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object message) {
                        combineResult(resultCode, message);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "stop sport success");
                        }
                    }
                });
            }
        });
        btn54.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder sb = new StringBuilder();
                final long endTime = System.currentTimeMillis();
                final long startTime = 0; // 开始时间为0，可以查询全部数据
                HiHealthDataQuery hiHealthDataQuery = new
                        HiHealthDataQuery(HiHealthPointType.DATA_POINT_STRESS, startTime,
                        endTime, new HiHealthDataQueryOption());
                HiHealthDataStore.getCount(mContext, hiHealthDataQuery, new ResultCallback() {
                    @Override
                    public void onResult(final int resultCode, final Object data) {
                        if (resultCode == HiHealthError.SUCCESS) {
                            final int count = (int) data;
                            //分页数
                            int pages = 3;
                            //查询数据条数限制在limit条以内
                            int limit = count / pages;
                            //数据从offset条开始查询
                            int offset = 0;
                            //根据数据产生时间的升序进行查询
                            int order = 0;
                            //指定分页数
                            for (int i = 0; i < pages; i++) {
                                offset = limit * i;
                                //如果是最后一次，查出剩下的
                                if (i == pages - 1) {
                                    limit = count - offset;
                                }
                                HiHealthDataQuery hiHealthDataQuery = new
                                        HiHealthDataQuery(HiHealthPointType.DATA_POINT_STRESS,
                                        startTime, endTime, new HiHealthDataQueryOption(limit, offset, order));
                                HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, 0, new ResultCallback() {
                                    @Override
                                    public void onResult(int resultCode, Object data) {
                                        if (resultCode == HiHealthError.SUCCESS) {
                                            if (data instanceof List){
                                                List dataList = (List) data;
                                                for (Object pointData : dataList){
                                                    if (pointData instanceof HiHealthPointData){
                                                        HiHealthPointData hiHealthPointData = (HiHealthPointData) pointData;
                                                        sb.append(" start time:"+hiHealthPointData.getStartTime());
                                                        sb.append(" end time:"+hiHealthPointData.getEndTime());
                                                        sb.append(" value:"+hiHealthPointData.getDoubleValue());
                                                    }
                                                }
                                            }
                                        }
                                        combineResult(resultCode, sb.toString());
                                        if (resultCode == HiHealthError.SUCCESS) {
                                            Log.i(TAG, "pagination query success");
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }


    private void bindDataView() {
        mContext = this;
        btn11 = findViewById(R.id.btn_click_11);
        btn12 = findViewById(R.id.btn_click_12);
        btn13 = findViewById(R.id.btn_click_13);
        btn21 = findViewById(R.id.btn_click_21);
        btn22 = findViewById(R.id.btn_click_22);
        btn23 = findViewById(R.id.btn_click_23);
        btn31 = findViewById(R.id.btn_click_31);
        btn32 = findViewById(R.id.btn_click_32);
        btn33 = findViewById(R.id.btn_click_33);
        btn41 = findViewById(R.id.btn_click_41);
        btn42 = findViewById(R.id.btn_click_42);
        btn43 = findViewById(R.id.btn_click_43);
        btn44 = findViewById(R.id.btn_click_44);
        btn45 = findViewById(R.id.btn_click_45);
        btn50 = findViewById(R.id.btn_click_50);
        btn51 = findViewById(R.id.btn_click_51);
        btn52 = findViewById(R.id.btn_click_52);
        btn53 = findViewById(R.id.btn_click_53);
        btn54 = findViewById(R.id.btn_click_54);
        tvRestult = findViewById(R.id.result_view);

    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null) {
                return;
            }
            handleMessageInfo(msg);
        }

        private void handleMessageInfo(Message msg) {
            String deviceList01 = String.valueOf(msg.obj);
            tvRestult.setText(deviceList01);
        }
    }

    public void sendMsg(String result) {
        Message message = Message.obtain();
        message.obj = result;
        mHandler.sendMessage(message);
    }

    public void combineResult(int resultCode, Object object) {
        StringBuilder sb = new StringBuilder();
        sb.append("resultCode = ").append(resultCode);
        sb.append(" result = ").append(object);
        sendMsg(sb.toString());
    }
}