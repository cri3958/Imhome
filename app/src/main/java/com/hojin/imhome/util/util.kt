package com.hojin.imhome.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList

class util {
    /**
     * 키보드 내리기
     */
    fun keyboard_down(context:Context, view: View){
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 퍼미션 체크 밑 요청하기
     */
    private var rejectedPermissionList = ArrayList<String>()
    private val multiplePermissionsCode = 100

    fun settingPermission(activity: Activity, context: Context, requiredPermissions: ArrayList<String>):Boolean{
        return if(checkPermissions(context,requiredPermissions))
            true
        else {
            if (requestPermissions(activity, context, requiredPermissions))
                true
            else
                settingPermission(activity,context,requiredPermissions)
        }
    }

    private fun checkPermissions(context:Context,requiredPermissions: ArrayList<String>):Boolean {//https://m.blog.naver.com/PostView.naver?blogId=chandong83&logNo=221616557088&proxyReferer=https:%2F%2Fwww.google.com%2F
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var isClear: Boolean = true//권한이 전부다 있음
        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                rejectedPermissionList.add(permission)
                isClear = false
            }
        }
        return isClear
    }

    private fun requestPermissions(activity: Activity,context: Context, requiredPermissions: ArrayList<String>):Boolean{
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(activity, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
        return checkPermissions(context,requiredPermissions)
    }
}