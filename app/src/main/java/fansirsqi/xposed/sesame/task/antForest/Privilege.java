package fansirsqi.xposed.sesame.task.antForest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fansirsqi.xposed.sesame.util.Log;
import fansirsqi.xposed.sesame.util.StatusUtil;

public class Privilege {

    public static final String TAG = Privilege.class.getSimpleName();


    // 青春特权🌸领取
    static boolean youthPrivilege() {
        try {
            if (!StatusUtil.canYouthPrivilegeToday()) return false;
            List<List<String>> taskList = Arrays.asList(
                    Arrays.asList("DNHZ_SL_college", "DAXUESHENG_SJK", "双击卡"),
                    Arrays.asList("DXS_BHZ", "NENGLIANGZHAO_20230807", "保护罩"),
                    Arrays.asList("DXS_JSQ", "JIASUQI_20230808", "加速器")
            );
            List<String> resultList = new ArrayList<>();
            for (List<String> task : taskList) {
                String queryParam = task.get(0); // 用于 queryTaskListV2 方法的第一个参数
                String receiveParam = task.get(1); // 用于 receiveTaskAwardV2 方法的第二个参数
                String taskName = task.get(2); // 标记名称
                String queryResult = AntForestRpcCall.queryTaskListV2(queryParam);
                JSONObject getTaskStatusObject = new JSONObject(queryResult);
                JSONArray taskInfoList = getTaskStatusObject.getJSONArray("forestTasksNew")
                        .getJSONObject(0).getJSONArray("taskInfoList");
                resultList.addAll(handleTaskList(taskInfoList, receiveParam, taskName));
            }
            if (resultList.stream().allMatch("处理成功"::equals)) {
                StatusUtil.setYouthPrivilegeToday();
                return true;
            } else return false;
        } catch (Exception e) {
            Log.runtime(AntForest.TAG, "youthPrivilege err:");
            Log.printStackTrace(AntForest.TAG, e);
            return false;
        }
    }

    /**
     * 处理任务列表
     */
    private static List<String> handleTaskList(JSONArray taskInfoList, String receiveParam, String taskName) throws JSONException {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < taskInfoList.length(); i++) {
            JSONObject taskInfo = taskInfoList.getJSONObject(i);
            JSONObject taskBaseInfo = taskInfo.getJSONObject("taskBaseInfo");

            if (receiveParam.equals(taskBaseInfo.getString("taskType"))) {
                String taskStatus = taskBaseInfo.getString("taskStatus");
                if ("RECEIVED".equals(taskStatus)) {
                    Log.other("青春特权🌸[" + taskName + "]已领取");
                } else if ("FINISHED".equals(taskStatus)) {
//                    Log.forest("青春特权🌸[" + taskName + "]尝试领取...");
                    String receiveResult = AntForestRpcCall.receiveTaskAwardV2(receiveParam);
                    JSONObject resultOfReceive = new JSONObject(receiveResult);
                    String resultDesc = resultOfReceive.getString("desc");
                    resultList.add(resultDesc);
                    if (resultDesc.equals("处理成功")) {
                        Log.forest("青春特权🌸[" + taskName + "]领取成功");
                    } else {
                        Log.forest("青春特权🌸[" + taskName + "]领取结果：" + resultDesc);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 青春特权每日签到红包
     */
    static void studentSignInRedEnvelope() {
        try {
            LocalTime currentTime = LocalTime.now();
            // 定义签到时间范围
            final LocalTime START_TIME = LocalTime.of(5, 0); // 5:00 AM
            final LocalTime END_TIME = LocalTime.of(10, 0);  // 10:00 AM
            if (currentTime.isBefore(START_TIME)) {
                Log.forest(" 青春特权🧧5点前不执行签到");
                return;
            }
            String tag = currentTime.isBefore(END_TIME) ? "double" : "single";
            studentTaskHandle(tag);
        } catch (Exception e) {
            Log.runtime(AntForest.TAG, "studentSignInRedEnvelope err:");
            Log.printStackTrace(AntForest.TAG, e);
        }
    }

    /**
     * 学生签到执行逻辑
     */
    static void studentTask(String tag) {
        try {
            String result = AntForestRpcCall.studentCheckin();
            JSONObject resultJson = new JSONObject(result);
            String resultDesc = resultJson.getString("resultDesc");

            if (resultDesc.contains("不匹配")) {
                Log.forest(" 青春特权🧧 " + tag + "：" + resultDesc + "可能账户不符合条件");
            } else {
                Log.forest(" 青春特权🧧 " + tag + "：" + resultDesc);
            }
        } catch (Exception e) {
            Log.runtime(AntForest.TAG, "studentTask err:");
            Log.printStackTrace(AntForest.TAG, e);
        }
    }

    /**
     * 处理不在签到时间范围内的逻辑
     */
    private static void studentTaskHandle(String tag) {
        try {
            if (!StatusUtil.canStudentTask()) return;

            String isTasked = AntForestRpcCall.studentQqueryCheckInModel();
            JSONObject isTaskedJson = new JSONObject(isTasked);
            String action = isTaskedJson.getJSONObject("studentCheckInInfo").getString("action");
            if ("DO_TASK".equals(action)) {
                Log.record(" 青春特权🧧今日已签到");
                StatusUtil.setStudentTaskToday();
            } else {
                studentTask(tag);
            }
        } catch (JSONException e) {
            Log.runtime(AntForest.TAG, "studentTaskHandle JSON err:");
            Log.printStackTrace(AntForest.TAG, e);
        } catch (Exception e) {
            Log.runtime(AntForest.TAG, "studentTaskHandle err:");
            Log.printStackTrace(AntForest.TAG, e);
        }
    }

}
