/*
 * FreeOTP
 *
 * Authors: Petter Arvidsson <petter.arvidsson@fidesmo.com>
 *
 * Copyright (C) 2014 Petter Arvidsson, Fidesmo AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.Tag;

public class NfcHelpers {

    public static void enableForegroundDispatch(Activity activity, NfcAdapter adapter) {
        Intent intent = activity.getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        enableForegroundDispatch(activity, adapter, intent);
    }

    public static void enableForegroundDispatch(Activity activity, NfcAdapter adapter, Intent intent) {
        if(adapter.isEnabled()) {
            PendingIntent tagIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            IntentFilter iso = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            adapter.enableForegroundDispatch(activity, tagIntent, new IntentFilter[]{iso},
                                             new String[][]{new String[]{IsoDep.class.getName()}});
        }
    }

    public static void disableForegroundDispatch(Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public static IsoDep getIsoTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null) {
            return IsoDep.get(tag);
        } else {
            return null;
        }
    }
}
