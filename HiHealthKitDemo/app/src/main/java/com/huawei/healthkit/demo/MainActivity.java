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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hihealth.CharacteristicConstant;
import com.huawei.hihealth.HiHealthDataKey;
import com.huawei.hihealth.StartSportParam;
import com.huawei.hihealth.error.HiHealthError;
import com.huawei.hihealth.listener.ResultCallback;
import com.huawei.hihealth.model.DurationGoal;
import com.huawei.hihealth.model.EventTypeInfo;
import com.huawei.hihealth.model.Goal;
import com.huawei.hihealth.model.GoalInfo;
import com.huawei.hihealth.model.MetaData;
import com.huawei.hihealth.model.MetricGoal;
import com.huawei.hihealth.model.ReceiverFilter;
import com.huawei.hihealth.model.Recurrence;
import com.huawei.hihealth.model.SampleEvent;
import com.huawei.hihealth.model.Subscriber;
import com.huawei.hihealthkit.HiHealthDataQuery;
import com.huawei.hihealthkit.HiHealthDataQueryOption;
import com.huawei.hihealthkit.data.HiHealthAtomicScope;
import com.huawei.hihealthkit.data.HiHealthExtendScope;
import com.huawei.hihealthkit.data.HiHealthKitConstant;
import com.huawei.hihealthkit.data.HiHealthPointData;
import com.huawei.hihealthkit.data.HiHealthSessionData;
import com.huawei.hihealthkit.data.HiHealthSetData;
import com.huawei.hihealthkit.data.store.HiHealthDataStore;
import com.huawei.hihealthkit.data.store.HiRealTimeCallback;
import com.huawei.hihealthkit.data.store.HiRealTimeListener;
import com.huawei.hihealthkit.data.store.HiSportDataCallback;
import com.huawei.hihealthkit.data.type.HiHealthPointType;
import com.huawei.hihealthkit.data.type.HiHealthSessionType;
import com.huawei.hihealthkit.data.type.HiHealthSetType;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Third-party Demo program page
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String PENDING_FILTER = "com.healthkit.test.receive.action";

    private static final int REQUEST_SIGN_IN_LOGIN = 8888;

    private Button signInBtn;

    private Button getGenderBtn;

    private Button getBirthdayBtn;

    private Button getHeightBtn;

    private Button getWeightBtn;

    private Button execQueryStepBtn;

    private Button getCountStepBtn;

    private Button execQueryRunBtn;

    private Button execQueryWeightBtn;

    private Button startReadingHeartRateBtn;

    private Button registerSportDataBtn;

    private Button saveSampleWeightBtn;

    private Button stopReadingHeartRateBtn;

    private Button unregisterSportDataBtn;

    private Button getApiLevelBtn;

    private Button deleteSampleWeightBtn;

    private Button startSportBtn;

    private Button stopSportBtn;

    private Button pauseSportBtn;

    private Button resumeSportBtn;

    private Button startSportExBtn;

    private Button sendDeviceControlBtn;

    private Button getCountPaginationBtn;

    private Button saveSampleCoreSleepBtn;

    private Button execQueryCoreSleepBtn;

    private Button subscribeDataExBtn;

    private Button unsubscribeDataExBtn;

    private TextView tvResult;

    private MyHandler mHandler = new MyHandler();

    private Context mContext;

    private BroadcastReceiver pendingReceiver;

    private ReceiverFilter filter;

    private Subscriber subscriber;

    private MetricGoal stepMetricGoal;

    private Recurrence recurrence;

    private List<Goal> goals;

    private EventTypeInfo goalInfo;

    private String openId;

    private HiSportDataCallback sportDataCallback = new HiSportDataCallback() {
        @Override
        public void onResult(int resultCode) {
            Log.i(TAG, "resultCode:" + resultCode);
            combineResult(resultCode, "status changed");
        }

        @Override
        public void onDataChanged(int state, Bundle bundle) {
            Log.i(TAG, "onChange state: " + state);
            if (state == 2) {
                Log.i(TAG, String.valueOf(bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_CALORIE)));
                Log.i(TAG, String.valueOf(bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_STEP)));
                Log.i(TAG, "heart rate : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_HEARTRATE));
                Log.i(TAG, "distance : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_DISTANCE));
                Log.i(TAG, "duration : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_DURATION));
                Log.i(TAG, "calorie : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_CALORIE));
                Log.i(TAG, "totalSteps : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_TOTAL_STEPS));
                Log.i(TAG, "totalCreep : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_TOTAL_CREEP));
                Log.i(TAG, "totalDescent : " + bundle.getInt(HiHealthKitConstant.BUNDLE_KEY_TOTAL_DESCENT));

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("state", state);

                    for (String key : bundle.keySet()) {
                        jsonObject.put(key, bundle.get(key));
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Exception message:" + e.getMessage());
                }
                sendMsg(jsonObject.toString());
            }
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
                    sb.append(", hr_info : " + jsonObject.getInt("hr_info"));
                    sb.append(", hrsqi_info : " + jsonObject.getInt("hrsqi_info"));
                    sb.append(", time_info : " + jsonObject.getLong("time_info"));
                    sb.append(", heartRateCredibility :" + jsonObject.getInt("heartRateCredibility"));
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

    private void bindDataView() {
        mContext = this;
        signInBtn = findViewById(R.id.btn_signIn);
        getGenderBtn = findViewById(R.id.btn_getGender);
        getBirthdayBtn = findViewById(R.id.btn_getBirthday);
        getHeightBtn = findViewById(R.id.btn_getHeight);
        getWeightBtn = findViewById(R.id.btn_getWeight);
        execQueryStepBtn = findViewById(R.id.btn_execQuery_Step);
        getCountStepBtn = findViewById(R.id.btn_getCount_Step);
        execQueryRunBtn = findViewById(R.id.btn_execQuery_Run);
        execQueryWeightBtn = findViewById(R.id.btn_execQuery_Weight);
        startReadingHeartRateBtn = findViewById(R.id.btn_startReadingHeartRate);
        registerSportDataBtn = findViewById(R.id.btn_registerSportData);
        saveSampleWeightBtn = findViewById(R.id.btn_saveSample_Weight);
        stopReadingHeartRateBtn = findViewById(R.id.btn_stopReadingHeartRate);
        unregisterSportDataBtn = findViewById(R.id.btn_unregisterSportData);
        getApiLevelBtn = findViewById(R.id.btn_getApiLevel);
        deleteSampleWeightBtn = findViewById(R.id.btn_deleteSample_Weight);
        startSportBtn = findViewById(R.id.btn_startSport);
        stopSportBtn = findViewById(R.id.btn_stopSport);
        pauseSportBtn = findViewById(R.id.btn_pauseSport);
        resumeSportBtn = findViewById(R.id.btn_resumeSport);
        startSportExBtn = findViewById(R.id.btn_startSportEx);
        sendDeviceControlBtn = findViewById(R.id.btn_sendDeviceControlinstruction);
        getCountPaginationBtn = findViewById(R.id.btn_getCount_pagination);
        tvResult = findViewById(R.id.result_view);
        saveSampleCoreSleepBtn = findViewById(R.id.btn_saveSample_CoreSleep);
        execQueryCoreSleepBtn = findViewById(R.id.btn_execQuery_CoreSleep);
        subscribeDataExBtn = findViewById(R.id.btn_subscribedataEx);
        unsubscribeDataExBtn = findViewById(R.id.btn_unsubscribedataEx);
    }

    private void setOnClickListener() {
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        getGenderBtn.setOnClickListener(new View.OnClickListener() {
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
        getBirthdayBtn.setOnClickListener(new View.OnClickListener() {
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
        getHeightBtn.setOnClickListener(new View.OnClickListener() {
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
        getWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.getWeight(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object weight) {
                        combineResult(resultCode, weight);
                    }
                });
            }
        });
        execQueryStepBtn.setOnClickListener(new View.OnClickListener() {
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
        getCountStepBtn.setOnClickListener(new View.OnClickListener() {
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
        execQueryRunBtn.setOnClickListener(new View.OnClickListener() {
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
        execQueryWeightBtn.setOnClickListener(new View.OnClickListener() {
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
                                    sb.append(", data end time : " + hiHealthData.getEndTime());
                                    sb.append(", weight : weight " + map.get(HiHealthPointType.DATA_POINT_WEIGHT));
                                    sb.append(", weight : bmi " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BMI));
                                    sb.append(", weight : muscle volume "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MUSCLES));
                                    sb.append(", weight : basic metabolism "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BMR));
                                    sb.append(
                                        ", weight : moisture " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MOISTURE));
                                    sb.append(" weight : visceral fat "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_FATLEVEL));
                                    sb.append(", weight : bone mineral content "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BONE_MINERAL));
                                    sb.append(
                                        ", weight : protein " + map.get(HiHealthPointType.DATA_POINT_WEIGHT_PROTEIN));
                                    sb.append(", weight : body score "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYSCORE));
                                    sb.append(", weight : physical age "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYAGE));
                                    sb.append(", weight : body fat percentage "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_BODYFAT));
                                    sb.append(", weight : body fat scale "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_IMPEDANCE));
                                    sb.append(", weight : moisture percentage "
                                        + map.get(HiHealthPointType.DATA_POINT_WEIGHT_MOISTURERATE));
                                }
                            }
                            combineResult(resultCode, sb.toString());
                        }
                    });
                }
            }
        });
        startReadingHeartRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.startReadingHeartRate(mContext, heartCallback);
            }
        });

        stopReadingHeartRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.stopReadingHeartRate(mContext, heartCallback);
            }
        });

        registerSportDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.registerSportData(mContext, sportDataCallback);
            }
        });

        unregisterSportDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.unregisterSportData(mContext, sportDataCallback);
            }
        });

        saveSampleWeightBtn.setOnClickListener(new View.OnClickListener() {
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
        getApiLevelBtn.setOnClickListener(new View.OnClickListener() {
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
        deleteSampleWeightBtn.setOnClickListener(new View.OnClickListener() {
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
                        combineResult(resultCode, data);
                        Log.i(TAG, "delete sample of the latest 24h onResult: " + resultCode);
                    }
                });
            }
        });
        startSportBtn.setOnClickListener(new View.OnClickListener() {
            AlertDialog dialog;
            int index;
            int sportType;
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.settings_parameter);
                    builder.setSingleChoiceItems(R.array.sport_types_name, index, (dialog, which) -> index = which);
                    builder.setPositiveButton(R.string.dialog_positive_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (index) {
                                case 0:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_WALK;
                                    break;
                                case 1:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_RUN;
                                    break;
                                case 2:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_BIKE;
                                    break;
                                case 3:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_TREADMILL;
                                    break;
                                case 4:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_ROW_MACHINE;
                                    break;
                                case 5:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_INDOOR_BIKE;
                                    break;
                                case 6:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_CROSS_TRAINER;
                                    break;
                                case 7:
                                    sportType = HiHealthKitConstant.SPORT_TYPE_OTHER_SPORT;
                                    break;
                                default:
                            }
                            Log.d(TAG, "sport type = " + sportType);
                            HiHealthDataStore.startSport(mContext, sportType, new ResultCallback() {
                                @Override
                                public void onResult(int resultCode, Object message) {
                                    combineResult(resultCode, message);
                                    if (resultCode == HiHealthError.SUCCESS) {
                                        Log.i(TAG, "start sport success");
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_negative_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.setCancelable(false);
                }
                dialog.show();
            }
        });
        stopSportBtn.setOnClickListener(new View.OnClickListener() {
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
        pauseSportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.pauseSport(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object message) {
                        combineResult(resultCode, message);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "pause sport success");
                        }
                    }
                });
            }
        });
        resumeSportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.resumeSport(mContext, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object message) {
                        combineResult(resultCode, message);
                        if (resultCode == HiHealthError.SUCCESS) {
                            Log.i(TAG, "resume sport success");
                        }
                    }
                });
            }
        });
        startSportExBtn.setOnClickListener(new View.OnClickListener() {
            View dialogView;
            AlertDialog dialog;
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.settings_parameter);
                    builder.setPositiveButton(R.string.dialog_positive_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogView != null) {
                                // Init view
                                EditText macAddressView = dialogView.findViewById(R.id.et_mac_address);
                                EditText deviceTypeView = dialogView.findViewById(R.id.et_device_type);
                                EditText sportTypeView = dialogView.findViewById(R.id.et_sport_type);
                                RadioGroup ftmpView = dialogView.findViewById(R.id.rg_ftmp);
                                RadioGroup backgroundSportView = dialogView.findViewById(R.id.rg_background_sport);

                                // Obtaining Input Data
                                String macAddress = macAddressView.getText().toString().trim();
                                String deviceType = deviceTypeView.getText().toString().trim();
                                String sportType = sportTypeView.getText().toString().trim();
                                int isSupportedFtmp = getRadioGroupResult(ftmpView);
                                int isBackgroundSport = getRadioGroupResult(backgroundSportView);

                                if (TextUtils.isEmpty(deviceType)) {
                                    deviceType = "0";
                                }
                                if (TextUtils.isEmpty(sportType)) {
                                    sportType = "0";
                                }

                                // Invoke the startSportEx method
                                StartSportParam param = new StartSportParam(macAddress, isSupportedFtmp,
                                    Integer.parseInt(deviceType),  Integer.parseInt(sportType));
                                param.putInt(HiHealthDataKey.IS_BACKGROUND, isBackgroundSport);
                                HiHealthDataStore.startSportEx(mContext, param, new ResultCallback() {
                                    @Override
                                    public void onResult(int resultCode, Object data) {
                                        combineResult(resultCode, data);
                                        if (resultCode == HiHealthError.SUCCESS) {
                                            Log.i(TAG, "start sportEx success");
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_negative_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialogView = View.inflate(mContext, R.layout.dialog_settings_start_sport_parameter, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                }
                dialog.show();
            }
        });
        sendDeviceControlBtn.setOnClickListener(new View.OnClickListener() {
            View dialogView;
            AlertDialog dialog;
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.settings_parameter);
                    builder.setPositiveButton(R.string.dialog_positive_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogView != null) {
                                // Init view
                                EditText controlView = dialogView.findViewById(R.id.et_device_control);

                                // Obtaining Input Data
                                String control = controlView.getText().toString().trim();

                                // Invoke the sendDeviceControlinstruction method
                                HiHealthDataStore.sendDeviceControlinstruction(mContext, control, new ResultCallback() {
                                    @Override
                                    public void onResult(int resultCode, Object data) {
                                        combineResult(resultCode, data);
                                        if (resultCode == HiHealthError.SUCCESS) {
                                            Log.i(TAG, "sendDeviceControlinstruction success");
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_negative_btn_name, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialogView = View.inflate(mContext, R.layout.dialog_settings_sendcontrol_parameter, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                }
                dialog.show();
            }
        });
        getCountPaginationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder sb = new StringBuilder();
                final long endTime = System.currentTimeMillis();
                final long startTime = 0;
                HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthPointType.DATA_POINT_STRESS,
                    startTime, endTime, new HiHealthDataQueryOption());
                HiHealthDataStore.getCount(mContext, hiHealthDataQuery, new ResultCallback() {
                    @Override
                    public void onResult(final int resultCode, final Object data) {
                        if (resultCode == HiHealthError.SUCCESS) {
                            final int count = (int) data;
                            // number of pages
                            int pages = 3;
                            // number of data on each page
                            int limit = count / pages;
                            // indicates the offset of the queried data
                            int offset = 0;
                            // sorting parameters
                            int order = 0;
                            for (int i = 0; i < pages; i++) {
                                offset = limit * i;
                                if (i == pages - 1) {
                                    limit = count - offset;
                                }
                                HiHealthDataQuery hiHealthDataQuery =
                                    new HiHealthDataQuery(HiHealthPointType.DATA_POINT_STRESS, startTime, endTime,
                                        new HiHealthDataQueryOption(limit, offset, order));
                                HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, 0, new ResultCallback() {
                                    @Override
                                    public void onResult(int resultCode, Object data) {
                                        if (resultCode == HiHealthError.SUCCESS) {
                                            if (data instanceof List) {
                                                List dataList = (List) data;
                                                for (Object pointData : dataList) {
                                                    if (pointData instanceof HiHealthPointData) {
                                                        HiHealthPointData hiHealthPointData =
                                                            (HiHealthPointData) pointData;
                                                        sb.append(" start time:" + hiHealthPointData.getStartTime());
                                                        sb.append(" end time:" + hiHealthPointData.getEndTime());
                                                        sb.append(" value:" + hiHealthPointData.getDoubleValue());
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
                        } else {
                            combineResult(resultCode, data);
                        }
                    }
                });
            }
        });
        saveSampleCoreSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save five types of CoreSleep data.
                int[] coreSleepTypes = new int[]{HiHealthSessionType.DATA_SESSION_CORE_SLEEP_SHALLOW,
                    HiHealthSessionType.DATA_SESSION_CORE_SLEEP_DREAM,
                    HiHealthSessionType.DATA_SESSION_CORE_SLEEP_DEEP,
                    HiHealthSessionType.DATA_SESSION_CORE_SLEEP_WAKE,
                    HiHealthSessionType.DATA_SESSION_CORE_SLEEP_NOON};
                final long oneMinuteInMillis = 1000L * 60;
                final long currentTime = System.currentTimeMillis();
                long time = currentTime - currentTime % oneMinuteInMillis;
                for (int i = 0; i < coreSleepTypes.length; i++) {
                    int type = coreSleepTypes[i];
                    long endTime = time - i * oneMinuteInMillis;
                    long starTime = endTime - oneMinuteInMillis;
                    HiHealthSessionData hiHealthSessionData =
                        new HiHealthSessionData(type, starTime, endTime);
                    HiHealthDataStore.saveSample(mContext, hiHealthSessionData, new ResultCallback() {
                        @Override
                        public void onResult(int resultCode, Object object) {
                            Log.i(TAG, "saveSample CoreSleep resultCode is " + resultCode);
                            combineResult(resultCode, object);
                        }
                    });
                }
            }
        });

        execQueryCoreSleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeout = 0;
                long endTime = System.currentTimeMillis();
                // Start time, 30 days ago.
                long startTime = endTime - 1000 * 60 * 60 * 24 * 30L;
                HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(HiHealthSessionType.DATA_SESSION_CORE_SLEEP,
                    startTime, endTime, new HiHealthDataQueryOption());
                HiHealthDataStore.execQuery(mContext, hiHealthDataQuery, timeout, new ResultCallback() {
                    @Override
                    public void onResult(int resultCode, Object data) {
                        Log.i(TAG, "query CoreSleep resultCode: " + resultCode);
                        if (resultCode == HiHealthError.SUCCESS) {
                            // If the query is successful, the result is a list.
                            List dataList = (ArrayList) data;
                            if (dataList.size() == 0) {
                                combineResult(resultCode, "No data.");
                            } else {
                                List<String> result = new ArrayList<>();
                                for (Object obj : dataList) {
                                    JSONObject json = new JSONObject();
                                    HiHealthSessionData sessionData = (HiHealthSessionData) obj;
                                    try {
                                        json.put("startTime: ", sessionData.getStartTime());
                                        json.put("endTime: ", sessionData.getEndTime());
                                        json.put("type: ", sessionData.getType());
                                    } catch (JSONException e) {
                                        Log.e(TAG, "JSONException: " + e.getMessage());
                                    }
                                    result.add(json.toString());
                                }
                                combineResult(resultCode, result.toString());
                            }
                        } else {
                            // If the query fails, the result is a message.
                            combineResult(resultCode, data);
                        }
                    }
                });
            }
        });

        subscribeDataExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(openId)) {
                    Log.i(TAG, "openId is empty, invoke the SignIn method to obtain the value.");
                    combineResult(2, "openId is empty, invoke the SignIn method to obtain the value.");
                    return;
                }
                pendingReceiver = new MyReceiver();
                IntentFilter intentFilter = new IntentFilter(PENDING_FILTER);
                registerReceiver(pendingReceiver, intentFilter);
                filter = new ReceiverFilter(getPackageName(), PENDING_FILTER);
                // Enter the real appId, subscriberId, secret.
                subscriber = new Subscriber("appId", "subscriberId", "secret", filter);
                stepMetricGoal = new MetricGoal(HiHealthPointType.DATA_POINT_STEP_SUM, "step", 1000);
                int unit = 1; // Indicates the day dimension.
                int periodCount = 100; // 100 day
                recurrence = new Recurrence(unit, periodCount);
                goals = new ArrayList<>();
                goals.add(stepMetricGoal);
                // The default start date is the current day.
                int startDay = Integer.parseInt(getStartDay());
                goalInfo = new GoalInfo(goals, startDay, recurrence, openId);
                HiHealthDataStore.subscribeDataEx(MainActivity.this, subscriber, goalInfo, new ResultCallback() {
                    @Override
                    public void onResult(int i, Object message) {
                        Log.i(TAG, "subscribe result code = " + i + ", message = " + message);
                        combineResult(i, message);
                    }
                });
            }
        });

        unsubscribeDataExBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiHealthDataStore.unSubscribeDataEx(MainActivity.this, subscriber, goalInfo, new ResultCallback() {
                    @Override
                    public void onResult(int i, Object object) {
                        Log.i(TAG, "unsubscribe result code = " + i + ", message = " + object);
                        combineResult(i, object);
                    }
                });
            }
        });
    }

    private String getStartDay() {
        Date date = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(date);
    }

    private class MyReceiver extends BroadcastReceiver {
        private static final String TAG = "MyReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "on receive data, action " + intent.getAction());
            if (!intent.hasExtra(HiHealthKitConstant.EVENT_SIGNATURE)) {
                Log.w(TAG, "wrong message, no need to deal");
                return;
            }
            byte[] signature = intent.getByteArrayExtra(HiHealthKitConstant.EVENT_SIGNATURE);
            // Compare the generated signature with the received signature. If they are the same, the message is correct. Otherwise, ignore the message.
            // Signature generation rule: type, subType, and openId are concatenated with underscores (_) in sequence. The result is then encrypted with secret using the HMAC-SHA256 algorithm.
            // Signature verification is ignored here.
            if (!intent.hasExtra(HiHealthKitConstant.SAMPLE_EVENT)) {
                Log.w(TAG, "no event is returned");
                return;
            }
            SampleEvent event = intent.getExtras().getParcelable(HiHealthKitConstant.SAMPLE_EVENT);
            MetaData metaData = event.getMetaData().get(0);

            if ("goalStatus".equals(metaData.getMetaKey())) {
                String metaValue = metaData.getMetaValue();
                try {
                    JSONObject goalStatus = new JSONArray(metaValue).getJSONObject(0);
                    JSONArray goals = new JSONArray(goalStatus.getString("goalAchieve"));
                    for (int i = 0; i < goals.length(); i++) {
                        JSONObject goal = goals.getJSONObject(i);
                        if (goal.getInt("goalType") == CharacteristicConstant.GoalType.METRIC_GOAL) {
                            MetricGoal metricGoalTarget = new Gson().fromJson(goal.toString(), MetricGoal.class);
                            String result = "data type " + metricGoalTarget.getDataType() + " value : "
                                + metricGoalTarget.getValue() + " is achieved";
                            Log.i(TAG, result);
                            combineResult(0, result);
                        } else if (goal.getInt("goalType") == CharacteristicConstant.GoalType.DURATION_GOAL) {
                            DurationGoal durationGoalTarget = new Gson().fromJson(goal.toString(), DurationGoal.class);
                            String result = "data type " + durationGoalTarget.getDataType() + " duration : "
                                + durationGoalTarget.getDuration() + " is achieved";
                            Log.i(TAG, result);
                            combineResult(0, result);
                        } else {
                            Log.i(TAG, "wrong goal type");
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, "json exception");
                }
            }
        }
    }

    /**
     * Add scopes to apply for and obtains the authorization process Intent.
     */
    private void signIn() {
        Log.i(TAG, "begin sign in");
        List<Scope> scopeList = new ArrayList<>();

        // Add scopes to apply for. The following only shows an example.
        // Developers need to add scopes according to their specific needs.

        // Reads the health behavior information in the Health app via Health Kit.
        scopeList.add(new Scope(HiHealthExtendScope.HEALTHKIT_EXTEND_HEALTHBEHAVIOR_READ));

        // Reads the basic workout information in the Health app via Health Kit.
        scopeList.add(new Scope(HiHealthExtendScope.HEALTHKIT_EXTEND_SPORT_READ));

        // Reads the heart health/sleep information in the Health app via Health Kit.
        scopeList.add(new Scope(HiHealthExtendScope.HEALTHKIT_EXTEND_REALTIME_HEART_READ));

        // Reads HUAWEI Health Kit body composition data.
        scopeList.add(new Scope(HiHealthAtomicScope.HEALTHKIT_BODYFAT_READ));

        // Writes HUAWEI Health Kit body composition data.
        scopeList.add(new Scope(HiHealthAtomicScope.HEALTHKIT_BODYFAT_WRITE));

        // Reads the sleep information to the Health app via Health Kit.
        scopeList.add(new Scope(HiHealthAtomicScope.HEALTHKIT_SLEEP_READ));

        // Writes the sleep information to the Health app via Health Kit.
        scopeList.add(new Scope(HiHealthAtomicScope.HEALTHKIT_SLEEP_WRITE));

        // Reads HUAWEI Health Kit stress data
        scopeList.add(new Scope(HiHealthAtomicScope.HEALTHKIT_STRESS_READ));

        // Configure authorization parameters.
        HuaweiIdAuthParamsHelper authParamsHelper =
            new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM);
        HuaweiIdAuthParams authParams =
            authParamsHelper.setIdToken().setId().setAccessToken().setScopeList(scopeList).createParams();

        // Initialize the HuaweiIdAuthService object.
        final HuaweiIdAuthService authService = HuaweiIdAuthManager.getService(getApplicationContext(), authParams);

        // Silent sign-in. If authorization has been granted by the current account, the authorization screen will not
        // display. This is an asynchronous method.
        Task<AuthHuaweiId> authHuaweiIdTask = authService.silentSignIn();

        // Add the callback for the call result.
        authHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId huaweiId) {
                // The silent sign-in is successful.
                Log.i(TAG, "silentSignIn success");
                openId = huaweiId.openId;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // The silent sign-in fails. This indicates that the authorization has not been granted by the current
                // account.
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.i(TAG, "sign failed status:" + apiException.getStatusCode());
                    Log.i(TAG, "begin sign in by intent");

                    // Call the sign-in API using the getSignInIntent() method.
                    Intent signInIntent = authService.getSignInIntent();

                    // Display the authorization screen by using the startActivityForResult() method of the activity.
                    startActivityForResult(signInIntent, REQUEST_SIGN_IN_LOGIN);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle only the authorized responses
        if (requestCode != REQUEST_SIGN_IN_LOGIN) {
            return;
        }

        // Obtain the authorization response from the intent.
        HuaweiIdAuthResult result = HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
        Log.d(TAG, "handleSignInResult status = " + result.getStatus() + ", result = " + result.isSuccess());
        if (result.isSuccess()) {
            Log.d(TAG, "sign in is success");
        } else {
            Toast toast = Toast.makeText(mContext,
                "Please check the Package Name and the Signing Certificate Fingerprint if not cancelled",
                Toast.LENGTH_LONG);
            toast.show();
        }
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
            tvResult.setText(deviceList01);
        }
    }

    private void sendMsg(String result) {
        Message message = Message.obtain();
        message.obj = result;
        mHandler.sendMessage(message);
    }

    /**
     * combine result code and result
     *
     * @param resultCode result code
     * @param object result
     */
    private void combineResult(int resultCode, Object object) {
        StringBuilder sb = new StringBuilder();
        sb.append("resultCode = ").append(resultCode);
        sb.append(" result = ").append(object);
        sendMsg(sb.toString());
    }

    private int getRadioGroupResult(RadioGroup radioGroup) {
        int result = 0;
        if (radioGroup != null && radioGroup.getChildCount() > 0) {
            RadioButton firstRadioButton = (RadioButton) radioGroup.getChildAt(radioGroup.getChildCount() - 1);
            result = firstRadioButton.isChecked() ? 0 : 1;
        }
        return result;
    }
}