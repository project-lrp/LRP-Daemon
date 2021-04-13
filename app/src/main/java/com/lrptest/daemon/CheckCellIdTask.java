package com.lrptest.daemon;

import android.os.AsyncTask;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class CheckCellIdTask extends AsyncTask<List<CellInfo>, Void, String> {
    private String message = "";
    private final String LOG_TAG = "LRP Daemon Cell Info";

    protected String doInBackground(List<CellInfo>... params) {
        List<CellInfo> curCellInfoList = params[0];
        //This method runs in the same thread as the UI.
        if (curCellInfoList == null) {
            Log.i(LOG_TAG, "curCellInfoList = Null");
        } else {
//            Log.d(LOG_TAG, "curCellInfoList =\n" + curCellInfoList);
            for (CellInfo c : curCellInfoList) {
                if (c.isRegistered()) {
//                    if (c instanceof CellInfoGsm) {
//                        Log.d(LOG_TAG, "GSM");
//                        CellIdentityGsm cellIdentityGsm = ((CellInfoGsm) c).getCellIdentity();
//                        message = String.format(Locale.US, "Registered Gsm Cell: Cid-Lac = %d-%d", cellIdentityGsm.getCid(), cellIdentityGsm.getLac());
//                    } else if (c instanceof CellInfoWcdma){
//                        Log.d(LOG_TAG, "WCDMA");
//                        CellIdentityWcdma cellIdentityWcdma = ((CellInfoWcdma) c).getCellIdentity();
//                        message = String.format(Locale.US, "Registered Wcdma Cell: Cid-Lac = %d-%d", cellIdentityWcdma.getCid(), cellIdentityWcdma.getLac());
//                    }
                    if (c instanceof CellInfoLte) {
//                        Log.d(LOG_TAG, "LTE");
                        CellIdentityLte cellIdentityLte = ((CellInfoLte) c).getCellIdentity();
                        message = String.format(Locale.US, "Registered LTE Cell: Pci-Tac = %d-%d", cellIdentityLte.getPci(), cellIdentityLte.getCi());
                    }
                }
            }
        }
        return message;
    }

    protected void onProgressUpdate(Void... progress) {
    }

    protected void onPostExecute(String result) {
    }
}
