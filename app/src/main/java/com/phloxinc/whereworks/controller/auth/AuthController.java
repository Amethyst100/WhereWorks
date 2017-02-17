package com.phloxinc.whereworks.controller.auth;

import android.util.Log;

import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.http.RequestManager;
import com.phloxinc.whereworks.prefs.Prefs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthController {

    private static AuthController instance;

    public static AuthController getInstance() {
        if (instance == null)
            instance = new AuthController();
        return instance;
    }

    public String login(String email, String password) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("email", email);
            params.put("password", password);
            params.put("process", Process.MEMBER_LOGIN);
            params.put("tokentype", String.valueOf(2));

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String loginWithNumber(String number, String password) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("msisdn", number);
            params.put("password", password);
            params.put("process", Process.MEMBER_LOGIN);
            params.put("tokentype", String.valueOf(2));

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String validateMember(String email, String number, String name, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("email", email);
            params.put("msisdn", number);
            params.put("name", name);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_VALIDATE);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String getMemberInfo(String userId, String token) {

        Map<String, String> params = new HashMap<> ();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_DETAIL);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> memberInfoMap = (Map<String, String>) response.get("data");
                    Prefs.putString("userName", memberInfoMap.get("mem_full_name"));
                    Prefs.putString("userEmail", memberInfoMap.get("mem_email"));
                    Prefs.putString("userNumber", memberInfoMap.get("mem_MSISDN"));

                    if (memberInfoMap.containsKey("mem_photo")) {
                        Prefs.putString("userPhoto", memberInfoMap.get("mem_photo"));
                    }

                    return "Success";
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String updateMemberInfo(String name, String number, String timezone, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("name", name);
            params.put("msisdn", number);
            params.put("timezone", timezone);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_UPDATE);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    Prefs.putString("userName", name);
                    Prefs.putString("userNumber", number);
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String changePassword(String oldPassword, String newPassword, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("password", oldPassword);
            params.put("newpassword", newPassword);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_CHANGE_PASSWORD);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }

    public String forgotPassword(String email) {
        try {
            Map<String, String> params = new HashMap<> ();
            params.put("email", email);
            params.put("process", Process.MEMBER_FORGOT_PASS);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("AuthController" , e.getMessage());
        }
        return null;
    }
}
