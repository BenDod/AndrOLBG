package com.bendod.androlbg.enumerations;

import com.bendod.androlbg.R;

import android.content.res.Resources;

public enum StatusCode {

    COMMUNICATION_NOT_STARTED(R.string.err_start),
    NO_ERROR(R.string.err_none),
    TIP_SAVED(R.string.info_tip_saved),
    LOGIN_PARSE_ERROR(R.string.err_parse),
    CONNECTION_FAILED(R.string.err_server),
    NO_LOGIN_INFO_STORED(R.string.err_login),
    UNKNOWN_ERROR(R.string.err_unknown),
    COMMUNICATION_ERROR(R.string.err_comm),
    WRONG_LOGIN_DATA(R.string.err_wrong),
    UNAPPROVED_LICENSE(R.string.err_license),
    MAINTENANCE(R.string.err_maintenance),
    TIP_POST_ERROR(R.string.err_tip_post_failed),
    NO_LOG_TEXT(R.string.warn_log_text_fill),
    NO_DATA_FROM_SERVER(R.string.err_log_failed_server),
    NOT_LOGGED_IN(R.string.init_login_popup_failed);

    final private int error_string;

    StatusCode(int error_string) {
        this.error_string = error_string;
    }

    public int getErrorString() {
        return error_string;
    }

    public String getErrorString(final Resources res) {
        return res.getString(error_string);
    }

}
